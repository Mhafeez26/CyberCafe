package frontend;

import backend.ReportsBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblSummary;
    private final ReportsBackend backend = new ReportsBackend();

    public ReportsPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel topPanel = new JPanel(new BorderLayout(4,4));
        topPanel.setBackground(UIConstants.CONTENT_BG);

        JLabel title = UIFactory.createTitleLabel("Reports");
        title.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));
        topPanel.add(title,BorderLayout.NORTH);

        JPanel btnRow = UIFactory.createButtonPanel();
        JButton btnDaily     = UIFactory.createPrimaryButton("Daily Revenue");
        JButton btnSessions  = UIFactory.createSecondaryButton("Session Report");
        JButton btnCustomers = UIFactory.createSecondaryButton("Customer Report");
        JButton btnPCUsage   = UIFactory.createSecondaryButton("PC Usage Report");
        btnDaily.setPreferredSize(new Dimension(130,32));
        btnSessions.setPreferredSize(new Dimension(130,32));
        btnCustomers.setPreferredSize(new Dimension(140,32));
        btnPCUsage.setPreferredSize(new Dimension(140,32));
        btnRow.add(btnDaily); btnRow.add(btnSessions);
        btnRow.add(btnCustomers); btnRow.add(btnPCUsage);
        topPanel.add(btnRow,BorderLayout.CENTER);

        lblSummary = UIFactory.createFormLabel(" ");
        lblSummary.setHorizontalAlignment(SwingConstants.CENTER);
        lblSummary.setForeground(UIConstants.PRIMARY);
        topPanel.add(lblSummary,BorderLayout.SOUTH);
        add(topPanel,BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table),BorderLayout.CENTER);

        btnDaily.addActionListener(e -> showDailyRevenue());
        btnSessions.addActionListener(e -> showSessionReport());
        btnCustomers.addActionListener(e -> showCustomerReport());
        btnPCUsage.addActionListener(e -> showPCUsageReport());

        showDailyRevenue();
   }

    private void showDailyRevenue() {
        setColumns("Date","Total Sessions","Total Revenue (PKR)");
        try {
            ResultSet rs = backend.getDailyRevenue();
            if (rs == null) return;
            double grand = 0; int total = 0;
            while (rs.next()) {
                double rev = rs.getDouble(3); int cnt = rs.getInt(2);
                grand += rev; total += cnt;
                tableModel.addRow(new Object[]{rs.getString(1),cnt,String.format("%.2f",rev)});
           }
            lblSummary.setText("Total: " + total + " sessions | Revenue: PKR " + String.format("%.2f",grand));
       } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this);}
   }

    private void showSessionReport() {
        setColumns("ID","PC","Customer","Start","End","Amount (PKR)","Status");
        try {
            ResultSet rs = backend.getSessionReport();
            if (rs == null) return;
            while (rs.next()) {
                String end = rs.getString(5);
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4) != null ? rs.getString(4).substring(0,16) : "-",
                    end != null && end.length() > 16 ? end.substring(0,16) : end,
                    String.format("%.2f",rs.getDouble(6)),rs.getString(7)
               });
           }
            lblSummary.setText("Last 100 sessions");
       } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this);}
   }

    private void showCustomerReport() {
        setColumns("ID","Name","Phone","Membership","Sessions","Total Spent (PKR)");
        try {
            ResultSet rs = backend.getCustomerReport();
            if (rs == null) return;
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getInt(5),String.format("%.2f",rs.getDouble(6))
               });
            lblSummary.setText("Customer report loaded");
       } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this);}
   }

    private void showPCUsageReport() {
        setColumns("PC","Category","Sessions","Total Hours","Revenue (PKR)");
        try {
            ResultSet rs = backend.getPCUsageReport();
            if (rs == null) return;
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getString(1),rs.getString(2),rs.getInt(3),
                    String.format("%.2f",rs.getDouble(4)),
                    String.format("%.2f",rs.getDouble(5))
               });
            lblSummary.setText("PC usage report loaded");
       } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this);}
   }

    private void setColumns(String... cols) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        for (String c : cols) tableModel.addColumn(c);
   }
}
