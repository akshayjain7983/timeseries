package org.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import org.timeseries.MultiTimeSeries;
import org.timeseries.util.TimeSeriesUtils;

public abstract class AbstractMultiTimeSeries<E> implements MultiTimeSeries<E>
{
    private final SortedMap<Instant, MultiTimeSeries.Entry<E>> timeSeriesStore;
    private final Function<Comparator<E>, Collection<E>> entryCollectionFactory;
    private final Comparator<E> entryCollectionComparator;
    
    protected AbstractMultiTimeSeries(Function<Comparator<E>, Collection<E>> entryCollectionFactory, Comparator<E> entryCollectionComparator)
    {
        this.timeSeriesStore = new TreeMap<Instant, MultiTimeSeries.Entry<E>>();
        this.entryCollectionFactory = entryCollectionFactory;
        this.entryCollectionComparator = entryCollectionComparator;
    }

    protected AbstractMultiTimeSeries(MultiTimeSeries<E> timeSeries, Function<Comparator<E>, Collection<E>> entryCollectionFactory, Comparator<E> entryCollectionComparator)
    {
        this(timeSeries.getEntries(), entryCollectionFactory, entryCollectionComparator);
    }
    
    protected AbstractMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries, Function<Comparator<E>, Collection<E>> entryCollectionFactory, Comparator<E> entryCollectionComparator) 
    {
        this(entryCollectionFactory, entryCollectionComparator);
        entries.forEach(e->{
            ZonedDateTime eventDateTime = e.getEventDateTime();
            Collection<E> events = e.getEvents();
            events.forEach(v->addToEntry(eventDateTime, v));
        });
    }
    
    protected void addEntry(Instant eventInstant, MultiTimeSeries.Entry<E> entry)
    {
        timeSeriesStore.put(eventInstant, entry);
    }
    
    protected void addToEntry(ZonedDateTime eventDateTime, E event)
    {
        Instant eventInstant = Instant.from(eventDateTime);
        if(timeSeriesStore.containsKey(eventInstant))
        {
            MultiTimeSeries.Entry<E> entry = timeSeriesStore.get(eventInstant);
            entry.getEvents().add(event);
        }
        else
        {
            MultiTimeSeriesEntry<E> entry = new MultiTimeSeriesEntry<>(eventDateTime, this.entryCollectionFactory.apply(entryCollectionComparator));
            entry.getEvents().add(event);
            addEntry(entry.getEventInstant(), entry);
        }
    }

    protected Collection<E> removeEntry(Instant eventInstant)
    {
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.remove(eventInstant).getEvents() : null;
    }
    
    protected boolean removeEvent(Instant eventInstant, E event)
    {
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.get(eventInstant).getEvents().remove(event) : false;
    }

    @Override
    public Collection<E> get(ZonedDateTime eventDateTime)
    {
        Instant eventInstant = Instant.from(eventDateTime);
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.get(eventInstant).getEvents() : null;
    }

    @Override
    public boolean contains(ZonedDateTime eventDateTime)
    {
        return timeSeriesStore.containsKey(Instant.from(eventDateTime));
    }

    @Override
    public boolean contains(ZonedDateTime eventDateTime, E event)
    {
        Instant eventInstant = Instant.from(eventDateTime);
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.get(eventInstant).getEvents().contains(event) : false;
    }

    @Override
    public int size()
    {
        return timeSeriesStore.size();
    }

    @Override
    public SortedSet<MultiTimeSeries.Entry<E>> getEntries()
    {
        return isNotEmpty() ? new TreeSet<MultiTimeSeries.Entry<E>>(timeSeriesStore.values()) : null;
    }

    @Override
    public Set<ZonedDateTime> eventDateTimes()
    {
        return TimeSeriesUtils.extractMultiTimeSeriesEventDateTimes(getEntries());
    }

    @Override
    public Collection<E> events()
    {
        return TimeSeriesUtils.extractMultiTimeSeriesEvents(getEntries());
    }

    @Override
    public MultiTimeSeries.Entry<E> start()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.firstKey()) : null;
    }

    @Override
    public MultiTimeSeries.Entry<E> end()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.lastKey()) : null;
    }

    @Override
    public Iterator<MultiTimeSeries.Entry<E>> iterator()
    {
        return getEntries().iterator();
    }

    @Override
    public Iterator<MultiTimeSeries.Entry<E>> descendingIterator()
    {
        return ((TreeSet<MultiTimeSeries.Entry<E>>)getEntries()).descendingIterator();
    }

    @Override
    public String toString()
    {
        return "MultiTimeSeries:" + getEntries();
    }
    
    public static class MultiTimeSeriesEntry<E> implements MultiTimeSeries.Entry<E>
    {

        private final Instant eventInstant;
        private final ZonedDateTime eventDateTime;
        private final Collection<E> events;
        
        public MultiTimeSeriesEntry(ZonedDateTime eventDateTime, Collection<E> events)
        {
            this.eventDateTime = eventDateTime;
            this.eventInstant = Instant.from(eventDateTime);
            this.events = events;
        }

        public ZonedDateTime getEventDateTime()
        {
            return eventDateTime;
        }

        public Collection<E> getEvents()
        {
            return events;
        }

        Instant getEventInstant()
        {
            return eventInstant;
        }

        @Override
        public int compareTo(MultiTimeSeries.Entry<E> otherEntry)
        {
            return this.eventInstant.compareTo(Instant.from(otherEntry.getEventDateTime()));
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((eventInstant == null) ? 0 : eventInstant.hashCode());
            result = prime * result + ((events == null) ? 0 : events.hashCode());
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MultiTimeSeriesEntry<E> other = MultiTimeSeriesEntry.class.cast(obj);
            if (eventInstant == null)
            {
                if (other.eventInstant != null)
                    return false;
            }
            else if (!eventInstant.equals(other.eventInstant))
                return false;
            if (events == null)
            {
                if (other.events != null)
                    return false;
            }
            else if (!events.equals(other.events))
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "[eventDateTime=" + eventDateTime + ", events=" + events + "]";
        }
    }

}
