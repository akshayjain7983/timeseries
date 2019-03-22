package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;

public interface ImmutableMultiTimeSeriesNavigableEvents<E> extends MultiTimeSeriesNavigableEvents<E>
{
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(ZonedDateTime eventDateTime, E event);
    
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(ZonedDateTime eventDateTime, Collection<E> events);
    
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(MultiTimeSeriesNavigableEvents.Entry<E> entry);
    
    public ImmutableMultiTimeSeriesNavigableEvents<E> with(Collection<MultiTimeSeriesNavigableEvents.Entry<E>> entries);
}
