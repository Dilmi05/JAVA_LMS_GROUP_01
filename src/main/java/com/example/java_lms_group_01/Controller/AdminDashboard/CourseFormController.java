package com.example.java_lms_group_01.Controller.AdminDashboard;

import com.example.java_lms_group_01.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class CourseFormController {

    @FXML
    private TextField txtCourseCode;

    @FXML
    private TextField txtCredits;

    @FXML
    private TextField txtDeptId;

    @FXML
    private CheckBox chkHasPractical;

    @FXML
    private CheckBox chkHasTheory;

    @FXML
    private TextField txtLecturerId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSemester;

    public void setupForCreate() {
        txtCourseCode.setDisable(false);
        chkHasTheory.setSelected(true);
        chkHasPractical.setSelected(false);
        txtSemester.setText("Level 1 Semester 2");
    }

    public void setupForEdit(Course course) {
        txtCourseCode.setText(course.getCourseCode());
        txtCourseCode.setDisable(true);
        txtName.setText(course.getName());
        txtCredits.setText(String.valueOf(course.getCredits()));
        chkHasTheory.setSelected(course.isHasTheory());
        chkHasPractical.setSelected(course.isHasPractical());
        txtLecturerId.setText(String.valueOf(course.getLecturerId()));
        txtDeptId.setText(String.valueOf(course.getDeptId()));
        txtSemester.setText(course.getSemester());
    }

    public Course buildCourse() {
        String courseCode = value(txtCourseCode);
        String name = value(txtName);
        String semester = value(txtSemester);

        if (courseCode.isBlank()) {
            throw new IllegalArgumentException("Course code is required.");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Course name is required.");
        }
        if (semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required.");
        }

        int credits;
        int lecturerId;
        int deptId;
        try {
            credits = Integer.parseInt(value(txtCredits));
            lecturerId = Integer.parseInt(value(txtLecturerId));
            deptId = Integer.parseInt(value(txtDeptId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Credits, Lecturer ID, and Department ID must be valid numbers.");
        }

        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be greater than 0.");
        }
        if (lecturerId <= 0 || deptId <= 0) {
            throw new IllegalArgumentException("Lecturer ID and Department ID must be positive numbers.");
        }

        return new Course(
                courseCode,
                name,
                credits,
                chkHasTheory.isSelected(),
                chkHasPractical.isSelected(),
                lecturerId,
                deptId,
                semester
        );
    }

    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }
}
