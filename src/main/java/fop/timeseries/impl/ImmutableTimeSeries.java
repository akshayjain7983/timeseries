package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import fop.timeseries.TimeSeries;

public final class ImmutableTimeSeries<E> extends AbstractTimeSeries<E>
{
    private ImmutableTimeSeries(Collection<TimeSeries.Entry<E>> entries)
    {
        super(entries);
    }
    
    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableTimeSeries");
    }

    @Override
    public E remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableTimeSeries");
    }
    
    public static <E> Builder<E> builder()
    {
        return new Builder<E>();
    }

    public static class Builder<E> 
    {
        private final Collection<TimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        
        public Builder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public void add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            TimeSeriesEntry<E> entry = new TimeSeriesEntry<E>(eventDateTime, event);
            entries.add(entry);
        }
        
        public ImmutableTimeSeries<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableTimeSeries<>(entries);   
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
