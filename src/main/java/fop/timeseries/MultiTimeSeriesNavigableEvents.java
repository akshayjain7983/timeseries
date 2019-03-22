package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NavigableSet;

public interface MultiTimeSeriesNavigableEvents<E> extends Iterable<MultiTimeSeriesNavigableEvents.Entry<E>>
{
    public void add(ZonedDateTime eventDateTime, E event);

    public NavigableSet<E> get(ZonedDateTime eventDateTime);

    public NavigableSet<E> remove(ZonedDateTime eventDateTime);
    
    public boolean remove(ZonedDateTime eventDateTime, E event);
    
    public boolean contains(ZonedDateTime eventDateTime);
    
    public boolean contains(ZonedDateTime eventDateTime, E event);

    public int size();
    
    public default boolean isEmpty()
    {
        return size()==0;
    }
    
    public default boolean isNotEmpty()
    {
        return size()>0;
    }

    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntries();
    
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, boolean fromInclusive, ZonedDateTime toEventDateTime,   boolean toInclusive);
    
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime, boolean inclusive);
    
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime, boolean inclusive);
    
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, ZonedDateTime toEventDateTime);

    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime);

    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime);
    
    public NavigableSet<ZonedDateTime> eventDateTimes();
    
    public NavigableSet<E> events();

    public MultiTimeSeriesNavigableEvents.Entry<E> start();

    public MultiTimeSeriesNavigableEvents.Entry<E> end();

    public Iterator<MultiTimeSeriesNavigableEvents.Entry<E>> iterator();

    public Iterator<MultiTimeSeriesNavigableEvents.Entry<E>> descendingIterator();
    
    public static interface Entry<E> extends Comparable<Entry<E>>
    {
        public ZonedDateTime getEventDateTime();

        public NavigableSet<E> getEvents();
    }
}
