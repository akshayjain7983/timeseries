package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.ImmutableMultiTimeSeries;
import fop.timeseries.MultiTimeSeries;

public class ImmutableMultiTimeSeriesImpl<E> extends AbstractMultiTimeSeries<E> implements ImmutableMultiTimeSeries<E>, Cloneable
{

    private ImmutableMultiTimeSeriesImpl(Collection<MultiTimeSeries.Entry<E>> entries)
    {
        super(entries, ()->new LinkedList<>());
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public Collection<E> remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public ImmutableMultiTimeSeries<E> with(ZonedDateTime eventDateTime, E event)
    {
        return ImmutableMultiTimeSeriesImpl.<E>builder().add(getEntries()).add(eventDateTime, event).build();
    }

    @Override
    public ImmutableMultiTimeSeries<E> with(ZonedDateTime eventDateTime, Collection<E> events)
    {
        return ImmutableMultiTimeSeriesImpl.<E>builder().add(getEntries()).add(eventDateTime, events).build();
    }
    
    @Override
    public ImmutableMultiTimeSeries<E> with(MultiTimeSeries.Entry<E> entry)
    {
        return ImmutableMultiTimeSeriesImpl.<E>builder().add(getEntries()).add(entry).build();
    }

    @Override
    public ImmutableMultiTimeSeries<E> with(Collection<Entry<E>> entries)
    {
        return ImmutableMultiTimeSeriesImpl.<E>builder().add(getEntries()).add(entries).build();
    }
    
    @Override
    public ImmutableMultiTimeSeriesImpl<E> clone()
    {
        return new ImmutableMultiTimeSeriesImpl<E>(getEntries());
    }
    
    public static <E> Builder<E> builder()
    {
        return new Builder<E>();
    }

    public static class Builder<E> 
    {
        //each entry in this collection will act as simple entry that is it will contain only one event. 
        //When we build the TimeSeries then the constructor will make it Multi
        private final Collection<MultiTimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        
        private Builder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            entries.add(MultiTimeSeriesEntry.of(eventDateTime, Arrays.asList(event)));
            return this;
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, Collection<E> events)
        {
            ensureBuilderValidity();
            entries.add(MultiTimeSeriesEntry.of(eventDateTime, events));
            return this;
        }
        
        public Builder<E> add(MultiTimeSeries.Entry<E> entry)
        {
            ensureBuilderValidity();
            entries.add(entry);
            return this;
        }
        
        public Builder<E> add(Collection<MultiTimeSeries.Entry<E>> entries)
        {
            ensureBuilderValidity();
            this.entries.addAll(entries);
            return this;
        }
        
        public ImmutableMultiTimeSeriesImpl<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableMultiTimeSeriesImpl<E>(entries);
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
