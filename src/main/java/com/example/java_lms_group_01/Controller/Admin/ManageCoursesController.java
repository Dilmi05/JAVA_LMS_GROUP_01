package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.UserRecord;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageCoursesController implements Initializable {

    @FXML
    private ComboBox<String> cmbDeptFilter;

    @FXML
    private TableColumn<Course, String> colCourseCode;

    @FXML
    private TableColumn<Course, Number> colCredits;

    @FXML
    private TableColumn<Course, String> colDeptId;

    @FXML
    private TableColumn<Course, String> colHasPractical;

    @FXML
    private TableColumn<Course, String> colHasTheory;

    @FXML
    private TableColumn<Course, String> colLecturerId;

    @FXML
    private TableColumn<Course, String> colName;

    @FXML
    private TableColumn<Course, String> colSemester;

    @FXML
    private TableView<Course> tblCourses;

    @FXML
    private TextField txtSearchCourse;

    private final AdminRepository adminRepository = new AdminRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureColumns();
        loadDepartmentFilter("All");
        loadCourses(null, null);

        txtSearchCourse.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        cmbDeptFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureColumns() {
        colHasPractical.setVisible(false);
        colCourseCode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseCode()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCredits.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCredit()));
        colHasTheory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseType()));
        colLecturerId.setCellValueFactory(data -> new SimpleStringProperty(value(data.getValue().getLecturerRegistrationNo())));
        colDeptId.setCellValueFactory(data -> new SimpleStringProperty(value(data.getValue().getDepartment())));
        colSemester.setCellValueFactory(data -> new SimpleStringProperty(value(data.getValue().getSemester())));
    }

    private void loadDepartmentFilter(String selectedValue) {
        try {
            // set values to combo BOX
            cmbDeptFilter.getItems().clear();
            cmbDeptFilter.getItems().add("All");
            cmbDeptFilter.getItems().addAll(adminRepository.findAllCourseDepartments());

            //To get value that selected user
            cmbDeptFilter.setValue(cmbDeptFilter.getItems().contains(selectedValue) ? selectedValue : "All");
        } catch (SQLException e) {
            showError("Failed to load department filters.", e);
        }
    }

    private void applyFilters() {
        String selectedDept = cmbDeptFilter.getValue();
        String department = "All".equals(selectedDept) ? null : selectedDept;
        loadCourses(department, txtSearchCourse.getText());
    }

    // Load courses using the current filter values.
    private void loadCourses(String department, String keyword) {
        try {
            List<Course> courses = adminRepository.findCoursesByFilters(department, keyword);
            tblCourses.getItems().setAll(courses);
        } catch (SQLException e) {
            showError("Failed to load courses.", e);
        }
    }

    @FXML
    void btnOnActionAddNewCourse(ActionEvent event) {
        Course course = showCourseForm(null);
        if (course == null) {
            return;
        }

        try {
            boolean saved = adminRepository.saveCourse(course);
            if (saved) {
                refreshCourses();
                showInfo("Course added successfully.");
            } else {
                showInfo("No course was added.");
            }
        } catch (SQLException e) {
            showError("Failed to add course.", e);
        }
    }

    @FXML
    void btnOnActionDeleteCourse(ActionEvent event) {
        Course selectedCourse = tblCourses.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showInfo("Please select a course to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText("Delete Course");
        confirmation.setContentText("Delete course " + selectedCourse.getCourseCode() + "?");
        Optional<ButtonType> answer = confirmation.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = adminRepository.deleteCourseByCode(selectedCourse.getCourseCode());
            if (deleted) {
                refreshCourses();
            } else {
                showInfo("No course was deleted.");
            }
        } catch (SQLException e) {
            showError("Failed to delete course.", e);
        }
    }

    @FXML
    void btnOnActionUpdateCourse(ActionEvent event) {
        Course selectedCourse = tblCourses.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showInfo("Please select a course to edit.");
            return;
        }

        Course updatedCourse = showCourseForm(selectedCourse);
        if (updatedCourse == null) {
            return;
        }

        try {
            boolean updated = adminRepository.updateCourse(updatedCourse);
            if (updated) {
                refreshCourses();
                showInfo("Course updated successfully.");
            } else {
                showInfo("No course was updated.");
            }
        } catch (SQLException e) {
            showError("Failed to update course.", e);
        }
    }

    @FXML
    void btnOnActionEnrollStudent(ActionEvent event) {
        Course selectedCourse = tblCourses.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showInfo("Please select a course to enroll a student.");
            return;
        }

        try {
            List<UserRecord> students = adminRepository.findStudents();
            if (students.isEmpty()) {
                showInfo("No students are available for enrollment.");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Enroll Student");
            dialog.setHeaderText("Enroll a student to " + selectedCourse.getCourseCode());

            ButtonType enrollButtonType = new ButtonType("Enroll", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(enrollButtonType, ButtonType.CANCEL);

            ComboBox<UserRecord> cmbStudent = new ComboBox<>();
            cmbStudent.getItems().setAll(students);
            configureStudentComboBox(cmbStudent);

            ComboBox<String> cmbEnrollmentStatus = new ComboBox<>();
            cmbEnrollmentStatus.getItems().addAll("active", "completed", "dropped");
            cmbEnrollmentStatus.setValue("active");

            TextField txtCourseCode = new TextField(selectedCourse.getCourseCode());
            txtCourseCode.setEditable(false);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Course Code:"), 0, 0);
            grid.add(txtCourseCode, 1, 0);
            grid.add(new Label("Student:"), 0, 1);
            grid.add(cmbStudent, 1, 1);
            grid.add(new Label("Enrollment Status:"), 0, 2);
            grid.add(cmbEnrollmentStatus, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(buttonType -> buttonType);

            Button enrollButton = (Button) dialog.getDialogPane().lookupButton(enrollButtonType);
            enrollButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
                UserRecord selectedStudent = cmbStudent.getValue();
                String status = cmbEnrollmentStatus.getValue();

                if (selectedStudent == null) {
                    showInfo("Please select a student.");
                    actionEvent.consume();
                    return;
                }

                if (status == null || status.isBlank()) {
                    showInfo("Please select an enrollment status.");
                    actionEvent.consume();
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() != enrollButtonType) {
                return;
            }

            UserRecord selectedStudent = cmbStudent.getValue();
            String status = cmbEnrollmentStatus.getValue();

            boolean saved = adminRepository.enrollStudentToCourse(
                    selectedStudent.getRegistrationNo(),
                    selectedCourse.getCourseCode(),
                    status
            );

            if (saved) {
                showInfo("Student enrolled successfully.");
            } else {
                showInfo("No enrollment was saved.");
            }
        } catch (SQLException e) {
            showError("Failed to enroll student.", e);
        }
    }

    // ADD and Update Course Form Access method
    private Course showCourseForm(Course existingCourse) {
        boolean editMode = existingCourse != null;
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Edit Course" : "Add New Course");
        dialog.setHeaderText(editMode ? "Update selected course details." : "Enter new course details.");

        ButtonType saveButtonType = new ButtonType(editMode ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        CourseFormController formController;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin/course_form.fxml"));
            Region formRoot = loader.load();
            formController = loader.getController();
            dialog.getDialogPane().setContent(formRoot);
        } catch (IOException e) {
            showError("Failed to open course form.", e);
            return null;
        }

        if (editMode) {
            formController.setupForEdit(existingCourse);
        } else {
            formController.setupForCreate();
        }

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        final Course[] resultHolder = new Course[1];
        saveButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            try {
                resultHolder[0] = formController.buildCourse();
            } catch (IllegalArgumentException e) {
                showInfo(e.getMessage());
                actionEvent.consume();
            }
        });

        dialog.setResultConverter(button -> button == saveButtonType ? resultHolder[0] : null);
        Optional<Course> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void refreshCourses() {
        String selectedDepartment = cmbDeptFilter.getValue();
        loadDepartmentFilter(selectedDepartment == null ? "All" : selectedDepartment);
        applyFilters();
    }

    private void configureStudentComboBox(ComboBox<UserRecord> comboBox) {
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserRecord student) {
                return student == null ? "" : formatStudentLabel(student);
            }

            @Override
            public UserRecord fromString(String string) {
                return null;
            }
        });

        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(UserRecord item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatStudentLabel(item));
            }
        });
    }

    private String formatStudentLabel(UserRecord student) {
        return value(student.getRegistrationNo()) + " - "
                + (value(student.getFirstName()) + " " + value(student.getLastName())).trim();
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
