package fop.timeseries;

import java.time.ZonedDateTime;
import java.util.Collection;

public interface MultiTimeSeries<E, C extends Collection<E>> extends TimeSeries<C>
{
    public void addEvent(ZonedDateTime eventDateTime, E event);
    
    public boolean contains(ZonedDateTime eventDateTime, E event);
    
    public boolean remove(ZonedDateTime eventDateTime, E event);
}
