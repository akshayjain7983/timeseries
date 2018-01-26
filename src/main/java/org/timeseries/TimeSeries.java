package org.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public interface TimeSeries<E> extends Iterable<TimeSeries.Entry<E>>
{
    public void add(ZonedDateTime eventDateTime, E event);

    public E get(ZonedDateTime eventDateTime);

    public E remove(ZonedDateTime eventDateTime);
    
    public boolean contains(ZonedDateTime eventDateTime);

    public int size();
    
    public default boolean isEmpty()
    {
        return size()==0;
    }
    
    public default boolean isNotEmpty()
    {
        return size()>0;
    }

    public SortedSet<TimeSeries.Entry<E>> getEntries();
    
    public Set<ZonedDateTime> eventDateTimes();
    
    public Collection<E> events();

    public TimeSeries.Entry<E> start();

    public TimeSeries.Entry<E> end();

    public Iterator<TimeSeries.Entry<E>> iterator();

    public Iterator<TimeSeries.Entry<E>> descendingIterator();

    public static interface Entry<E> extends Comparable<Entry<E>>
    {
        public ZonedDateTime getEventDateTime();

        public E getEvent();
    }
}
