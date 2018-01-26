package org.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.timeseries.MultiTimeSeries;

public class ImmutableSimpleMultiTimeSeries<E> extends AbstractMultiTimeSeries<E>
{

    private ImmutableSimpleMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries)
    {
        super(entries, t->new LinkedList<>(), null);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSimpleMultiTimeSeries");
    }

    @Override
    public Collection<E> remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSimpleMultiTimeSeries");
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableSimpleMultiTimeSeries");
    }

    public static class Builder<E> 
    {
        //each entry in this collection will act as simple entry that is it will contain only one event. 
        //When we build the TimeSeries then the constructor will make it Multi
        private final Collection<MultiTimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        
        public Builder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public void add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            MultiTimeSeriesEntry<E> entry = new MultiTimeSeriesEntry<E>(eventDateTime, new ArrayList<E>(1));
            entry.getEvents().add(event);
            entries.add(entry);
        }
        
        public ImmutableSimpleMultiTimeSeries<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableSimpleMultiTimeSeries<E>(entries);
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
