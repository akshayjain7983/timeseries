package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.NavigableSet;

public interface MultiTimeSeriesNavigableEvents<E> extends MultiTimeSeries<E>
{
    @Override
    public NavigableSet<E> get(ZonedDateTime eventDateTime);

    @Override
    public NavigableSet<E> remove(ZonedDateTime eventDateTime);
    
    @Override
    public NavigableSet<E> events();
    
    public static interface Entry<E> extends MultiTimeSeries.Entry<E>
    {
        public NavigableSet<E> getEvents();
    }
}
