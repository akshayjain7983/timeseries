package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

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

    public SortedSet<MultiTimeSeries.Entry<E>> getEntries();
    
    public SortedSet<ZonedDateTime> eventDateTimes();
    
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
