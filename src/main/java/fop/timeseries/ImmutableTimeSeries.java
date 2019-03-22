package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;

public interface ImmutableTimeSeries<E> extends TimeSeries<E>
{
    public ImmutableTimeSeries<E> with(ZonedDateTime eventDateTime, E event);
    
    public ImmutableTimeSeries<E> with(TimeSeries.Entry<E> entry);
    
    public ImmutableTimeSeries<E> with(Collection<TimeSeries.Entry<E>> entries);
}
