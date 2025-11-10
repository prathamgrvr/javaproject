package com.example.inventory.gui;

import com.example.inventory.core.InventoryManager;
import com.example.inventory.core.PolicyConfig;
import com.example.inventory.data.ItemDataGenerator;
import com.example.inventory.model.Item;
import com.example.inventory.store.InventoryStore;
import com.example.inventory.util.InventoryReports;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Main GUI Window for Smart Inventory Manager
 */
public class MainWindow extends JFrame {
    private InventoryStore store;
    private InventoryManager manager;
    private PolicyConfig config;
    
    // Components
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private InventoryTablePanel inventoryTablePanel;
    private SalesRecordingPanel salesRecordingPanel;
    private ReplenishmentPanel replenishmentPanel;
    private ReportsPanel reportsPanel;
    
    public MainWindow() {
        initializeSystem();
        initializeGUI();
    }
    
    private void initializeSystem() {
        config = PolicyConfig.defaultConfig();
        manager = new InventoryManager(config);
        store = new InventoryStore();
        
        // Load 50 items
        List<Item> items = ItemDataGenerator.generate50Items();
        for (Item item : items) {
            store.addItem(item);
        }
    }
    
    private void initializeGUI() {
        setTitle("Smart Inventory Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create menu bar
        createMenuBar();
        
        // Create main content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Create panels
        dashboardPanel = new DashboardPanel();
        inventoryTablePanel = new InventoryTablePanel();
        salesRecordingPanel = new SalesRecordingPanel();
        replenishmentPanel = new ReplenishmentPanel();
        reportsPanel = new ReportsPanel();
        
        // Add tabs
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
        tabbedPane.addTab("📦 Inventory", inventoryTablePanel);
        tabbedPane.addTab("💰 Record Sales", salesRecordingPanel);
        tabbedPane.addTab("🔄 Replenishment", replenishmentPanel);
        tabbedPane.addTab("📈 Reports", reportsPanel);
        
        add(tabbedPane);
        
        // Refresh all panels
        refreshAllPanels();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Actions Menu
        JMenu actionsMenu = new JMenu("Actions");
        JMenuItem processDailyItem = new JMenuItem("Process Daily Update");
        processDailyItem.addActionListener(e -> processDailyUpdate());
        JMenuItem simulateWorkflowItem = new JMenuItem("Simulate Daily Workflow");
        simulateWorkflowItem.addActionListener(e -> simulateDailyWorkflow());
        actionsMenu.add(processDailyItem);
        actionsMenu.add(simulateWorkflowItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.addActionListener(e -> refreshAllPanels());
        viewMenu.add(refreshItem);
        
        menuBar.add(fileMenu);
        menuBar.add(actionsMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void processDailyUpdate() {
        SwingUtilities.invokeLater(() -> {
            manager.processDailyUpdate(store.getAllItems());
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, 
                "Daily update processed successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void simulateDailyWorkflow() {
        SwingUtilities.invokeLater(() -> {
            List<Item> inventory = store.getAllItems();
            
            // Simulate sales
            for (Item item : inventory) {
                int sales = (int)(item.getDailyDemand() + (Math.random() * 2 - 1));
                sales = Math.max(0, sales);
                manager.recordDailySales(item, sales);
            }
            
            // Process update
            manager.processDailyUpdate(inventory);
            
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, 
                "Daily workflow simulation completed!", 
                "Simulation Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void refreshAllPanels() {
        dashboardPanel.refresh();
        inventoryTablePanel.refresh();
        salesRecordingPanel.refresh();
        replenishmentPanel.refresh();
        reportsPanel.refresh();
    }
    
    // Inner classes for panels
    class DashboardPanel extends JPanel {
        private JLabel totalItemsValue, lowStockValue, reorderNeededValue, totalValueValue;
        private JTextArea alertsArea;
        
        public DashboardPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Top metrics panel
            JPanel metricsPanel = createMetricsPanel();
            add(metricsPanel, BorderLayout.NORTH);
            
            // Alerts panel
            JPanel alertsPanel = createAlertsPanel();
            add(alertsPanel, BorderLayout.CENTER);
            
            // Bottom action buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton refreshBtn = new JButton("Refresh");
            refreshBtn.addActionListener(e -> refresh());
            JButton processBtn = new JButton("Process Daily Update");
            processBtn.addActionListener(e -> processDailyUpdate());
            buttonPanel.add(refreshBtn);
            buttonPanel.add(processBtn);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private JPanel createMetricsPanel() {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Key Metrics"));
            
            totalItemsValue = createMetricCard(panel, "Total Items", "0");
            lowStockValue = createMetricCard(panel, "Low Stock Items", "0");
            reorderNeededValue = createMetricCard(panel, "Items Needing Reorder", "0");
            totalValueValue = createMetricCard(panel, "Total Inventory Value", "$0.00");
            
            return panel;
        }
        
        private JLabel createMetricCard(JPanel parent, String title, String value) {
            JPanel container = new JPanel(new BorderLayout());
            container.setBorder(BorderFactory.createRaisedBevelBorder());
            container.setBackground(new Color(240, 248, 255));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            titleLabel.setForeground(Color.GRAY);
            
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            valueLabel.setForeground(new Color(0, 100, 200));
            
            container.add(titleLabel, BorderLayout.NORTH);
            container.add(valueLabel, BorderLayout.CENTER);
            container.setPreferredSize(new Dimension(200, 80));
            
            parent.add(container);
            return valueLabel;
        }
        
        private JPanel createAlertsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Low Stock Alerts"));
            
            alertsArea = new JTextArea();
            alertsArea.setEditable(false);
            alertsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            alertsArea.setBackground(new Color(255, 250, 250));
            
            JScrollPane scrollPane = new JScrollPane(alertsArea);
            scrollPane.setPreferredSize(new Dimension(0, 300));
            panel.add(scrollPane);
            
            return panel;
        }
        
        public void refresh() {
            List<Item> items = store.getAllItems();
            List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(items);
            
            // Update metrics
            totalItemsValue.setText(String.valueOf(items.size()));
            
            long lowStockCount = items.stream()
                .filter(item -> item.getCurrentStock() <= item.getReorderLevel())
                .count();
            lowStockValue.setText(String.valueOf(lowStockCount));
            
            long reorderCount = decisions.stream()
                .filter(d -> d.needsReorder)
                .count();
            reorderNeededValue.setText(String.valueOf(reorderCount));
            
            double totalValue = items.stream()
                .mapToDouble(item -> item.getCurrentStock() * item.getUnitCost())
                .sum();
            totalValueValue.setText("$" + new DecimalFormat("#,##0.00").format(totalValue));
            
            // Update alerts
            List<String> alerts = InventoryReports.generateLowStockAlerts(items);
            alertsArea.setText(alerts.isEmpty() ? "No low stock alerts." : String.join("\n", alerts));
        }
    }
    
    class InventoryTablePanel extends JPanel {
        private JTable table;
        private DefaultTableModel tableModel;
        private JTextField searchField;
        
        public InventoryTablePanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            searchPanel.add(new JLabel("Search:"));
            searchField = new JTextField(20);
            searchField.addActionListener(e -> filterTable());
            JButton searchBtn = new JButton("Search");
            searchBtn.addActionListener(e -> filterTable());
            searchPanel.add(searchField);
            searchPanel.add(searchBtn);
            add(searchPanel, BorderLayout.NORTH);
            
            // Table
            String[] columns = {"ID", "Name", "Current Stock", "Daily Demand", "Lead Time", "Reorder Level", "Unit Cost"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(tableModel);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(70, 130, 180));
            table.getTableHeader().setForeground(Color.WHITE);
            
            // Color code rows
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        Item item = store.getAllItems().get(row);
                        if (item.getCurrentStock() <= item.getReorderLevel()) {
                            c.setBackground(new Color(255, 200, 200));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                    return c;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
            
            // Bottom info
            JLabel infoLabel = new JLabel("Total items: " + store.getItemCount());
            add(infoLabel, BorderLayout.SOUTH);
        }
        
        private void filterTable() {
            String searchText = searchField.getText().toLowerCase();
            tableModel.setRowCount(0);
            
            store.getAllItems().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchText) || 
                               String.valueOf(item.getItemID()).contains(searchText))
                .forEach(item -> {
                    Object[] row = {
                        item.getItemID(),
                        item.getName(),
                        item.getCurrentStock(),
                        String.format("%.2f", item.getDailyDemand()),
                        item.getLeadTime(),
                        item.getReorderLevel(),
                        String.format("$%.2f", item.getUnitCost())
                    };
                    tableModel.addRow(row);
                });
        }
        
        public void refresh() {
            tableModel.setRowCount(0);
            store.getAllItems().forEach(item -> {
                Object[] row = {
                    item.getItemID(),
                    item.getName(),
                    item.getCurrentStock(),
                    String.format("%.2f", item.getDailyDemand()),
                    item.getLeadTime(),
                    item.getReorderLevel(),
                    String.format("$%.2f", item.getUnitCost())
                };
                tableModel.addRow(row);
            });
        }
    }
    
    class SalesRecordingPanel extends JPanel {
        private JComboBox<Item> itemCombo;
        private JTextField quantityField;
        private JButton recordBtn;
        private JTextArea logArea;
        
        public SalesRecordingPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Select Item:"), gbc);
            gbc.gridx = 1;
            itemCombo = new JComboBox<>();
            itemCombo.setPreferredSize(new Dimension(300, 30));
            formPanel.add(itemCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Quantity Sold:"), gbc);
            gbc.gridx = 1;
            quantityField = new JTextField(10);
            formPanel.add(quantityField, gbc);
            
            gbc.gridx = 1; gbc.gridy = 2;
            recordBtn = new JButton("Record Sale");
            recordBtn.setPreferredSize(new Dimension(150, 35));
            recordBtn.addActionListener(e -> recordSale());
            formPanel.add(recordBtn, gbc);
            
            add(formPanel, BorderLayout.NORTH);
            
            // Log area
            logArea = new JTextArea();
            logArea.setEditable(false);
            logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(logArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
            add(scrollPane, BorderLayout.CENTER);
        }
        
        private void recordSale() {
            Item item = (Item) itemCombo.getSelectedItem();
            if (item == null) {
                JOptionPane.showMessageDialog(this, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 0) {
                    throw new NumberFormatException();
                }
                
                int oldStock = item.getCurrentStock();
                manager.recordDailySales(item, quantity);
                int newStock = item.getCurrentStock();
                
                logArea.append(String.format("[%s] Recorded %d units sold for %s (ID=%d). Stock: %d -> %d\n",
                    java.time.LocalDateTime.now().toString().substring(0, 19),
                    quantity, item.getName(), item.getItemID(), oldStock, newStock));
                
                quantityField.setText("");
                refreshAllPanels();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public void refresh() {
            itemCombo.removeAllItems();
            store.getAllItems().forEach(itemCombo::addItem);
        }
    }
    
    class ReplenishmentPanel extends JPanel {
        private JTable table;
        private DefaultTableModel tableModel;
        private JButton processBtn, placeOrdersBtn;
        
        public ReplenishmentPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            processBtn = new JButton("Process Daily Update");
            processBtn.addActionListener(e -> refresh());
            placeOrdersBtn = new JButton("Place Orders (Auto-Replenish)");
            placeOrdersBtn.addActionListener(e -> placeOrders());
            buttonPanel.add(processBtn);
            buttonPanel.add(placeOrdersBtn);
            add(buttonPanel, BorderLayout.NORTH);
            
            // Table
            String[] columns = {"ID", "Name", "Stock", "Forecast/day", "Safety Stock", "ROP", "EOQ", "Status", "Order Qty"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(tableModel);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(70, 130, 180));
            table.getTableHeader().setForeground(Color.WHITE);
            
            // Color code reorder rows
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected && tableModel.getRowCount() > row) {
                        String status = (String) tableModel.getValueAt(row, 7);
                        if ("REORDER".equals(status)) {
                            c.setBackground(new Color(255, 220, 200));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                    return c;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
        }
        
        private void placeOrders() {
            List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(store.getAllItems());
            int ordersPlaced = 0;
            
            for (InventoryManager.ReplenishmentDecision decision : decisions) {
                if (decision.needsReorder && decision.orderQuantity > 0) {
                    manager.placeOrder(decision.item, decision.orderQuantity);
                    ordersPlaced++;
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                ordersPlaced > 0 ? "Placed " + ordersPlaced + " orders." : "No orders needed.",
                "Orders", 
                JOptionPane.INFORMATION_MESSAGE);
            
            refresh();
            refreshAllPanels();
        }
        
        public void refresh() {
            tableModel.setRowCount(0);
            List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(store.getAllItems());
            
            for (InventoryManager.ReplenishmentDecision decision : decisions) {
                Object[] row = {
                    decision.item.getItemID(),
                    decision.item.getName(),
                    decision.item.getCurrentStock(),
                    String.format("%.2f", decision.forecastedDemand),
                    decision.safetyStock,
                    decision.reorderPoint,
                    decision.orderQuantity,
                    decision.needsReorder ? "REORDER" : "OK",
                    decision.needsReorder ? decision.orderQuantity : 0
                };
                tableModel.addRow(row);
            }
        }
    }
    
    class ReportsPanel extends JPanel {
        private JTextArea weeklyReportArea, monthlyReportArea;
        private JButton weeklyBtn, monthlyBtn;
        
        public ReportsPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            weeklyBtn = new JButton("Generate Weekly Report");
            weeklyBtn.addActionListener(e -> generateWeeklyReport());
            monthlyBtn = new JButton("Generate Monthly Report");
            monthlyBtn.addActionListener(e -> generateMonthlyReport());
            buttonPanel.add(weeklyBtn);
            buttonPanel.add(monthlyBtn);
            add(buttonPanel, BorderLayout.NORTH);
            
            // Tabbed reports
            JTabbedPane reportTabs = new JTabbedPane();
            
            weeklyReportArea = new JTextArea();
            weeklyReportArea.setEditable(false);
            weeklyReportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            reportTabs.addTab("Weekly Report", new JScrollPane(weeklyReportArea));
            
            monthlyReportArea = new JTextArea();
            monthlyReportArea.setEditable(false);
            monthlyReportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            reportTabs.addTab("Monthly Report", new JScrollPane(monthlyReportArea));
            
            add(reportTabs, BorderLayout.CENTER);
        }
        
        private void generateWeeklyReport() {
            List<Item> items = store.getAllItems();
            List<InventoryManager.ReplenishmentDecision> decisions = manager.processDailyUpdate(items);
            String report = InventoryReports.generateWeeklyReport(items, decisions);
            weeklyReportArea.setText(report);
        }
        
        private void generateMonthlyReport() {
            List<Item> items = store.getAllItems();
            String report = InventoryReports.generateMonthlyReport(items);
            monthlyReportArea.setText(report);
        }
        
        public void refresh() {
            generateWeeklyReport();
            generateMonthlyReport();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

