package com.example.inventory.model;

import java.util.Objects;

public class Product {
    private final String sku;
    private final String name;
    private final double unitCost;
    private final double annualHoldingRate;
    private final int workingDaysPerYear;

    public Product(String sku, String name, double unitCost, double annualHoldingRate, int workingDaysPerYear) {
        this.sku = sku;
        this.name = name;
        this.unitCost = unitCost;
        this.annualHoldingRate = annualHoldingRate;
        this.workingDaysPerYear = workingDaysPerYear;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public double getUnitCost() { return unitCost; }
    public double getAnnualHoldingRate() { return annualHoldingRate; }
    public int getWorkingDaysPerYear() { return workingDaysPerYear; }

    public double dailyHoldingCost() {
        return (unitCost * annualHoldingRate) / workingDaysPerYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() { return Objects.hash(sku); }

    @Override
    public String toString() {
        return "Product{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", unitCost=" + unitCost +
                ", holdingRate=" + annualHoldingRate +
                '}';
    }
}

