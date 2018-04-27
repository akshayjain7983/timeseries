package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

import fop.timeseries.MultiTimeSeries;
import fop.timeseries.MultiTimeSeriesNavigableEvents;

public abstract class AbstractMultiTimeSeriesNavigableEvents<E> extends AbstractMultiTimeSeries<E> implements MultiTimeSeriesNavigableEvents<E>
{
    private final Comparator<E> entryCollectionComparator;
    
    public AbstractMultiTimeSeriesNavigableEvents(Comparator<E> entryCollectionComparator)
    {
        super(()->new TreeSet<>(entryCollectionComparator));
        this.entryCollectionComparator = entryCollectionComparator;
    }

    public AbstractMultiTimeSeriesNavigableEvents(MultiTimeSeriesNavigableEvents<E> timeSeries, Comparator<E> entryCollectionComparator)
    {
        super(timeSeries, ()->new TreeSet<>(entryCollectionComparator));
        this.entryCollectionComparator = entryCollectionComparator;
    }

    public AbstractMultiTimeSeriesNavigableEvents(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries, Comparator<E> entryCollectionComparator)
    {
        super(()->new TreeSet<>(entryCollectionComparator));
        entries.forEach(e->super.addEntry(Instant.from(e.getEventDateTime()), e));
        this.entryCollectionComparator = entryCollectionComparator;
    }
    
    @Override
    protected void addToEntry(ZonedDateTime eventDateTime, E event)
    {
        validateEntryEvent(event);
        super.addToEntry(eventDateTime, event);
    }

    @Override
    public NavigableSet<E> get(ZonedDateTime eventDateTime)
    {
        return (NavigableSet<E>)super.get(eventDateTime);
    }

    @Override
    public NavigableSet<E> events()
    {
        //TODO
        
        return null;
    }
    
    @Override
    protected MultiTimeSeries.Entry<E> createEntry(ZonedDateTime eventDateTime, Collection<E> events)
    {
        return new MultiTimeSeriesNavigableEventsEntry<E>(eventDateTime, (NavigableSet<E>)events);
    }
    
    public static class MultiTimeSeriesNavigableEventsEntry<E> extends MultiTimeSeriesEntry<E> implements MultiTimeSeriesNavigableEvents.Entry<E>
    {
        public MultiTimeSeriesNavigableEventsEntry(ZonedDateTime eventDateTime, NavigableSet<E> events)
        {
            super(eventDateTime, events);
        }

        @Override
        public NavigableSet<E> getEvents()
        {
            return (NavigableSet<E>)super.getEvents();
        }
    }

    protected Comparator<E> getEntryCollectionComparator()
    {
        return entryCollectionComparator;
    }
    
    protected void validateEntryEvent(E event) 
    {
        if(getEntryCollectionComparator() == null && !Comparable.class.isAssignableFrom(event.getClass()))
        {
            throw new IllegalArgumentException("MultiTimeSeriesNavigableEvents is neither initiated with a entryCollectionComparator nor the events are Comparable");
        }
    }
}
