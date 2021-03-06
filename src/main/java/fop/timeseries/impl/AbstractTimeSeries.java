package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import fop.timeseries.TimeSeries;
import fop.timeseries.util.TimeSeriesUtils;

public abstract class AbstractTimeSeries<E> implements TimeSeries<E>
{
    protected final NavigableMap<Instant, TimeSeries.Entry<E>> timeSeriesStore;

    protected AbstractTimeSeries()
    {
        this.timeSeriesStore = new TreeMap<Instant, TimeSeries.Entry<E>>();
    }

    protected AbstractTimeSeries(TimeSeries<E> timeSeries)
    {
        this(timeSeries.getEntries());
    }
    
    protected AbstractTimeSeries(Collection<TimeSeries.Entry<E>> entries) 
    {
        this.timeSeriesStore = new TreeMap<Instant, TimeSeries.Entry<E>>();
        entries.forEach(e->addEntry(Instant.from(e.getEventDateTime()), e));
    }
    
    protected void addEntry(Instant eventInstant, TimeSeries.Entry<E> entry)
    {
        timeSeriesStore.put(eventInstant, entry);
    }

    protected E removeEntry(Instant eventInstant)
    {
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.remove(eventInstant).getEvent() : null;
    }
    
    @Override
    public E get(ZonedDateTime eventDateTime)
    {
        Instant eventInstant = Instant.from(eventDateTime);
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.get(eventInstant).getEvent() : null;
    }
    
    @Override
    public boolean contains(ZonedDateTime eventDateTime) 
    {
        return timeSeriesStore.containsKey(Instant.from(eventDateTime));
    }

    @Override
    public int size()
    {
        return timeSeriesStore.size();
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntries()
    {
        return isNotEmpty() ? new TreeSet<TimeSeries.Entry<E>>(timeSeriesStore.values()) : new TreeSet<TimeSeries.Entry<E>>();
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, boolean fromInclusive, ZonedDateTime toEventDateTime,   boolean toInclusive) 
    {
        return getEntries().subSet(TimeSeriesEntry.of(fromEventDateTime, null), fromInclusive, TimeSeriesEntry.of(toEventDateTime, null), toInclusive);
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime, boolean inclusive)
    {
        return getEntries().headSet(TimeSeriesEntry.of(toEventDateTime, null), inclusive);
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime, boolean inclusive)
    {
        return getEntries().tailSet(TimeSeriesEntry.of(fromEventDateTime, null), inclusive);
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, ZonedDateTime toEventDateTime)
    {
        return getEntriesSubSet(fromEventDateTime, false, toEventDateTime, false);
    }
    
    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime)
    {
        return getEntriesHeadSet(toEventDateTime, false);
    }

    @Override
    public NavigableSet<TimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime)
    {
        return getEntriesTailSet(fromEventDateTime, false);
    }
    
    @Override
    public NavigableSet<ZonedDateTime> eventDateTimes()
    {
        return TimeSeriesUtils.extractTimeSeriesEventDateTimes(getEntries());
    }
    
    @Override
    public Collection<E> events()
    {
        return TimeSeriesUtils.extractTimeSeriesEvents(getEntries());
    }

    @Override
    public TimeSeries.Entry<E> start()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.firstKey()) : null;
    }

    @Override
    public TimeSeries.Entry<E> end()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.lastKey()) : null;
    }

    @Override
    public Iterator<TimeSeries.Entry<E>> iterator()
    {
        return getEntries().iterator();
    }

    @Override
    public Iterator<TimeSeries.Entry<E>> descendingIterator()
    {
        return getEntries().descendingIterator();
    }

    @Override
    public String toString()
    {
        return "TimeSeries:" + getEntries();
    }
    
    public static class TimeSeriesEntry<E> implements TimeSeries.Entry<E>
    {
        private final Instant eventInstant;
        private final ZonedDateTime eventDateTime;
        private final E event;

        public static <E> TimeSeriesEntry<E> of(ZonedDateTime eventDateTime, E event) 
        {
            return new TimeSeriesEntry<>(eventDateTime, event);
        }
        
        TimeSeriesEntry(ZonedDateTime eventDateTime, E event)
        {
            this.eventDateTime = eventDateTime;
            this.eventInstant = Instant.from(eventDateTime);
            this.event = event;
        }

        Instant getEventInstant()
        {
            return eventInstant;
        }
        
        @Override
        public ZonedDateTime getEventDateTime()
        {
            return eventDateTime;
        }

        @Override
        public E getEvent()
        {
            return event;
        }

        @Override
        public int compareTo(Entry<E> otherEntry)
        {
            return this.eventInstant.compareTo(Instant.from(otherEntry.getEventDateTime()));
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((event == null) ? 0 : event.hashCode());
            result = prime * result + ((eventInstant == null) ? 0 : eventInstant.hashCode());
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
            if (!(obj instanceof TimeSeriesEntry))
                return false;
            TimeSeriesEntry<E> other = TimeSeriesEntry.class.cast(obj);
            if (event == null)
            {
                if (other.event != null)
                    return false;
            }
            else if (!event.equals(other.event))
                return false;
            if (eventInstant == null)
            {
                if (other.eventInstant != null)
                    return false;
            }
            else if (!eventInstant.equals(other.eventInstant))
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "[eventDateTime=" + eventDateTime + ", event=" + event + "]";
        }
    }

    protected NavigableMap<Instant, TimeSeries.Entry<E>> getTimeSeriesStore()
    {
        return timeSeriesStore;
    }
}
