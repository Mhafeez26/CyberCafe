package factory;

import frontend.*;
import javax.swing.JPanel;

// factory design pattern
public class PanelFactory {

    public static JPanel createPanel(String pageName) {
        switch (pageName) {
            case "Computer Management":  return new ComputerPanel();
            case "Customer Management":  return new CustomerPanel();
            case "Session Management":   return new SessionPanel();
            case "Billing":        return new BillingPanel();
            case "Reservations":   return new ReservationPanel();
            case "Employee Management":  return new EmployeePanel();
            case "Reports":        return new ReportsPanel();
            case "Settings":       return new SettingsPanel();
            default:               return new JPanel();
        }
    }
}
