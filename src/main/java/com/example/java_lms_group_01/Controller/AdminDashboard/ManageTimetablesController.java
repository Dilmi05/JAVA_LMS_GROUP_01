package com.example.java_lms_group_01.Controller.AdminDashboard;

import com.example.java_lms_group_01.Repository.TimetableRepository;
import com.example.java_lms_group_01.model.Timetable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageTimetablesController implements Initializable {

    @FXML
    private ComboBox<String> cmbFilterDepartment;

    @FXML
    private ComboBox<String> cmbFilterSemester;

    @FXML
    private TableColumn<Timetable, String> colAcademicYear;

    @FXML
    private TableColumn<Timetable, Number> colDepartmentId;

    @FXML
    private TableColumn<Timetable, Number> colSemester;

    @FXML
    private TableColumn<Timetable, Number> colTimetableId;

    @FXML
    private TableView<Timetable> tblTimetable;

    @FXML
    private TextField txtSearchAcademicYear;

    private final TimetableRepository timetableRepository = new TimetableRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureColumns();
        loadDepartmentFilter("All");
        loadSemesterFilter("All");
        loadTimetables(null, null, null);

        cmbFilterDepartment.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        cmbFilterSemester.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        txtSearchAcademicYear.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureColumns() {
        colTimetableId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTimetableId()));
        colDepartmentId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDepartmentId()));
        colSemester.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSemester()));
        colAcademicYear.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAcademicYear()));
    }

    private void loadDepartmentFilter(String selectedValue) {
        try {
            cmbFilterDepartment.getItems().clear();
            cmbFilterDepartment.getItems().add("All");
            for (Integer departmentId : timetableRepository.findAllDepartmentIds()) {
                cmbFilterDepartment.getItems().add(String.valueOf(departmentId));
            }
            cmbFilterDepartment.setValue(cmbFilterDepartment.getItems().contains(selectedValue) ? selectedValue : "All");
        } catch (SQLException e) {
            showError("Failed to load department filters.", e);
        }
    }

    private void loadSemesterFilter(String selectedValue) {
        try {
            cmbFilterSemester.getItems().clear();
            cmbFilterSemester.getItems().add("All");
            for (Integer semester : timetableRepository.findAllSemesters()) {
                cmbFilterSemester.getItems().add(String.valueOf(semester));
            }
            cmbFilterSemester.setValue(cmbFilterSemester.getItems().contains(selectedValue) ? selectedValue : "All");
        } catch (SQLException e) {
            showError("Failed to load semester filters.", e);
        }
    }

    private void applyFilters() {
        Integer departmentId = null;
        Integer semester = null;

        String selectedDepartment = cmbFilterDepartment.getValue();
        if (selectedDepartment != null && !"All".equals(selectedDepartment)) {
            departmentId = Integer.parseInt(selectedDepartment);
        }

        String selectedSemester = cmbFilterSemester.getValue();
        if (selectedSemester != null && !"All".equals(selectedSemester)) {
            semester = Integer.parseInt(selectedSemester);
        }

        loadTimetables(departmentId, semester, txtSearchAcademicYear.getText());
    }

    private void loadTimetables(Integer departmentId, Integer semester, String academicYearKeyword) {
        try {
            List<Timetable> timetables = timetableRepository.findByFilters(departmentId, semester, academicYearKeyword);
            tblTimetable.getItems().setAll(timetables);
        } catch (SQLException e) {
            showError("Failed to load timetables.", e);
        }
    }

    @FXML
    void btnOnActionAddNewSchedule(ActionEvent event) {
        Timetable timetable = showTimetableDialog(null);
        if (timetable == null) {
            return;
        }

        try {
            boolean saved = timetableRepository.save(timetable);
            if (saved) {
                refreshFiltersAndTable();
                showInfo("Timetable added successfully.");
            } else {
                showInfo("No timetable was added.");
            }
        } catch (SQLException e) {
            showError("Failed to add timetable.", e);
        }
    }

    @FXML
    void btnOnActionDeleteSchedule(ActionEvent event) {
        Timetable selected = tblTimetable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a timetable to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText("Delete Timetable");
        confirmation.setContentText("Delete timetable ID " + selected.getTimetableId() + "?");
        Optional<ButtonType> answer = confirmation.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = timetableRepository.deleteById(selected.getTimetableId());
            if (deleted) {
                refreshFiltersAndTable();
                showInfo("Timetable deleted successfully.");
            } else {
                showInfo("No timetable was deleted.");
            }
        } catch (SQLException e) {
            showError("Failed to delete timetable.", e);
        }
    }

    @FXML
    void btnOnActionUpdateSchedule(ActionEvent event) {
        Timetable selected = tblTimetable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a timetable to edit.");
            return;
        }

        Timetable updated = showTimetableDialog(selected);
        if (updated == null) {
            return;
        }

        try {
            boolean changed = timetableRepository.update(updated);
            if (changed) {
                refreshFiltersAndTable();
                showInfo("Timetable updated successfully.");
            } else {
                showInfo("No timetable was updated.");
            }
        } catch (SQLException e) {
            showError("Failed to update timetable.", e);
        }
    }

    private Timetable showTimetableDialog(Timetable existing) {
        boolean editMode = existing != null;

        Dialog<Timetable> dialog = new Dialog<>();
        dialog.setTitle(editMode ? "Edit Timetable" : "Add Timetable");
        dialog.setHeaderText(editMode ? "Update selected timetable details." : "Enter new timetable details.");

        ButtonType saveButtonType = new ButtonType(editMode ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField txtDepartmentId = new TextField();
        TextField txtSemester = new TextField();
        TextField txtAcademicYear = new TextField();

        if (editMode) {
            txtDepartmentId.setText(String.valueOf(existing.getDepartmentId()));
            txtSemester.setText(String.valueOf(existing.getSemester()));
            txtAcademicYear.setText(existing.getAcademicYear());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Department ID:"), 0, 0);
        grid.add(txtDepartmentId, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(txtSemester, 1, 1);
        grid.add(new Label("Academic Year:"), 0, 2);
        grid.add(txtAcademicYear, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            int departmentId;
            int semester;
            String academicYear = txtAcademicYear.getText() == null ? "" : txtAcademicYear.getText().trim();

            try {
                departmentId = Integer.parseInt(txtDepartmentId.getText().trim());
                semester = Integer.parseInt(txtSemester.getText().trim());
            } catch (NumberFormatException e) {
                showInfo("Department ID and Semester must be valid numbers.");
                return null;
            }

            if (departmentId <= 0 || semester <= 0) {
                showInfo("Department ID and Semester must be positive values.");
                return null;
            }

            if (academicYear.isBlank()) {
                showInfo("Academic Year is required.");
                return null;
            }

            return new Timetable(
                    editMode ? existing.getTimetableId() : 0,
                    departmentId,
                    semester,
                    academicYear
            );
        });

        Optional<Timetable> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void refreshFiltersAndTable() {
        String selectedDepartment = cmbFilterDepartment.getValue();
        String selectedSemester = cmbFilterSemester.getValue();
        loadDepartmentFilter(selectedDepartment);
        loadSemesterFilter(selectedSemester);
        applyFilters();
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
