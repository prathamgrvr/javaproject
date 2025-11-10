package com.example.inventory.core;

import com.example.inventory.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Manager implementing the specified workflow:
 * - Daily Sales Update
 * - Forecast Next Day Demand (EMA/SMA)
 * - Calculate Reorder Point (with Safety Stock)
 * - Check Replenishment (Continuous Review (s, Q) policy)
 * - Track Lead Time
 */
public class InventoryManager {
    private final PolicyConfig config;
    
    public InventoryManager(PolicyConfig config) {
        this.config = config;
    }
    
    /**
     * Daily update workflow as per specification
     * For each item:
     * 1. Reduce stock based on sales
     * 2. Forecast next day demand using EMA/SMA
     * 3. Calculate safety stock and reorder point
     * 4. Check if reorder needed and calculate EOQ
     */
    public List<ReplenishmentDecision> processDailyUpdate(List<Item> inventory) {
        List<ReplenishmentDecision> decisions = new ArrayList<>();
        
        for (Item item : inventory) {
            // Step 1: Forecast next day demand using EMA or SMA
            double forecast = calculateForecast(item);
            
            // Step 2: Calculate safety stock
            double demandStdDev = Forecasting.stdDev(item.getDailySalesHistory());
            int safetyStock = Policies.computeSafetyStock(demandStdDev, config.zServiceLevel, item.getLeadTime());
            
            // Step 3: Calculate reorder point (ROP)
            int reorderPoint = Policies.computeReorderPoint(forecast, item.getLeadTime(), safetyStock);
            item.setReorderLevel(reorderPoint); // Update reorder level
            
            // Step 4: Check if reorder needed (CurrentStock <= ReorderPoint)
            boolean needsReorder = item.getCurrentStock() <= reorderPoint;
            
            // Step 5: Calculate EOQ if reorder needed
            int orderQuantity = 0;
            if (needsReorder) {
                double annualDemand = forecast * 365.0;
                orderQuantity = Policies.computeEOQ(
                    annualDemand, 
                    item.getOrderingCost(), 
                    item.getAnnualHoldingCost() / 365.0
                );
            }
            
            decisions.add(new ReplenishmentDecision(
                item, forecast, safetyStock, reorderPoint, orderQuantity, needsReorder
            ));
        }
        
        return decisions;
    }
    
    /**
     * Forecast next day demand using EMA or SMA
     */
    private double calculateForecast(Item item) {
        List<Integer> history = item.getDailySalesHistory();
        if (history.isEmpty()) {
            return item.getDailyDemand();
        }
        
        switch (config.forecastingMethod) {
            case SMA:
                return Forecasting.simpleMovingAverage(history, config.smaWindowDays);
            case EXPONENTIAL:
            default:
                return Forecasting.exponentialSmoothing(history, config.expAlpha);
        }
    }
    
    /**
     * Record daily sales for an item
     */
    public void recordDailySales(Item item, int quantity) {
        item.recordDailySales(quantity);
    }
    
    /**
     * Place an order for an item
     */
    public void placeOrder(Item item, int quantity) {
        // In a real system, this would create a purchase order
        // For now, we just track that an order was placed
        // Stock will be received after leadTime days
        System.out.println(String.format("ORDER PLACED: ItemID=%d, Name='%s', Quantity=%d, ExpectedDelivery=%d days",
                item.getItemID(), item.getName(), quantity, item.getLeadTime()));
    }
    
    /**
     * Replenishment decision result
     */
    public static class ReplenishmentDecision {
        public final Item item;
        public final double forecastedDemand;
        public final int safetyStock;
        public final int reorderPoint;
        public final int orderQuantity;
        public final boolean needsReorder;
        
        public ReplenishmentDecision(Item item, double forecastedDemand, int safetyStock, 
                                     int reorderPoint, int orderQuantity, boolean needsReorder) {
            this.item = item;
            this.forecastedDemand = forecastedDemand;
            this.safetyStock = safetyStock;
            this.reorderPoint = reorderPoint;
            this.orderQuantity = orderQuantity;
            this.needsReorder = needsReorder;
        }
        
        public String toDisplayString() {
            return String.format(
                "ItemID=%d | %s | Stock=%d | Forecast=%.2f/day | SS=%d | ROP=%d | EOQ=%d | %s",
                item.getItemID(), item.getName(), item.getCurrentStock(), 
                forecastedDemand, safetyStock, reorderPoint, orderQuantity,
                needsReorder ? String.format("REORDER -> Qty=%d", orderQuantity) : "OK"
            );
        }
    }
}
