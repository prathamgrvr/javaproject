package com.example.inventory;

import com.example.inventory.core.InventoryManager;
import com.example.inventory.core.PolicyConfig;
import com.example.inventory.data.ItemDataGenerator;
import com.example.inventory.model.Item;
import com.example.inventory.store.InventoryStore;
import com.example.inventory.util.InventoryReports;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main application implementing the Smart Inventory Manager
 * Workflow as specified:
 * 1. Initialize Inventory (50 items)
 * 2. Daily Sales Update
 * 3. Forecast Next Day Demand
 * 4. Calculate Reorder Point
 * 5. Check Replenishment
 * 6. Track Lead Time
 * 7. Alerts and Reports
 */
public class Main {
    private static InventoryStore store;
    private static InventoryManager manager;
    
    public static void main(String[] args) {
        // Check if GUI mode is requested (default) or CLI mode
        boolean useGUI = true;
        if (args.length > 0 && args[0].equals("--cli")) {
            useGUI = false;
        }
        
        if (useGUI) {
            // Launch GUI
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    new com.example.inventory.gui.MainWindow().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error starting GUI: " + e.getMessage());
                    System.out.println("Falling back to CLI mode...");
                    runCLI();
                }
            });
        } else {
            runCLI();
        }
    }
    
    private static void runCLI() {
        // Initialize system
        PolicyConfig config = PolicyConfig.defaultConfig();
        manager = new InventoryManager(config);
        store = new InventoryStore();
        
        // Step 1: Initialize Inventory - Load 50 items
        System.out.println("Initializing inventory with 50 items...");
        List<Item> items = ItemDataGenerator.generate50Items();
        for (Item item : items) {
            store.addItem(item);
        }
        System.out.println("✓ Inventory initialized with " + store.getItemCount() + " items\n");
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Smart Inventory Manager ===");
            System.out.println("1) List all items");
            System.out.println("2) Record daily sales (update inventory)");
            System.out.println("3) Process daily update (forecast & check replenishment)");
            System.out.println("4) Show replenishment decisions");
            System.out.println("5) Place orders (auto-replenishment)");
            System.out.println("6) View low stock alerts");
            System.out.println("7) Generate weekly report");
            System.out.println("8) Generate monthly report");
            System.out.println("9) Simulate daily workflow");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    listAllItems();
                    break;
                case "2":
                    recordDailySales(scanner);
                    break;
                case "3":
                    processDailyUpdate();
                    break;
                case "4":
                    showReplenishmentDecisions();
                    break;
                case "5":
                    placeOrders();
                    break;
                case "6":
                    showLowStockAlerts();
                    break;
                case "7":
                    generateWeeklyReport();
                    break;
                case "8":
                    generateMonthlyReport();
                    break;
                case "9":
                    simulateDailyWorkflow();
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void listAllItems() {
        System.out.println("\n=== All Items ===");
        List<Item> items = store.getAllItems();
        System.out.printf("%-5s %-30s %-10s %-12s %-10s %-12s%n", 
                "ID", "Name", "Stock", "DailyDemand", "LeadTime", "ReorderLevel");
        System.out.println("-".repeat(85));
        for (Item item : items) {
            System.out.printf("%-5d %-30s %-10d %-12.2f %-10d %-12d%n",
                    item.getItemID(), item.getName(), item.getCurrentStock(),
                    item.getDailyDemand(), item.getLeadTime(), item.getReorderLevel());
        }
        System.out.println("\nTotal items: " + items.size());
    }
    
    private static void recordDailySales(Scanner scanner) {
        System.out.print("Enter ItemID: ");
        int itemID = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter quantity sold today: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());
        
        Optional<Item> itemOpt = store.getItemByID(itemID);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            manager.recordDailySales(item, quantity);
            System.out.println(String.format("✓ Recorded %d units sold for %s (ID=%d). New stock: %d",
                    quantity, item.getName(), itemID, item.getCurrentStock()));
        } else {
            System.out.println("Item not found with ID: " + itemID);
        }
    }
    
    private static void processDailyUpdate() {
        System.out.println("\n=== Processing Daily Update ===");
        System.out.println("Forecasting demand, calculating safety stock, and checking replenishment...\n");
        
        List<Item> inventory = store.getAllItems();
        List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(inventory);
        
        System.out.println("Daily update completed for " + decisions.size() + " items.");
        System.out.println("Items needing reorder: " + 
                decisions.stream().filter(d -> d.needsReorder).count());
    }
    
    private static void showReplenishmentDecisions() {
        System.out.println("\n=== Replenishment Decisions ===");
        List<Item> inventory = store.getAllItems();
        List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(inventory);
        
        for (InventoryManager.ReplenishmentDecision decision : decisions) {
            System.out.println(decision.toDisplayString());
        }
    }
    
    private static void placeOrders() {
        System.out.println("\n=== Placing Orders ===");
        List<Item> inventory = store.getAllItems();
        List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(inventory);
        
        int ordersPlaced = 0;
        for (InventoryManager.ReplenishmentDecision decision : decisions) {
            if (decision.needsReorder && decision.orderQuantity > 0) {
                manager.placeOrder(decision.item, decision.orderQuantity);
                ordersPlaced++;
            }
        }
        
        if (ordersPlaced == 0) {
            System.out.println("No orders needed at this time.");
        } else {
            System.out.println("\n✓ Placed " + ordersPlaced + " orders.");
        }
    }
    
    private static void showLowStockAlerts() {
        System.out.println("\n=== Low Stock Alerts ===");
        List<Item> inventory = store.getAllItems();
        List<String> alerts = InventoryReports.generateLowStockAlerts(inventory);
        
        if (alerts.isEmpty()) {
            System.out.println("No low stock alerts.");
        } else {
            alerts.forEach(System.out::println);
        }
    }
    
    private static void generateWeeklyReport() {
        System.out.println("\n=== Weekly Report ===");
        List<Item> inventory = store.getAllItems();
        List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(inventory);
        String report = InventoryReports.generateWeeklyReport(inventory, decisions);
        System.out.println(report);
    }
    
    private static void generateMonthlyReport() {
        System.out.println("\n=== Monthly Report ===");
        List<Item> inventory = store.getAllItems();
        String report = InventoryReports.generateMonthlyReport(inventory);
        System.out.println(report);
    }
    
    private static void simulateDailyWorkflow() {
        System.out.println("\n=== Simulating Daily Workflow ===");
        System.out.println("This simulates the complete daily update process:\n");
        
        List<Item> inventory = store.getAllItems();
        
        // Step 1: Simulate some random daily sales
        System.out.println("Step 1: Recording daily sales...");
        for (Item item : inventory) {
            // Simulate sales based on daily demand with some variation
            int sales = (int)(item.getDailyDemand() + (Math.random() * 2 - 1));
            sales = Math.max(0, sales);
            manager.recordDailySales(item, sales);
        }
        System.out.println("✓ Sales recorded for all items\n");
        
        // Step 2: Process daily update
        System.out.println("Step 2: Processing daily update (forecast, safety stock, ROP)...");
        List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(inventory);
        System.out.println("✓ Daily update processed\n");
        
        // Step 3: Show results
        System.out.println("Step 3: Replenishment decisions:");
        int reorderCount = 0;
        for (InventoryManager.ReplenishmentDecision decision : decisions) {
            if (decision.needsReorder) {
                System.out.println(decision.toDisplayString());
                reorderCount++;
            }
        }
        if (reorderCount == 0) {
            System.out.println("No items need reordering at this time.");
        }
        System.out.println();
        
        // Step 4: Show alerts
        System.out.println("Step 4: Low stock alerts:");
        List<String> alerts = InventoryReports.generateLowStockAlerts(inventory);
        if (alerts.isEmpty()) {
            System.out.println("No low stock alerts.");
        } else {
            alerts.forEach(System.out::println);
        }
        
        System.out.println("\n✓ Daily workflow simulation completed!");
    }
}
