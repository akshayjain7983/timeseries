package fop.timeseries.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Supplier;

import fop.timeseries.ImmutableMultiTimeSeries;
import fop.timeseries.util.ImmutableBuilders;

public final class ImmutableMultiTimeSeriesImpl<E, C extends Collection<E>> extends AbstractMultiTimeSeries<E, C> implements ImmutableMultiTimeSeries<E, C>
{
    public ImmutableMultiTimeSeriesImpl(Collection<Entry<C>> entries, Supplier<C> entryCollectionFactory)
    {
        super(entryCollectionFactory);
        entries.forEach(e->addEntry(Instant.from(e.getEventDateTime()), e));
    }
    
    @Override
    public void addEvent(ZonedDateTime eventDateTime, E event) 
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public void add(ZonedDateTime eventDateTime, C eventCollection)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public C remove(ZonedDateTime eventDateTime)
    {
        throw new UnsupportedOperationException("Cannot change ImmutableMultiTimeSeriesImpl");
    }

    @Override
    public ImmutableMultiTimeSeries<E, C> with(ZonedDateTime eventDateTime, E event)
    {
        return ImmutableBuilders.<E, C>multiBuilder().entryCollectionFactory(getEntryCollectionFactory()).add(eventDateTime, event).build();
    }

    @Override
    public ImmutableMultiTimeSeries<E, C> with(ZonedDateTime eventDateTime, C eventCollection)
    {
        return ImmutableBuilders.<E, C>multiBuilder().entryCollectionFactory(getEntryCollectionFactory()).add(eventDateTime, eventCollection).build();
    }

    @Override
    public ImmutableMultiTimeSeries<E, C> with(Entry<C> entry)
    {
        return ImmutableBuilders.<E, C>multiBuilder().entryCollectionFactory(getEntryCollectionFactory()).add(entry).build();
    }

    @Override
    public ImmutableMultiTimeSeries<E, C> with(Collection<Entry<C>> entries)
    {
        return ImmutableBuilders.<E, C>multiBuilder().entryCollectionFactory(getEntryCollectionFactory()).add(entries).build();
    }

}
