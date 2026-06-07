package frontend;

import backend.EmployeeBackend;
import util.ExceptionHandler;
import uifactory.UIFactory;
import uifactory.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeePanel extends JPanel{

    private JTable table;

    private DefaultTableModel tableModel;

    private JTextField txtName,txtPhone,txtPosition,txtSalary,txtSearch;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private int selectedId=-1;
    private final EmployeeBackend backend=new EmployeeBackend();

    public EmployeePanel(){

        setLayout(new BorderLayout(8,8));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));


        JPanel form=UIFactory.createFormPanel("Employee Details");

        txtName    =UIFactory.createTextField(15);
        txtPhone   =UIFactory.createTextField(15);
        txtPosition=UIFactory.createTextField(15);

        txtSalary  =UIFactory.createTextField(15);
        txtUsername=UIFactory.createTextField(15);

        txtPassword=UIFactory.createPasswordField(15);
        cbRole     =UIFactory.createComboBox();
        loadRoles();


        form.add(UIFactory.createFormLabel("Name:"),    UIFactory.gbc(0,0,1));
        form.add(txtName,                               UIFactory.gbc(1,0,1));

        form.add(UIFactory.createFormLabel("Phone:"),   UIFactory.gbc(0,1,1));

        form.add(txtPhone,                              UIFactory.gbc(1,1,1));
        form.add(UIFactory.createFormLabel("Position:"),UIFactory.gbc(0,2,1));

        form.add(txtPosition,                           UIFactory.gbc(1,2,1));
        form.add(UIFactory.createFormLabel("Salary:"),  UIFactory.gbc(0,3,1));

        form.add(txtSalary,                             UIFactory.gbc(1,3,1));
        form.add(UIFactory.createFormLabel("Username:"),UIFactory.gbc(0,4,1));
        form.add(txtUsername,                           UIFactory.gbc(1,4,1));


        form.add(UIFactory.createFormLabel("Password:"),UIFactory.gbc(0,5,1));


        form.add(txtPassword,                           UIFactory.gbc(1,5,1));

        form.add(UIFactory.createFormLabel("Role:"),    UIFactory.gbc(0,6,1));
        form.add(cbRole,                                UIFactory.gbc(1,6,1));



        JPanel btnRow=UIFactory.createButtonPanel();

        JButton btnAdd   =UIFactory.createPrimaryButton("Add");
        JButton btnUpdate=UIFactory.createSecondaryButton("Update");

        JButton btnDelete=UIFactory.createDangerButton("Delete");

        JButton btnClear =UIFactory.createNeutralButton("Clear");
        btnRow.add(btnAdd); btnRow.add(btnUpdate); btnRow.add(btnDelete); btnRow.add(btnClear);
        form.add(btnRow,UIFactory.gbc(0,7,2));

        JPanel searchRow=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        searchRow.setBackground(UIConstants.PANEL_BG);
        txtSearch=UIFactory.createTextField(20);


        JButton btnSearch=UIFactory.createSecondaryButton("Search");
        JButton btnAll   =UIFactory.createNeutralButton("Show All");
        searchRow.add(UIFactory.createFormLabel("Search:"));
        searchRow.add(txtSearch); searchRow.add(btnSearch); searchRow.add(btnAll);
        form.add(searchRow,UIFactory.gbc(0,8,2));



        add(form,BorderLayout.NORTH);

        String[] cols={"ID","Name","Phone","Position","Salary"};
        tableModel=new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){ return false; }
      };

        table=UIFactory.createTable(tableModel);
        add(UIFactory.createTableScrollPane(table),BorderLayout.CENTER);

        loadEmployees(null);

        table.getSelectionModel().addListSelectionListener(e ->{
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0){

                int row=table.getSelectedRow();
                selectedId=(int) tableModel.getValueAt(row,0);
                txtName.setText((String) tableModel.getValueAt(row,1));

                txtPhone.setText((String) tableModel.getValueAt(row,2));
                txtPosition.setText((String) tableModel.getValueAt(row,3));
                txtSalary.setText(String.valueOf(tableModel.getValueAt(row,4)));
          }
      });

        btnAdd.addActionListener(e -> addEmployee());

        btnUpdate.addActionListener(e -> updateEmployee());

        btnDelete.addActionListener(e -> deleteEmployee());

        btnClear.addActionListener(e -> clearForm());

        btnSearch.addActionListener(e -> loadEmployees(txtSearch.getText().trim()));

        btnAll.addActionListener(e ->{ txtSearch.setText(""); loadEmployees(null); });
  }


    private void loadRoles(){
        cbRole.removeAllItems();
        try{
            ResultSet rs=backend.getAllRoles();
            if (rs==null) return;

            while (rs.next()) cbRole.addItem(rs.getString(2)+" [ID:"+rs.getInt(1)+"]");
      }
        catch (SQLException e){ ExceptionHandler.handleSQLException(e,this); }
  }

    private int getRoleId(){
        String item=(String) cbRole.getSelectedItem();
        if (item==null) return 2;
        return Integer.parseInt(item.replaceAll(".*\\[ID:(\\d+)\\]","$1"));
  }

    private void loadEmployees(String keyword){
        tableModel.setRowCount(0);
        try{

            ResultSet rs=backend.searchEmployees(keyword);
            if (rs==null) return;

            while (rs.next())

                tableModel.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getDouble(5)});
      }
        catch (SQLException e){
            ExceptionHandler.handleSQLException(e,this); }
  }

    private void addEmployee(){



        String name  =txtName.getText().trim();
        String phone =txtPhone.getText().trim();
        String salary=txtSalary.getText().trim();

        if (!ExceptionHandler.validateNotEmpty(name,  "Name",  this)) return;

        if (!ExceptionHandler.validatePhone(phone,this)) return;


        if (!salary.isEmpty() && !ExceptionHandler.validateNumeric(salary,"Salary",this)) return;

        double sal     =salary.isEmpty() ? 0 : Double.parseDouble(salary);
        String username=txtUsername.getText().trim();
        String password=new String(txtPassword.getPassword()).trim();

        if (backend.addEmployee(name,phone,txtPosition.getText().trim(),sal,username,password,getRoleId())){
            loadEmployees(null); clearForm();
      }
  }

    private void updateEmployee(){

        if (!ExceptionHandler.validateSelection(selectedId,this)) return;

        String name  =txtName.getText().trim();

        String salary=txtSalary.getText().trim();

        if (!ExceptionHandler.validateNotEmpty(name,  "Name",  this)) return;

        if (!salary.isEmpty() && !ExceptionHandler.validateNumeric(salary,"Salary",this)) return;


        double sal=salary.isEmpty() ? 0 : Double.parseDouble(salary);

        if (backend.updateEmployee(selectedId,name,txtPhone.getText().trim(),txtPosition.getText().trim(),sal)){
            loadEmployees(null); clearForm();
      }
  }

    private void deleteEmployee(){

        if (!ExceptionHandler.validateSelection(selectedId,this)) return;

        if (!ExceptionHandler.confirmDelete(this)) return;

        if (backend.deleteEmployee(selectedId)){ loadEmployees(null); clearForm(); }

    }

    private void clearForm(){

        txtName.setText(""); txtPhone.setText(""); txtPosition.setText("");
        txtSalary.setText(""); txtUsername.setText(""); txtPassword.setText("");
        selectedId=-1; table.clearSelection();

    }
}
