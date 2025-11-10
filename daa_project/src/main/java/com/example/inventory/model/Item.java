package com.example.inventory.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Item class matching the specification:
 * - ItemID: unique identifier
 * - Name: item name
 * - CurrentStock: units currently available
 * - DailyDemand: average daily demand
 * - LeadTime: days required to get a new stock
 * - ReorderLevel: minimum stock before reordering
 */
public class Item {
    private final int itemID;
    private final String name;
    private int currentStock;
    private double dailyDemand; // average daily demand
    private final int leadTime; // days required to get a new stock
    private int reorderLevel; // minimum stock before reordering (calculated dynamically)
    
    // Historical sales data for forecasting
    private final List<Integer> dailySalesHistory;
    
    // Cost parameters for EOQ calculation
    private final double unitCost;
    private final double orderingCost;
    private final double holdingCostRate; // annual holding cost as percentage of unit cost
    
    public Item(int itemID, String name, int currentStock, double dailyDemand, 
                int leadTime, int reorderLevel, double unitCost, double orderingCost, double holdingCostRate) {
        this.itemID = itemID;
        this.name = name;
        this.currentStock = currentStock;
        this.dailyDemand = dailyDemand;
        this.leadTime = leadTime;
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
        this.orderingCost = orderingCost;
        this.holdingCostRate = holdingCostRate;
        this.dailySalesHistory = new ArrayList<>();
        
        // Initialize with some historical data based on daily demand
        for (int i = 0; i < 30; i++) {
            // Add some variation around daily demand
            int sales = (int) (dailyDemand + (Math.random() * dailyDemand * 0.3 - dailyDemand * 0.15));
            dailySalesHistory.add(Math.max(0, sales));
        }
    }
    
    // Getters
    public int getItemID() { return itemID; }
    public String getName() { return name; }
    public int getCurrentStock() { return currentStock; }
    public double getDailyDemand() { return dailyDemand; }
    public int getLeadTime() { return leadTime; }
    public int getReorderLevel() { return reorderLevel; }
    public List<Integer> getDailySalesHistory() { return dailySalesHistory; }
    public double getUnitCost() { return unitCost; }
    public double getOrderingCost() { return orderingCost; }
    public double getHoldingCostRate() { return holdingCostRate; }
    
    // Setters
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public void setDailyDemand(double dailyDemand) { this.dailyDemand = dailyDemand; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    
    // Business methods
    public void recordDailySales(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Sales quantity must be >= 0");
        currentStock = Math.max(0, currentStock - quantity);
        dailySalesHistory.add(quantity);
        // Keep last 90 days of history
        if (dailySalesHistory.size() > 90) {
            dailySalesHistory.remove(0);
        }
    }
    
    public void receiveStock(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Received quantity must be >= 0");
        currentStock += quantity;
    }
    
    public double getAnnualHoldingCost() {
        return unitCost * holdingCostRate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return itemID == item.itemID;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemID);
    }
    
    @Override
    public String toString() {
        return String.format("Item{ID=%d, Name='%s', Stock=%d, DailyDemand=%.2f, LeadTime=%d, ReorderLevel=%d}",
                itemID, name, currentStock, dailyDemand, leadTime, reorderLevel);
    }
}



