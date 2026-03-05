package com.example.java_lms_group_01.Controller.AdminDashboard;

import com.example.java_lms_group_01.Repository.UserRepository;
import com.example.java_lms_group_01.model.UserManagementRow;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {

    @FXML
    private TabPane tabUsers;
    @FXML
    private Tab tabAdmins;
    @FXML
    private Tab tabLecturers;
    @FXML
    private Tab tabStudents;
    @FXML
    private Tab tabTechnicalOfficers;

    @FXML
    private TableColumn<UserManagementRow, String> adminAccessLevel;
    @FXML
    private TableColumn<UserManagementRow, Number> adminDeptId;
    @FXML
    private TableColumn<UserManagementRow, String> adminEmail;
    @FXML
    private TableColumn<UserManagementRow, String> adminFirstName;
    @FXML
    private TableColumn<UserManagementRow, String> adminGender;
    @FXML
    private TableColumn<UserManagementRow, Number> adminId;
    @FXML
    private TableColumn<UserManagementRow, String> adminLastName;
    @FXML
    private TableColumn<UserManagementRow, String> adminPhone;
    @FXML
    private TableView<UserManagementRow> tblAdmins;

    @FXML
    private TableColumn<UserManagementRow, Number> lecDeptId;
    @FXML
    private TableColumn<UserManagementRow, String> lecEmail;
    @FXML
    private TableColumn<UserManagementRow, String> lecFirstName;
    @FXML
    private TableColumn<UserManagementRow, String> lecGender;
    @FXML
    private TableColumn<UserManagementRow, Number> lecId;
    @FXML
    private TableColumn<UserManagementRow, String> lecLastName;
    @FXML
    private TableColumn<UserManagementRow, String> lecPhone;
    @FXML
    private TableColumn<UserManagementRow, String> lecPosition;
    @FXML
    private TableColumn<UserManagementRow, String> lecRegNo;
    @FXML
    private TableView<UserManagementRow> tblLecturers;

    @FXML
    private TableColumn<UserManagementRow, String> stuBatchId;
    @FXML
    private TableColumn<UserManagementRow, Number> stuDeptId;
    @FXML
    private TableColumn<UserManagementRow, String> stuEmail;
    @FXML
    private TableColumn<UserManagementRow, String> stuFirstName;
    @FXML
    private TableColumn<UserManagementRow, String> stuGender;
    @FXML
    private TableColumn<UserManagementRow, Number> stuId;
    @FXML
    private TableColumn<UserManagementRow, String> stuLastName;
    @FXML
    private TableColumn<UserManagementRow, String> stuPhone;
    @FXML
    private TableColumn<UserManagementRow, String> stuRegNo;
    @FXML
    private TableColumn<UserManagementRow, String> stuStatus;
    @FXML
    private TableView<UserManagementRow> tblStudents;

    @FXML
    private TableColumn<UserManagementRow, Number> toDeptId;
    @FXML
    private TableColumn<UserManagementRow, String> toEmail;
    @FXML
    private TableColumn<UserManagementRow, String> toFirstName;
    @FXML
    private TableColumn<UserManagementRow, String> toGender;
    @FXML
    private TableColumn<UserManagementRow, Number> toId;
    @FXML
    private TableColumn<UserManagementRow, String> toLab;
    @FXML
    private TableColumn<UserManagementRow, String> toLastName;
    @FXML
    private TableColumn<UserManagementRow, String> toPhone;
    @FXML
    private TableColumn<UserManagementRow, String> toPosition;
    @FXML
    private TableColumn<UserManagementRow, String> toShift;
    @FXML
    private TableView<UserManagementRow> tblTechnicalOfficers;

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureAdminTable();
        configureLecturerTable();
        configureStudentTable();
        configureTechnicalOfficerTable();
        loadAllTables();
    }

    @FXML
    void btnOnActionRefresh(ActionEvent event) {
        loadAllTables();
    }

    @FXML
    void btnOnActionAdd(ActionEvent event) {
        String role = getActiveRole();
        if (role == null) {
            return;
        }

        try {
            UserManagementRow row = showRoleDialog(role, null);
            if (row == null) {
                return;
            }

            boolean created = switch (role) {
                case "Admin" -> userRepository.createAdmin(row);
                case "Lecturer" -> userRepository.createLecturer(row);
                case "Student" -> userRepository.createStudent(row);
                case "TechnicalOfficer" -> userRepository.createTechnicalOfficer(row);
                default -> false;
            };

            if (created) {
                loadAllTables();
                showInfo(role + " created successfully.");
            }
        } catch (IllegalArgumentException e) {
            showInfo(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to add " + role + ".", e);
        }
    }

    @FXML
    void btnOnActionEdit(ActionEvent event) {
        String role = getActiveRole();
        UserManagementRow selected = getSelectedRowByRole(role);
        if (selected == null) {
            showInfo("Please select a row in the active tab.");
            return;
        }

        try {
            UserManagementRow row = showRoleDialog(role, selected);
            if (row == null) {
                return;
            }

            boolean updated = switch (role) {
                case "Admin" -> userRepository.updateAdmin(row);
                case "Lecturer" -> userRepository.updateLecturer(row);
                case "Student" -> userRepository.updateStudent(row);
                case "TechnicalOfficer" -> userRepository.updateTechnicalOfficer(row);
                default -> false;
            };

            if (updated) {
                loadAllTables();
                showInfo(role + " updated successfully.");
            }
        } catch (IllegalArgumentException e) {
            showInfo(e.getMessage());
        } catch (SQLException e) {
            showError("Failed to update " + role + ".", e);
        }
    }

    @FXML
    void btnOnActionDelete(ActionEvent event) {
        String role = getActiveRole();
        UserManagementRow selected = getSelectedRowByRole(role);
        if (selected == null) {
            showInfo("Please select a row in the active tab.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText("Delete " + role);
        confirmation.setContentText("Delete user ID " + selected.getUserId() + " and related " + role + " record?");
        Optional<ButtonType> answer = confirmation.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = switch (role) {
                case "Admin" -> userRepository.deleteAdmin(selected.getUserId());
                case "Lecturer" -> userRepository.deleteLecturer(selected.getUserId());
                case "Student" -> userRepository.deleteStudent(selected.getUserId());
                case "TechnicalOfficer" -> userRepository.deleteTechnicalOfficer(selected.getUserId());
                default -> false;
            };

            if (deleted) {
                loadAllTables();
                showInfo(role + " deleted successfully.");
            }
        } catch (SQLException e) {
            showError("Failed to delete " + role + ".", e);
        }
    }

    private String getActiveRole() {
        Tab selectedTab = tabUsers.getSelectionModel().getSelectedItem();
        if (selectedTab == tabAdmins) {
            return "Admin";
        }
        if (selectedTab == tabLecturers) {
            return "Lecturer";
        }
        if (selectedTab == tabStudents) {
            return "Student";
        }
        if (selectedTab == tabTechnicalOfficers) {
            return "TechnicalOfficer";
        }
        return null;
    }

    private UserManagementRow getSelectedRowByRole(String role) {
        return switch (role) {
            case "Admin" -> tblAdmins.getSelectionModel().getSelectedItem();
            case "Lecturer" -> tblLecturers.getSelectionModel().getSelectedItem();
            case "Student" -> tblStudents.getSelectionModel().getSelectedItem();
            case "TechnicalOfficer" -> tblTechnicalOfficers.getSelectionModel().getSelectedItem();
            default -> null;
        };
    }

    private UserManagementRow showRoleDialog(String role, UserManagementRow existing) {
        return switch (role) {
            case "Admin" -> showAdminDialog(existing);
            case "Lecturer" -> showLecturerDialog(existing);
            case "Student" -> showStudentDialog(existing);
            case "TechnicalOfficer" -> showTechnicalOfficerDialog(existing);
            default -> null;
        };
    }

    private UserManagementRow showAdminDialog(UserManagementRow existing) {
        boolean edit = existing != null;
        Dialog<UserManagementRow> dialog = baseDialog(edit ? "Edit Admin" : "Add Admin", edit);

        TextField[] common = commonFields(existing);
        DatePicker dob = dateOfBirthPicker(existing);
        ComboBox<String> gender = genderBox(existing);

        TextField txtDeptId = new TextField(edit && existing.getDeptId() != null ? String.valueOf(existing.getDeptId()) : "");
        DatePicker dateAppointment = new DatePicker(edit ? existing.getDateOfAppointment() : LocalDate.now());
        TextField txtAccessLevel = new TextField(edit ? value(existing.getAccessLevel()) : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int r = addCommonGrid(grid, common, dob, gender);
        grid.add(new Label("Dept ID:"), 0, r);
        grid.add(txtDeptId, 1, r++);
        grid.add(new Label("Date of Appointment:"), 0, r);
        grid.add(dateAppointment, 1, r++);
        grid.add(new Label("Access Level:"), 0, r);
        grid.add(txtAccessLevel, 1, r);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                return null;
            }
            return new UserManagementRow(
                    edit ? existing.getUserId() : 0,
                    required(common[0], "First name"),
                    required(common[1], "Last name"),
                    required(common[2], "Email"),
                    value(common[3]),
                    value(common[4]),
                    dob.getValue(),
                    gender.getValue(),
                    "Admin",
                    null,
                    parseRequiredInt(txtDeptId, "Dept ID"),
                    null,
                    null,
                    null,
                    dateAppointment.getValue(),
                    value(txtAccessLevel),
                    null,
                    null,
                    null,
                    null
            );
        });

        return dialog.showAndWait().orElse(null);
    }

    private UserManagementRow showLecturerDialog(UserManagementRow existing) {
        boolean edit = existing != null;
        Dialog<UserManagementRow> dialog = baseDialog(edit ? "Edit Lecturer" : "Add Lecturer", edit);

        TextField[] common = commonFields(existing);
        DatePicker dob = dateOfBirthPicker(existing);
        ComboBox<String> gender = genderBox(existing);

        TextField txtReg = new TextField(edit ? value(existing.getRegistrationNo()) : "");
        TextField txtDeptId = new TextField(edit && existing.getDeptId() != null ? String.valueOf(existing.getDeptId()) : "");
        TextField txtPosition = new TextField(edit ? value(existing.getPosition()) : "");
        DatePicker dateJoining = new DatePicker(edit ? existing.getDateOfJoining() : LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int r = addCommonGrid(grid, common, dob, gender);
        grid.add(new Label("Registration No:"), 0, r);
        grid.add(txtReg, 1, r++);
        grid.add(new Label("Dept ID:"), 0, r);
        grid.add(txtDeptId, 1, r++);
        grid.add(new Label("Position:"), 0, r);
        grid.add(txtPosition, 1, r++);
        grid.add(new Label("Date of Joining:"), 0, r);
        grid.add(dateJoining, 1, r);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                return null;
            }
            return new UserManagementRow(
                    edit ? existing.getUserId() : 0,
                    required(common[0], "First name"),
                    required(common[1], "Last name"),
                    required(common[2], "Email"),
                    value(common[3]),
                    value(common[4]),
                    dob.getValue(),
                    gender.getValue(),
                    "Lecturer",
                    required(txtReg, "Registration No"),
                    parseRequiredInt(txtDeptId, "Dept ID"),
                    null,
                    null,
                    required(txtPosition, "Position"),
                    null,
                    null,
                    dateJoining.getValue(),
                    null,
                    null,
                    null
            );
        });

        return dialog.showAndWait().orElse(null);
    }

    private UserManagementRow showStudentDialog(UserManagementRow existing) {
        boolean edit = existing != null;
        Dialog<UserManagementRow> dialog = baseDialog(edit ? "Edit Student" : "Add Student", edit);

        TextField[] common = commonFields(existing);
        DatePicker dob = dateOfBirthPicker(existing);
        ComboBox<String> gender = genderBox(existing);

        TextField txtReg = new TextField(edit ? value(existing.getRegistrationNo()) : "");
        TextField txtDeptId = new TextField(edit && existing.getDeptId() != null ? String.valueOf(existing.getDeptId()) : "");
        TextField txtBatchId = new TextField(edit && existing.getBatchId() != null ? String.valueOf(existing.getBatchId()) : "");
        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("proper", "repeat", "suspended");
        cmbStatus.setValue(edit ? value(existing.getStatus()) : "proper");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int r = addCommonGrid(grid, common, dob, gender);
        grid.add(new Label("Registration No:"), 0, r);
        grid.add(txtReg, 1, r++);
        grid.add(new Label("Dept ID:"), 0, r);
        grid.add(txtDeptId, 1, r++);
        grid.add(new Label("Batch ID (optional):"), 0, r);
        grid.add(txtBatchId, 1, r++);
        grid.add(new Label("Status:"), 0, r);
        grid.add(cmbStatus, 1, r);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                return null;
            }
            return new UserManagementRow(
                    edit ? existing.getUserId() : 0,
                    required(common[0], "First name"),
                    required(common[1], "Last name"),
                    required(common[2], "Email"),
                    value(common[3]),
                    value(common[4]),
                    dob.getValue(),
                    gender.getValue(),
                    "Student",
                    required(txtReg, "Registration No"),
                    parseRequiredInt(txtDeptId, "Dept ID"),
                    parseOptionalInt(txtBatchId),
                    requiredCombo(cmbStatus, "Status"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        });

        return dialog.showAndWait().orElse(null);
    }

    private UserManagementRow showTechnicalOfficerDialog(UserManagementRow existing) {
        boolean edit = existing != null;
        Dialog<UserManagementRow> dialog = baseDialog(edit ? "Edit Technical Officer" : "Add Technical Officer", edit);

        TextField[] common = commonFields(existing);
        DatePicker dob = dateOfBirthPicker(existing);
        ComboBox<String> gender = genderBox(existing);

        TextField txtDeptId = new TextField(edit && existing.getDeptId() != null ? String.valueOf(existing.getDeptId()) : "");
        TextField txtPosition = new TextField(edit ? value(existing.getPosition()) : "");
        TextField txtLab = new TextField(edit ? value(existing.getLabAssigned()) : "");
        TextField txtShift = new TextField(edit ? value(existing.getShift()) : "");
        TextArea txtQualifications = new TextArea(edit ? value(existing.getQualifications()) : "");
        txtQualifications.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int r = addCommonGrid(grid, common, dob, gender);
        grid.add(new Label("Dept ID:"), 0, r);
        grid.add(txtDeptId, 1, r++);
        grid.add(new Label("Position:"), 0, r);
        grid.add(txtPosition, 1, r++);
        grid.add(new Label("Lab Assigned:"), 0, r);
        grid.add(txtLab, 1, r++);
        grid.add(new Label("Shift:"), 0, r);
        grid.add(txtShift, 1, r++);
        grid.add(new Label("Qualifications:"), 0, r);
        grid.add(txtQualifications, 1, r);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                return null;
            }
            return new UserManagementRow(
                    edit ? existing.getUserId() : 0,
                    required(common[0], "First name"),
                    required(common[1], "Last name"),
                    required(common[2], "Email"),
                    value(common[3]),
                    value(common[4]),
                    dob.getValue(),
                    gender.getValue(),
                    "TechnicalOfficer",
                    null,
                    parseRequiredInt(txtDeptId, "Dept ID"),
                    null,
                    null,
                    required(txtPosition, "Position"),
                    null,
                    null,
                    null,
                    value(txtLab),
                    value(txtShift),
                    value(txtQualifications)
            );
        });

        return dialog.showAndWait().orElse(null);
    }

    private Dialog<UserManagementRow> baseDialog(String title, boolean edit) {
        Dialog<UserManagementRow> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(edit ? "Update selected record." : "Enter details.");
        ButtonType save = new ButtonType(edit ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        return dialog;
    }

    private TextField[] commonFields(UserManagementRow existing) {
        TextField txtFirstName = new TextField(existing == null ? "" : value(existing.getFirstName()));
        TextField txtLastName = new TextField(existing == null ? "" : value(existing.getLastName()));
        TextField txtEmail = new TextField(existing == null ? "" : value(existing.getEmail()));
        TextField txtAddress = new TextField(existing == null ? "" : value(existing.getAddress()));
        TextField txtPhone = new TextField(existing == null ? "" : value(existing.getPhoneNumber()));
        return new TextField[]{txtFirstName, txtLastName, txtEmail, txtAddress, txtPhone};
    }

    private DatePicker dateOfBirthPicker(UserManagementRow existing) {
        return new DatePicker(existing == null ? null : existing.getDateOfBirth());
    }

    private ComboBox<String> genderBox(UserManagementRow existing) {
        ComboBox<String> cmbGender = new ComboBox<>();
        cmbGender.getItems().addAll("Male", "Female", "Other");
        cmbGender.setValue(existing == null ? null : existing.getGender());
        return cmbGender;
    }

    private int addCommonGrid(GridPane grid, TextField[] common, DatePicker dob, ComboBox<String> gender) {
        int r = 0;
        grid.add(new Label("First Name:"), 0, r);
        grid.add(common[0], 1, r++);
        grid.add(new Label("Last Name:"), 0, r);
        grid.add(common[1], 1, r++);
        grid.add(new Label("Email:"), 0, r);
        grid.add(common[2], 1, r++);
        grid.add(new Label("Address:"), 0, r);
        grid.add(common[3], 1, r++);
        grid.add(new Label("Phone:"), 0, r);
        grid.add(common[4], 1, r++);
        grid.add(new Label("Date of Birth:"), 0, r);
        grid.add(dob, 1, r++);
        grid.add(new Label("Gender:"), 0, r);
        grid.add(gender, 1, r++);
        return r;
    }

    private void loadAllTables() {
        try {
            tblAdmins.getItems().setAll(userRepository.findAdmins());
            tblLecturers.getItems().setAll(userRepository.findLecturers());
            tblStudents.getItems().setAll(userRepository.findStudents());
            tblTechnicalOfficers.getItems().setAll(userRepository.findTechnicalOfficers());
        } catch (SQLException e) {
            showError("Failed to load user tables.", e);
        }
    }

    private void configureAdminTable() {
        adminId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getUserId()));
        adminFirstName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        adminLastName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        adminEmail.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getEmail())));
        adminPhone.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPhoneNumber())));
        adminGender.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getGender())));
        adminDeptId.setCellValueFactory(d -> new SimpleIntegerProperty(num(d.getValue().getDeptId())));
        adminAccessLevel.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getAccessLevel())));
    }

    private void configureLecturerTable() {
        lecId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getUserId()));
        lecFirstName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        lecLastName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        lecEmail.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getEmail())));
        lecPhone.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPhoneNumber())));
        lecGender.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getGender())));
        lecRegNo.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getRegistrationNo())));
        lecDeptId.setCellValueFactory(d -> new SimpleIntegerProperty(num(d.getValue().getDeptId())));
        lecPosition.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPosition())));
    }

    private void configureStudentTable() {
        stuId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getUserId()));
        stuFirstName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        stuLastName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        stuEmail.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getEmail())));
        stuPhone.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPhoneNumber())));
        stuGender.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getGender())));
        stuRegNo.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getRegistrationNo())));
        stuDeptId.setCellValueFactory(d -> new SimpleIntegerProperty(num(d.getValue().getDeptId())));
        stuBatchId.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getBatchId())));
        stuStatus.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getStatus())));
    }

    private void configureTechnicalOfficerTable() {
        toId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getUserId()));
        toFirstName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        toLastName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        toEmail.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getEmail())));
        toPhone.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPhoneNumber())));
        toGender.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getGender())));
        toDeptId.setCellValueFactory(d -> new SimpleIntegerProperty(num(d.getValue().getDeptId())));
        toPosition.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getPosition())));
        toLab.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getLabAssigned())));
        toShift.setCellValueFactory(d -> new SimpleStringProperty(value(d.getValue().getShift())));
    }

    private int num(Integer n) {
        return n == null ? 0 : n;
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private String value(Integer n) {
        return n == null ? "" : String.valueOf(n);
    }

    private String value(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }

    private String value(TextArea textArea) {
        return textArea.getText() == null ? "" : textArea.getText().trim();
    }

    private String required(TextField textField, String fieldName) {
        String text = value(textField);
        if (text.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return text;
    }

    private String requiredCombo(ComboBox<String> comboBox, String fieldName) {
        String value = comboBox.getValue();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value;
    }

    private Integer parseRequiredInt(TextField textField, String fieldName) {
        try {
            int n = Integer.parseInt(value(textField));
            if (n <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private Integer parseOptionalInt(TextField textField) {
        String text = value(textField);
        if (text.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Batch ID must be a valid number.");
        }
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
