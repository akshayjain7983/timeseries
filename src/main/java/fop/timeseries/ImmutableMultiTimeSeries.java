package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;

public interface ImmutableMultiTimeSeries<E, C extends Collection<E>> extends MultiTimeSeries<E, C>
{
    public ImmutableMultiTimeSeries<E, C> with(ZonedDateTime eventDateTime, E event);
    
    public ImmutableMultiTimeSeries<E, C> with(ZonedDateTime eventDateTime, C eventCollection);
    
    public ImmutableMultiTimeSeries<E, C> with(TimeSeries.Entry<C> entry);
    
    public ImmutableMultiTimeSeries<E, C> with(Collection<TimeSeries.Entry<C>> entries);
}
