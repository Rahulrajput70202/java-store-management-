import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StoreManagementGUI extends JFrame {
    private JTextField nameField, priceField, quantityField, searchField;
    private DefaultTableModel tableModel;
    private JTable table;

    public StoreManagementGUI() {
        setTitle("🛒 Store Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setUIFont(new Font("Segoe UI", Font.PLAIN, 14));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Item Details"));

        nameField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();
        searchField = new JTextField();

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Search Item:"));
        panel.add(searchField);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Item Name", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(22);

        // Auto-fill fields on row click
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(tableModel.getValueAt(row, 0).toString());
                priceField.setText(tableModel.getValueAt(row, 1).toString());
                quantityField.setText(tableModel.getValueAt(row, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory"));
        return scrollPane;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = createStyledButton("Add Item");
        JButton updateButton = createStyledButton("Update");
        JButton deleteButton = createStyledButton("Delete");
        JButton searchButton = createStyledButton("Search");
        JButton billButton = createStyledButton("Generate Bill");
        JButton clearButton = createStyledButton("Clear All");

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        searchButton.addActionListener(e -> searchItem());
        billButton.addActionListener(e -> generateBill());
        clearButton.addActionListener(e -> clearAll());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        panel.add(billButton);
        panel.add(clearButton);

        return panel;
    }

    // 🎨 Colorful Buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normal, hover;
        switch (text) {
            case "Add Item":
                normal = new Color(46, 204, 113); // Green
                hover = new Color(39, 174, 96);
                break;
            case "Update":
                normal = new Color(241, 196, 15); // Yellow
                hover = new Color(243, 156, 18);
                break;
            case "Delete":
                normal = new Color(231, 76, 60); // Red
                hover = new Color(192, 57, 43);
                break;
            case "Search":
                normal = new Color(52, 152, 219); // Blue
                hover = new Color(41, 128, 185);
                break;
            case "Generate Bill":
                normal = new Color(155, 89, 182); // Purple
                hover = new Color(142, 68, 173);
                break;
            case "Clear All":
                normal = new Color(127, 140, 141); // Grey
                hover = new Color(99, 110, 114);
                break;
            default:
                normal = new Color(60, 130, 200);
                hover = new Color(30, 100, 180);
        }

        button.setBackground(normal);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });

        return button;
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String qtyText = quantityField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()) {
            showMessage("Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(qtyText);
            if (price <= 0 || quantity <= 0) {
                showMessage("Price and quantity must be positive.");
                return;
            }

            // Add item or update existing
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    int existingQty = (int) tableModel.getValueAt(i, 2);
                    tableModel.setValueAt(existingQty + quantity, i, 2);
                    clearInputs();
                    return;
                }
            }

            tableModel.addRow(new Object[]{name, price, quantity});
            clearInputs();
        } catch (NumberFormatException e) {
            showMessage("Invalid price or quantity.");
        }
    }

    private void updateItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Please select an item to update.");
            return;
        }

        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (price <= 0 || qty <= 0) {
                showMessage("Price and quantity must be positive.");
                return;
            }

            tableModel.setValueAt(name, row, 0);
            tableModel.setValueAt(price, row, 1);
            tableModel.setValueAt(qty, row, 2);
            clearInputs();
        } catch (NumberFormatException e) {
            showMessage("Invalid input.");
        }
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
        } else {
            showMessage("Please select an item to delete.");
        }
    }

    private void searchItem() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            showMessage("Please enter a name to search.");
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String name = tableModel.getValueAt(i, 0).toString().toLowerCase();
            if (name.contains(keyword)) {
                table.setRowSelectionInterval(i, i);
                return;
            }
        }

        showMessage("Item not found.");
    }

    private void generateBill() {
        if (tableModel.getRowCount() == 0) {
            showMessage("Inventory is empty.");
            return;
        }

        double total = 0;
        StringBuilder bill = new StringBuilder("------ BILL ------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String name = tableModel.getValueAt(i, 0).toString();
            double price = (double) tableModel.getValueAt(i, 1);
            int qty = (int) tableModel.getValueAt(i, 2);
            double line = price * qty;
            bill.append(String.format("%s - %d x %.2f = %.2f\n", name, qty, price, line));
            total += line;
        }

        bill.append("------------------\nTotal: ").append(String.format("%.2f", total));
        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Generated Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        clearInputs();
    }

    private void clearInputs() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        searchField.setText("");
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TitledBorder.font", font);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StoreManagementGUI::new);
    }
}
