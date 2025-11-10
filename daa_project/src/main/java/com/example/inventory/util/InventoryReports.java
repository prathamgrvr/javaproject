package com.example.inventory.util;

import com.example.inventory.core.InventoryManager;
import com.example.inventory.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates alerts and reports for inventory management
 */
public class InventoryReports {
    
    /**
     * Generate low stock alerts
     */
    public static List<String> generateLowStockAlerts(List<Item> inventory) {
        return inventory.stream()
            .filter(item -> item.getCurrentStock() <= item.getReorderLevel())
            .map(item -> String.format("ALERT: %s (ID=%d) is LOW - Stock=%d, ReorderLevel=%d",
                    item.getName(), item.getItemID(), item.getCurrentStock(), item.getReorderLevel()))
            .collect(Collectors.toList());
    }
    
    /**
     * Generate weekly report on stock movement
     */
    public static String generateWeeklyReport(List<Item> inventory, 
                                              List<InventoryManager.ReplenishmentDecision> decisions) {
        StringBuilder report = new StringBuilder();
        report.append("=== WEEKLY INVENTORY REPORT ===\n\n");
        
        int totalItems = inventory.size();
        int lowStockItems = (int) inventory.stream()
            .filter(item -> item.getCurrentStock() <= item.getReorderLevel())
            .count();
        int itemsNeedingReorder = (int) decisions.stream()
            .filter(d -> d.needsReorder)
            .count();
        
        double totalHoldingCost = inventory.stream()
            .mapToDouble(item -> item.getCurrentStock() * item.getUnitCost() * item.getHoldingCostRate() / 365.0)
            .sum();
        
        int totalStockouts = (int) inventory.stream()
            .filter(item -> item.getCurrentStock() == 0)
            .count();
        
        report.append(String.format("Total Items: %d\n", totalItems));
        report.append(String.format("Low Stock Items: %d\n", lowStockItems));
        report.append(String.format("Items Needing Reorder: %d\n", itemsNeedingReorder));
        report.append(String.format("Stockouts: %d\n", totalStockouts));
        report.append(String.format("Estimated Daily Holding Cost: $%.2f\n\n", totalHoldingCost));
        
        report.append("Top 10 Items by Current Stock:\n");
        inventory.stream()
            .sorted((a, b) -> Integer.compare(b.getCurrentStock(), a.getCurrentStock()))
            .limit(10)
            .forEach(item -> report.append(String.format("  %s (ID=%d): %d units\n",
                    item.getName(), item.getItemID(), item.getCurrentStock())));
        
        return report.toString();
    }
    
    /**
     * Generate monthly report
     */
    public static String generateMonthlyReport(List<Item> inventory) {
        StringBuilder report = new StringBuilder();
        report.append("=== MONTHLY INVENTORY REPORT ===\n\n");
        
        double totalInventoryValue = inventory.stream()
            .mapToDouble(item -> item.getCurrentStock() * item.getUnitCost())
            .sum();
        
        double totalAnnualHoldingCost = inventory.stream()
            .mapToDouble(item -> item.getCurrentStock() * item.getUnitCost() * item.getHoldingCostRate())
            .sum();
        
        double avgDailyDemand = inventory.stream()
            .mapToDouble(Item::getDailyDemand)
            .average()
            .orElse(0.0);
        
        report.append(String.format("Total Inventory Value: $%.2f\n", totalInventoryValue));
        report.append(String.format("Annual Holding Cost: $%.2f\n", totalAnnualHoldingCost));
        report.append(String.format("Average Daily Demand: %.2f units/item\n\n", avgDailyDemand));
        
        report.append("Items with Highest Demand:\n");
        inventory.stream()
            .sorted((a, b) -> Double.compare(b.getDailyDemand(), a.getDailyDemand()))
            .limit(10)
            .forEach(item -> report.append(String.format("  %s (ID=%d): %.2f units/day\n",
                    item.getName(), item.getItemID(), item.getDailyDemand())));
        
        return report.toString();
    }
}



