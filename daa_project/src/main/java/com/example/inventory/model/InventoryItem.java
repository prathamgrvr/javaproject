package com.example.inventory.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryItem {
    private final Product product;
    private int onHand;
    private int onOrder;
    private final List<Integer> dailyDemandHistory; // last N days

    public InventoryItem(Product product, int onHand, int onOrder, List<Integer> dailyDemandHistory) {
        this.product = product;
        this.onHand = onHand;
        this.onOrder = onOrder;
        this.dailyDemandHistory = new ArrayList<>(dailyDemandHistory);
    }

    public Product getProduct() { return product; }
    public int getOnHand() { return onHand; }
    public int getOnOrder() { return onOrder; }
    public List<Integer> getDailyDemandHistory() { return dailyDemandHistory; }

    public int getInventoryPosition() { return onHand + onOrder; }

    public void consume(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
        this.onHand = Math.max(0, this.onHand - quantity);
        this.dailyDemandHistory.add(quantity);
        if (this.dailyDemandHistory.size() > 90) {
            this.dailyDemandHistory.remove(0);
        }
    }

    public void receive(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
        this.onOrder = Math.max(0, this.onOrder - quantity);
        this.onHand += quantity;
    }

    public void placeOrder(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
        this.onOrder += quantity;
    }

    @Override
    public String toString() {
        return String.format("%s | OnHand=%d, OnOrder=%d, Pos=%d, Hist(days)=%d",
                product.getSku() + " - " + product.getName(), onHand, onOrder, getInventoryPosition(), dailyDemandHistory.size());
    }
}

