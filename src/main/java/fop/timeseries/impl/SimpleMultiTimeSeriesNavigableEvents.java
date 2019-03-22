package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.NavigableSet;

import fop.timeseries.MultiTimeSeriesNavigableEvents;

public class SimpleMultiTimeSeriesNavigableEvents<E> extends AbstractMultiTimeSeriesNavigableEvents<E> implements Cloneable
{
    public SimpleMultiTimeSeriesNavigableEvents()
    {
        super(null);
    }

    public SimpleMultiTimeSeriesNavigableEvents(MultiTimeSeriesNavigableEvents<E> timeSeries)
    {
        super(timeSeries, null);
    }

    public SimpleMultiTimeSeriesNavigableEvents(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries)
    {
        super(entries, null);
    }
    
    public SimpleMultiTimeSeriesNavigableEvents(Comparator<E> entryCollectionComparator)
    {
        super(entryCollectionComparator);
    }

    public SimpleMultiTimeSeriesNavigableEvents(MultiTimeSeriesNavigableEvents<E> timeSeries, Comparator<E> entryEventsNavigableSetComparator)
    {
        super(timeSeries, entryEventsNavigableSetComparator);
    }

    public SimpleMultiTimeSeriesNavigableEvents(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries, Comparator<E> entryEventsNavigableSetComparator)
    {
        super(entries, entryEventsNavigableSetComparator);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, E event)
    {
        super.addToEntry(eventDateTime, event);
    }

    @Override
    public NavigableSet<E> remove(ZonedDateTime eventDateTime)
    {
        return (NavigableSet<E>)super.removeEntry(Instant.from(eventDateTime));
    }

    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event)
    {
        return super.removeEvent(Instant.from(eventDateTime), event);
    }
    
    @Override
    public SimpleMultiTimeSeriesNavigableEvents<E> clone()
    {
        return new SimpleMultiTimeSeriesNavigableEvents<>(this, getEntryEventsNavigableSetComparator());
    }
}
