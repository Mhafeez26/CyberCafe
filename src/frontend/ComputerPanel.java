package frontend;

import backend.ComputerBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ComputerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private JComboBox<String> cbCategory, cbStatus;
    private JTextField txtGame;
    private int selectedId = -1;
    private final ComputerBackend backend = new ComputerBackend();

    public ComputerPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIConstants.FONT_SUBTITLE);
        tabs.addTab("Computers", buildComputerTab());
        tabs.addTab("Games",     buildGamesTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildComputerTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIConstants.CONTENT_BG);

        JPanel form = UIFactory.createFormPanel("Computer Details");

        txtName    = UIFactory.createTextField(15);
        cbCategory = UIFactory.createComboBox();
        cbStatus   = UIFactory.createComboBox(new String[]{"Available", "Occupied", "Maintenance"});
        loadCategories();

        form.add(UIFactory.createFormLabel("PC Name:"),  UIFactory.gbc(0, 0, 1));
        form.add(txtName,                                UIFactory.gbc(1, 0, 1));
        form.add(UIFactory.createFormLabel("Category:"), UIFactory.gbc(0, 1, 1));
        form.add(cbCategory,                             UIFactory.gbc(1, 1, 1));
        form.add(UIFactory.createFormLabel("Status:"),   UIFactory.gbc(0, 2, 1));
        form.add(cbStatus,                               UIFactory.gbc(1, 2, 1));

        JPanel btnRow = UIFactory.createButtonPanel();
        JButton btnAdd    = UIFactory.createPrimaryButton("Add");
        JButton btnUpdate = UIFactory.createSecondaryButton("Update");
        JButton btnDelete = UIFactory.createDangerButton("Delete");
        JButton btnClear  = UIFactory.createNeutralButton("Clear");
        btnRow.add(btnAdd); btnRow.add(btnUpdate); btnRow.add(btnDelete); btnRow.add(btnClear);
        form.add(btnRow, UIFactory.gbc(0, 3, 2));

        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Category", "Hourly Rate", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        panel.add(UIFactory.createTableScrollPane(table), BorderLayout.CENTER);

        loadComputers();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 4));
            }
        });

        btnAdd.addActionListener(e -> addComputer());
        btnUpdate.addActionListener(e -> updateComputer());
        btnDelete.addActionListener(e -> deleteComputer());
        btnClear.addActionListener(e -> clearForm());
        return panel;
    }

    private JPanel buildGamesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIConstants.CONTENT_BG);

        JPanel form = UIFactory.createFormPanel("Add Game");
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));

        txtGame = UIFactory.createTextField(15);
        JComboBox<String> cbPC = UIFactory.createComboBox();
        loadPCsIntoCombo(cbPC);
        JButton btnAdd = UIFactory.createPrimaryButton("Add Game");
        JButton btnDel = UIFactory.createDangerButton("Delete Game");

        form.add(UIFactory.createFormLabel("Game Name:")); form.add(txtGame);
        form.add(UIFactory.createFormLabel("PC:"));        form.add(cbPC);
        form.add(btnAdd); form.add(btnDel);
        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"ID", "Game Name", "PC"};
        DefaultTableModel gModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable gTable = UIFactory.createTable(gModel);
        panel.add(UIFactory.createTableScrollPane(gTable), BorderLayout.CENTER);
        loadGames(gModel);

        btnAdd.addActionListener(e -> {
            String name   = txtGame.getText().trim();
            String pcStr  = (String) cbPC.getSelectedItem();
            if (!ExceptionHandler.validateNotEmpty(name, "Game Name", this)) return;
            if (pcStr == null) { ExceptionHandler.handleValidationError("PC select karein.", this); return; }
            int pcId = Integer.parseInt(pcStr.split(" - ")[0]);
            if (backend.addGame(name, pcId)) { txtGame.setText(""); loadGames(gModel); }
        });

        btnDel.addActionListener(e -> {
            int row = gTable.getSelectedRow();
            if (!ExceptionHandler.validateSelection(row, this)) return;
            int id = (int) gModel.getValueAt(row, 0);
            if (backend.deleteGame(id)) loadGames(gModel);
        });

        return panel;
    }

    private void loadCategories() {
        cbCategory.removeAllItems();
        try {
            ResultSet rs = backend.getAllCategories();
            if (rs == null) return;
            while (rs.next())
                cbCategory.addItem(rs.getString(2) + " - PKR " + rs.getDouble(3) + " [ID:" + rs.getInt(1) + "]");
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadComputers() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = backend.getAllComputers();
            if (rs == null) return;
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getString(5)});
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadGames(DefaultTableModel m) {
        m.setRowCount(0);
        try {
            ResultSet rs = backend.getAllGames();
            if (rs == null) return;
            while (rs.next()) m.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadPCsIntoCombo(JComboBox<String> cb) {
        cb.removeAllItems();
        try {
            ResultSet rs = backend.getAllComputersForCombo();
            if (rs == null) return;
            while (rs.next()) cb.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private int getCategoryId() {
        String item = (String) cbCategory.getSelectedItem();
        if (item == null) return -1;
        return Integer.parseInt(item.replaceAll(".*\\[ID:(\\d+)\\]", "$1"));
    }

    private void addComputer() {
        String name = txtName.getText().trim();
        if (!ExceptionHandler.validateNotEmpty(name, "PC Name", this)) return;
        if (backend.addComputer(name, getCategoryId(), (String) cbStatus.getSelectedItem()))
            { loadComputers(); clearForm(); }
    }

    private void updateComputer() {
        if (!ExceptionHandler.validateSelection(selectedId, this)) return;
        String name = txtName.getText().trim();
        if (!ExceptionHandler.validateNotEmpty(name, "PC Name", this)) return;
        if (backend.updateComputer(selectedId, name, getCategoryId(), (String) cbStatus.getSelectedItem()))
            { loadComputers(); clearForm(); }
    }

    private void deleteComputer() {
        if (!ExceptionHandler.validateSelection(selectedId, this)) return;
        if (!ExceptionHandler.confirmDelete(this)) return;
        if (backend.deleteComputer(selectedId)) { loadComputers(); clearForm(); }
    }

    private void clearForm() {
        txtName.setText(""); cbStatus.setSelectedIndex(0); selectedId = -1; table.clearSelection();
    }
}
