package controllers;

import views.SearchCountry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SearchCountryController {
    SearchCountry searchCountryView;
    DatabaseConnection databaseConnection;

    private String searchPlaceHolder = "Enter country name";
    private String newNamePlaceHolder = "Enter new country name";
    Connection connection;
    Statement continentStatement;
    PreparedStatement searchPreparedStatement;
    PreparedStatement deletePreparedStatement;
    PreparedStatement updatePreparedStatement;
    PreparedStatement continentIdPreparedStatement;
    ResultSet searchResultSet;
    ResultSet continentResultSet;
    ResultSet continentIdResultSet;

    public SearchCountryController(SearchCountry searchCountryView){
        this.searchCountryView = searchCountryView;
        this.databaseConnection = new DatabaseConnection();

        AddCountryController.setPlaceHolder(searchCountryView.getSearchText(), searchPlaceHolder);
        AddCountryController.setPlaceHolder(searchCountryView.getNewNameText(), newNamePlaceHolder);

        manageStatements();

        searchCountryView.addUpdateButtonListener(new UpdateButtonActionListener());
        searchCountryView.addDeleteButtonListener(new DeleteActionListener());
        searchCountryView.addSubmitUpdateButtonListener(new SubmitUpdateActionListener());
        searchCountryView.addSearchButtonListener(new SearchButtonActionListener());
    }

    public void manageStatements(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

            continentStatement = connection.createStatement();
            continentResultSet = continentStatement.executeQuery("select name from continent order by name");
            manageComboBox(searchCountryView.getContinentComboBox(), continentResultSet);
        } catch (Exception ex){
            System.out.println(ex);
        }
    }

    // controller for combo box
    public void manageComboBox(JComboBox<String> comboBox, ResultSet resultSet){
        try {
            DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
            comboBoxModel.addElement("Keep continent");
            while(resultSet.next()){
                comboBoxModel.addElement(resultSet.getString("name"));
            }
            comboBox.setModel(comboBoxModel);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // searches for a country that already exists
    public String validCountryName(String name){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

            searchPreparedStatement = connection.prepareStatement(
                    "SELECT country_name FROM country WHERE country_name = ?"
            );
            searchPreparedStatement.setString(1, name);
            searchResultSet = searchPreparedStatement.executeQuery();
            if(searchResultSet.next()){
                return searchResultSet.getString("country_name");
            }
            else{
                return null;
            }
        } catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    class UpdateButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchCountryView.getContinentComboBox().setVisible(true);
            searchCountryView.getNewNameText().setVisible(true);
            searchCountryView.getSubmitUpdateButton().setVisible(true);
        }
    }

    // controller for delete operation
    class DeleteActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

                deletePreparedStatement = connection.prepareStatement(
                        "DELETE FROM country WHERE country_name = ?"
                );
                deletePreparedStatement.setString(1, searchCountryView.getSearchText().getText());
                deletePreparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        searchCountryView.getSearchText().getText() + " deleted", "Delete message", JOptionPane.INFORMATION_MESSAGE);

                searchCountryView.getSearchButton().setVisible(false);
                searchCountryView.getDeleteButton().setVisible(false);
            } catch (Exception ex){
                System.out.println(ex);
            }
        }
    }

    /**
     * controller for update operation
     * 4 cases:
     *  - nothing updated
     *  - only continent updated
     *  - only country name updated
     *  - both updated
     */
    class SubmitUpdateActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

                String continent = searchCountryView.getContinentComboBox().getSelectedItem().toString();
                System.out.println(continent);

                if(searchCountryView.getNewNameText().getText().equals("") || searchCountryView.getNewNameText().getText().equals(newNamePlaceHolder)){
                    if(continent.equals("Keep continent")){
                        // nothing updated
                        JOptionPane.showMessageDialog(null,
                                "No changes made", "Update message", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        // only continent updated
                        continentIdPreparedStatement = connection.prepareStatement(
                                "SELECT id FROM continent WHERE name = ?"
                        );
                        continentIdPreparedStatement.setString(1, searchCountryView.getContinentComboBox().getSelectedItem().toString());
                        continentIdResultSet = continentIdPreparedStatement.executeQuery();
                        if(continentIdResultSet.next()){
                            int continentId = continentIdResultSet.getInt("id");

                            //idk why the statement below works only if it is in a single line
                            updatePreparedStatement = connection.prepareStatement(
                                    "UPDATE country SET continent = ? WHERE country_name = ?"
                            );
                            updatePreparedStatement.setInt(1, continentId);
                            updatePreparedStatement.setString(2, searchCountryView.getSearchText().getText());

                            updatePreparedStatement.executeUpdate();
                        }
                        else{
                            // shouldn't enter here
                            JOptionPane.showMessageDialog(null,
                                    "Continent ID not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else {
                    if (continent.equals("Keep continent")) {
                        // only country name updated
                        updatePreparedStatement = connection.prepareStatement(
                                "UPDATE country SET country_name = ? WHERE country_name = ?"
                        );
                        updatePreparedStatement.setString(1, searchCountryView.getNewNameText().getText());
                        updatePreparedStatement.setString(2, searchCountryView.getSearchText().getText());
                        updatePreparedStatement.executeUpdate();
                    } else {
                        // both updated
                        continentIdPreparedStatement = connection.prepareStatement(
                                "SELECT id FROM continent WHERE name = ?"
                        );
                        continentIdPreparedStatement.setString(1, searchCountryView.getContinentComboBox().getSelectedItem().toString());
                        continentIdResultSet = continentIdPreparedStatement.executeQuery();
                        if (continentIdResultSet.next()) {
                            int continentId = continentIdResultSet.getInt("id");
                            updatePreparedStatement = connection.prepareStatement(
                                    "UPDATE country SET country_name = ?, continent = ? WHERE country_name = ?"
                            );
                            updatePreparedStatement.setString(1, searchCountryView.getNewNameText().getText());
                            updatePreparedStatement.setInt(2, continentId);
                            updatePreparedStatement.setString(3, searchCountryView.getSearchText().getText());

                            updatePreparedStatement.executeUpdate();
                        } else {
                            // shouldn't enter here
                            JOptionPane.showMessageDialog(null,
                                    "Continent ID not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                JOptionPane.showMessageDialog(null,
                        searchCountryView.getSearchText().getText() + " updated", "Update message", JOptionPane.INFORMATION_MESSAGE);

                searchCountryView.getContinentComboBox().setVisible(false);
                searchCountryView.getNewNameText().setVisible(false);
                searchCountryView.getUpdateButton().setVisible(false);
                searchCountryView.getDeleteButton().setVisible(false);
                searchCountryView.getSubmitUpdateButton().setVisible(false);
            }catch (Exception ex){
                System.out.println(ex);
            }
        }
    }

    // controller for submit button when submitting update
    class SearchButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String nameEntered = searchCountryView.getSearchText().getText();
            if (nameEntered == null || nameEntered.equals("") || nameEntered.equals(searchPlaceHolder)) {
                searchCountryView.getInvalidNameLabel().setVisible(true);
                searchCountryView.getUpdateButton().setVisible(false);
                searchCountryView.getDeleteButton().setVisible(false);
            } else {
                String countryFound = validCountryName(nameEntered);
                if (countryFound != null) {
                    searchCountryView.getInvalidNameLabel().setVisible(false);
                    searchCountryView.getUpdateButton().setVisible(true);
                    searchCountryView.getDeleteButton().setVisible(true);
                } else {
                    searchCountryView.getInvalidNameLabel().setText("Invalid name");
                    searchCountryView.getInvalidNameLabel().setVisible(true);
                    searchCountryView.getUpdateButton().setVisible(false);
                    searchCountryView.getDeleteButton().setVisible(false);
                }
            }
        }
    }
}
