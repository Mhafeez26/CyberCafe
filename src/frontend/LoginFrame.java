package frontend;

import backend.LoginBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final LoginBackend backend = new LoginBackend();

    public LoginFrame() {
        setTitle("Cyber Cafe - Login");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UIConstants.CONTENT_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel lblTitle = UIFactory.createTitleLabel("Cyber Cafe Management");
        lblTitle.setForeground(UIConstants.TEXT_ON_PRIMARY);
        header.add(lblTitle, BorderLayout.CENTER);
        main.add(header, BorderLayout.NORTH);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(UIConstants.CONTENT_BG);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel form = UIFactory.createFormPanel("Login");
        GridBagConstraints gbc = UIFactory.gbc(0, 0, 1);

        form.add(UIFactory.createFormLabel("Username:"), UIFactory.gbc(0, 0, 1));
        txtUsername = UIFactory.createTextField(15);
        form.add(txtUsername, UIFactory.gbc(1, 0, 1));

        form.add(UIFactory.createFormLabel("Password:"), UIFactory.gbc(0, 1, 1));
        txtPassword = UIFactory.createPasswordField(15);
        form.add(txtPassword, UIFactory.gbc(1, 1, 1));

        JButton btnLogin = UIFactory.createPrimaryButton("Login");
        btnLogin.setPreferredSize(new Dimension(220, 34));
        GridBagConstraints btnGbc = UIFactory.gbc(0, 2, 2);
        btnGbc.anchor = GridBagConstraints.CENTER;
        form.add(btnLogin, btnGbc);

        formWrapper.add(form);
        main.add(formWrapper, BorderLayout.CENTER);
        add(main);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (!ExceptionHandler.validateNotEmpty(username, "Username", this)) return;
        if (!ExceptionHandler.validateNotEmpty(password, "Password", this)) return;

        String[] result = backend.authenticate(username, password);

        if (result != null) {
            dispose();
            new DashboardFrame(result[1], result[2], Integer.parseInt(result[0])).setVisible(true);
        } else {
            ExceptionHandler.handleValidationError("Username ya password galat hai.", this);
            txtPassword.setText("");
        }
    }
}
