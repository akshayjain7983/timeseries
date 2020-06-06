package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

import fop.timeseries.TimeSeries;

public class SimpleMultiTimeSeries<E, C extends Collection<E>> extends AbstractMultiTimeSeries<E, C>
{
    public SimpleMultiTimeSeries()
    {
        super();
    }
    
    public SimpleMultiTimeSeries(Collection<Entry<C>> entries, Supplier<C> entryCollectionFactory)
    {
        super(entries, entryCollectionFactory);
    }

    public SimpleMultiTimeSeries(Supplier<C> entryCollectionFactory)
    {
        super(entryCollectionFactory);
    }

    public SimpleMultiTimeSeries(TimeSeries<C> timeSeries, Supplier<C> entryCollectionFactory)
    {
        super(timeSeries, entryCollectionFactory);
    }
    
    @Override
    public void addEvent(ZonedDateTime eventDateTime, E event) 
    {
        C collection = get(eventDateTime);
        if(Objects.isNull(collection)) {
            collection = entryCollectionFactory.get();
            add(eventDateTime, collection);
        }
        
        collection.add(event);
    }

    @Override
    public void add(ZonedDateTime eventDateTime, C event)
    {
        TimeSeriesEntry<C> entry = TimeSeriesEntry.of(eventDateTime, event);
        super.addEntry(entry.getEventInstant(), entry);        
    }

    @Override
    public C remove(ZonedDateTime eventDateTime)
    {
        return super.removeEntry(Instant.from(eventDateTime));
    }
}
