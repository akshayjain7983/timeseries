package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.MultiTimeSeries;

public class ImmutableSortedMultiTimeSeries<E> extends AbstractMultiTimeSeries<E>
{
    private ImmutableSortedMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries, Comparator<E> entryCollectionComparator)
    {
        super(entries, t->new TreeSet<E>(t), entryCollectionComparator);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSortedMultiTimeSeries");
    }

    @Override
    public Collection<E> remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSortedMultiTimeSeries");
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSortedMultiTimeSeries");
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
        private final Collection<MultiTimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        private final Comparator<E> entryCollectionComparator;
        
        private Builder(Comparator<E> entryCollectionComparator)
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
            this.entryCollectionComparator = entryCollectionComparator;
        }
        
        public void add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            MultiTimeSeriesEntry<E> entry = new MultiTimeSeriesEntry<E>(eventDateTime, new ArrayList<E>(1));
            entry.getEvents().add(event);
            entries.add(entry);
        }
        
        public ImmutableSortedMultiTimeSeries<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableSortedMultiTimeSeries<E>(entries, entryCollectionComparator);
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
