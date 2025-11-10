package com.example.inventory.store;

import com.example.inventory.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Inventory Store using List<Item> as specified
 */
public class InventoryStore {
    private final List<Item> inventory;
    private final Map<Integer, Item> itemMap; // For efficient lookup by ItemID
    
    public InventoryStore() {
        this.inventory = new ArrayList<>();
        this.itemMap = new HashMap<>();
    }
    
    public void addItem(Item item) {
        inventory.add(item);
        itemMap.put(item.getItemID(), item);
    }
    
    public List<Item> getAllItems() {
        return new ArrayList<>(inventory);
    }
    
    public Optional<Item> getItemByID(int itemID) {
        return Optional.ofNullable(itemMap.get(itemID));
    }
    
    public int getItemCount() {
        return inventory.size();
    }
}



