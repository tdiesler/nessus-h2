package io.nessus.test.common;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.nessus.common.AbstractTest;
import io.nessus.common.utils.NumericUtils;

public class NumericUtilsTest extends AbstractTest {

    @Test
    public void testNumericUtils() throws Exception {
    	
    	double dval = NumericUtils.round(10.0 / 3, 0.01);
    	Assert.assertEquals("3.33", "" + dval);
    	
    	dval = NumericUtils.gain(7, 10);
    	Assert.assertEquals("42.86", "" + dval);
    	
    	dval = NumericUtils.gain(7, 7);
    	Assert.assertEquals("0.0", "" + dval);
    	
    	dval = NumericUtils.pcnt(10, 7);
    	Assert.assertEquals("142.86", "" + dval);
    	
    	dval = NumericUtils.pcnt(7, 7);
    	Assert.assertEquals("100.0", "" + dval);
    	
    	dval = 5.0 + Double.NaN;
    	Assert.assertEquals("NaN", "" + dval);
    	
    	boolean bval = 5.0 < Double.NaN;
    	Assert.assertEquals(false, bval);
    	
    	bval = 5.0 > Double.NaN;
    	Assert.assertEquals(false, bval);
    }  

    @Test
    public void testRange() throws Exception {
    	
    	List<Integer> irange = NumericUtils.range(1, 5, 5);
    	Assert.assertEquals("[1, 2, 3, 4, 5]", irange.toString());
    	
    	irange = NumericUtils.range(1, 10, 3);
    	Assert.assertEquals("[1, 5, 10]", irange.toString());
    	
    	List<Double> drange = NumericUtils.range(1.0, 5, 5);
    	Assert.assertEquals("[1.0, 2.0, 3.0, 4.0, 5.0]", drange.toString());
    	
    	drange = NumericUtils.range(1.0, 10, 3);
    	Assert.assertEquals("[1.0, 5.5, 10.0]", drange.toString());
    }  
}
