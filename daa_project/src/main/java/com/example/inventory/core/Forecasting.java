package com.example.inventory.core;

import java.util.List;

public final class Forecasting {
    private Forecasting() {}

    public enum Method { SMA, EXPONENTIAL }

    public static double simpleMovingAverage(List<Integer> history, int window) {
        if (history.isEmpty() || window <= 0) return 0.0;
        int size = history.size();
        int start = Math.max(0, size - window);
        double sum = 0.0;
        int n = 0;
        for (int i = start; i < size; i++) {
            sum += history.get(i);
            n++;
        }
        return n == 0 ? 0.0 : sum / n;
    }

    public static double exponentialSmoothing(List<Integer> history, double alpha) {
        if (history.isEmpty()) return 0.0;
        double s = history.get(0);
        for (int i = 1; i < history.size(); i++) {
            s = alpha * history.get(i) + (1 - alpha) * s;
        }
        return s;
    }

    public static double stdDev(List<Integer> values) {
        int n = values.size();
        if (n == 0) return 0.0;
        double mean = values.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
        double var = 0.0;
        for (int v : values) {
            double d = v - mean;
            var += d * d;
        }
        var = var / Math.max(1, n - 1);
        return Math.sqrt(var);
    }
}

