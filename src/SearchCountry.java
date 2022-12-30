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

    public SearchCountry() {
        this.setContentPane(this.SearchPanel);
        this.setTitle("Search country");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        this.setVisible(true);

        setPlaceHolder(searchText, searchPlaceHolder);
        setPlaceHolder(newNameText, newNamePlaceHolder);
        invalidNameLabel.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        newNameText.setVisible(false);
        continentComboBox.setVisible(false);
        submitUpdateButton.setVisible(false);

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

            continentStatement = connection.createStatement();
            continentResultSet = continentStatement.executeQuery("select name from continent order by name");
            manageComboBox(continentComboBox, continentResultSet);
        } catch (Exception ex){
            System.out.println(ex);
        }

        deleteButton.addActionListener(new DeleteActionListener());

        updateButton.addActionListener(new UpdateButtonActionListener());

        submitUpdateButton.addActionListener(new SubmitUpdateActionListener());

        searchButton.addActionListener(new SearchButtonActionListener());
    }

    // somehow create a class to contain reused code -> in 'controllers' package
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


    class UpdateButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            continentComboBox.setVisible(true);
            newNameText.setVisible(true);
            submitUpdateButton.setVisible(true);
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
                deletePreparedStatement.setString(1, searchText.getText());
                deletePreparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        searchText.getText() + " deleted", "Delete message", JOptionPane.INFORMATION_MESSAGE);

                updateButton.setVisible(false);
                deleteButton.setVisible(false);
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

                String continent = continentComboBox.getSelectedItem().toString();
                System.out.println(continent);

                if(newNameText.getText().equals("") || newNameText.getText().equals(newNamePlaceHolder)){
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
                        continentIdPreparedStatement.setString(1, continentComboBox.getSelectedItem().toString());
                        continentIdResultSet = continentIdPreparedStatement.executeQuery();
                        if(continentIdResultSet.next()){
                            int continentId = continentIdResultSet.getInt("id");

                            //idk why the statement below works only if it is in a single line
                            updatePreparedStatement = connection.prepareStatement(
                                    "UPDATE country SET continent = ? WHERE country_name = ?"
                            );
                            updatePreparedStatement.setInt(1, continentId);
                            updatePreparedStatement.setString(2, searchText.getText());

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
                        updatePreparedStatement.setString(1, newNameText.getText());
                        updatePreparedStatement.setString(2, searchText.getText());
                        updatePreparedStatement.executeUpdate();
                    } else {
                        // both updated
                        continentIdPreparedStatement = connection.prepareStatement(
                                "SELECT id FROM continent WHERE name = ?"
                        );
                        continentIdPreparedStatement.setString(1, continentComboBox.getSelectedItem().toString());
                        continentIdResultSet = continentIdPreparedStatement.executeQuery();
                        if (continentIdResultSet.next()) {
                            int continentId = continentIdResultSet.getInt("id");
                            updatePreparedStatement = connection.prepareStatement(
                                    "UPDATE country SET country_name = ?, continent = ? WHERE country_name = ?"
                            );
                            updatePreparedStatement.setString(1, newNameText.getText());
                            updatePreparedStatement.setInt(2, continentId);
                            updatePreparedStatement.setString(3, searchText.getText());

                            updatePreparedStatement.executeUpdate();
                        } else {
                            // shouldn't enter here
                            JOptionPane.showMessageDialog(null,
                                    "Continent ID not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                JOptionPane.showMessageDialog(null,
                        searchText.getText() + " updated", "Update message", JOptionPane.INFORMATION_MESSAGE);

                continentComboBox.setVisible(false);
                newNameText.setVisible(false);
                updateButton.setVisible(false);
                deleteButton.setVisible(false);
                submitUpdateButton.setVisible(false);
            }catch (Exception ex){
                System.out.println(ex);
            }
        }
    }

    // controller for submit button when submitting update
    class SearchButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String nameEntered = searchText.getText();
            if (nameEntered == null || nameEntered.equals("") || nameEntered.equals(searchPlaceHolder)) {
                invalidNameLabel.setVisible(true);
                updateButton.setVisible(false);
                deleteButton.setVisible(false);
            } else {
                String countryFound = validCountryName(nameEntered);
                if (countryFound != null) {
                    invalidNameLabel.setVisible(false);
                    updateButton.setVisible(true);
                    deleteButton.setVisible(true);
                } else {
                    invalidNameLabel.setText("Invalid name");
                    invalidNameLabel.setVisible(true);
                    updateButton.setVisible(false);
                    deleteButton.setVisible(false);
                }
            }
        }
    }
}
