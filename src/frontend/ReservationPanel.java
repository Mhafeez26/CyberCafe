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
import java.util.Date;

public class ReservationPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPC, cbCustomer;
    private JSpinner spinStart, spinEnd;
    private final ReservationBackend backend         = new ReservationBackend();
    private final ComputerBackend    computerBackend = new ComputerBackend();
    private final CustomerBackend    customerBackend = new CustomerBackend();

    public ReservationPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topArea = new JPanel(new BorderLayout(0, 4));
        topArea.setBackground(UIConstants.CONTENT_BG);

        JPanel form = UIFactory.createFormPanel("Book Reservation");
        form.setLayout(new GridBagLayout());

        cbPC       = UIFactory.createComboBox();
        cbCustomer = UIFactory.createComboBox();
        cbPC.setPreferredSize(new Dimension(130, 28));
        cbCustomer.setPreferredSize(new Dimension(150, 28));
        loadPCs();
        loadCustomers();

        SpinnerDateModel startModel = new SpinnerDateModel();
        spinStart = new JSpinner(startModel);
        spinStart.setFont(UIConstants.FONT_FIELD);
        spinStart.setEditor(new JSpinner.DateEditor(spinStart, "yyyy-MM-dd HH:mm"));
        spinStart.setPreferredSize(new Dimension(145, 28));

        SpinnerDateModel endModel = new SpinnerDateModel();
        spinEnd = new JSpinner(endModel);
        spinEnd.setFont(UIConstants.FONT_FIELD);
        spinEnd.setEditor(new JSpinner.DateEditor(spinEnd, "yyyy-MM-dd HH:mm"));
        spinEnd.setPreferredSize(new Dimension(145, 28));
        spinEnd.setValue(new Date(System.currentTimeMillis() + 3600_000L));

        JButton btnBook   = UIFactory.createPrimaryButton("Book");
        JButton btnCancel = UIFactory.createDangerButton("Cancel Reservation");

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.NONE;




        g.gridy = 0;
        g.gridx = 0; form.add(UIFactory.createFormLabel("PC:"), g);
        g.gridx = 1; form.add(cbPC, g);
        g.gridx = 2; form.add(UIFactory.createFormLabel("Customer:"), g);
        g.gridx = 3; form.add(cbCustomer, g);
        g.gridx = 4; form.add(UIFactory.createFormLabel("Start Time:"), g);
        g.gridx = 5; form.add(spinStart, g);
        g.gridx = 6; form.add(UIFactory.createFormLabel("End Time:"), g);
        g.gridx = 7; form.add(spinEnd, g);



        g.gridy  = 1;

        g.gridx  = 0;
        g.gridwidth = 2;
        form.add(btnBook, g);
        g.gridx  = 2;
        g.gridwidth = 3;
        form.add(btnCancel, g);

        topArea.add(form, BorderLayout.CENTER);

        JLabel surchargeNote = UIFactory.createFormLabel(
                "  * Reservation surcharge of PKR " + (int) ReservationBackend.RESERVATION_SURCHARGE +
                        " will be added to the bill.");
        surchargeNote.setForeground(new Color(160, 80, 0));
        topArea.add(surchargeNote, BorderLayout.SOUTH);

        add(topArea, BorderLayout.NORTH);

        // ── CENTER: table ──
        String[] cols = {"ID", "PC", "Customer", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table), BorderLayout.CENTER);

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
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadCustomers() {
        cbCustomer.removeAllItems();
        try {
            ResultSet rs = customerBackend.getAllCustomersForCombo();
            if (rs == null) return;
            while (rs.next()) cbCustomer.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = backend.getAllReservations();
            if (rs == null) return;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toString().substring(0, 16),
                        rs.getTimestamp(5).toString().substring(0, 16),
                        rs.getString(6)
                });
            }
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e, this); }
    }

    private void bookReservation() {
        String pcStr   = (String) cbPC.getSelectedItem();
        String custStr = (String) cbCustomer.getSelectedItem();
        if (pcStr == null)   { ExceptionHandler.handleValidationError("Please select a PC.",       this); return; }
        if (custStr == null) { ExceptionHandler.handleValidationError("Please select a Customer.", this); return; }

        Date startDate = (Date) spinStart.getValue();
        Date endDate   = (Date) spinEnd.getValue();

        if (!endDate.after(startDate)) {
            ExceptionHandler.handleValidationError("End time must be after start time.", this);
            return;
        }

        int pcId   = Integer.parseInt(pcStr.split(" - ")[0]);
        int custId = Integer.parseInt(custStr.split(" - ")[0]);

        if (backend.bookReservation(pcId, custId, (java.sql.Date) startDate, (java.sql.Date) endDate)) {
            JOptionPane.showMessageDialog(this,
                    "Reservation booked successfully!\nSurcharge of PKR " +
                            (int) ReservationBackend.RESERVATION_SURCHARGE + " will be added at billing.");
            loadReservations();
        }
    }

    private void cancelReservation() {
        int row = table.getSelectedRow();
        if (!ExceptionHandler.validateSelection(row, this)) return;
        if (!ExceptionHandler.confirmDelete(this)) return;
        int id = (int) tableModel.getValueAt(row, 0);
        if (backend.cancelReservation(id)) loadReservations();
    }
}