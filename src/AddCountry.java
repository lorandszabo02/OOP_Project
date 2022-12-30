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

    Connection connection;
    Statement continentStatement; // to get continents
    PreparedStatement addCountryStatement; // create operation
    PreparedStatement getContinentIdStatement; // continent id is needed for the insert
    PreparedStatement countExistingCountries; // see if country already exists
    ResultSet continentResultSet;
    ResultSet nrOfExistingCountries; // 1 if country already exists

    public AddCountry(){
        this.setContentPane(this.NewCountryPanel);
        this.setTitle("New country");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        this.setVisible(true);

        setPlaceHolder(nameTextField, "Enter country name");
        setPlaceHolder(countryID, "Enter country ID (3 chars)");

        setValidationLabelsInvisible();
        setSeparatorsInvisible();
        addCountryButton.setBorder(BorderFactory.createBevelBorder(EtchedBorder.RAISED));

        addCountryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateNewCountry();
            }
        });

        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");
            continentStatement = connection.createStatement();
            continentResultSet = continentStatement.executeQuery("select name from continent order by name;");

            manageComboBox(continentComboBox, continentResultSet);
        } catch(Exception e){
            e.printStackTrace();
        }

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

    // function to manage the combo box containing continents
    public void manageComboBox(JComboBox<String> comboBox, ResultSet resultSet){
        try {
            DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
            while(resultSet.next()){
                comboBoxModel.addElement(resultSet.getString("name"));
            }
            comboBox.setModel(comboBoxModel);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setPlaceHolder(JTextField textField, String placeHolder){
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

    public void validateNewCountry(){
        String nameOfCountry = nameTextField.getText();
        String idOfCountry = countryID.getText();
        String continentOfCountry = (String) continentComboBox.getSelectedItem();

        boolean valid = true;
        if(nameOfCountry.length() < 3 || nameOfCountry.equals("Enter country name")){
            NameInvalidLabel.setText("Country name incorrect");
            NameInvalidLabel.setVisible(true);
            valid = false;
        }
        if(idOfCountry.length() != 3){
            IDInvalidLabel.setVisible(true);
            valid = false;
        }
        if(continentOfCountry == null){
            ContinentInvalidLabel.setVisible(true);
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
                    NameInvalidLabel.setText("Country already exists");
                    NameInvalidLabel.setVisible(true);
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
            IDInvalidLabel.setVisible(false);
            NameInvalidLabel.setVisible(false);
            ContinentInvalidLabel.setVisible(false);
            nameTextField.setText("");
            countryID.setText("");

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
}