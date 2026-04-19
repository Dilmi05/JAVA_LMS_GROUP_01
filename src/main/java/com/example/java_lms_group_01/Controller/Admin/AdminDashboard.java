package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.UserImageRepository;
import com.example.java_lms_group_01.util.LoggedInAdmin;
import com.example.java_lms_group_01.util.ProfileImageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;

public class AdminDashboard {

    @FXML
    private Button btnCourses;

    @FXML
    private Button btnNotices;

    @FXML
    private Button btnEnrollments;

    @FXML
    private Button btnTimetable;

    @FXML
    private Button btnUsers;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblAdminRegistrationNo;

    @FXML
    private ImageView imgProfile;

    private final UserImageRepository userImageRepository = new UserImageRepository();

    public void setAdminData(String registrationNumber) {
        LoggedInAdmin.setRegistrationNo(registrationNumber);

        if (lblAdminRegistrationNo != null) {
            lblAdminRegistrationNo.setText("Registration No: " + registrationNumber);
        }

        loadProfileImage(registrationNumber);
    }

    @FXML
    void btnOnActionLogout(ActionEvent event) {
        LoggedInAdmin.clear();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login_page.fxml"));
            Parent rootNode = fxmlLoader.load();

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setTitle("Login Page");
            currentStage.setScene(new Scene(rootNode));
            currentStage.centerOnScreen();

        } catch (IOException exception) {
            System.out.println("Navigation Error Unable to load the login page : "+exception.getMessage());
        }

    }

    @FXML
    void navCourses(ActionEvent event) {
        loadSubView("/view/Admin/manage_course.fxml");
    }

    @FXML
    void navNotices(ActionEvent event) {
        loadSubView("/view/Admin/manage_notices.fxml");
    }

    @FXML
    void navEnrollments(ActionEvent event) {
        loadSubView("/view/Admin/admin_enrollments.fxml");
    }

    @FXML
    void navTimetable(ActionEvent event) {
        loadSubView("/view/Admin/manage_timetables.fxml");
    }

    @FXML
    void navUsers(ActionEvent event) {
        loadSubView("/view/Admin/manage_users.fxml");
    }

    private void loadSubView(String fxmlPath) {
        try {
            URL fxmlResource = getClass().getResource(fxmlPath);

            if (fxmlResource == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
            Parent loadedView = fxmlLoader.load();

            contentArea.getChildren().setAll(loadedView);

            AnchorPane.setTopAnchor(loadedView, 0.0);
            AnchorPane.setBottomAnchor(loadedView, 0.0);
            AnchorPane.setLeftAnchor(loadedView, 0.0);
            AnchorPane.setRightAnchor(loadedView, 0.0);

        } catch (IOException exception) {
            System.err.println("Error loading view: " + fxmlPath);
            exception.printStackTrace();
        }
    }

    private void loadProfileImage(String registrationNumber) {
        try {
            String imagePath = userImageRepository.findImagePathByUserId(registrationNumber);
            ProfileImageUtil.loadImage(imgProfile, imagePath);
        } catch (SQLException exception) {
            ProfileImageUtil.loadImage(imgProfile, null);
        }
    }

}
