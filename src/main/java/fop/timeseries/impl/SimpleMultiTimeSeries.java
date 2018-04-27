package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;

import fop.timeseries.MultiTimeSeries;

public class SimpleMultiTimeSeries<E> extends AbstractMultiTimeSeries<E>
{
    public SimpleMultiTimeSeries()
    {
        super(()->new LinkedList<>());
    }

    public SimpleMultiTimeSeries(MultiTimeSeries<E> timeSeries)
    {
        super(timeSeries, ()->new LinkedList<>());
    }

    public SimpleMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries)
    {
        super(entries, ()->new LinkedList<>());
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        super.addToEntry(eventDateTime, event);
    }

    @Override
    public Collection<E> remove(ZonedDateTime eventDateTime)
    {
        return super.removeEntry(Instant.from(eventDateTime));
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        return super.removeEvent(Instant.from(eventDateTime), event);
    }
}
