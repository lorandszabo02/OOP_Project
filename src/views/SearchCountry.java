package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchCountry extends JFrame{
    private JTextField searchText;
    private JButton searchButton;
    private JPanel SearchPanel;
    private JLabel invalidNameLabel;
    private JButton updateButton;
    private JButton deleteButton;
    private JTextField newNameText;
    private JComboBox continentComboBox;
    private JButton submitUpdateButton;

    public SearchCountry() {
        this.setContentPane(this.SearchPanel);
        this.setTitle("Search country");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        this.setVisible(true);


        invalidNameLabel.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        newNameText.setVisible(false);
        continentComboBox.setVisible(false);
        submitUpdateButton.setVisible(false);
    }

    public void addDeleteButtonListener(ActionListener l){
        deleteButton.addActionListener(l);
    }

    public void addUpdateButtonListener(ActionListener l){
        updateButton.addActionListener(l);
    }

    public JTextField getSearchText() {
        return searchText;
    }

    public void setSearchText(JTextField searchText) {
        this.searchText = searchText;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(JButton searchButton) {
        this.searchButton = searchButton;
    }

    public JLabel getInvalidNameLabel() {
        return invalidNameLabel;
    }

    public void setInvalidNameLabel(JLabel invalidNameLabel) {
        this.invalidNameLabel = invalidNameLabel;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(JButton updateButton) {
        this.updateButton = updateButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(JButton deleteButton) {
        this.deleteButton = deleteButton;
    }

    public JTextField getNewNameText() {
        return newNameText;
    }

    public void setNewNameText(JTextField newNameText) {
        this.newNameText = newNameText;
    }

    public JComboBox getContinentComboBox() {
        return continentComboBox;
    }

    public void setContinentComboBox(JComboBox continentComboBox) {
        this.continentComboBox = continentComboBox;
    }

    public JButton getSubmitUpdateButton() {
        return submitUpdateButton;
    }

    public void setSubmitUpdateButton(JButton submitUpdateButton) {
        this.submitUpdateButton = submitUpdateButton;
    }

    public void addSubmitUpdateButtonListener(ActionListener l){
        submitUpdateButton.addActionListener(l);
    }

    public void addSearchButtonListener(ActionListener l){
        searchButton.addActionListener(l);
    }
}
