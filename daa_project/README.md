# Smart Inventory Manager (Java)

A comprehensive inventory management system implementing supply chain optimization algorithms for demand forecasting, lead time optimization, and automated replenishment strategies.

## Features

### Inventory Control Algorithms
- **Demand Forecasting**: Exponential Moving Average (EMA) and Simple Moving Average (SMA)
- **Lead Time Optimization**: Safety Stock calculation with service level
- **Inventory Replenishment**: Economic Order Quantity (EOQ) and Continuous Review (s, Q) policy
- **Automated Replenishment**: Triggers orders when stock reaches reorder points

### System Capabilities
- **50 Items**: Pre-configured with realistic inventory data
- **Daily Sales Updates**: Record and track daily sales
- **Forecast Next Day Demand**: Predict future demand using EMA/SMA
- **Calculate Reorder Point**: Dynamic ROP calculation with safety stock
- **Check Replenishment**: Automatic detection of items needing reorder
- **Alerts and Reports**: Low stock alerts, weekly and monthly reports

## Data Structure

The system uses `List<Item>` where each Item has:
- **ItemID**: Unique identifier (1-50)
- **Name**: Item name
- **CurrentStock**: Units currently available
- **DailyDemand**: Average daily demand
- **LeadTime**: Days required to get new stock
- **ReorderLevel**: Minimum stock before reordering (calculated dynamically)

### Example Data
| ItemID | Name | CurrentStock | DailyDemand | LeadTime | ReorderLevel |
|--------|------|--------------|-------------|----------|--------------|
| 1 | Chocolate Bar | 50 | 5.0 | 7 | 20 |
| 2 | Candy Cane | 30 | 3.0 | 5 | 15 |
| 3 | Lollipop | 80 | 8.0 | 10 | 40 |

## Algorithms Implemented

### 1. Demand Forecasting
**Algorithm**: Exponential Moving Average (EMA) or Simple Moving Average (SMA)

**Formula (EMA)**:
```
Forecast_{t+1} = α · Demand_t + (1-α) · Forecast_t
```
- α = smoothing factor (0 < α < 1, default: 0.4)
- Stores historical daily sales and predicts next day demand

### 2. Lead Time Optimization
**Algorithm**: Safety Stock Calculation

**Formula**:
```
Safety Stock = Z · σ_demand · √LeadTime
```
- Z = service factor (1.65 for 95% service level)
- σ_demand = standard deviation of daily demand

**Reorder Point (ROP)**:
```
ROP = DailyDemand · LeadTime + SafetyStock
```

### 3. Inventory Replenishment Strategy
**Algorithm**: (s, Q) policy or Continuous Review (Q-system)

**Policy**: Reorder when `CurrentStock <= ReorderPoint`

**Order Quantity**: Economic Order Quantity (EOQ)

**EOQ Formula**:
```
Q = √(2 · Demand · OrderingCost / HoldingCost)
```
- Balances ordering cost vs holding cost

## System Design Workflow

1. **Initialize Inventory**: Load 50 items with current stock and daily demand
2. **Daily Sales Update**: Record sales of each item at end of day
3. **Forecast Next Day Demand**: Apply EMA/SMA using historical sales
4. **Calculate Reorder Point**: Use safety stock formula to account for variability
5. **Check Replenishment**: For each item, if `CurrentStock <= ROP`, trigger an order for EOQ quantity
6. **Track Lead Time**: Record expected delivery date and update stock upon arrival
7. **Alerts and Reports**: 
   - Notify when items are low or delayed
   - Generate weekly/monthly reports on stock movement, holding cost, and stockouts

## Requirements

- JDK 17 or higher
- Gradle (optional - can compile with javac)

## How to Run

### GUI Mode (Default - Recommended)
The application launches with a modern graphical user interface by default.

```bash
# Compile
mkdir -p build/classes
javac -d build/classes -sourcepath src/main/java src/main/java/com/example/inventory/*.java src/main/java/com/example/inventory/**/*.java

# Run GUI
java -cp build/classes com.example.inventory.Main
```

### CLI Mode (Command Line Interface)
To run in command-line mode instead:

```bash
java -cp build/classes com.example.inventory.Main --cli
```

### Using Gradle (if installed)
```bash
gradle run  # Launches GUI by default
```

## User Interface

### GUI Features

The application provides a modern, tabbed interface with the following sections:

#### 📊 Dashboard Tab
- **Key Metrics**: Total items, low stock count, items needing reorder, total inventory value
- **Low Stock Alerts**: Real-time alerts for items below reorder level
- **Quick Actions**: Refresh and process daily update buttons

#### 📦 Inventory Tab
- **Searchable Table**: View all 50 items with search functionality
- **Color Coding**: Low stock items highlighted in red
- **Columns**: ID, Name, Current Stock, Daily Demand, Lead Time, Reorder Level, Unit Cost

#### 💰 Record Sales Tab
- **Item Selection**: Dropdown to select any item
- **Sales Entry**: Enter quantity sold
- **Transaction Log**: History of all sales recorded

#### 🔄 Replenishment Tab
- **Decision Table**: Shows forecast, safety stock, ROP, EOQ for each item
- **Status Indicators**: Color-coded reorder status
- **Auto-Replenish**: One-click order placement for all items needing reorder

#### 📈 Reports Tab
- **Weekly Report**: Stock movement, holding costs, top items by stock
- **Monthly Report**: Inventory value, annual holding costs, high-demand items
- **Tabbed View**: Switch between weekly and monthly reports

### CLI Mode Usage

When running with `--cli` flag, you'll see a text menu:

1. **List all items**: View all 50 items with their current status
2. **Record daily sales**: Update inventory with daily sales
3. **Process daily update**: Run forecast and check replenishment
4. **Show replenishment decisions**: View EOQ, ROP, and reorder recommendations
5. **Place orders**: Execute automatic replenishment
6. **View low stock alerts**: See items below reorder level
7. **Generate weekly report**: Stock movement, holding costs, top items
8. **Generate monthly report**: Inventory value, annual holding costs, high-demand items
9. **Simulate daily workflow**: Complete automated daily process

## Configuration

Default configuration in `PolicyConfig.defaultConfig()`:
- **Forecasting Method**: Exponential smoothing (alpha=0.4)
- **SMA Window**: 7 days (if using SMA)
- **Service Level**: z=1.65 (~95% service level)
- **Lead Time**: Average 5 days, Std Dev 1.5 days
- **Ordering Cost**: $25.0 per order

## Benefits

✅ **Reduces stockouts**: Maintains safety stock and optimal reorder points  
✅ **Avoids overstocking**: EOQ optimization minimizes holding costs  
✅ **Automated calculations**: Data-driven demand forecasts and reorder points  
✅ **Cost optimization**: Balances ordering and holding costs  
✅ **Real-time alerts**: Low stock notifications for proactive management  
✅ **Comprehensive reporting**: Weekly and monthly insights

## Project Structure

```
src/main/java/com/example/inventory/
├── Main.java                    # Main application entry point
├── model/
│   └── Item.java               # Item class matching specification
├── core/
│   ├── InventoryManager.java   # Main inventory management logic
│   ├── Forecasting.java        # EMA/SMA forecasting algorithms
│   ├── Policies.java           # EOQ, Safety Stock, ROP calculations
│   └── PolicyConfig.java        # Configuration parameters
├── store/
│   └── InventoryStore.java     # Inventory storage (List<Item>)
├── data/
│   └── ItemDataGenerator.java  # Generates 50 sample items
└── util/
    └── InventoryReports.java   # Reports and alerts generator
```

## Summary of Algorithms

| Task | Algorithm/Method |
|------|------------------|
| Demand Forecasting | EMA / SMA |
| Lead Time Optimization | Safety Stock, Reorder Point (ROP) |
| Replenishment Policy | EOQ, Continuous Review (s, Q) |
| Stock Tracking | List<Item> with HashMap lookup |
