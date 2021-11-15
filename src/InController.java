import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import repositories.Database;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InController {
  private Scene scene;
  private Parent root;
  private Stage stage;
  @FXML
  private TextField licensePlateTextField, timeInField;
  @FXML
  private RadioButton vehicleBicycles, vehicleTypeCar, vehicleTypeMotorbike, carSeats1, carSeats2, carSeats3;
  @FXML
  private Label carSeatsLabel, errorLabel, errorLabel1, errorLabel2;
  @FXML
  private AnchorPane InPane;

  public void closeAPP() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Close!");
    alert.setHeaderText("You're about to close the application!");
    alert.setContentText("Do you want to exit?");
    // Get the Stage.
    stage = (Stage) alert.getDialogPane().getScene().getWindow();
    // Add a custom icon.
    stage.getIcons().add(new Image("images/sgd.png"));
    if (alert.showAndWait().orElse(null) == ButtonType.OK) {
      stage = (Stage) InPane.getScene().getWindow();
      stage.close();
    }
  }

  public void carTypeChecked() {
    //set Car Seats section to disabled state each time "vehicleTypeCar" radiobutton is selected
    if (vehicleBicycles.isSelected() || vehicleTypeMotorbike.isSelected()) {
      carSeatsLabel.setDisable(true);
      carSeats1.setDisable(true);
      carSeats2.setDisable(true);
      carSeats3.setDisable(true);
      timeInField.setText("");
      if (vehicleBicycles.isSelected()) {
        randomLP();
        errorLabel1.setText("");
      } else {
        licensePlateTextField.setText("");
      }
    } else {
      timeInField.setText("");
      carSeatsLabel.setDisable(false);
      carSeats1.setDisable(false);
      carSeats2.setDisable(false);
      carSeats3.setDisable(false);
      licensePlateTextField.setText("");
    }
  }

  public void getTimeIn() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    timeInField.setText(dtf.format(now));
    errorLabel2.setText("");
  }

  public void goToIn(ActionEvent event) throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("InScene.fxml")));
    //Stage stage = (Stage) menuBar.getScene().getWindow();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  private MenuBar menuBar;

  public void goToOut() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("OutScene.fxml")));
    //stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void goToHistory() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("HistoryScene.fxml")));
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void goToAdmin() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AdminScene.fxml")));
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void goToHelp() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("HelpScene.fxml")));
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void goToAbout() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AboutScene.fxml")));
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void logout() throws IOException {
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginScene.fxml")));
    Stage stage = (Stage) menuBar.getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void limitLength() {
    licensePlateTextField.lengthProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.intValue() > oldValue.intValue()) {
        // Check if the new character is greater than LIMIT
        if (licensePlateTextField.getText().length() >= 11) {
          errorLabel.setTextFill(Color.RED);
          errorLabel.setText("License Plate length must be <= 10!");
          errorLabel1.setTextFill(Color.RED);
          errorLabel1.setText("!");
          // if its 11th character then just setText to previous
          // one
          licensePlateTextField.setText(licensePlateTextField.getText().substring(0, 10));
        } else {
          errorLabel1.setText("");
          errorLabel.setText("");
        }
      }
    });
  }

  public int getTotalParking() throws SQLException {
    Connection connection = Database.getInstance().getConnection();
    //Creating the Statement object
    Statement stmt = connection.createStatement();
    //Query to get the number of rows in a table
    String query = "select count(*) from parking where status = 1";
    //Executing the query
    ResultSet rs = stmt.executeQuery(query);
    //Retrieving the result
    rs.next();
    return rs.getInt(1);
  }

  public int getTotalSlots() {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet;
    try {
      connection = Database.getInstance().getConnection();
      preparedStatement = connection.prepareStatement("select slots from pricevsslots where type = 'slotsleft'");
      resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        return resultSet.getInt("slots");
      }
    } catch (SQLException e) {
      Logger.getLogger(InController.class.getName()).log(Level.SEVERE, null, e);
    } finally {
      try {
        if (preparedStatement != null) {
          preparedStatement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        Logger.getLogger(InController.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    return 0;
  }

  public void submitIn() throws SQLException {
    if (timeInField.getText().length() == 0 || licensePlateTextField.getText().length() == 0) {
      if (licensePlateTextField.getText().length() == 0) {
        errorLabel1.setText("!");
      }
      if (timeInField.getText().length() == 0) {
        errorLabel2.setText("!");
      }
      errorLabel.setTextFill(Color.RED);
      errorLabel.setText("Please fill all text field before submit!");
    } else {
      if (getTotalParking() < getTotalSlots()) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        try {
          connection = Database.getInstance().getConnection();
          preparedStatement = connection.prepareStatement("select * from parking where license_plate = ? AND status = 1");
          preparedStatement.setString(1, licensePlateTextField.getText());
          resultSet = preparedStatement.executeQuery();
          if (resultSet.next()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("License plate existing!");
            errorLabel1.setTextFill(Color.RED);
            errorLabel1.setText("!");
          } else {
            preparedStatement = connection.prepareStatement("insert into parking(license_plate, type, seat, ticket, time_in) values(?, ?, ?, ?, ?)");
            preparedStatement.setString(1, licensePlateTextField.getText());
            if (vehicleBicycles.isSelected()) {
              preparedStatement.setString(2, "Bicycles");
            } else if (vehicleTypeMotorbike.isSelected()) {
              preparedStatement.setString(2, "Motorbike");
            } else if (vehicleTypeCar.isSelected()) {
              preparedStatement.setString(2, "Car");
            }
            if (vehicleBicycles.isSelected() || vehicleTypeMotorbike.isSelected()) {
              preparedStatement.setString(3, "0");
            } else if (carSeats1.isSelected()) {
              preparedStatement.setString(3, "4-8");
            } else if (carSeats2.isSelected()) {
              preparedStatement.setString(3, "9-29");
            } else if (carSeats3.isSelected()) {
              preparedStatement.setString(3, "30+");
            }
            PreparedStatement preparedStatement1;
            ResultSet resultSet1;
            preparedStatement1 = connection.prepareStatement("SELECT * FROM ticket LIMIT 0,1");
            resultSet1 = preparedStatement1.executeQuery();
            if (resultSet1.next()) {
              preparedStatement1 = connection.prepareStatement("select * from ticket where license_plate = ?");
              preparedStatement1.setString(1, licensePlateTextField.getText());
              resultSet1 = preparedStatement1.executeQuery();
              if (!resultSet1.next()) {
                preparedStatement.setString(4, "0");
              } else {
                preparedStatement.setString(4, "1");
              }
            } else {
              preparedStatement.setString(4, "0");
            }
            preparedStatement.setString(5, timeInField.getText());
            int kq = preparedStatement.executeUpdate();
            if (kq > 0) {
              errorLabel.setTextFill(Color.GREEN);
              errorLabel.setText("Submitted " + licensePlateTextField.getText() + "!");
            } else {
              errorLabel.setTextFill(Color.RED);
              errorLabel.setText("We can't submitted your record at this time. Please try again!");
            }
          }
        } catch (SQLException e) {
          Logger.getLogger(InController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
          try {
            if (preparedStatement != null) {
              preparedStatement.close();
            }
            if (connection != null) {
              connection.close();
            }
          } catch (SQLException e) {
            Logger.getLogger(InController.class.getName()).log(Level.SEVERE, null, e);
          }
        }
      } else {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText("Out of slots!");
      }
    }
  }

  public void randomLP() {
    // chose a Character random from this String
    String AlphaNumericString = "0123456789";
    // create StringBuffer size of AlphaNumericString
    StringBuilder sb = new StringBuilder(9);
    for (int i = 0; i < 9; i++) {
      // generate a random number between
      // 0 to AlphaNumericString variable length
      int index
        = (int) (AlphaNumericString.length()
        * Math.random());
      // add Character one by one in end of sb
      sb.append(AlphaNumericString
        .charAt(index));
    }
    licensePlateTextField.setText(sb.toString());
  }
}
