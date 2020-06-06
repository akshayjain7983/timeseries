package fop.timeseries.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fop.timeseries.ImmutableMultiTimeSeries;
import fop.timeseries.ImmutableTimeSeries;
import fop.timeseries.TimeSeries;
import fop.timeseries.TimeSeries.Entry;
import fop.timeseries.impl.AbstractTimeSeries.TimeSeriesEntry;
import fop.timeseries.impl.ImmutableMultiTimeSeriesImpl;
import fop.timeseries.impl.ImmutableTimeSeriesImpl;

public final class ImmutableBuilders
{
    private ImmutableBuilders() {}
    
    public static <E> Builder<E> builder()
    {
        return new Builder<E>();
    }
    
    public static <E, C extends Collection<E>> MultiBuilder<E, C> multiBuilder()
    {
        return new MultiBuilder<E, C>();
    }

    public static class MultiBuilder<E, C extends Collection<E>>
    {
        private final Collection<TimeSeries.Entry<C>> entries;
        private final AtomicBoolean builderExpired;
        private Supplier<C> entryCollectionFactory;
        
        private MultiBuilder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public MultiBuilder<E, C> add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            entries.add((TimeSeries.Entry<C>)TimeSeriesEntry.of(eventDateTime, List.of(event)));
            return this;
        }
        
        public MultiBuilder<E, C> add(ZonedDateTime eventDateTime, C eventCollection)
        {
            ensureBuilderValidity();
            entries.add(TimeSeriesEntry.of(eventDateTime, eventCollection));
            return this;
        }
        
        public MultiBuilder<E, C> add(TimeSeries.Entry<C> entry)
        {
            ensureBuilderValidity();
            entries.add(entry);
            return this;
        }
        
        public MultiBuilder<E, C> add(Collection<TimeSeries.Entry<C>> entries)
        {
            ensureBuilderValidity();
            this.entries.addAll(entries);
            return this;
        }
        
        public MultiBuilder<E, C> entryCollectionFactory(Supplier<C> entryCollectionFactory)
        {
            this.entryCollectionFactory = entryCollectionFactory;
            return this;
        }
        
        public ImmutableMultiTimeSeries<E, C> build()
        {
            ensureBuilderValidity();
            try
            {
                if(Objects.isNull(entryCollectionFactory))
                {
                    entryCollectionFactory = ()->(C)new TreeSet<E>();
                }
                return new ImmutableMultiTimeSeriesImpl<>(buildUniqueUnmodifiableEntires(entries), entryCollectionFactory);   
            }
            finally
            {
                builderExpired.set(true);
            }
        }
        
        private Collection<Entry<C>> buildUniqueUnmodifiableEntires(Collection<Entry<C>> entries)
        {
            Map<Instant, Entry<C>> uniqueEntries = new HashMap<>();
            
            entries.forEach(e->{
                
                ZonedDateTime eventDateTime = e.getEventDateTime();
                Instant eventInstant = Instant.from(eventDateTime);
                C eventCollection = e.getEvent();
                Entry<C> existingUniqueEntry = uniqueEntries.get(eventInstant);
                if(Objects.isNull(existingUniqueEntry)) 
                {
                    existingUniqueEntry = TimeSeriesEntry.of(eventDateTime, entryCollectionFactory.get());
                    uniqueEntries.put(eventInstant, existingUniqueEntry);
                }
                
                existingUniqueEntry.getEvent().addAll(eventCollection);
            });
            
            Collection<Entry<C>> uniqueUnmodifiableEntries = 
                    uniqueEntries.values().stream().map(e->{
                        
                        ZonedDateTime eventDateTime = e.getEventDateTime();
                        C eventCollection = e.getEvent();
                        C unmodifiableEventCollection = (C)Collections.unmodifiableCollection(eventCollection);
                        return TimeSeriesEntry.of(eventDateTime, unmodifiableEventCollection);
                        
                    }).collect(Collectors.toList());
            
            return uniqueUnmodifiableEntries;
        }

        private void ensureBuilderValidity()
        {
            if(builderExpired.get())
            {
                throw new IllegalStateException("Builder already used to build once. Create a new builder");
            }
        }
    }
    
    
    public static class Builder<E> 
    {
        private final Collection<TimeSeries.Entry<E>> entries;
        private final AtomicBoolean builderExpired;
        
        private Builder()
        {
            this.entries = new ArrayList<>();
            this.builderExpired = new AtomicBoolean(false);
        }
        
        public Builder<E> add(ZonedDateTime eventDateTime, E event)
        {
            ensureBuilderValidity();
            entries.add(TimeSeriesEntry.of(eventDateTime, event));
            return this;
        }
        
        public Builder<E> add(TimeSeries.Entry<E> entry)
        {
            ensureBuilderValidity();
            entries.add(entry);
            return this;
        }
        
        public Builder<E> add(Collection<TimeSeries.Entry<E>> entries)
        {
            ensureBuilderValidity();
            this.entries.addAll(entries);
            return this;
        }
        
        public ImmutableTimeSeries<E> build()
        {
            ensureBuilderValidity();
            try
            {
                return new ImmutableTimeSeriesImpl<>(entries);   
            }
            finally
            {
                builderExpired.set(true);
            }
        }
        
        private void ensureBuilderValidity()
        {
            if(builderExpired.get())
            {
                throw new IllegalStateException("Builder already used to build once. Create a new builder");
            }
        }
    }
}
