package fop.timeseries.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import fop.timeseries.MultiTimeSeries;
import fop.timeseries.TimeSeries;

public class TimeSeriesUtils
{
    private TimeSeriesUtils()
    {}
    
    public static <E> Set<ZonedDateTime> extractTimeSeriesEventDateTimes(SortedSet<TimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEventDateTime()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    public static <E> Collection<E> extractTimeSeriesEvents(SortedSet<TimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEvent()).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public static <E> Set<ZonedDateTime> extractMultiTimeSeriesEventDateTimes(SortedSet<MultiTimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEventDateTime()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    public static <E> Collection<E> extractMultiTimeSeriesEvents(SortedSet<MultiTimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEvents()).flatMap(e->e.stream()).collect(Collectors.toCollection(ArrayList::new));
    }
}
