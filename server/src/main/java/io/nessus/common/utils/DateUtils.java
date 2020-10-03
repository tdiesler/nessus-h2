package io.nessus.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;
import io.nessus.common.TimeRange;

public final class DateUtils {

    // Hide ctor
    private DateUtils() {};
    
    public static Date addTime(Date tstamp, long millis) {
        return addTime(tstamp, millis, TimeUnit.MILLISECONDS);
    }
    
    public static Date addTime(Date tstamp, long amount, TimeUnit unit) {
        return new Date(tstamp.getTime() + unit.toMillis(amount));
    }
    
    public static TimeRange addTime(TimeRange trange, long amount, TimeUnit unit) {
        Date tstart = trange.getStartTime();
        Date tend = trange.getEndTime();
		if (tstart != null) tstart = addTime(tstart, amount, unit);
        if (tend != null) tend = addTime(tend, amount, unit);
        return new TimeRange(tstart, tend);
    }
    
    public static Date subTime(Date tstamp, long millis) {
        return subTime(tstamp, millis, TimeUnit.MILLISECONDS);
    }
    
    public static Date subTime(Date tstamp, long amount, TimeUnit unit) {
        return new Date(tstamp.getTime() - unit.toMillis(amount));
    }

    public static TimeRange subTime(TimeRange trange, long millis) {
        return subTime(trange, millis, TimeUnit.MILLISECONDS);
    }
    
    public static TimeRange subTime(TimeRange trange, long amount, TimeUnit unit) {
        Date tstart = subTime(trange.getStartTime(), amount, unit);
        Date tend = subTime(trange.getEndTime(), amount, unit);
        return new TimeRange(tstart, tend);
    }
    
	public static List<TimeRange> rangeseries(TimeRange trange, long amount, TimeUnit unit) {
		long total = elapsedTime(trange, unit);
		AssertState.isTrue(total % amount == 0, String.format("Not divisibal %d/%d", total, amount));
		List<TimeRange> result = new ArrayList<>();
		Date tstart = trange.getStartTime();
		Date tend = trange.getEndTime();
		while (tstart.compareTo(tend) < 0) {
			TimeRange auxrng = new TimeRange(tstart, addTime(tstart, amount, unit));
            tstart = auxrng.getEndTime();
			result.add(auxrng);
		}
		return result;
	}
	
	public static List<Date> timeseries(TimeRange trange, long amount, TimeUnit unit) {
        List<Date> daysInRange = rangeseries(trange, amount, unit).stream()
                .map(tr -> tr.getStartTime())
                .collect(Collectors.toList());
        return daysInRange;
    }
	
    public static TimeRange adjustTimeRange(TimeRange timeRange, long amount, TimeUnit unit) {
        AssertArg.notNull(timeRange, "Null timeRange");
        
        Date endTime = timeRange.getEndTime();
        endTime = endTime != null ? adjustTime(endTime, amount, unit) : null;
        
        Date startTime = timeRange.getStartTime();
        startTime = startTime != null ? adjustTime(startTime, amount, unit) : null;
        
        return new TimeRange(startTime, endTime);
    }

    public static Date adjustTime(Date tstamp, long amount, TimeUnit unit) {
        long millis = unit.toMillis(amount);
		long chunks = tstamp.getTime() / millis;
        return new Date(chunks * millis);
    }

    public static Date assertOnBoundary(Date tstamp, long duration, TimeUnit unit) {
        long modms = tstamp.getTime() % unit.toMillis(duration);
        AssertState.isTrue(isOnBoundary(tstamp, duration, unit), 
                String.format("Not a multiple of %d %s (%d min extra): %s", duration, unit, modms / 60000, format(tstamp)));
        return tstamp;
    }

    public static boolean isOnBoundary(Date tstamp, long duration, TimeUnit unit) {
        long modms = tstamp.getTime() % unit.toMillis(duration);
        return modms == 0L;
    }

    public static long elapsedTime(Date startTime) {
        return elapsedTime(startTime, new Date(), TimeUnit.MILLISECONDS);
    }

    public static long elapsedTime(Date startTime, TimeUnit unit) {
        return elapsedTime(startTime, new Date(), unit);
    }

    public static long elapsedTime(Date startTime, Date endTime) {
        return elapsedTime(startTime, endTime, TimeUnit.MILLISECONDS);
    }

    public static long elapsedTime(Date startTime, Date endTime, TimeUnit unit) {
        long elapsed = endTime.getTime() - startTime.getTime();
        return elapsed / unit.toMillis(1);
    }

    public static long elapsedTime(TimeRange timeRange) {
        return elapsedTime(timeRange.getStartTime(), timeRange.getEndTime(), TimeUnit.MILLISECONDS);
    }

    public static long elapsedTime(TimeRange timeRange, TimeUnit unit) {
        return elapsedTime(timeRange.getStartTime(), timeRange.getEndTime(), unit);
    }

    public static String elapsedTimeString(Date startTime) {
        return elapsedTimeString(startTime, new Date());
    }
    
    public static String elapsedTimeString(Date startTime, Date endTime) {
        return elapsedTimeString(elapsedTime(startTime, endTime, TimeUnit.MILLISECONDS));
    }
    
    public static String elapsedTimeString(TimeRange timeRange) {
        return elapsedTimeString(timeRange.getStartTime(), timeRange.getEndTime());
    }
    
    public static String elapsedTimeString(Long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long hours = seconds / 3600;
        long mins = (seconds - hours * 3600) / 60;
        long secs = (seconds - hours * 3600) % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
    
    public static Date max(Date t1, Date t2) {
        return new Date(Math.max(t1.getTime(), t2.getTime()));
    }
    
    public static Date min(Date t1, Date t2) {
        return new Date(Math.min(t1.getTime(), t2.getTime()));
    }
    
    public static String format(Date tstamp) {
        return getTimestampFormat().format(tstamp);
    }

    public static String format(Date tstamp, boolean dateOnly) {
        SimpleDateFormat sdf = dateOnly ? getDateFormat() : getTimestampFormat();
        return sdf.format(tstamp);
    }

	public static String format(TimeRange trange) {
		return format(trange, false);
	}
	
	public static String format(TimeRange trange, boolean dateOnly) {
		String tstart = format(trange.getStartTime(), dateOnly);
        String tend = format(trange.getEndTime(), dateOnly);
        return String.format("[%s - %s]", tstart, tend);
	}
	
    public static Date parse(String tstr) {
        try {
            if (tstr.contains(" "))
                return getTimestampFormat().parse(tstr);
            else
                return getDateFormat().parse(tstr);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /*
     * SimpleDateFormat is not thread safe  
     */
    
    private static ThreadLocal<SimpleDateFormat> tstampAssociation = new ThreadLocal<>();
    private static ThreadLocal<SimpleDateFormat> dateAssociation = new ThreadLocal<>();
    
    private static SimpleDateFormat getTimestampFormat() {
        synchronized (tstampAssociation) {
            SimpleDateFormat format = tstampAssociation.get();
            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                tstampAssociation.set(format);
            }
            return format;
        }
    }
    
    private static SimpleDateFormat getDateFormat() {
        synchronized (dateAssociation) {
            SimpleDateFormat format = dateAssociation.get();
            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateAssociation.set(format);
            }
            return format;
        }
    }
}
