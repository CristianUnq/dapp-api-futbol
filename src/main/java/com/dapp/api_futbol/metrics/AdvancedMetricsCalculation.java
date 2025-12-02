package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.dto.AdvancedMetricsDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AdvancedMetricsCalculation {

    // Compute full Stat object (count, mean, median, stddev, min, max)
    public AdvancedMetricsDTO.Stat computeStats(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return new AdvancedMetricsDTO.Stat(0, 0, 0, 0, 0, 0);
        }
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        double mean = computeMean(sorted);
        double median = computeMedian(sorted);
        double stddev = computeStdDev(sorted);
        double min = sorted.get(0);
        double max = sorted.get(n - 1);
        return new AdvancedMetricsDTO.Stat(n, mean, median, stddev, min, max);
    }

    // Convenience: compute mean
    public double computeMean(List<Integer> values) {
        if (values == null || values.isEmpty()) return 0.0;
        double sum = 0.0;
        for (int v : values) sum += v;
        return sum / values.size();
    }

    // Convenience: compute median
    public double computeMedian(List<Integer> values) {
        if (values == null || values.isEmpty()) return 0.0;
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n % 2 == 1) return sorted.get(n / 2);
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    // Convenience: compute sample standard deviation
    public double computeStdDev(List<Integer> values) {
        if (values == null || values.size() < 2) return 0.0;
        double mean = computeMean(values);
        double sumSq = 0.0;
        for (int v : values) sumSq += Math.pow(v - mean, 2);
        return Math.sqrt(sumSq / (values.size() - 1));
    }

    // Compute z-score for a single value given mean and stddev
    public Double computeZScore(Integer value, double mean, double stddev) {
        if (value == null) return null;
        // If there is no variation (stddev == 0) we return 0.0 as neutral z-score
        if (stddev == 0.0) return 0.0;
        double z = (value - mean) / stddev;
        // Round to 2 decimals for cleaner output
        return Math.round(z * 100.0) / 100.0;
    }

}
