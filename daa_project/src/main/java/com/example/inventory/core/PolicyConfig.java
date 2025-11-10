package com.example.inventory.core;

public class PolicyConfig {
    public final Forecasting.Method forecastingMethod;
    public final int smaWindowDays;
    public final double expAlpha;
    public final double zServiceLevel; // e.g., 1.65 for ~95% service level
    public final int avgLeadTimeDays;
    public final double leadTimeStdDays;
    public final double orderingCostPerOrder;

    public PolicyConfig(Forecasting.Method forecastingMethod,
                        int smaWindowDays,
                        double expAlpha,
                        double zServiceLevel,
                        int avgLeadTimeDays,
                        double leadTimeStdDays,
                        double orderingCostPerOrder) {
        this.forecastingMethod = forecastingMethod;
        this.smaWindowDays = smaWindowDays;
        this.expAlpha = expAlpha;
        this.zServiceLevel = zServiceLevel;
        this.avgLeadTimeDays = avgLeadTimeDays;
        this.leadTimeStdDays = leadTimeStdDays;
        this.orderingCostPerOrder = orderingCostPerOrder;
    }

    public static PolicyConfig defaultConfig() {
        return new PolicyConfig(
                Forecasting.Method.EXPONENTIAL,
                7,
                0.4,
                1.65,
                5,
                1.5,
                25.0
        );
    }
}

