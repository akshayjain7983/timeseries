package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.ImmutableTimeSeries;
import fop.timeseries.TimeSeries;

public final class ImmutableTimeSeriesImpl<E> extends AbstractTimeSeries<E> implements ImmutableTimeSeries<E>, Cloneable
{
    private ImmutableTimeSeriesImpl(Collection<TimeSeries.Entry<E>> entries)
    {
        super(entries);
    }
    
    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableTimeSeriesImpl");
    }

    @Override
    public E remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableTimeSeriesImpl");
    }

    @Override
    public ImmutableTimeSeries<E> with(ZonedDateTime eventDateTime, E event)
    {
        return ImmutableTimeSeriesImpl.<E>builder().add(getEntries()).add(eventDateTime, event).build();
    }
    
    @Override
    public ImmutableTimeSeries<E> with(TimeSeries.Entry<E> entry)
    {
        return ImmutableTimeSeriesImpl.<E>builder().add(getEntries()).add(entry).build();
    }

    @Override
    public ImmutableTimeSeries<E> with(Collection<Entry<E>> entries)
    {
        return ImmutableTimeSeriesImpl.<E>builder().add(getEntries()).add(entries).build();
    }
    
    @Override
    public ImmutableTimeSeriesImpl<E> clone() 
    {
        return new ImmutableTimeSeriesImpl<E>(getEntries());
    }
    
    public static <E> Builder<E> builder()
    {
        return new Builder<E>();
    }

    public static class Builder<E> 
    {
        private final Collection<TimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        
        private Builder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            entries.add(TimeSeriesEntry.of(eventDateTime, event));
            return this;
        }
        
        public Builder<E> add(TimeSeries.Entry<E> entry)
        {
            ensureBuilderValidity();
            entries.add(entry);
            return this;
        }
        
        public Builder<E> add(Collection<TimeSeries.Entry<E>> entries)
        {
            ensureBuilderValidity();
            this.entries.addAll(entries);
            return this;
        }
        
        public ImmutableTimeSeriesImpl<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableTimeSeriesImpl<>(entries);   
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
