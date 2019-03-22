package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;

public interface ImmutableMultiTimeSeries<E> extends MultiTimeSeries<E>
{
    public ImmutableMultiTimeSeries<E> with(ZonedDateTime eventDateTime, E event);
    
    public ImmutableMultiTimeSeries<E> with(ZonedDateTime eventDateTime, Collection<E> events);
    
    public ImmutableMultiTimeSeries<E> with(MultiTimeSeries.Entry<E> entry);
    
    public ImmutableMultiTimeSeries<E> with(Collection<MultiTimeSeries.Entry<E>> entries);
}
