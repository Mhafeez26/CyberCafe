package frontend;

import backend.DashboardBackend;
import factory.PanelFactory;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame{



    private JPanel contentPanel;
    private final DashboardBackend backend=new DashboardBackend();

    private JButton activeNavButton=null;

    public DashboardFrame(String loggedUser,String role,int userId){
        setTitle("Cyber Cafe - Dashboard (" + loggedUser + ")");
        setSize(1100,680);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main=new JPanel(new BorderLayout());
        main.add(buildSidebar(),BorderLayout.WEST);

        contentPanel=UIFactory.createContentPanel();
        main.add(contentPanel,BorderLayout.CENTER);

        add(main);
        showDashboard();
    }

    private JPanel buildSidebar(){
        JPanel sidebar=new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210,0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12,6,12,6));

        JLabel logo=UIFactory.createTitleLabel("Cyber Cafe");
        logo.setForeground(UIConstants.TEXT_ON_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0,0,14,0));
        sidebar.add(logo);

        String[] pages ={
                "Dashboard","Computer Mgmt","Customer Mgmt",
                "Session Mgmt","Billing","Reservations",
                "Employee Mgmt","Reports","Settings","Logout"
        };

        for (String page : pages){
            JButton btn=UIFactory.createNavButton(page);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0,3)));

            btn.addActionListener(e ->{
                navigate(page);
                setActiveNavButton(btn);
            });

            if ("Dashboard".equals(page)){
                setActiveNavButton(btn);
            }
        }
        return sidebar;
    }


    private void setActiveNavButton(JButton btn){
        if (activeNavButton != null){
            activeNavButton.setBackground(UIConstants.SIDEBAR_BG);
            activeNavButton.setForeground(UIConstants.TEXT_ON_PRIMARY);
        }
        activeNavButton=btn;
        activeNavButton.setBackground(UIConstants.PRIMARY);
        activeNavButton.setForeground(UIConstants.TEXT_ON_PRIMARY);
    }

    private void navigate(String page){
        contentPanel.removeAll();
        if ("Dashboard".equals(page)){
            showDashboard();
         }
        else if ("Logout".equals(page)){
            dispose();
            new LoginFrame().setVisible(true);
        }

        else{
            try{
                contentPanel.add(PanelFactory.createPanel(page));
            }

            catch (Exception e){
                ExceptionHandler.handleGeneralException(e,this);
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDashboard(){
        contentPanel.removeAll();
        JPanel panel=new JPanel(new BorderLayout(10,10));
        panel.setBackground(UIConstants.CONTENT_BG);

        JLabel title=UIFactory.createTitleLabel("Dashboard Overview");


        title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        panel.add(title,BorderLayout.NORTH);

        JPanel statsPanel=new JPanel(new GridLayout(1,4,12,0));
        statsPanel.setBackground(UIConstants.CONTENT_BG);
        statsPanel.add(UIFactory.createStatCard("Total Computers", String.valueOf(backend.getTotalComputers())));
        statsPanel.add(UIFactory.createStatCard("Active Sessions", String.valueOf(backend.getActiveSessions())));
        statsPanel.add(UIFactory.createStatCard("Available PCs",   String.valueOf(backend.getAvailableComputers())));
        statsPanel.add(UIFactory.createStatCard("Today's Revenue", "PKR " + String.format("%.2f",backend.getTodayRevenue())));

        panel.add(statsPanel,BorderLayout.CENTER);

        JPanel pcGrid=buildPCStatusGrid();
        JScrollPane scroll=new JScrollPane(pcGrid);


        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.PRIMARY,1),
                "Live PC Status",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                UIConstants.FONT_SUBTITLE,UIConstants.PRIMARY
        ));
        scroll.setPreferredSize(new Dimension(0,210));
        scroll.getViewport().setBackground(UIConstants.PANEL_BG);
        panel.add(scroll,BorderLayout.SOUTH);

        contentPanel.add(panel);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildPCStatusGrid(){
        JPanel grid=new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        grid.setBackground(UIConstants.PANEL_BG);
        try{
            ResultSet rs=backend.getLivePCStatus();
            if (rs==null) return grid;
            while (rs.next()){
                String name  =rs.getString(1);
                String status=rs.getString(2);

                JPanel pc=new JPanel(new BorderLayout(0,2));
                pc.setPreferredSize(new Dimension(100,62));
                pc.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

                JLabel lblName=UIFactory.createFormLabel(name);
                lblName.setHorizontalAlignment(SwingConstants.CENTER);
                pc.add(lblName,BorderLayout.CENTER);

                JLabel lblStatus=new JLabel(status,SwingConstants.CENTER);
                lblStatus.setFont(UIConstants.FONT_TABLE_CELL);
                pc.add(lblStatus,BorderLayout.SOUTH);

                if ("Available".equals(status))        pc.setBackground(UIConstants.STATUS_AVAILABLE);
                else if ("Occupied".equals(status))    pc.setBackground(UIConstants.STATUS_OCCUPIED);
                else                                   pc.setBackground(UIConstants.STATUS_MAINTENANCE);
                pc.setOpaque(true);
                grid.add(pc);
            }
        } catch (Exception e){

            ExceptionHandler.handleGeneralException(e,this);
        }
        return grid;
    }
}