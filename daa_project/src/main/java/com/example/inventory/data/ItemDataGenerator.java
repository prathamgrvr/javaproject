package com.example.inventory.data;

import com.example.inventory.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates 50 sample items for the inventory store
 */
public class ItemDataGenerator {
    
    // Item names for a retail/candy store
    private static final String[] ITEM_NAMES = {
        "Chocolate Bar", "Candy Cane", "Lollipop", "Gummy Bears", "Jelly Beans",
        "Marshmallow", "Hard Candy", "Chocolate Chip Cookie", "Caramel", "Toffee",
        "Peppermint", "Licorice", "Gum Drops", "Rock Candy", "Fudge",
        "Taffy", "Sour Patch Kids", "Skittles", "M&Ms", "Reese's Pieces",
        "Hershey's Kisses", "Twix", "Snickers", "Kit Kat", "Milky Way",
        "Butterfinger", "Baby Ruth", "Almond Joy", "3 Musketeers", "PayDay",
        "Crunch Bar", "Mounds", "Dove Chocolate", "Ghirardelli Squares", "Lindt Truffle",
        "Toblerone", "Ferrero Rocher", "Godiva Chocolate", "See's Candies", "Russell Stover",
        "Jelly Belly", "Starburst", "Life Savers", "Werther's Original", "Ricola",
        "Halls", "Cough Drop", "Menthol Candy", "Throat Lozenges", "Vitamin C Drops"
    };
    
    public static List<Item> generate50Items() {
        List<Item> items = new ArrayList<>();
        
        // First 3 items match the specification exactly
        items.add(new Item(1, "Chocolate Bar", 50, 5.0, 7, 20, 2.50, 25.0, 0.20));
        items.add(new Item(2, "Candy Cane", 30, 3.0, 5, 15, 1.50, 25.0, 0.20));
        items.add(new Item(3, "Lollipop", 80, 8.0, 10, 40, 0.75, 25.0, 0.20));
        
        // Generate remaining 47 items with varied parameters
        for (int i = 4; i <= 50; i++) {
            String name = ITEM_NAMES[(i - 1) % ITEM_NAMES.length];
            
            // Vary the parameters realistically
            int currentStock = 20 + (int)(Math.random() * 100); // 20-120 units
            double dailyDemand = 2.0 + Math.random() * 10.0; // 2-12 units/day
            int leadTime = 3 + (int)(Math.random() * 10); // 3-13 days
            int reorderLevel = (int)(dailyDemand * leadTime * 1.2); // ~20% buffer
            
            double unitCost = 0.50 + Math.random() * 5.0; // $0.50 - $5.50
            double orderingCost = 20.0 + Math.random() * 10.0; // $20 - $30
            double holdingCostRate = 0.15 + Math.random() * 0.15; // 15% - 30%
            
            items.add(new Item(i, name, currentStock, dailyDemand, leadTime, 
                             reorderLevel, unitCost, orderingCost, holdingCostRate));
        }
        
        return items;
    }
}



