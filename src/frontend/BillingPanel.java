package frontend;

import backend.BillingBackend;
import backend.ReservationBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea receiptArea;
    private final BillingBackend      backend             = new BillingBackend();
    private final ReservationBackend  reservationBackend  = new ReservationBackend();

    public BillingPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = UIFactory.createTitleLabel("Billing - End Sessions");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(title, BorderLayout.NORTH);



        String[] cols = {"Session ID", "PC", "Customer", "Start Time", "Hrs", "Rate/hr",
                "Subtotal", "Disc%", "Tax%", "Surcharge", "Total"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setBackground(UIConstants.CONTENT_BG);

        receiptArea = new JTextArea(8, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setBackground(UIConstants.PANEL_BG);
        receiptArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        JScrollPane receiptScroll = new JScrollPane(receiptArea);
        receiptScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.PRIMARY, 1),
                "Receipt Preview",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                UIConstants.FONT_SUBTITLE, UIConstants.PRIMARY
        ));
        south.add(receiptScroll, BorderLayout.CENTER);

        JPanel btnRow = UIFactory.createButtonPanel();
        JButton btnLoad  = UIFactory.createSecondaryButton("Load Active Sessions");
        JButton btnEnd   = UIFactory.createPrimaryButton("End Session & Bill");
        JButton btnPrint = UIFactory.createNeutralButton("Print Receipt");
        btnRow.add(btnLoad);
        btnRow.add(btnEnd);
        btnRow.add(btnPrint);
        south.add(btnRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadActiveSessions());
        btnEnd.addActionListener(e -> endSession());
        btnPrint.addActionListener(e -> printReceipt());

        loadActiveSessions();
    }

    private void loadActiveSessions() {
        tableModel.setRowCount(0);
        double tax = backend.getTaxPercent();
        try {
            ResultSet rs = backend.getActiveSessionsForBilling();
            if (rs == null) return;
            while (rs.next()) {
                int    id       = rs.getInt(1);
                String pc       = rs.getString(2);
                String cust     = rs.getString(3);
                Timestamp start = rs.getTimestamp(4);
                double rate     = rs.getDouble(5);
                double disc     = rs.getDouble(6);
                int    pcId     = rs.getInt(7);
                int    custId   = rs.getInt(8);

                double surcharge = (custId != 0 && reservationBackend.hasReservationSurcharge(pcId, custId))
                        ? ReservationBackend.RESERVATION_SURCHARGE : 0.0;

                double[] bill = backend.calculateBill(rate, start.getTime(), disc, tax, surcharge);

                tableModel.addRow(new Object[]{
                        id, pc, cust,
                        start.toString().substring(0, 16),
                        String.format("%.2f", bill[0]),
                        rate,
                        String.format("%.2f", bill[1]),
                        disc,
                        tax,
                        String.format("%.2f", surcharge),
                        String.format("%.2f", bill[5])
                });
            }
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, this);
        }
    }

    private void endSession() {
        int row = table.getSelectedRow();
        if (!ExceptionHandler.validateSelection(row, this)) return;

        int    sessionId = (int)    tableModel.getValueAt(row, 0);
        double total     = Double.parseDouble((String) tableModel.getValueAt(row, 10));

        if (backend.endSession(sessionId, total)) {
            generateReceipt(row, total);
            JOptionPane.showMessageDialog(this, "Session ended. Total: PKR " + String.format("%.2f", total));
            loadActiveSessions();
        }
    }

    private void generateReceipt(int row, double total) {
        String[] settings = backend.getSettingsForReceipt();
        String sysName = settings[0], currency = settings[1], footer = settings[2];

        double surcharge = Double.parseDouble((String) tableModel.getValueAt(row, 9));

        StringBuilder sb = new StringBuilder();
        sb.append("=============================\n");
        sb.append("    ").append(sysName).append("\n");
        sb.append("=============================\n");
        sb.append("PC       : ").append(tableModel.getValueAt(row, 1)).append("\n");
        sb.append("Customer : ").append(tableModel.getValueAt(row, 2)).append("\n");
        sb.append("Start    : ").append(tableModel.getValueAt(row, 3)).append("\n");
        sb.append("Duration : ").append(tableModel.getValueAt(row, 4)).append(" hrs\n");
        sb.append("Rate     : ").append(currency).append(" ").append(tableModel.getValueAt(row, 5)).append("/hr\n");
        sb.append("Subtotal : ").append(currency).append(" ").append(tableModel.getValueAt(row, 6)).append("\n");
        sb.append("Discount : ").append(tableModel.getValueAt(row, 7)).append("%\n");
        sb.append("Tax      : ").append(tableModel.getValueAt(row, 8)).append("%\n");
        if (surcharge > 0) {
            sb.append("Reserv.  : ").append(currency).append(" ").append(String.format("%.2f", surcharge)).append("\n");
        }
        sb.append("-----------------------------\n");
        sb.append("TOTAL    : ").append(currency).append(" ").append(String.format("%.2f", total)).append("\n");
        sb.append("=============================\n");
        sb.append(footer).append("\n");

        receiptArea.setText(sb.toString());
    }

    private void printReceipt() {
        if (receiptArea.getText().isEmpty()) {
            ExceptionHandler.handleValidationError("Please end a session first to generate a receipt.", this);
            return;
        }
        try {
            receiptArea.print();
        } catch (Exception e) {
            ExceptionHandler.handleGeneralException(e, this);
        }
    }
}