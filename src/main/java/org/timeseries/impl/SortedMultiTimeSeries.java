package org.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.timeseries.MultiTimeSeries;

public class SortedMultiTimeSeries<E> extends AbstractMultiTimeSeries<E>
{
    public SortedMultiTimeSeries()
    {
        super(t->new TreeSet<>(t), null);
    }

    public SortedMultiTimeSeries(MultiTimeSeries<E> timeSeries)
    {
        super(timeSeries, t->new TreeSet<>(t), null);
    }

    public SortedMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries)
    {
        super(entries, t->new TreeSet<>(t), null);
    }
    
    public SortedMultiTimeSeries(Comparator<E> entryCollectionComparator)
    {
        super(t->new TreeSet<>(t), entryCollectionComparator);
    }

    public SortedMultiTimeSeries(MultiTimeSeries<E> timeSeries, Comparator<E> entryCollectionComparator)
    {
        super(timeSeries, t->new TreeSet<>(t), entryCollectionComparator);
    }

    public SortedMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries, Comparator<E> entryCollectionComparator)
    {
        super(entries, t->new TreeSet<>(t), entryCollectionComparator);
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
