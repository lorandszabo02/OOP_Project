package views;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;


public class ShowCountryForm extends JFrame{
    private JPanel MainPanel;
    private JTable countryTable;
    private JButton pressMeButton;
    private JComboBox<String> chooseContinent;
    private JButton addNewCountryButton;
    private JButton searchCountryButton;
    private String[] columnNames = {"Country", "Continent"};

    Connection connection;


    public ShowCountryForm(){
        this.setContentPane(this.MainPanel);
        setTitle("Countries");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        this.setSize(400, 400);

        countryTable.setAutoResizeMode(JTable.WIDTH);
        addNewCountryButton.setBorder(BorderFactory.createBevelBorder(EtchedBorder.RAISED));
        searchCountryButton.setBorder(BorderFactory.createBevelBorder(EtchedBorder.RAISED));
        pressMeButton.setBorder(BorderFactory.createBevelBorder(EtchedBorder.RAISED));
    }

    public JTable getCountryTable() {
        return countryTable;
    }

    public void setCountryTable(JTable countryTable) {
        this.countryTable = countryTable;
    }

    public JButton getPressMeButton() {
        return pressMeButton;
    }

    public void setPressMeButton(JButton pressMeButton) {
        this.pressMeButton = pressMeButton;
    }

    public JComboBox<String> getChooseContinent() {
        return chooseContinent;
    }

    public void setChooseContinent(JComboBox<String> chooseContinent) {
        this.chooseContinent = chooseContinent;
    }

    public JButton getAddNewCountryButton() {
        return addNewCountryButton;
    }

    public void setAddNewCountryButton(JButton addNewCountryButton) {
        this.addNewCountryButton = addNewCountryButton;
    }

    public JButton getSearchCountryButton() {
        return searchCountryButton;
    }

    public void setSearchCountryButton(JButton searchCountryButton) {
        this.searchCountryButton = searchCountryButton;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void addChooseContinentListener(ActionListener action){
        chooseContinent.addActionListener(action);
    }

    public void addNewCountryButtonListener(ActionListener actionListener){
        addNewCountryButton.addActionListener(actionListener);
    }

    public void addSearchButtonListener(ActionListener l){
        searchCountryButton.addActionListener(l);
    }

    public void addPressMeListener(ActionListener l){
        pressMeButton.addActionListener(l);
    }
}
