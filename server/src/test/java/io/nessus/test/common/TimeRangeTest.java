package io.nessus.test.common;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import io.nessus.common.AbstractTest;
import io.nessus.common.TimeRange;
import io.nessus.common.utils.DateUtils;

public class TimeRangeTest extends AbstractTest {

    @Test
    public void testTimeRange() throws Exception {
        
        Date tsA = DateUtils.parse("1990-02-01 00:00:00");
        Date tsB = DateUtils.parse("1990-03-01 00:00:00");
        Date tsC = DateUtils.parse("1990-04-01 00:00:00");
        Date tsD = DateUtils.parse("1990-05-01 00:00:00");
        
        // |-----| |-----|
        
        TimeRange trA = new TimeRange(tsA, tsB);
        TimeRange trB = new TimeRange(tsC, tsD);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertFalse(trA.intersects(trB));
        Assert.assertFalse(trB.intersects(trA));
        
        Assert.assertFalse(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
        
        Assert.assertTrue(trA.contains(tsA)); // start time inclusiv
        Assert.assertFalse(trA.contains(tsB)); // end time exclusiv
        
        // |-----|-----|
        
        trA = new TimeRange(tsA, tsB);
        trB = new TimeRange(tsB, tsC);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertFalse(trA.intersects(trB));
        Assert.assertFalse(trB.intersects(trA));
        
        Assert.assertFalse(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
        
        // |---|-|----|
        
        trA = new TimeRange(tsA, tsC);
        trB = new TimeRange(tsB, tsD);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertTrue(trA.intersects(trB));
        Assert.assertTrue(trB.intersects(trA));
        
        Assert.assertFalse(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
        
        // |-------------|
        //    |-----|
        
        trA = new TimeRange(tsA, tsD);
        trB = new TimeRange(tsB, tsC);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertTrue(trA.intersects(trB));
        Assert.assertTrue(trB.intersects(trA));
        
        Assert.assertTrue(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
        
        //    |-----|
        //    |-----|
        
        trA = new TimeRange(tsA, tsB);
        trB = new TimeRange(tsA, tsB);
        
        Assert.assertTrue(trA.equals(trB));
        Assert.assertTrue(trB.equals(trA));
        
        Assert.assertTrue(trA.intersects(trB));
        Assert.assertTrue(trB.intersects(trA));
        
        Assert.assertTrue(trA.contains(trB));
        Assert.assertTrue(trB.contains(trA));
        
        //    |-----|
        //    null values
        
        trA = new TimeRange(tsA, null);
        trB = new TimeRange(tsA, null);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertFalse(trA.intersects(trB));
        Assert.assertFalse(trB.intersects(trA));
        
        Assert.assertFalse(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
        
        //    null values
        //    |-----|
        
        trA = new TimeRange(null, tsB);
        trB = new TimeRange(null, tsB);
        
        Assert.assertFalse(trA.equals(trB));
        Assert.assertFalse(trB.equals(trA));
        
        Assert.assertFalse(trA.intersects(trB));
        Assert.assertFalse(trB.intersects(trA));
        
        Assert.assertFalse(trA.contains(trB));
        Assert.assertFalse(trB.contains(trA));
    }  
}
