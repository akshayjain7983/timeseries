package fop.timeseries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fop.timeseries.impl.SimpleTimeSeries;

class TestSimpleTimeSeries
{
    private static Random randomNumberGenerator;
    
    @BeforeAll
    public static void setupBeforeClass()
    {
        randomNumberGenerator = new Random();
    }

    @Test
    void testSimpleTimeSeries()
    {
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
    }

    @Test
    void testSimpleTimeSeriesTimeSeriesOfE()
    {
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), randomNumberGenerator.nextInt());
        SimpleTimeSeries<Integer> testSimpleTimeSeriesTimeSeriesOfE = new SimpleTimeSeries<>(testSimpleTimeSeries);
        assertNotNull(testSimpleTimeSeriesTimeSeriesOfE);
        assertEquals(1, testSimpleTimeSeriesTimeSeriesOfE.size());
    }

    @Test
    void testAdd()
    {
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), randomNumberGenerator.nextInt());
        assertEquals(1, testSimpleTimeSeries.size());
    }

    @Test
    void testRemove()
    {
        ZonedDateTime eventDateTime = ZonedDateTime.now();
        Integer event = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(eventDateTime, event);
        assertEquals(1, testSimpleTimeSeries.size());
        Integer eventRemoved = testSimpleTimeSeries.remove(eventDateTime);
        assertEquals(event, eventRemoved);
        assertEquals(0, testSimpleTimeSeries.size());
    }

    @Test
    void testGet()
    {
        ZonedDateTime eventDateTime = ZonedDateTime.now();
        Integer event = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(eventDateTime, event);
        assertEquals(1, testSimpleTimeSeries.size());
        Integer eventGet = testSimpleTimeSeries.get(eventDateTime);
        assertEquals(event, eventGet);
        assertEquals(1, testSimpleTimeSeries.size());
    }

    @Test
    void testContains()
    {
        ZonedDateTime eventDateTime = ZonedDateTime.now();
        Integer event = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(eventDateTime, event);
        assertEquals(1, testSimpleTimeSeries.size());
        boolean contains = testSimpleTimeSeries.contains(eventDateTime);
        assertTrue(contains);
    }

    @Test
    void testSize()
    {
        ZonedDateTime eventDateTime = ZonedDateTime.now();
        Integer event = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(eventDateTime, event);
        assertEquals(1, testSimpleTimeSeries.size());
    }

    @Test
    void testGetEntries() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        SortedSet<TimeSeries.Entry<Integer>> entries = testSimpleTimeSeries.getEntries();
        //check if sorted by time or not.
        ZonedDateTime lastEntryTime = null;
        for(TimeSeries.Entry<Integer> entry:entries)
        {
            if(lastEntryTime != null)
                assertFalse(lastEntryTime.isAfter(entry.getEventDateTime()));
            lastEntryTime = entry.getEventDateTime();
        }
    }

    @Test
    void testEventDateTimes() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        SortedSet<ZonedDateTime> entries = testSimpleTimeSeries.eventDateTimes();
        //check if sorted by time or not.
        ZonedDateTime lastEntryTime = null;
        for(ZonedDateTime entryTime:entries)
        {
            if(lastEntryTime != null)
                assertFalse(lastEntryTime.isAfter(entryTime));
            lastEntryTime = entryTime;
        }
    }

    @Test
    void testEvents() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        Collection<Integer> events = testSimpleTimeSeries.events();
        Integer[] eventArr = events.toArray(new Integer[2]);
        assertEquals(event2, eventArr[0]);
        assertEquals(event1, eventArr[1]);
    }

    @Test
    void testStart() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        TimeSeries.Entry<Integer> start = testSimpleTimeSeries.start();
        assertEquals(event2, start.getEvent());
    }

    @Test
    void testEnd() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        TimeSeries.Entry<Integer> end = testSimpleTimeSeries.end();
        assertEquals(event1, end.getEvent());
    }

    @Test
    void testIterator() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        Iterator<TimeSeries.Entry<Integer>> entryIterator = testSimpleTimeSeries.iterator();
        //validate that its ascending iterator in time and that changes to this iterator will not affect time series
        ZonedDateTime lastEntryTime = null;
        while(entryIterator.hasNext())
        {
            TimeSeries.Entry<Integer> entry = entryIterator.next();
            if(lastEntryTime != null)
                assertFalse(lastEntryTime.isAfter(entry.getEventDateTime()));
            lastEntryTime = entry.getEventDateTime();
            
            entryIterator.remove();
        }
        
        assertEquals(2, testSimpleTimeSeries.size());
    }

    @Test
    void testDescendingIterator() throws InterruptedException
    {
        Integer event1 = randomNumberGenerator.nextInt();
        Integer event2 = randomNumberGenerator.nextInt();
        SimpleTimeSeries<Integer> testSimpleTimeSeries = new SimpleTimeSeries<>();
        assertNotNull(testSimpleTimeSeries);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event2);
        Thread.sleep(100);
        testSimpleTimeSeries.add(ZonedDateTime.now(), event1);
        assertEquals(2, testSimpleTimeSeries.size());
        Iterator<TimeSeries.Entry<Integer>> entryIterator = testSimpleTimeSeries.descendingIterator();
        //validate that its descending iterator in time and that changes to this iterator will not affect time series
        ZonedDateTime lastEntryTime = null;
        while(entryIterator.hasNext())
        {
            TimeSeries.Entry<Integer> entry = entryIterator.next();
            if(lastEntryTime != null)
                assertFalse(lastEntryTime.isBefore(entry.getEventDateTime()));
            lastEntryTime = entry.getEventDateTime();
            
            entryIterator.remove();
        }
        
        assertEquals(2, testSimpleTimeSeries.size());
    }

}
