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
    PreparedStatement preparedStatementForTable;
    Statement statement1, statement2;
    ResultSet resultSetForTable, resultSetForComboBox, resultSetForFullTable;

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

        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/travel", "postgres", "postgres");

            statement1 = connection.createStatement();
            statement2 = connection.createStatement();

            preparedStatementForTable = connection.prepareStatement(
                    "select country_name, name from country join continent" +
                            " on country.continent = continent.id where continent.name LIKE ?;"
            );
            resultSetForComboBox = statement2.executeQuery("select name from continent order by name;");
            resultSetForFullTable = statement1.executeQuery("select country_name, name from country join continent on country.continent = continent.id;");

            manageComboBox(chooseContinent, resultSetForComboBox);
            manageCountryTable(countryTable, resultSetForFullTable, columnNames);

            chooseContinent.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    try{
                        String country = (String) chooseContinent.getSelectedItem();
                        if(country.equals("All")){
                            country = "%";
                        }
                        preparedStatementForTable.setString(1, country);
                        resultSetForTable = preparedStatementForTable.executeQuery();
                        manageCountryTable(countryTable, resultSetForTable, columnNames);
                    } catch (Exception ex){
                        System.out.println(ex);
                    }
                }
            });

            addNewCountryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AddCountry addCountry = new AddCountry();
                }
            });

            searchCountryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SearchCountry searchCountry = new SearchCountry();
                }
            });
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
