package frontend;

import backend.ReservationBackend;
import backend.ComputerBackend;
import backend.CustomerBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReservationPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPC,cbCustomer;
    private JSpinner spinDate;
    private final ReservationBackend backend         = new ReservationBackend();
    private final ComputerBackend    computerBackend = new ComputerBackend();
    private final CustomerBackend    customerBackend = new CustomerBackend();

    public ReservationPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel form = UIFactory.createFormPanel("Book Reservation");
        form.setLayout(new FlowLayout(FlowLayout.LEFT,8,6));

        cbPC       = UIFactory.createComboBox();
        cbCustomer = UIFactory.createComboBox();
        loadPCs(); loadCustomers();

        SpinnerDateModel dm = new SpinnerDateModel();
        spinDate = new JSpinner(dm);
        spinDate.setFont(UIConstants.FONT_FIELD);
        spinDate.setEditor(new JSpinner.DateEditor(spinDate,"yyyy-MM-dd HH:mm"));

        JButton btnBook   = UIFactory.createPrimaryButton("Book");
        JButton btnCancel = UIFactory.createDangerButton("Cancel Reservation");
        btnCancel.setPreferredSize(new Dimension(150,32));

        form.add(UIFactory.createFormLabel("PC:"));        form.add(cbPC);
        form.add(UIFactory.createFormLabel("Customer:"));  form.add(cbCustomer);
        form.add(UIFactory.createFormLabel("Date/Time:")); form.add(spinDate);
        form.add(btnBook); form.add(btnCancel);
        add(form,BorderLayout.NORTH);

        String[] cols = {"ID","PC","Customer","Reservation Date","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r,int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table),BorderLayout.CENTER);

        loadReservations();

        btnBook.addActionListener(e -> bookReservation());
        btnCancel.addActionListener(e -> cancelReservation());
    }

    private void loadPCs() {
        cbPC.removeAllItems();
        try {
            ResultSet rs = computerBackend.getAllComputersForCombo();
            if (rs == null) return;
            while (rs.next()) cbPC.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void loadCustomers() {
        cbCustomer.removeAllItems();
        try {
            ResultSet rs = customerBackend.getAllCustomersForCombo();
            if (rs == null) return;
            while (rs.next()) cbCustomer.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = backend.getAllReservations();
            if (rs == null) return;
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getTimestamp(4).toString().substring(0,16),rs.getString(5)
                });
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void bookReservation() {
        String pcStr   = (String) cbPC.getSelectedItem();
        String custStr = (String) cbCustomer.getSelectedItem();
        if (pcStr == null)   { ExceptionHandler.handleValidationError("Please select PC",      this); return; }
        if (custStr == null) { ExceptionHandler.handleValidationError("Please select Customer",this); return; }

        int pcId   = Integer.parseInt(pcStr.split(" - ")[0]);
        int custId = Integer.parseInt(custStr.split(" - ")[0]);
        java.util.Date d = (java.util.Date) spinDate.getValue();

        if (backend.bookReservation(pcId,custId,d)) {
            JOptionPane.showMessageDialog(this,"Reservation book ho gayi!");
            loadReservations();
        }
    }

    private void cancelReservation() {
        int row = table.getSelectedRow();
        if (!ExceptionHandler.validateSelection(row,this)) return;
        if (!ExceptionHandler.confirmDelete(this)) return;
        int id = (int) tableModel.getValueAt(row,0);
        if (backend.cancelReservation(id)) loadReservations();
    }
}
