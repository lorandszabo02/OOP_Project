package controllers;

import views.AddCountry;
import views.ShowCountryForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class AddCountryController {
    AddCountry addCountryView;
    DatabaseConnection databaseConnection;

    Connection connection;
    Statement continentStatement; // to get continents
    PreparedStatement addCountryStatement; // create operation
    PreparedStatement getContinentIdStatement; // continent id is needed for the insert
    PreparedStatement countExistingCountries; // see if country already exists
    ResultSet continentResultSet;
    ResultSet nrOfExistingCountries; // 1 if country already exists

    public AddCountryController(AddCountry addCountryView){
        this.addCountryView = addCountryView;
        this.databaseConnection = new DatabaseConnection();

        manageStatements();

        setPlaceHolder(addCountryView.getNameTextField(), "Enter country name");
        setPlaceHolder(addCountryView.getCountryID(), "Enter country ID (3 chars)");

        addCountryView.addAddCountryButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateNewCountry();
            }
        });
    }

    public void validateNewCountry(){
        String nameOfCountry = addCountryView.getNameTextField().getText();
        String idOfCountry = addCountryView.getCountryID().getText();
        String continentOfCountry = (String) addCountryView.getContinentComboBox().getSelectedItem();

        boolean valid = true;
        if(nameOfCountry.length() < 3 || nameOfCountry.equals("Enter country name")){
            addCountryView.getNameInvalidLabel().setText("Country name incorrect");
            addCountryView.getNameInvalidLabel().setVisible(true);
            valid = false;
        }
        if(idOfCountry.length() != 3){
            addCountryView.getIDInvalidLabel().setVisible(true);
            valid = false;
        }
        if(continentOfCountry == null){
            addCountryView.getContinentInvalidLabel().setVisible(true);
            valid = false;
        }

        try {
            countExistingCountries = connection.prepareStatement(
                    "SELECT COUNT(*) AS existsCountry FROM country" +
                            " WHERE country_name = ? OR id = ?"
            );
            countExistingCountries.setString(1, nameOfCountry);
            countExistingCountries.setString(2, idOfCountry);
            nrOfExistingCountries = countExistingCountries.executeQuery();
            if(nrOfExistingCountries.next()){
                int nrOfCountry = nrOfExistingCountries.getInt("existsCountry");
                // if country already exists
                if(nrOfCountry != 0){
                    addCountryView.getNameInvalidLabel().setText("Country already exists");
                    addCountryView.getNameInvalidLabel().setVisible(true);
                    valid = false;
                }
            }
            else{
                System.out.println("Something went wrong");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }


        if(valid){
            JOptionPane.showMessageDialog(null, "Country " +
                    nameOfCountry + " added", "Operation successful", JOptionPane.INFORMATION_MESSAGE);
            addCountryView.getIDInvalidLabel().setVisible(false);
            addCountryView.getNameInvalidLabel().setVisible(false);
            addCountryView.getContinentInvalidLabel().setVisible(false);
            addCountryView.getNameTextField().setText("");
            addCountryView.getCountryID().setText("");

            try {
                getContinentIdStatement = connection.prepareStatement(
                        "select id from continent where name = ?;"
                );
                getContinentIdStatement.setString(1, continentOfCountry);
                ResultSet resultSet = getContinentIdStatement.executeQuery();

                int continentId = 0;
                if(resultSet.next()){
                    continentId = resultSet.getInt("id");
                }
                else {
                    System.out.println("ID not found");
                }


                addCountryStatement = connection.prepareStatement(
                        "insert into country (id, continent, country_name)" +
                                "values (?, ?, ?);"
                );
                addCountryStatement.setString(1, idOfCountry.toUpperCase());
                addCountryStatement.setInt(2, continentId);
                addCountryStatement.setString(3, nameOfCountry);
                addCountryStatement.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void manageStatements(){
        try{
            connection = databaseConnection.getConnection();
            continentStatement = connection.createStatement();
            continentResultSet = continentStatement.executeQuery("select name from continent order by name;");

            ShowCountryController.manageComboBox(addCountryView.getContinentComboBox(), continentResultSet);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void setPlaceHolder(JTextField textField, String placeHolder){
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeHolder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeHolder);
                }
            }
        });
    }

}
