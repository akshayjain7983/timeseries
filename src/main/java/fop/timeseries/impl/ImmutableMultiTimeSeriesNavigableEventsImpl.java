package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.ImmutableMultiTimeSeriesNavigableEvents;
import fop.timeseries.MultiTimeSeriesNavigableEvents;

public class ImmutableMultiTimeSeriesNavigableEventsImpl<E> extends AbstractMultiTimeSeriesNavigableEvents<E> implements ImmutableMultiTimeSeriesNavigableEvents<E>, Cloneable
{
    private ImmutableMultiTimeSeriesNavigableEventsImpl(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries, Comparator<E> entryEventsNavigableSetComparator)
    {
        super(entries, entryEventsNavigableSetComparator);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEventsImpl");
    }

    @Override
    public NavigableSet<E> remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEventsImpl");
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesNavigableEventsImpl");
    }

    @Override
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(ZonedDateTime eventDateTime, E event)
    {
        return ImmutableMultiTimeSeriesNavigableEventsImpl.<E>builder().add(getEntries()).add(eventDateTime, event).build();
    }

    @Override
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(ZonedDateTime eventDateTime, Collection<E> events)
    {
        return ImmutableMultiTimeSeriesNavigableEventsImpl.<E>builder().add(getEntries()).add(eventDateTime, events).build();
    }

    @Override
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(Entry<E> entry)
    {
        return ImmutableMultiTimeSeriesNavigableEventsImpl.<E>builder().add(getEntries()).add(entry).build();
    }

    @Override
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(Collection<Entry<E>> entries)
    {
        return ImmutableMultiTimeSeriesNavigableEventsImpl.<E>builder().add(getEntries()).add(entries).build();
    }
    
    @Override
    public ImmutableMultiTimeSeriesNavigableEventsImpl<E> clone()
    {
        return new ImmutableMultiTimeSeriesNavigableEventsImpl<>(getEntries(), getEntryEventsNavigableSetComparator());
    }
    
    public static <E> Builder<E> builder()
    {
        return builder(null);
    }
    
    public static <E> Builder<E> builder(Comparator<E> entryEventsNavigableSetComparator)
    {
        return new Builder<E>(entryEventsNavigableSetComparator);
    }

    public static class Builder<E> 
    {
        //each entry in this collection will act as simple entry that is it will contain only one event. 
        //When we build the TimeSeries then the constructor will make it Multi
        private final NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        private final Comparator<E> entryEventsNavigableSetComparator;
        
        private Builder(Comparator<E> entryEventsNavigableSetComparator)
        {
            this.entries = new TreeSet<MultiTimeSeriesNavigableEvents.Entry<E>>();
            this.builderExpired = new AtomicBoolean(false);
            this.entryEventsNavigableSetComparator = entryEventsNavigableSetComparator;
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            NavigableSet<E> eventSet = new TreeSet<>(entryEventsNavigableSetComparator);
            eventSet.add(event);
            entries.add(MultiTimeSeriesNavigableEventsEntry.of(eventDateTime, eventSet));
            return this;
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, Collection<E> events)
        {
            ensureBuilderValidity();
            NavigableSet<E> eventSet = new TreeSet<>(entryEventsNavigableSetComparator);
            eventSet.addAll(events);
            entries.add(MultiTimeSeriesNavigableEventsEntry.of(eventDateTime, eventSet));
            return this;
        }
        
        public Builder<E> add(MultiTimeSeriesNavigableEvents.Entry<E> entry)
        {
            ensureBuilderValidity();
            entries.add(entry);
            return this;
        }
        
        public Builder<E> add(Collection<MultiTimeSeriesNavigableEvents.Entry<E>> entries)
        {
            ensureBuilderValidity();
            this.entries.addAll(entries);
            return this;
        }
        
        public ImmutableMultiTimeSeriesNavigableEventsImpl<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableMultiTimeSeriesNavigableEventsImpl<E>(entries, entryEventsNavigableSetComparator);
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
