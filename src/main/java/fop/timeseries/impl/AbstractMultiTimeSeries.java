package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

import fop.timeseries.MultiTimeSeries;
import fop.timeseries.util.TimeSeriesUtils;

public abstract class AbstractMultiTimeSeries<E> implements MultiTimeSeries<E>
{
    private final NavigableMap<Instant, MultiTimeSeries.Entry<E>> timeSeriesStore;
    private final Supplier<Collection<E>> entryCollectionFactory;
    
    protected AbstractMultiTimeSeries(Supplier<Collection<E>> entryCollectionFactory)
    {
        this.timeSeriesStore = new TreeMap<Instant, MultiTimeSeries.Entry<E>>();
        this.entryCollectionFactory = entryCollectionFactory;
    }

    protected AbstractMultiTimeSeries(MultiTimeSeries<E> timeSeries, Supplier<Collection<E>> entryCollectionFactory)
    {
        this(timeSeries.getEntries(), entryCollectionFactory);
    }
    
    protected AbstractMultiTimeSeries(Collection<MultiTimeSeries.Entry<E>> entries, Supplier<Collection<E>> entryCollectionFactory) 
    {
        this(entryCollectionFactory);
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
        Collection<E> entryCollection = null;
        if(timeSeriesStore.containsKey(eventInstant))
        {
            entryCollection = timeSeriesStore.get(eventInstant).getEvents();
        }
        
        MultiTimeSeries.Entry<E> entry = createEntry(eventDateTime, entryCollection, event);
        addEntry(eventInstant, entry);
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
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntries()
    {
        return isNotEmpty() ? new TreeSet<MultiTimeSeries.Entry<E>>(timeSeriesStore.values()) : null;
    }
    
    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, boolean fromInclusive, ZonedDateTime toEventDateTime,   boolean toInclusive) 
    {
        return getEntries().subSet(createEntry(fromEventDateTime, null, null), fromInclusive, createEntry(toEventDateTime, null, null), toInclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime, boolean inclusive)
    {
        return getEntries().headSet(createEntry(toEventDateTime, null, null), inclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime, boolean inclusive)
    {
        return getEntries().tailSet(createEntry(fromEventDateTime, null, null), inclusive);
    }
    
    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesSubSet(ZonedDateTime fromEventDateTime, ZonedDateTime toEventDateTime)
    {
        return getEntriesSubSet(fromEventDateTime, false, toEventDateTime, false);
    }
    
    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesHeadSet(ZonedDateTime toEventDateTime)
    {
        return getEntriesHeadSet(toEventDateTime, false);
    }

    @Override
    public NavigableSet<MultiTimeSeries.Entry<E>> getEntriesTailSet(ZonedDateTime fromEventDateTime)
    {
        return getEntriesTailSet(fromEventDateTime, false);
    }

    @Override
    public NavigableSet<ZonedDateTime> eventDateTimes()
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
    
    protected MultiTimeSeries.Entry<E> createEntry(ZonedDateTime eventDateTime, Collection<E> events, E event)
    {
        Collection<E> newEventsCollection = entryCollectionFactory.get();
        if(Objects.nonNull(events))
        {
            newEventsCollection.addAll(events);
        }
        
        if(Objects.nonNull(event))
        {
            newEventsCollection.add(event);
        }
        
        return MultiTimeSeriesEntry.of(eventDateTime, newEventsCollection);
    }
    
    public static class MultiTimeSeriesEntry<E> implements MultiTimeSeries.Entry<E>
    {
        private final Instant eventInstant;
        private final ZonedDateTime eventDateTime;
        private final Collection<E> events;
        
        public static <E> MultiTimeSeriesEntry<E> of(ZonedDateTime eventDateTime, Collection<E> events)
        {
            return new MultiTimeSeriesEntry<>(eventDateTime, events);
        }
        
        MultiTimeSeriesEntry(ZonedDateTime eventDateTime, Collection<E> events)
        {
            this.eventDateTime = eventDateTime;
            this.eventInstant = Instant.from(eventDateTime);
            this.events = setupEvents(events);
        }

        private Collection<E> setupEvents(Collection<E> events)
        {
            Class<?> eventsCollectionClass = events.getClass();
            if(List.class.isAssignableFrom(eventsCollectionClass))
            {
                return Collections.unmodifiableList((List<E>)events);
            }
            
            if(NavigableSet.class.isAssignableFrom(eventsCollectionClass))
            {
                return Collections.unmodifiableNavigableSet((NavigableSet<E>)events);
            }
            
            if(SortedSet.class.isAssignableFrom(eventsCollectionClass))
            {
                return Collections.unmodifiableSortedSet((SortedSet<E>)events);
            }
            
            return Collections.unmodifiableCollection(events);
        }

        public ZonedDateTime getEventDateTime()
        {
            return eventDateTime;
        }

        public Collection<E> getEvents()
        {
            return events;
        }

        public Instant getEventInstant()
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

    protected NavigableMap<Instant, MultiTimeSeries.Entry<E>> getTimeSeriesStore()
    {
        return timeSeriesStore;
    }

    protected Supplier<Collection<E>> getEntryCollectionFactory()
    {
        return entryCollectionFactory;
    }
}
