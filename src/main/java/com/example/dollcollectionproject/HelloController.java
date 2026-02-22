package com.example.dollcollectionproject;

import com.example.dollcollectionproject.model.Doll;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class HelloController {

    // This @FXML tag tells Java to look for the ID "dollList" in your FXML file
    @FXML
    private ListView<Doll> dollList;
    // This is for selected image preview
    @FXML
    private ImageView imagePreview;
    // The field where you type the Doll's name
    @FXML
    private TextField nameInput;
    // Creates link to our Database Manager
    private DatabaseManager dbManager = new DatabaseManager();
    // Stores the path from the picker
    private String selectedImagePath = "";



    // Behaves just like main method in normal java code
    @FXML
    public void initialize() {
        // Ask the DatabaseManager for the REAL list of dolls
        List<Doll> data = dbManager.getAllDolls();
        // Clean before using, just in case
        dollList.getItems().clear();
        // Just add the whole Doll objects
        dollList.getItems().addAll(data);

        // Set the "Cell Factory" (The instructions on how to draw a Doll)
        setCustomListCell();

        // Add listener to open new window with details, AND CLEAR SELECTION
        dollList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openDetailWindow(newVal);

                // clear selection so the next click (even on the same item) works
                javafx.application.Platform.runLater(() -> dollList.getSelectionModel().clearSelection());
            }
        });
    }

    // Displaying ListView<Doll> dollList on MAIN WINDOW
    private void setCustomListCell() {
        dollList.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();

            @Override
            protected void updateItem(Doll doll, boolean empty) {
                // This tells parent ListCell provided by JavaFX to prepare basic setup before we start customizing
                super.updateItem(doll, empty);

                if (empty || doll == null) {
                    setText(null);
                    setGraphic(null);
                } else {
//////LATER UNCOMMENT:
//                    String hintText = (doll.getHint() == null || doll.getHint().isEmpty())
//                            ? ""
//                            : " (Hint: " + doll.getHint() + ")";

                    // 1. Set the text
                    setText(doll.getName());
/////////LATER ADD: setText(doll.getName() + hintText);
                    // 2. Load the image from your 'closet', but now with full path (for sql etc.)
                    try {
                        // We add "file:" to the start so Java knows to look on your C: drive
                        javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + doll.getImagePath());

                        imageView.setImage(img);
                        imageView.setFitWidth(75);  // Make it a small thumbnail
                        imageView.setPreserveRatio(true);

                        setGraphic(imageView); // Put the image next to the text
                    } catch (Exception e) {
                        System.out.println("Could not find image: " + doll.getImagePath());
                    }
                }
            }
        });
    }

    // This button opens the Windows/Android file picker. It's for search, not for save!
    @FXML
    protected void onPickFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Doll Image");

        // Ensure the user only sees image folders
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );

        Stage stage = (Stage) nameInput.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // We save the path so we can use it in the Save function below
            selectedImagePath = selectedFile.getAbsolutePath();
            System.out.println("Image ready: " + selectedImagePath);

            // This is where we use imagePreview and view selected image before saving
            Image img = new Image("file:" + selectedImagePath);
            imagePreview.setImage(img);

            // OPTIONAL: Auto-fill the name field with the filename
            nameInput.setText(selectedFile.getName().split("\\.")[0]);

            System.out.println("Image ready and indicator updated: " + selectedImagePath);
        }

    }

    // This button actually talks to the SQL database. It's for actual save.
    @FXML
    protected void onSaveClick() {
        String name = nameInput.getText();

        if (!name.isEmpty() && !selectedImagePath.isEmpty()) {
            // Adding the doll to the SQL 'closet.db'
            dbManager.addDoll(name, selectedImagePath);
            // REFRESH THE LIST:
            dollList.getItems().setAll(dbManager.getAllDolls());
            // Clearing everything for the next doll sql entry
            nameInput.clear();
            selectedImagePath = "";
            imagePreview.setImage(null);
            System.out.println("Doll added successfully!");
        } else {
            System.out.println("Error: Name or Image missing!");
        }
    }

    // Displaying new window with DOLL DETAILS
    private void openDetailWindow(Doll selectedDoll) {
        try {
            // point to the new FXML file and load root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("doll-detail-view.fxml"));
            Parent root = loader.load();

            // get the controller for the NEW detailed doll window
            DollDetailController controller = loader.getController();

            // hand over clicked doll to the new window,
            controller.setDoll(selectedDoll, dollList.getItems());

            // pop the window open
            Stage stage = new Stage();
            stage.setTitle("Doll Profile: " + selectedDoll.getName());
            stage.setScene(new Scene(root));

            // --- THE MAGIC LINE ---
            // This makes the window "Modal", blocking interaction with the main list, and preventing from opening many doll detail windows
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            // ----------------------

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO OPEN: Check if 'doll-detail-view.fxml' is in the right folder!");
        }
    }
}