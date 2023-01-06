package views;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddCountry extends JFrame{
    private JTextField nameTextField;
    private JComboBox<String> continentComboBox;
    private JButton addCountryButton;
    private JPanel NewCountryPanel;
    private JTextField countryID;
    private JSeparator separator1;
    private JSeparator separator2;
    private JSeparator separator3;
    private JSeparator separator4;
    private JSeparator separator5;
    private JSeparator separator6;
    private JSeparator separator7;
    private JLabel IDInvalidLabel;
    private JLabel NameInvalidLabel;
    private JLabel ContinentInvalidLabel;


    public JTextField getNameTextField() {
        return nameTextField;
    }

    public void setNameTextField(JTextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    public JComboBox<String> getContinentComboBox() {
        return continentComboBox;
    }

    public void setContinentComboBox(JComboBox<String> continentComboBox) {
        this.continentComboBox = continentComboBox;
    }

    public JButton getAddCountryButton() {
        return addCountryButton;
    }

    public void setAddCountryButton(JButton addCountryButton) {
        this.addCountryButton = addCountryButton;
    }

    public JTextField getCountryID() {
        return countryID;
    }

    public void setCountryID(JTextField countryID) {
        this.countryID = countryID;
    }

    public JLabel getIDInvalidLabel() {
        return IDInvalidLabel;
    }

    public void setIDInvalidLabel(JLabel IDInvalidLabel) {
        this.IDInvalidLabel = IDInvalidLabel;
    }

    public JLabel getNameInvalidLabel() {
        return NameInvalidLabel;
    }

    public void setNameInvalidLabel(JLabel nameInvalidLabel) {
        NameInvalidLabel = nameInvalidLabel;
    }

    public JLabel getContinentInvalidLabel() {
        return ContinentInvalidLabel;
    }

    public void setContinentInvalidLabel(JLabel continentInvalidLabel) {
        ContinentInvalidLabel = continentInvalidLabel;
    }

    public AddCountry(){
        this.setContentPane(this.NewCountryPanel);
        this.setTitle("New country");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        this.setVisible(true);

        setValidationLabelsInvisible();
        setSeparatorsInvisible();
        addCountryButton.setBorder(BorderFactory.createBevelBorder(EtchedBorder.RAISED));
    }

    public void setSeparatorsInvisible(){
        separator1.setVisible(false);
        separator2.setVisible(false);
        separator3.setVisible(false);
        separator4.setVisible(false);
        separator5.setVisible(false);
        separator6.setVisible(false);
        separator7.setVisible(false);
    }

    public void setValidationLabelsInvisible(){
        IDInvalidLabel.setVisible(false);
        NameInvalidLabel.setVisible(false);
        ContinentInvalidLabel.setVisible(false);
    }

    public void addAddCountryButtonListener(ActionListener l){
        addCountryButton.addActionListener(l);
    }
}