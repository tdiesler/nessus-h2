package io.nessus.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.nessus.common.AssertArg;

public final class NumericUtils {

    // Hide ctor
    private NumericUtils() {}

    public static Double gain(double startval, double endval) {
        return round(100 * (endval / startval - 1), 0.01);
    }

    public static Double pcnt(double some, double all) {
        return round(100.0 * some / all, 0.01);
    }
    
    public static Double round(double val, double rnd) {
    	double factor = 1 / rnd;
    	return Math.round(factor * val) / factor;
    }
    
    public static <T extends Number> Double min(Collection<T> values) {
    	AssertArg.notNull(values, "Null values");
        if (values.isEmpty()) return Double.NaN;
        return values.stream().mapToDouble(Number::doubleValue).min().getAsDouble();
    }
    
    public static <T extends Number> Double max(Collection<T> values) {
    	AssertArg.notNull(values, "Null values");
        if (values.isEmpty()) return Double.NaN;
        return values.stream().mapToDouble(Number::doubleValue).max().getAsDouble();
    }
    
    public static <T extends Number> Double sum(Collection<T> values) {
    	AssertArg.notNull(values, "Null values");
        if (values.isEmpty()) return Double.NaN;
        return values.stream().mapToDouble(Number::doubleValue).sum();
    }
    
    public static <T extends Number> Double mean(Collection<T> values) {
    	AssertArg.notNull(values, "Null values");
        if (values.isEmpty()) return Double.NaN;
        return sum(values) / values.size();
    }
    
    public static <T extends Number> Double median(Collection<T> values) {
        AssertArg.notNull(values, "Null values");
        List<T> sorted = values.stream().sorted().collect(Collectors.toList());
        if (sorted.isEmpty()) return Double.NaN;
        if (sorted.size() == 1) return sorted.get(0).doubleValue();
        int size = sorted.size();
        int idx = size / 2;
        double median;
        if (size % 2 == 0) {
            T v1 = sorted.get(idx - 1);
            T v2 = sorted.get(idx);
            median = mean(Arrays.asList(v1, v2));
        } else {
            median = sorted.get(idx).doubleValue();
        }
        return median;
    }
    
    public static <T extends Number> Double stdev(Collection<T> values) {
    	AssertArg.notNull(values, "Null values");
        if (values.isEmpty()) return Double.NaN;
        double aux = 0;
        double mean = mean(values);
        for (T val : values) {
            aux += Math.pow(val.doubleValue() - mean, 2);
        }
        return Math.sqrt(aux / values.size());
    }
    
	public static List<Integer> range(int start, int end, int elements) {
		List<Integer> range = range(1.0 * start, end, elements).stream()
				.mapToInt(val -> val.intValue()).boxed()
				.collect(Collectors.toList());
        return range;
	}
    
	public static List<Double> range(double start, double end, int elements) {
		AssertArg.isTrue(elements > 0, "Non positive number of elements");
		if (elements == 1) return Arrays.asList(start);
		double step = (end - start) / (elements - 1);
		List <Double> range = new ArrayList<>();
		Double val = start;
        for (int i = 0; i < elements; i++) {
            range.add(val);
            val += step;
        }
		return range;
	}
    
	/**
	 * Replaces null values in the given values list with the last known
	 * previous value.
	 * 
	 * e.g.   [3, null, null, 5] => [3, 3, 3, 5] 
	 */
    public static <T extends Number> void replaceNaN(List<T> values) {
        
        BiFunction<List<T>, Integer, T> findLast = (data, i) -> {
            T res = null;
            int j = i - 1;
            while (res == null && 0 <= j) {
                res = data.get(j--);
            }
            return res;
        };
        
        for (int i = 0; i < values.size(); i++) {
            T val = values.get(i);
            if (val != null) {
                int j = i - 1;
                T last = findLast.apply(values, i);
                while (0 <= j && values.get(j) == null) {
                    values.set(j--, last);
                }
            }
        }
    }
    
    public static Double failsafeDouble(String val) { 
		try { 
			return Double.valueOf(val); 
		} catch (NumberFormatException ex) { 
			return Double.NaN; 
		}
	}
}
