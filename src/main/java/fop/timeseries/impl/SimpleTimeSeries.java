package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;

import fop.timeseries.TimeSeries;

public class SimpleTimeSeries<E> extends AbstractTimeSeries<E>
{
    public SimpleTimeSeries()
    {
        super();
    }

    public SimpleTimeSeries(TimeSeries<E> timeSeries)
    {
        super(timeSeries);
    }
    
    @Override
    public SimpleTimeSeries<E> clone()
    {
        return new SimpleTimeSeries<>(this);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        TimeSeriesEntry<E> entry = TimeSeriesEntry.of(eventDateTime, event);
        super.addEntry(entry.getEventInstant(), entry);
    }

    @Override
    public E remove(ZonedDateTime eventDateTime)
    {
        return super.removeEntry(Instant.from(eventDateTime));
    }

}
