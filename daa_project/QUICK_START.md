# Quick Start Guide - Smart Inventory Manager GUI

## Running the Application

### Step 1: Compile
```powershell
# Create output directory
mkdir -Force build\classes

# Compile all Java files
$files = Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName }
javac -d build\classes -sourcepath src\main\java $files
```

### Step 2: Run GUI
```powershell
java -cp build\classes com.example.inventory.Main
```

The GUI window will open automatically!

## GUI Overview

### 📊 Dashboard Tab
- **At a Glance**: See key metrics immediately
  - Total items in inventory
  - Number of low stock items
  - Items needing reorder
  - Total inventory value
- **Alerts**: View all low stock alerts in real-time
- **Quick Actions**: Process daily updates with one click

### 📦 Inventory Tab
- **Browse All Items**: See all 50 items in a searchable table
- **Search**: Type in the search box to filter by name or ID
- **Color Coding**: 
  - Red background = Low stock (below reorder level)
  - White background = Normal stock levels
- **Columns**: ID, Name, Stock, Daily Demand, Lead Time, Reorder Level, Unit Cost

### 💰 Record Sales Tab
1. **Select Item**: Choose from dropdown (all 50 items)
2. **Enter Quantity**: Type the number of units sold
3. **Click "Record Sale"**: Updates inventory automatically
4. **View Log**: See transaction history below

### 🔄 Replenishment Tab
- **View Decisions**: See forecast, safety stock, ROP, and EOQ for each item
- **Status Column**: 
  - "OK" = No reorder needed
  - "REORDER" = Needs immediate reorder (highlighted in orange)
- **Auto-Replenish**: Click "Place Orders" to automatically place orders for all items needing reorder

### 📈 Reports Tab
- **Weekly Report**: 
  - Stock movement summary
  - Holding costs
  - Top 10 items by stock
  - Click "Generate Weekly Report" button
- **Monthly Report**:
  - Total inventory value
  - Annual holding costs
  - Top 10 items by demand
  - Click "Generate Monthly Report" button

## Menu Options

### File Menu
- **Exit**: Close the application

### Actions Menu
- **Process Daily Update**: Run forecasting and replenishment checks for all items
- **Simulate Daily Workflow**: Automatically simulate sales and process updates

### View Menu
- **Refresh All**: Refresh all tabs with latest data

## Tips

1. **Start with Dashboard**: Get an overview of your inventory status
2. **Record Sales Regularly**: Use the Record Sales tab to update inventory after each day
3. **Check Replenishment**: Visit the Replenishment tab to see what needs ordering
4. **Generate Reports**: Use Reports tab for weekly/monthly analysis
5. **Use Search**: In Inventory tab, quickly find items by typing their name or ID

## Keyboard Shortcuts

- **Tab**: Navigate between fields
- **Enter**: Submit forms (Record Sales, Search)
- **Escape**: Close dialogs

## Troubleshooting

**GUI doesn't open?**
- Make sure Java is installed: `java -version`
- Check compilation was successful
- Try CLI mode: `java -cp build\classes com.example.inventory.Main --cli`

**Can't find items?**
- Use the search box in Inventory tab
- Check that items are loaded (should see 50 items)

**Reports not showing?**
- Click the "Generate" button for the report you want
- Reports are generated on-demand

