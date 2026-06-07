package frontend;

import backend.SessionBackend;
import backend.CustomerBackend;
import backend.ComputerBackend;
import observer.SessionObserver;
import observer.SessionEventPublisher;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SessionPanel extends JPanel implements SessionObserver {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPC,cbCustomer;
    private final Map<Integer,Long> startTimes = new HashMap<>();
    private Timer timer;
    private final SessionBackend backend = new SessionBackend();
    private final ComputerBackend computerBackend = new ComputerBackend();
    private final CustomerBackend customerBackend = new CustomerBackend();

    public SessionPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel form = UIFactory.createFormPanel("Start New Session");
        form.setLayout(new FlowLayout(FlowLayout.LEFT,8,6));

        cbPC       = UIFactory.createComboBox();
        cbCustomer = UIFactory.createComboBox();
        loadAvailablePCs();
        loadCustomers();

        JButton btnStart   = UIFactory.createPrimaryButton("Start Session");
        JButton btnRefresh = UIFactory.createSecondaryButton("Refresh");

        form.add(UIFactory.createFormLabel("PC:"));       form.add(cbPC);
        form.add(UIFactory.createFormLabel("Customer:")); form.add(cbCustomer);
        form.add(btnStart); form.add(btnRefresh);
        add(form,BorderLayout.NORTH);

        String[] cols = {"Session ID","PC","Customer","Start Time","Duration","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r,int c) { return false; }
        };
        table = UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table),BorderLayout.CENTER);

        loadActiveSessions();

        btnStart.addActionListener(e -> startSession());
        btnRefresh.addActionListener(e -> { loadAvailablePCs(); loadActiveSessions(); });

        SessionEventPublisher.getInstance().addObserver(this);

        timer = new Timer(1000,e -> updateDurations());
        timer.start();
    }

    @Override
    public void onSessionStarted(int computerId) {
        loadAvailablePCs();
        loadActiveSessions();
    }

    @Override
    public void onSessionEnded(int computerId) {
        loadAvailablePCs();
        loadActiveSessions();
    }

    private void loadAvailablePCs() {
        cbPC.removeAllItems();
        try {
            ResultSet rs = computerBackend.getAvailableComputers();
            if (rs == null) return;
            while (rs.next()) cbPC.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void loadCustomers() {
        cbCustomer.removeAllItems();
        cbCustomer.addItem("0 - Walk-in");
        try {
            ResultSet rs = customerBackend.getAllCustomersForCombo();
            if (rs == null) return;
            while (rs.next()) cbCustomer.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void loadActiveSessions() {
        tableModel.setRowCount(0);
        startTimes.clear();
        try {
            ResultSet rs = backend.getActiveSessions();
            if (rs == null) return;
            while (rs.next()) {
                int sessionId   = rs.getInt(1);
                Timestamp start = rs.getTimestamp(4);
                startTimes.put(sessionId,start.getTime());
                long elapsed = (System.currentTimeMillis() - start.getTime()) / 1000;
                tableModel.addRow(new Object[]{
                    sessionId,rs.getString(2),rs.getString(3),
                    start.toString().substring(0,16),
                    backend.formatDuration(elapsed),rs.getString(5)
                });
            }
        } catch (SQLException e) { ExceptionHandler.handleSQLException(e,this); }
    }

    private void updateDurations() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int sessionId = (int) tableModel.getValueAt(i,0);
            Long startMs  = startTimes.get(sessionId);
            if (startMs != null) {
                long elapsed = (System.currentTimeMillis() - startMs) / 1000;
                tableModel.setValueAt(backend.formatDuration(elapsed),i,4);
            }
        }
    }

    private void startSession() {
        String pcStr   = (String) cbPC.getSelectedItem();
        String custStr = (String) cbCustomer.getSelectedItem();
        if (pcStr == null) { ExceptionHandler.handleValidationError("No available any PC",this); return; }

        int pcId   = Integer.parseInt(pcStr.split(" - ")[0]);
        int custId = Integer.parseInt(custStr.split(" - ")[0]);

        if (backend.startSession(pcId,custId)) {
            JOptionPane.showMessageDialog(this,"Session starts!");
            loadAvailablePCs();
            loadActiveSessions();
        }
    }
}
