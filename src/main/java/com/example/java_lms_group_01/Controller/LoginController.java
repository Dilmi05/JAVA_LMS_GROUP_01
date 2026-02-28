package com.example.java_lms_group_01.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private ComboBox<?> comboGender;

    @FXML
    private ComboBox<?> comboRole;

    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPass;

    @FXML
    private TextField regAddress;

    @FXML
    private DatePicker regDob;

    @FXML
    private TextField regEmail;

    @FXML
    private TextField regFirstName;

    @FXML
    private TextField regLastName;

    @FXML
    private TextField regPhone;

    @FXML
    void btnOnActionCreateAccount(ActionEvent event) {

    }

    @FXML
    void btnOnActionLogin(ActionEvent event) {

    }

}
