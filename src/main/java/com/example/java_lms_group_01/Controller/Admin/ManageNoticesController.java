package com.example.java_lms_group_01.Controller.Admin;

import com.example.java_lms_group_01.Repository.AdminRepository;
import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.util.LoggedInAdmin;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageNoticesController implements Initializable {

    @FXML
    private TableView<Notice> tblNotices;

    @FXML
    private TableColumn<Notice, Number> colNoticeId;

    @FXML
    private TableColumn<Notice, String> colTitle;

    @FXML
    private TableColumn<Notice, String> colDate;

    @FXML
    private TableColumn<Notice, String> colAuthor;

    @FXML
    private TextField txtSearchNotice;

    private final AdminRepository adminRepository = new AdminRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadNotices(null);

        txtSearchNotice.textProperty().addListener((obs, oldValue, newValue) ->
                loadNotices(newValue)
        );
    }

    private void setupTableColumns() {
        colNoticeId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getNoticeId()));

        colTitle.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPublishDate() == null
                                ? ""
                                : data.getValue().getPublishDate().toString()
                ));

        colAuthor.setCellValueFactory(data ->
                new SimpleStringProperty(getSafeText(data.getValue().getCreatedBy())));
    }

    private void loadNotices(String keyword) {
        try {
            List<Notice> noticeList;

            if (keyword == null || keyword.trim().isEmpty()) {
                noticeList = adminRepository.findAllNotices();
            } else {
                noticeList = adminRepository.findNoticesByKeyword(keyword);
            }

            tblNotices.getItems().setAll(noticeList);

        } catch (SQLException exception) {
            showErrorMessage("Failed to load notices.", exception);
        }
    }

    @FXML
    void btnOnActionAddNewNotice(ActionEvent event) {
        Notice newNotice = openNoticeDialog(null);

        if (newNotice == null) return;

        try {
            boolean isSaved = adminRepository.saveNotice(newNotice);

            if (isSaved) {
                refreshTable();
                showInfoMessage("Notice added successfully.");
            } else {
                showInfoMessage("No notice was added.");
            }

        } catch (SQLException exception) {
            showErrorMessage("Failed to add notice.", exception);
        }
    }

    @FXML
    void btnOnActionDeleteNotice(ActionEvent event) {
        Notice selectedNotice = tblNotices.getSelectionModel().getSelectedItem();

        if (selectedNotice == null) {
            showInfoMessage("Please select a notice to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setHeaderText("Delete Notice");
        confirmationAlert.setContentText("Delete notice: " + selectedNotice.getTitle() + "?");

        Optional<ButtonType> userChoice = confirmationAlert.showAndWait();

        if (userChoice.isEmpty() || userChoice.get() != ButtonType.OK) return;

        try {
            boolean isDeleted = adminRepository.deleteNoticeById(selectedNotice.getNoticeId());

            if (isDeleted) {
                refreshTable();
            } else {
                showInfoMessage("No notice was deleted.");
            }

        } catch (SQLException exception) {
            showErrorMessage("Failed to delete notice.", exception);
        }
    }

    @FXML
    void btnOnActionViewNotice(ActionEvent event) {
        Notice selectedNotice = tblNotices.getSelectionModel().getSelectedItem();

        if (selectedNotice == null) {
            showInfoMessage("Please select a notice to view or edit.");
            return;
        }

        Notice updatedNotice = openNoticeDialog(selectedNotice);

        if (updatedNotice == null) return;

        try {
            boolean isUpdated = adminRepository.updateNotice(updatedNotice);

            if (isUpdated) {
                refreshTable();
                showInfoMessage("Notice updated successfully.");
            } else {
                showInfoMessage("No notice was updated.");
            }

        } catch (SQLException exception) {
            showErrorMessage("Failed to update notice.", exception);
        }
    }

    // ADD and Update Notice Form Access method
    private Notice openNoticeDialog(Notice existingNotice) {

        boolean isEditMode = existingNotice != null;
        String adminRegistrationNumber = getSafeText(LoggedInAdmin.getRegistrationNo());

        Dialog<Notice> noticeDialog = new Dialog<>();
        noticeDialog.setTitle(isEditMode ? "Edit Notice" : "Create Notice");

        ButtonType saveButton = new ButtonType(
                isEditMode ? "Update" : "Save",
                ButtonBar.ButtonData.OK_DONE
        );

        noticeDialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField txtTitle = new TextField();
        TextArea txtContent = new TextArea();
        DatePicker datePicker = new DatePicker();
        TextField txtCreatedBy = new TextField();

        txtContent.setPrefRowCount(5);

        if (isEditMode) {
            txtTitle.setText(existingNotice.getTitle());
            txtContent.setText(existingNotice.getContent());
            datePicker.setValue(existingNotice.getPublishDate());
            txtCreatedBy.setText(getSafeText(existingNotice.getCreatedBy()));
        } else {
            datePicker.setValue(LocalDate.now());
            txtCreatedBy.setText(adminRegistrationNumber);
        }

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        formGrid.add(new Label("Title:"), 0, 0);
        formGrid.add(txtTitle, 1, 0);
        formGrid.add(new Label("Content:"), 0, 1);
        formGrid.add(txtContent, 1, 1);
        formGrid.add(new Label("Publish Date:"), 0, 2);
        formGrid.add(datePicker, 1, 2);
        formGrid.add(new Label("Created By:"), 0, 3);
        formGrid.add(txtCreatedBy, 1, 3);

        noticeDialog.getDialogPane().setContent(formGrid);

        noticeDialog.setResultConverter(button -> {

            if (button != saveButton) return null;

            String title = getTextFieldValue(txtTitle);
            String content = getTextAreaValue(txtContent);
            LocalDate publishDate = datePicker.getValue();

            if (title.isBlank()) {
                showInfoMessage("Title is required.");
                return null;
            }

            if (publishDate == null) {
                showInfoMessage("Publish date is required.");
                return null;
            }

            String createdBy = getTextFieldValue(txtCreatedBy);

            if (createdBy.isBlank()) {
                createdBy = adminRegistrationNumber;
            }

            if (createdBy.isBlank()) {
                showInfoMessage("Created By is required.");
                return null;
            }

            return new Notice(
                    isEditMode ? existingNotice.getNoticeId() : 0,
                    title,
                    content,
                    publishDate,
                    createdBy
            );
        });

        Optional<Notice> result = noticeDialog.showAndWait();
        return result.orElse(null);
    }



    private void refreshTable() {
        loadNotices(txtSearchNotice.getText());
    }

    private String getTextFieldValue(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }

    private String getTextAreaValue(TextArea textArea) {
        return textArea.getText() == null ? "" : textArea.getText().trim();
    }

    private String getSafeText(String text) {
        return text == null ? "" : text;
    }

    private void showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message + "\n" + exception.getMessage());
        alert.showAndWait();
    }
}
