package io.nessus.common;

import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

import io.nessus.common.utils.DateUtils;

public class TimeRange implements Comparable<TimeRange> {
    
    private final Date startTime;
    private final Date endTime;
    
    public TimeRange(String startTime, String endTime) throws ParseException {
        this(startTime != null ? DateUtils.parse(startTime) : null, endTime != null ? DateUtils.parse(endTime) : null);
    }
    
    public TimeRange(Date startTime, Date endTime) {
        AssertArg.isTrue(startTime != null || endTime != null, "Null range boundaries");
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
	public static TimeRange fromString(String spec) {
		AssertArg.isTrue(spec.startsWith("[") && spec.endsWith("]"), "Invalid time range: " + spec);
		spec = spec.substring(1, spec.length() - 1);
		int idx = spec.indexOf(" - ");
		Function<String, Date> fnct = val -> {
		    if (val == null || val.equals("null")) return null;
		    return DateUtils.parse(val); 
		};
        Date tstart = fnct.apply(spec.substring(0, idx));
		Date tend = fnct.apply(spec.substring(idx + 3));
		return new TimeRange(tstart, tend);
	}
	
    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean contains(Date tstamp) {
        boolean match = true;
        match &= startTime == null || startTime.compareTo(tstamp) <= 0;
        match &= endTime == null || tstamp.compareTo(endTime) < 0;
        return match;
    }
    
    public boolean contains(TimeRange tr) {
        boolean match = contains(this, tr);
        return match;
    }
    
    public static boolean contains(TimeRange trA, TimeRange trB) {
        if (trA.getStartTime() == null || trA.getEndTime() == null) return false;
        if (trB.getStartTime() == null || trB.getEndTime() == null) return false;
        boolean match = trA.getStartTime().compareTo(trB.getStartTime()) <= 0;
        match &= trB.getEndTime().compareTo(trA.getEndTime()) <= 0;
        return match;
    }
    
    public boolean intersects(TimeRange tr) {
        boolean match = intersects(this, tr);
        return match;
    }
    
    private static boolean intersects(TimeRange trA, TimeRange trB) {
        if (trA.getStartTime() == null || trA.getEndTime() == null) return false;
        if (trB.getStartTime() == null || trB.getEndTime() == null) return false;
        if (contains(trA, trB) || contains(trB, trA)) return true;
        if (trA.getStartTime().equals(trB.getEndTime())) return false;
        if (trA.getEndTime().equals(trB.getStartTime())) return false;
        boolean match = trA.contains(trB.getEndTime()) || trA.contains(trB.getStartTime());
        return match;
    }
    
    @Override
    public int compareTo(TimeRange o) {
        if (startTime != null && o.startTime != null) 
            return startTime.compareTo(o.startTime);
        if (endTime != null && o.endTime != null) 
            return endTime.compareTo(o.endTime);
        return 0;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = 31 * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimeRange))return false;
        TimeRange other = (TimeRange) obj;
        if (startTime == null || endTime == null) return false; 
        if (!startTime.equals(other.startTime)) return false;
        if (!endTime.equals(other.endTime)) return false;
        return true;
    }

    public String toString() {
        String start = startTime != null ? DateUtils.format(startTime) : null;
        String end = endTime != null ? DateUtils.format(endTime) : null;
        if (start != null && end != null) {
        	int idx1 = start.indexOf(" 00:00:00");
        	int idx2 = end.indexOf(" 00:00:00");
        	if (idx1 > 0 && idx2 > 0) {
        		start = start.substring(0, idx1);
        		end = end.substring(0, idx2);
        	}
        }
        return String.format("[%s - %s]", start, end); 
    }
}
