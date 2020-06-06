package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.Collection;

import fop.timeseries.ImmutableTimeSeries;
import fop.timeseries.TimeSeries;
import fop.timeseries.util.ImmutableBuilders;

public final class ImmutableTimeSeriesImpl<E> extends AbstractTimeSeries<E> implements ImmutableTimeSeries<E>, Cloneable
{
    public ImmutableTimeSeriesImpl(Collection<TimeSeries.Entry<E>> entries)
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
        return ImmutableBuilders.<E>builder().add(getEntries()).add(eventDateTime, event).build();
    }
    
    @Override
    public ImmutableTimeSeries<E> with(TimeSeries.Entry<E> entry)
    {
        return ImmutableBuilders.<E>builder().add(getEntries()).add(entry).build();
    }

    @Override
    public ImmutableTimeSeries<E> with(Collection<Entry<E>> entries)
    {
        return ImmutableBuilders.<E>builder().add(getEntries()).add(entries).build();
    }
    
    @Override
    public ImmutableTimeSeriesImpl<E> clone() 
    {
        return new ImmutableTimeSeriesImpl<E>(getEntries());
    }
}
