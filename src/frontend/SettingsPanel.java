package frontend;

import backend.SettingsBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JTextField txtSystemName,txtTax,txtCurrency,txtFooter;
    private final SettingsBackend backend = new SettingsBackend();

    public SettingsPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = UIFactory.createTitleLabel("System Settings");
        title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        add(title,BorderLayout.NORTH);

        JPanel form = UIFactory.createFormPanel("Configuration");

        txtSystemName = UIFactory.createTextField(20);
        txtTax        = UIFactory.createTextField(20);
        txtCurrency   = UIFactory.createTextField(20);
        txtFooter     = UIFactory.createTextField(30);

        form.add(UIFactory.createFormLabel("System Name:"),  UIFactory.gbc(0,0,1));
        form.add(txtSystemName,                              UIFactory.gbc(1,0,1));
        form.add(UIFactory.createFormLabel("Tax %:"),        UIFactory.gbc(0,1,1));
        form.add(txtTax,                                     UIFactory.gbc(1,1,1));
        form.add(UIFactory.createFormLabel("Currency:"),     UIFactory.gbc(0,2,1));
        form.add(txtCurrency,                                UIFactory.gbc(1,2,1));
        form.add(UIFactory.createFormLabel("Receipt Footer:"),UIFactory.gbc(0,3,1));
        form.add(txtFooter,                                  UIFactory.gbc(1,3,1));

        JButton btnSave = UIFactory.createPrimaryButton("Save Settings");
        btnSave.setPreferredSize(new Dimension(140,34));
        GridBagConstraints btnGbc = UIFactory.gbc(0,4,2);
        btnGbc.anchor = GridBagConstraints.CENTER;
        form.add(btnSave,btnGbc);

        add(form,BorderLayout.CENTER);
        loadSettings();
        btnSave.addActionListener(e -> saveSettings());
    }

    private void loadSettings() {
        String[] s = backend.loadSettings();
        txtSystemName.setText(s[0]);
        txtTax.setText(s[1]);
        txtCurrency.setText(s[2]);
        txtFooter.setText(s[3]);
    }

    private void saveSettings() {
        String systemName = txtSystemName.getText().trim();
        String taxStr     = txtTax.getText().trim();
        String currency   = txtCurrency.getText().trim();
        if (!ExceptionHandler.validateNotEmpty(systemName,"System Name",this)) return;
        if (!ExceptionHandler.validateNumeric(taxStr,"Tax %",this)) return;
        if (!ExceptionHandler.validateNotEmpty(currency,"Currency",this)) return;

        double tax = Double.parseDouble(taxStr);
        if (backend.saveSettings(systemName,tax,currency,txtFooter.getText().trim())) {
            JOptionPane.showMessageDialog(this,"Settings saved!");
        }
    }
}
