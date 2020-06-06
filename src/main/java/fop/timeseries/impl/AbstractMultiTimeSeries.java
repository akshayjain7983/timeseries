package fop.timeseries.impl;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Supplier;

import fop.timeseries.MultiTimeSeries;
import fop.timeseries.TimeSeries;

public abstract class AbstractMultiTimeSeries<E, C extends Collection<E>> extends AbstractTimeSeries<C> implements MultiTimeSeries<E, C>
{
    protected final Supplier<C> entryCollectionFactory;
    
    @SuppressWarnings("unchecked")
    public AbstractMultiTimeSeries()
    {
        super();
        this.entryCollectionFactory = ()->(C)new TreeSet<E>();
    }
    
    public AbstractMultiTimeSeries(Supplier<C> entryCollectionFactory)
    {
        super();
        this.entryCollectionFactory = entryCollectionFactory;
    }

    public AbstractMultiTimeSeries(TimeSeries<C> timeSeries, Supplier<C> entryCollectionFactory)
    {
        super(timeSeries);
        this.entryCollectionFactory = entryCollectionFactory;
    }

    public AbstractMultiTimeSeries(Collection<Entry<C>> entries, Supplier<C> entryCollectionFactory)
    {
        this.entryCollectionFactory = entryCollectionFactory;
        entries.forEach(e->{
            ZonedDateTime eventDateTime = e.getEventDateTime();
            C eventCollection = e.getEvent();
            eventCollection.forEach(v->addEvent(eventDateTime, v));
        });
    }
    
    @Override
    public boolean contains(ZonedDateTime eventDateTime, E event) 
    {
        return contains(eventDateTime) && Objects.nonNull(get(eventDateTime)) && get(eventDateTime).contains(event);
    }
    
    @Override
    public boolean remove(ZonedDateTime eventDateTime, E event) {
        
        Collection<E> collection = get(eventDateTime);
        if(Objects.nonNull(collection)) {
            return collection.remove(event);
        }
        
        return false;
    }

    protected Supplier<C> getEntryCollectionFactory()
    {
        return entryCollectionFactory;
    }
}
