package controllers;

import views.AddCountry;
import views.SearchCountry;
import views.ShowCountryForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ShowCountryController {

    ShowCountryForm showCountryFormView;
    DatabaseConnection databaseConnection;

    Connection connection;
    PreparedStatement preparedStatementForTable; // reads countries of a certain continent
    Statement everyContinentStatement;
    Statement continentStatement;
    ResultSet resultSetForTable; // contains country-continent pairs of a certain continent
    ResultSet resultSetForComboBox; // contains continents for combo box
    ResultSet resultSetForFullTable; // contains country-continent pairs for every continent

    public ShowCountryController(ShowCountryForm showCountryFormView) {
        this.showCountryFormView = showCountryFormView;
        this.databaseConnection = new DatabaseConnection();

        manageStatements();

        showCountryFormView.addChooseContinentListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String country = (String) showCountryFormView.getChooseContinent().getSelectedItem();
                    if(country.equals("All")){
                        country = "%";
                    }
                    preparedStatementForTable.setString(1, country);
                    resultSetForTable = preparedStatementForTable.executeQuery();
                    manageCountryTable(showCountryFormView.getCountryTable(), resultSetForTable, showCountryFormView.getColumnNames());
                } catch (Exception ex){
                    System.out.println(ex);
                }
            }
        });

        showCountryFormView.addNewCountryButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCountry addCountry = new AddCountry();
            }
        });

        showCountryFormView.addSearchButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchCountry searchCountry = new SearchCountry();
            }
        });
    }

    public void manageStatements(){
        try{
            connection = databaseConnection.getConnection();

            everyContinentStatement = connection.createStatement();
            continentStatement = connection.createStatement();

            preparedStatementForTable = connection.prepareStatement(
                    "select country_name, name from country join continent" +
                            " on country.continent = continent.id where continent.name LIKE ?;"
            );
            resultSetForComboBox = continentStatement.executeQuery("select name from continent order by name;");
            resultSetForFullTable = everyContinentStatement.executeQuery("select country_name, name from country join continent on country.continent = continent.id;");

            manageComboBox(showCountryFormView.getChooseContinent(), resultSetForComboBox);
            manageCountryTable(showCountryFormView.getCountryTable(), resultSetForFullTable, showCountryFormView.getColumnNames());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void manageCountryTable(JTable countryTable, ResultSet resultSet,
                                   String[] columnNames){
        try{
            String countryName, continentName;

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(columnNames);
            countryTable.setModel(tableModel);
            int nrOfRecords = 0;
            while(resultSet.next()){
                nrOfRecords++;
                countryName = resultSet.getString("country_name");
                continentName = resultSet.getString("name");

                tableModel.addRow(new Object[]{countryName, continentName});
            }
            if (nrOfRecords < 1) {
                JOptionPane.showMessageDialog(null, "No Record Found", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (nrOfRecords == 1) {
                System.out.println(nrOfRecords + " Record Found");
            } else {
                System.out.println(nrOfRecords + " Records Found");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void manageComboBox(JComboBox<String> comboBox, ResultSet resultSet){
        try {
            DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
            comboBoxModel.addElement("All");
            while(resultSet.next()){
                comboBoxModel.addElement(resultSet.getString("name"));
            }
            comboBox.setModel(comboBoxModel);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
