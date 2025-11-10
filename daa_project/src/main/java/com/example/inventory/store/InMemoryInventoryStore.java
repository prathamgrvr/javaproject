package com.example.inventory.store;

import com.example.inventory.model.InventoryItem;

import java.util.*;

public class InMemoryInventoryStore {
    private final Map<String, InventoryItem> skuToItem = new HashMap<>();

    public Optional<InventoryItem> getBySku(String sku) {
        return Optional.ofNullable(skuToItem.get(sku));
    }

    public void upsertItem(InventoryItem item) {
        skuToItem.put(item.getProduct().getSku(), item);
    }

    public List<InventoryItem> listItems() {
        return new ArrayList<>(skuToItem.values());
    }
}

