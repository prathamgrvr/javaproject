package com.example.inventory.core;

/**
 * Policy calculations for inventory management:
 * - EOQ (Economic Order Quantity)
 * - Safety Stock
 * - Reorder Point (ROP)
 */
public final class Policies {
    private Policies() {}
    
    /**
     * EOQ Formula: Q = sqrt(2 * Demand * OrderingCost / HoldingCost)
     * Balances ordering cost vs holding cost
     */
    public static int computeEOQ(double annualDemandUnits, double orderCost, double dailyHoldingCost) {
        if (annualDemandUnits <= 0 || orderCost <= 0 || dailyHoldingCost <= 0) return 0;
        double H = dailyHoldingCost * 365.0; // Annual holding cost
        double eoq = Math.sqrt((2.0 * annualDemandUnits * orderCost) / H);
        return (int) Math.ceil(eoq);
    }
    
    /**
     * Safety Stock Calculation: Z * σ_demand * sqrt(LeadTime)
     * Accounts for variability in demand and supplier delays
     * Z = service factor (1.65 for 95% service level)
     * σ_demand = standard deviation of daily demand
     */
    public static int computeSafetyStock(double demandStdDevPerDay, double z, double leadTimeDays) {
        double ss = z * demandStdDevPerDay * Math.sqrt(Math.max(0.0, leadTimeDays));
        return (int) Math.ceil(ss);
    }
    
    /**
     * Reorder Point (ROP) = DailyDemand * LeadTime + SafetyStock
     * Continuous Review (s, Q) policy: Reorder when CurrentStock <= ROP
     */
    public static int computeReorderPoint(double forecastDailyDemand, double leadTimeDays, int safetyStock) {
        double ltd = forecastDailyDemand * leadTimeDays; // lead time demand
        return (int) Math.ceil(ltd) + safetyStock;
    }
}
