package fop.timeseries.util;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import fop.timeseries.MultiTimeSeries;
import fop.timeseries.TimeSeries;

public class TimeSeriesUtils
{
    private TimeSeriesUtils()
    {}
    
    public static <E> NavigableSet<ZonedDateTime> extractTimeSeriesEventDateTimes(SortedSet<TimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEventDateTime()).collect(Collectors.toCollection(TreeSet::new));
    }
    
    public static <E> Collection<E> extractTimeSeriesEvents(SortedSet<TimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEvent()).collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static <E> NavigableSet<ZonedDateTime> extractMultiTimeSeriesEventDateTimes(NavigableSet<MultiTimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEventDateTime()).collect(Collectors.toCollection(TreeSet::new));
    }
    
    public static <E> Collection<E> extractMultiTimeSeriesEvents(NavigableSet<MultiTimeSeries.Entry<E>> entries)
    {
        return entries.stream().map(e->e.getEvents()).flatMap(e->e.stream()).collect(Collectors.toCollection(LinkedList::new));
    }
}
