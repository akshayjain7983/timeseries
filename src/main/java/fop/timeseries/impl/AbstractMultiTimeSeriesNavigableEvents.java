package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

import fop.timeseries.MultiTimeSeriesNavigableEvents;
import fop.timeseries.util.TimeSeriesUtils;

public abstract class AbstractMultiTimeSeriesNavigableEvents<E> implements MultiTimeSeriesNavigableEvents<E>
{
    private final NavigableMap<Instant, MultiTimeSeriesNavigableEvents.Entry<E>> timeSeriesStore;
    private final Supplier<NavigableSet<E>> entryEventsNavigableSetFactory;
    private final Comparator<E> entryEventsNavigableSetComparator;
    
    public AbstractMultiTimeSeriesNavigableEvents()
    {
        this(null);
    }
    
    public AbstractMultiTimeSeriesNavigableEvents(Comparator<E> entryEventsNavigableSetComparator)
    {
        this.timeSeriesStore = new TreeMap<>();
        this.entryEventsNavigableSetFactory = ()->new TreeSet<>(entryEventsNavigableSetComparator);
        this.entryEventsNavigableSetComparator = entryEventsNavigableSetComparator;
    }
    
    protected AbstractMultiTimeSeriesNavigableEvents(NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> entries, Comparator<E> entryEventsNavigableSetComparator) 
    {
        this(entryEventsNavigableSetComparator);
        entries.forEach(e->{
            ZonedDateTime eventDateTime = e.getEventDateTime();
            Collection<E> events = e.getEvents();
            events.forEach(v->addToEntry(eventDateTime, v));
        });
    }
    
    public AbstractMultiTimeSeriesNavigableEvents(MultiTimeSeriesNavigableEvents<E> timeSeries, Comparator<E> entryEventsNavigableSetComparator)
    {
        this(timeSeries.getEntries(), entryEventsNavigableSetComparator);
    }
    
    protected void addToEntry(ZonedDateTime eventDateTime, E event)
    {
        validateEntryEvent(event);
        Instant eventInstant = Instant.from(eventDateTime);
        NavigableSet<E> entryCollection = null;
        if(timeSeriesStore.containsKey(eventInstant))
        {
            entryCollection = timeSeriesStore.get(eventInstant).getEvents();
        }
        
        MultiTimeSeriesNavigableEvents.Entry<E> entry = createEntry(eventDateTime, entryCollection, event);
        addEntry(eventInstant, entry);
    }
    
    protected void addEntry(Instant eventInstant, MultiTimeSeriesNavigableEvents.Entry<E> entry)
    {
        timeSeriesStore.put(eventInstant, entry);
    }

    protected NavigableSet<E> removeEntry(Instant eventInstant)
    {
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.remove(eventInstant).getEvents() : null;
    }
    
    protected boolean removeEvent(Instant eventInstant, E event)
    {
        return timeSeriesStore.containsKey(eventInstant) ? timeSeriesStore.get(eventInstant).getEvents().remove(event) : false;
    }

    @Override
    public NavigableSet<E> get(ZonedDateTime eventDateTime)
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
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntries()
    {
        return isNotEmpty() ? new TreeSet<MultiTimeSeriesNavigableEvents.Entry<E>>(timeSeriesStore.values()) : null;
    }
    
    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, boolean fromInclusive, ZonedDateTime toEventDateTime,   boolean toInclusive) 
    {
        return getEntries().subSet(createEntry(fromEventDateTime, null, null), fromInclusive, createEntry(toEventDateTime, null, null), toInclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime, boolean inclusive)
    {
        return getEntries().headSet(createEntry(toEventDateTime, null, null), inclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime, boolean inclusive)
    {
        return getEntries().tailSet(createEntry(fromEventDateTime, null, null), inclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, ZonedDateTime toEventDateTime)
    {
        return getEntriesSubSet(fromEventDateTime, false, toEventDateTime, false);
    }
    
    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime)
    {
        return getEntriesHeadSet(toEventDateTime, false);
    }

    @Override
    public NavigableSet<MultiTimeSeriesNavigableEvents.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime)
    {
        return getEntriesTailSet(fromEventDateTime, false);
    }

    @Override
    public NavigableSet<ZonedDateTime> eventDateTimes()
    {
        return TimeSeriesUtils.extractMultiTimeSeriesNavigableEventsEventDateTimes(getEntries());
    }

    @Override
    public NavigableSet<E> events()
    {
        return TimeSeriesUtils.extractMultiTimeSeriesNavigableEventsEvents(getEntries(), getEntryEventsNavigableSetComparator());
    }

    @Override
    public MultiTimeSeriesNavigableEvents.Entry<E> start()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.firstKey()) : null;
    }

    @Override
    public MultiTimeSeriesNavigableEvents.Entry<E> end()
    {
        return isNotEmpty() ? timeSeriesStore.get(timeSeriesStore.lastKey()) : null;
    }

    @Override
    public Iterator<MultiTimeSeriesNavigableEvents.Entry<E>> iterator()
    {
        return getEntries().iterator();
    }

    @Override
    public Iterator<MultiTimeSeriesNavigableEvents.Entry<E>> descendingIterator()
    {
        return ((TreeSet<MultiTimeSeriesNavigableEvents.Entry<E>>)getEntries()).descendingIterator();
    }
    
    protected MultiTimeSeriesNavigableEvents.Entry<E> createEntry(ZonedDateTime eventDateTime, NavigableSet<E> events, E event)
    {
        NavigableSet<E> newEventsNavigableSet = getEntryEventsNavigableSetFactory().get();
        if(Objects.nonNull(events))
        {
            newEventsNavigableSet.addAll(events);
        }
        
        if(Objects.nonNull(event))
        {
            newEventsNavigableSet.add(event);
        }
        
        return MultiTimeSeriesNavigableEventsEntry.of(eventDateTime, newEventsNavigableSet);
    }
    
    public static class MultiTimeSeriesNavigableEventsEntry<E> implements MultiTimeSeriesNavigableEvents.Entry<E>
    {
        private final Instant eventInstant;
        private final ZonedDateTime eventDateTime;
        private final NavigableSet<E> events;
        
        public static <E> MultiTimeSeriesNavigableEventsEntry<E> of(ZonedDateTime eventDateTime, NavigableSet<E> events)
        {
            return new MultiTimeSeriesNavigableEventsEntry<>(eventDateTime, events);
        }
        
        MultiTimeSeriesNavigableEventsEntry(ZonedDateTime eventDateTime, NavigableSet<E> events)
        {
            this.eventDateTime = eventDateTime;
            this.eventInstant = Instant.from(eventDateTime);
            this.events = Collections.unmodifiableNavigableSet(events);
        }

        @Override
        public ZonedDateTime getEventDateTime()
        {
            return eventDateTime;
        }
        
        @Override
        public NavigableSet<E> getEvents()
        {
            return events;
        }

        @Override
        public int compareTo(MultiTimeSeriesNavigableEvents.Entry<E> otherEntry)
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
            MultiTimeSeriesNavigableEventsEntry<E> other = MultiTimeSeriesNavigableEventsEntry.class.cast(obj);
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

    protected Supplier<NavigableSet<E>> getEntryEventsNavigableSetFactory()
    {
        return entryEventsNavigableSetFactory;
    }
    
    protected Comparator<E> getEntryEventsNavigableSetComparator()
    {
        return entryEventsNavigableSetComparator;
    }
    
    protected void validateEntryEvent(E event) 
    {
        if(getEntryEventsNavigableSetComparator() == null && !Comparable.class.isAssignableFrom(event.getClass()))
        {
            throw new IllegalArgumentException("MultiTimeSeriesNavigableEvents is neither initiated with a getEntryEventsNavigableSetComparator nor the events are Comparable");
        }
    }
}
