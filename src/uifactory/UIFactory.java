package uifactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;

public class UIFactory {

    //table
    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model) {


            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? UIConstants.PANEL_BG : UIConstants.TABLE_ROW_ALT);
                } else {
                    c.setBackground(UIConstants.SECONDARY);
                    c.setForeground(UIConstants.TEXT_ON_PRIMARY);
                }
                if (!isRowSelected(row)) c.setForeground(Color.DARK_GRAY);
                c.setFont(UIConstants.FONT_TABLE_CELL);
                return c;
            }
        };

        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.setFont(UIConstants.FONT_TABLE_CELL);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(UIConstants.BORDER_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(UIConstants.SECONDARY);
        table.setSelectionForeground(UIConstants.TEXT_ON_PRIMARY);
        table.setFillsViewportHeight(true);


        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.PRIMARY);
        header.setForeground(UIConstants.TEXT_ON_PRIMARY);

        header.setFont(UIConstants.FONT_TABLE_HEADER);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);



        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        sp.getViewport().setBackground(UIConstants.PANEL_BG);
        return sp;
    }


    //buttons
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setBackground(UIConstants.PRIMARY);
        btn.setForeground(UIConstants.TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }


    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setBackground(UIConstants.SECONDARY);
        btn.setForeground(UIConstants.TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setBackground(new Color(190, 40, 40));
        btn.setForeground(UIConstants.TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }


    public static JButton createNeutralButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }

    //labels
    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(UIConstants.FONT_TITLE);
        return lbl;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_SUBTITLE);
        return lbl;
    }

    public static JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_LABEL);
        return lbl;
    }

    public static JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(UIConstants.FONT_STAT_VALUE);
        return lbl;
    }


    //fields and combos
    public static JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(UIConstants.FONT_FIELD);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(UIConstants.FONT_FIELD);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return pf;
    }

    public static JComboBox<String> createComboBox() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(UIConstants.FONT_FIELD);
        return cb;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(UIConstants.FONT_FIELD);
        return cb;
    }


    //panels
    public static JPanel createFormPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.PANEL_BG);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.PRIMARY, 1),
                title,
                TitledBorder.LEFT, TitledBorder.TOP,
                UIConstants.FONT_SUBTITLE, UIConstants.PRIMARY
        );
        p.setBorder(BorderFactory.createCompoundBorder(
                border,
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return p;
    }

    public static JPanel createContentPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return p;
    }


    public static JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBackground(UIConstants.PANEL_BG);
        return p;
    }

    public static JPanel createStatCard(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(UIConstants.PANEL_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.PRIMARY, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(UIConstants.FONT_STAT_VALUE);

        JLabel lblName = new JLabel(label, SwingConstants.CENTER);
        lblName.setFont(UIConstants.FONT_STAT_LABEL);


        JPanel topBar = new JPanel();
        topBar.setBackground(UIConstants.PRIMARY);
        topBar.setPreferredSize(new Dimension(0, 6));

        p.add(topBar, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        p.add(lblName, BorderLayout.SOUTH);
        return p;
    }


    //nav buttons
    public static JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_NAV);
        btn.setBackground(UIConstants.SIDEBAR_BG);
        btn.setForeground(UIConstants.TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }



    //layout helpers
    public static GridBagConstraints gbc(int x, int y, int width) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y; g.gridwidth = width;
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }
}