package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.EnrollmentRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminEnrollmentController {

    @FXML
    private TextField txtSearchEnrollment;

    @FXML
    private ComboBox<String> cmbBatchFilter;

    @FXML
    private TableView<EnrollmentRecord> tblEnrollments;

    @FXML
    private TableColumn<EnrollmentRecord, String> colStudentReg;

    @FXML
    private TableColumn<EnrollmentRecord, String> colStudentName;

    @FXML
    private TableColumn<EnrollmentRecord, String> colBatch;

    @FXML
    private TableColumn<EnrollmentRecord, String> colCourseCode;

    @FXML
    private TableColumn<EnrollmentRecord, String> colCourseName;

    @FXML
    private TableColumn<EnrollmentRecord, String> colEnrollmentDate;

    @FXML
    private TableColumn<EnrollmentRecord, String> colStatus;

    private final AdminRepository adminRepository = new AdminRepository();

    @FXML
    public void initialize() {
        configureTable();
        loadBatchFilter("All");
        loadEnrollments();

        txtSearchEnrollment.textProperty().addListener((obs, oldValue, newValue) -> loadEnrollments());
        cmbBatchFilter.valueProperty().addListener((obs, oldValue, newValue) -> loadEnrollments());
    }

    @FXML
    private void btnOnActionAddEnrollment() {
        EnrollmentRecord selected = selectedRecord();
        if (selected == null) {
            return;
        }

        try {
            List<Course> courses = adminRepository.findAvailableCoursesForStudent(selected.getStudentReg());

            if (courses.isEmpty()) {
                showInfo("No available courses for the selected student.");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Enrollment");
            dialog.setHeaderText("Create a new active enrollment for " + selected.getStudentReg());

            ButtonType saveButtonType = new ButtonType("Add Enrollment", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            ComboBox<String> cmbCourse = new ComboBox<>();
            List<String> courseLabels = new ArrayList<>();

            for (Course course : courses) {
                courseLabels.add(value(course.getCourseCode()) + " - " + value(course.getName()));
            }

            cmbCourse.getItems().setAll(courseLabels);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Student Reg:"), 0, 0);
            grid.add(new Label(value(selected.getStudentReg())), 1, 0);
            grid.add(new Label("Student Name:"), 0, 1);
            grid.add(new Label(value(selected.getStudentName())), 1, 1);
            grid.add(new Label("Course:"), 0, 2);
            grid.add(cmbCourse, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(buttonType -> buttonType);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() != saveButtonType) {
                return;
            }

            int selectedIndex = cmbCourse.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) {
                showInfo("Please select a course.");
                return;
            }

            Course selectedCourse = courses.get(selectedIndex);
            boolean saved = adminRepository.createEnrollment(
                    selected.getStudentReg(),
                    selectedCourse.getCourseCode()
            );

            if (saved) {
                loadBatchFilter(cmbBatchFilter.getValue());
                loadEnrollments();
                showInfo("Enrollment created as active.");
            } else {
                showInfo("No enrollment was created.");
            }
        } catch (IllegalArgumentException | SQLException e) {
            showError("Failed to create enrollment.", e);
        }
    }

    @FXML
    private void btnOnActionMakeCompleted() {
        updateSelectedEnrollmentStatus("completed");
    }

    @FXML
    private void btnOnActionMakeDropped() {
        updateSelectedEnrollmentStatus("dropped");
    }

    @FXML
    private void btnOnActionRefresh() {
        loadBatchFilter(cmbBatchFilter.getValue());
        loadEnrollments();
    }

    private void configureTable() {
        colStudentReg.setCellValueFactory(data -> data.getValue().studentRegProperty());
        colStudentName.setCellValueFactory(data -> data.getValue().studentNameProperty());
        colBatch.setCellValueFactory(data -> data.getValue().batchProperty());
        colCourseCode.setCellValueFactory(data -> data.getValue().courseCodeProperty());
        colCourseName.setCellValueFactory(data -> data.getValue().courseNameProperty());
        colEnrollmentDate.setCellValueFactory(data -> data.getValue().enrollmentDateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
    }

    private void loadBatchFilter(String selectedValue) {
        try {
            String currentValue = selectedValue == null ? "All" : selectedValue;
            cmbBatchFilter.getItems().clear();
            cmbBatchFilter.getItems().add("All");
            cmbBatchFilter.getItems().addAll(adminRepository.findStudentBatches());
            cmbBatchFilter.setValue(cmbBatchFilter.getItems().contains(currentValue) ? currentValue : "All");
        } catch (SQLException e) {
            showError("Failed to load batch filter.", e);
        }
    }

    private void loadEnrollments() {
        try {
            List<EnrollmentRecord> enrollments = adminRepository.findEnrollments(
                    txtSearchEnrollment.getText(),
                    cmbBatchFilter.getValue()
            );
            tblEnrollments.getItems().setAll(enrollments);
        } catch (SQLException e) {
            showError("Failed to load students.", e);
        }
    }

    private void updateSelectedEnrollmentStatus(String status) {
        EnrollmentRecord selected = selectedRecord();
        if (selected == null) {
            return;
        }

        if (!selected.hasEnrollment()) {
            showInfo("This student does not have an enrollment for status update.");
            return;
        }

        try {
            boolean updated = adminRepository.updateEnrollmentStatus(selected.getEnrollmentId(), status);
            if (updated) {
                loadEnrollments();
                showInfo("Enrollment status updated to " + status + ".");
            } else {
                showInfo("No enrollment was updated.");
            }
        } catch (IllegalArgumentException | SQLException e) {
            showError("Failed to update enrollment status.", e);
        }
    }

    private EnrollmentRecord selectedRecord() {
        EnrollmentRecord selected = tblEnrollments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a student row.");
            return null;
        }
        return selected;
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Database Error");
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}
