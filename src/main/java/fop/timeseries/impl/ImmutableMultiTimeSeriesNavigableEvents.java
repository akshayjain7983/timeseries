package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.MultiTimeSeriesNavigableEvents;

public class ImmutableMultiTimeSeriesNavigableEvents<E> extends AbstractMultiTimeSeriesNavigableEvents<E>
{
    private ImmutableMultiTimeSeriesNavigableEvents(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries, Comparator<E> entryCollectionComparator)
    {
        super(entries, entryCollectionComparator);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEvents");
    }

    @Override
    public NavigableSet<E> remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEvents");
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEvents");
    }
    
    public static <E> Builder<E> builder()
    {
        return builder(null);
    }
    
    public static <E> Builder<E> builder(Comparator<E> entryCollectionComparator)
    {
        return new Builder<E>(entryCollectionComparator);
    }

    public static class Builder<E> 
    {
        //each entry in this collection will act as simple entry that is it will contain only one event. 
        //When we build the TimeSeries then the constructor will make it Multi
        private final NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        private final Comparator<E> entryCollectionComparator;
        
        private Builder(Comparator<E> entryCollectionComparator)
        {
            this.entries = new TreeSet<MultiTimeSeriesNavigableEvents.Entry<E>>();
            this.builderExpired = new AtomicBoolean(false);
            this.entryCollectionComparator = entryCollectionComparator;
        }
        
        public void add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            MultiTimeSeriesNavigableEventsEntry<E> entry = new MultiTimeSeriesNavigableEventsEntry<E>(eventDateTime, new TreeSet<E>());
            entry.getEvents().add(event);
            entries.add(entry);
        }
        
        public ImmutableMultiTimeSeriesNavigableEvents<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableMultiTimeSeriesNavigableEvents<E>(entries, entryCollectionComparator);
            }
            finally
            {
                builderExpired.set(true);
            }
        }
        
        private void ensureBuilderValidity()
        {
            if(builderExpired.get())
            {
                throw new IllegalStateException("Builder already used to build once. Create a new builder");
            }
        }
    }
}
