package frontend;

import backend.CustomerBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPhone, txtEmail, txtSearch;
    private JComboBox<String> cbMembership;
    private int selectedId = -1;
    private final CustomerBackend backend = new CustomerBackend();

    public CustomerPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = UIFactory.createFormPanel("Customer Details");

        txtName  = UIFactory.createTextField(15);
        txtPhone = UIFactory.createTextField(15);
        txtEmail = UIFactory.createTextField(15);
        cbMembership = UIFactory.createComboBox();
        loadMemberships();


        form.add(UIFactory.createFormLabel("Name:"),       UIFactory.gbc(0, 0, 1));
        form.add(txtName,                                  UIFactory.gbc(1, 0, 1));
        form.add(UIFactory.createFormLabel("Phone:"),      UIFactory.gbc(0, 1, 1));

        form.add(txtPhone,                                 UIFactory.gbc(1, 1, 1));
        form.add(UIFactory.createFormLabel("Email:"),      UIFactory.gbc(0, 2, 1));

        form.add(txtEmail,                                 UIFactory.gbc(1, 2, 1));

        form.add(UIFactory.createFormLabel("Membership:"), UIFactory.gbc(0, 3, 1));

        form.add(cbMembership,                             UIFactory.gbc(1, 3, 1));

        JPanel btnRow = UIFactory.createButtonPanel();

        JButton btnAdd    = UIFactory.createPrimaryButton("Add");

        JButton btnUpdate = UIFactory.createSecondaryButton("Update");

        JButton btnDelete = UIFactory.createDangerButton("Delete");

        JButton btnClear  = UIFactory.createNeutralButton("Clear");

        btnRow.add(btnAdd); btnRow.add(btnUpdate); btnRow.add(btnDelete); btnRow.add(btnClear);

        GridBagConstraints btnGbc = UIFactory.gbc(0, 4, 2);
        form.add(btnRow, btnGbc);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.setBackground(UIConstants.PANEL_BG);
        txtSearch = UIFactory.createTextField(20);

        JButton btnSearch = UIFactory.createSecondaryButton("Search");
        JButton btnAll    = UIFactory.createNeutralButton("Show All");

        searchRow.add(UIFactory.createFormLabel("Search:"));
        searchRow.add(txtSearch); searchRow.add(btnSearch); searchRow.add(btnAll);
        form.add(searchRow, UIFactory.gbc(0, 5, 2));

        add(form, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone", "Email", "Membership"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table), BorderLayout.CENTER);

        loadCustomers(null);

        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {

                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtPhone.setText((String) tableModel.getValueAt(row, 2));
                txtEmail.setText((String) tableModel.getValueAt(row, 3));
            }

        });

        btnAdd.addActionListener(e -> addCustomer());

        btnUpdate.addActionListener(e -> updateCustomer());

        btnDelete.addActionListener(e -> deleteCustomer());

        btnClear.addActionListener(e -> clearForm());

        btnSearch.addActionListener(e -> loadCustomers(txtSearch.getText().trim()));

        btnAll.addActionListener(e -> { txtSearch.setText(""); loadCustomers(null); });
    }


    private void loadMemberships() {

        cbMembership.removeAllItems();
        try {
            ResultSet rs = backend.getAllMemberships();
            if (rs == null) return;
            while (rs.next())


                cbMembership.addItem(rs.getString(2) + " (" + rs.getDouble(3) + "%) [ID:" + rs.getInt(1) + "]");
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadCustomers(String keyword) {
        tableModel.setRowCount(0);

        try {

            ResultSet rs = backend.searchCustomers(keyword);

            if (rs == null) return;

            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});

        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private int getMembershipId() {
        String item = (String) cbMembership.getSelectedItem();

        if (item == null) return 1;

        return Integer.parseInt(item.replaceAll(".*\\[ID:(\\d+)\\]", "$1"));
    }

    private void addCustomer() {
        String name  = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if (!ExceptionHandler.validateNotEmpty(name,  "Name",  this)) return;

        if (!ExceptionHandler.validatePhone(phone, this)) return;


        if (!ExceptionHandler.validateEmail(email, this)) return;
        if (backend.addCustomer(name, phone, email, getMembershipId()))
            { loadCustomers(null); clearForm(); }
    }

    private void updateCustomer() {

        if (!ExceptionHandler.validateSelection(selectedId, this)) return;

        String name  = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if (!ExceptionHandler.validateNotEmpty(name,  "Name",  this)) return;

        if (!ExceptionHandler.validatePhone(phone, this)) return;

        if (!ExceptionHandler.validateEmail(email, this)) return;

        if (backend.updateCustomer(selectedId, name, phone, email, getMembershipId()))
            { loadCustomers(null); clearForm(); }
    }

    private void deleteCustomer() {

        if (!ExceptionHandler.validateSelection(selectedId, this)) return;

         if (!ExceptionHandler.confirmDelete(this)) return;

        if (backend.deleteCustomer(selectedId)) { loadCustomers(null); clearForm(); }
    }

    private void clearForm() {
        txtName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        cbMembership.setSelectedIndex(0); selectedId = -1; table.clearSelection();
    }
}
