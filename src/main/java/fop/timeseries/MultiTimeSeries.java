package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;

public interface MultiTimeSeries<E> extends Iterable<MultiTimeSeries.Entry<E>>
{

    public void add(ZonedDateTime eventDateTime, E event);

    public Collection<E> get(ZonedDateTime eventDateTime);

    public Collection<E> remove(ZonedDateTime eventDateTime);
    
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

    public NavigableSet<MultiTimeSeries.Entry<E>> getEntries();
    
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, boolean fromInclusive, ZonedDateTime toEventDateTime,   boolean toInclusive);
    
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime, boolean inclusive);
    
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime, boolean inclusive);
    
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, ZonedDateTime toEventDateTime);

    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime);

    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime);
    
    public NavigableSet<ZonedDateTime> eventDateTimes();
    
    public Collection<E> events();

    public MultiTimeSeries.Entry<E> start();

    public MultiTimeSeries.Entry<E> end();

    public Iterator<MultiTimeSeries.Entry<E>> iterator();

    public Iterator<MultiTimeSeries.Entry<E>> descendingIterator();
    
    
    public static interface Entry<E> extends Comparable<Entry<E>>
    {
        public ZonedDateTime getEventDateTime();

        public Collection<E> getEvents();
    }
}
