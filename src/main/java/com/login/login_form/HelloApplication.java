package com.login.login_form;
//We perform all necessary imports
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import java.time.LocalDate;

public class HelloApplication extends Application {
    //Here we declare all the controls precisely with their corresponding "id" that we placed previously
    @FXML
    private TextField txtlogin_username;
    @FXML
    private PasswordField txtlogin_password;
    @FXML
    private TextField txtsignup_username;
    @FXML
    private TextField txtsignup_email;
    @FXML
    private PasswordField txtsignup_password;
    @FXML
    private PasswordField txtsignup_repeatpassword;
    @FXML
    private DatePicker datepick_signup;
    @FXML
    private RadioButton rabtn_signup_male;
    @FXML
    private CheckBox checkbox_signup_terms;

    @FXML
    private void handleSignupButton(ActionEvent event) {
        // Check if the "I agree" checkbox is selected
        if (!checkbox_signup_terms.isSelected()) {
            // Show an error message if the checkbox is not selected
            showAlert("Error", "You must agree to the terms and conditions to sign up.");
            return;
        }

        // Retrieve user input values
        String username = txtsignup_username.getText();
        String email = txtsignup_email.getText();
        String password = txtsignup_password.getText();
        String check_repeatpassword = txtsignup_repeatpassword.getText();
        LocalDate birthdate = datepick_signup.getValue();
        String gender = rabtn_signup_male.isSelected() ? "Male" : "Female";

        // Check if any of the required fields are empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || check_repeatpassword.isEmpty() || birthdate == null || gender.isEmpty()) {
            // Show an error message if any required field is empty
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_shop", "root", "toor");

            // Define an SQL query to insert user data into the database
            String insertQuery = "INSERT INTO Users (username, email, password, birthdate, gender) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setDate(4, java.sql.Date.valueOf(birthdate));
            preparedStatement.setString(5, gender);

            // Execute the SQL query and get the number of affected rows
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Show a success message if the registration is successful
                showAlert("Information", "Registration successful in the database.");
                System.out.println("Registration successful in the database.");
            } else {
                // Show an error message if the registration fails
                System.out.println("Error registering in the database.");
                showAlert("Error", "Error registering in the database.");
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions that may occur
            e.printStackTrace();
        }
    }



    @FXML
    private void handleLoginButton(ActionEvent event) {
        // Get the entered username or email and password from the text fields
        String usernameOrEmail = txtlogin_username.getText();
        String password = txtlogin_password.getText();

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_shop", "root", "toor");

            // Define the SQL query to select user data based on username/email and password
            String query = "SELECT * FROM Users WHERE (username = ? OR email = ?) AND password = ?";

            // Create a prepared statement to execute the SQL query
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usernameOrEmail);
            preparedStatement.setString(2, usernameOrEmail);
            preparedStatement.setString(3, password);

            // Execute the SQL query and retrieve the result set
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a user with the provided credentials exists in the database
            if (resultSet.next()) {
                // If a user is found, display a welcome message with user information
                String welcomeMessage = "Welcome!\n"
                        + "Username: " + resultSet.getString("username")
                        + "\nEmail: " + resultSet.getString("email")
                        + "\nGender: " + resultSet.getString("gender")
                        + "\nDate of Birth: " + resultSet.getString("birthdate");

                showAlert("Successful Login", welcomeMessage);
            } else {
                // If no user is found, display a login failed message
                showAlert("Login Failed", "Incorrect username or password.");
            }
        } catch (SQLException e) {
            // Handle any database-related errors by printing the stack trace
            e.printStackTrace();
        }
    }


    // This method is named showAlert and is declared as private, meaning it can only be accessed within the current class.
    private void showAlert(String title, String content) {
        // Create a new instance of the Alert class with the type INFORMATION.
        Alert alert = new Alert(AlertType.INFORMATION);
        // Set the title of the alert dialog to the provided 'title' parameter.
        alert.setTitle(title);
        // Set the header text of the alert dialog to null, meaning there won't be a header text.
        alert.setHeaderText(null);
        // Set the content text of the alert dialog to the provided 'content' parameter.
        alert.setContentText(content);
        // Show the alert dialog and wait for the user's response before continuing.
        alert.showAndWait();
    }
    @Override
    public void start(Stage stage) throws IOException {
        // Create an instance of FXMLLoader to load an FXML file (XML-based GUI layout).
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Create a new Scene and load the content from the FXML file.
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        // Set the title of the stage (window) to "Login or Sign-Up Form!"
        stage.setTitle("Login or Sign-Up Form!");

        // Set the scene (content) of the stage to the one we created above.
        stage.setScene(scene);

        // Display the stage to show the GUI to the user.
        stage.show();
    }
    public static void main(String[] args) {
        // Launch the JavaFX application.
        launch();
    }

}
