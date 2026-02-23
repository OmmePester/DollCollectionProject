package com.example.dollcollectionproject;

import com.example.dollcollectionproject.model.Doll;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DollDetailController {

    @FXML
    private ImageView detailImage;

    // Use TextField for everything you want the user to be able to edit
    @FXML
    private TextField detailName;
    @FXML
    private TextField hintField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField yearField;

    private DatabaseManager dbManager = new DatabaseManager();

    private Doll currentDoll;

    private javafx.collections.ObservableList<Doll> parentList;

    // This method will be called by the Main Controller to "send" the doll data here
    public void setDoll(Doll doll, javafx.collections.ObservableList<Doll> list) {
        this.currentDoll = doll;    // to change the doll whose details we view
        this.parentList = list;     // to change main list

        // Filling all fields from the currentDoll object
        detailName.setText(doll.getName());
        hintField.setText(doll.getHint());
        descriptionArea.setText(doll.getDescription());
        brandField.setText(doll.getBrand());
        modelField.setText(doll.getModel());
        yearField.setText(String.valueOf(doll.getYear())); // Convert int to String for the UI

        try {
            // Removed getResourceAsStream(); Use "file:" + the absolute path for sql updated version of code
            Image img = new Image("file:" + doll.getImagePath());
            detailImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Image not found: " + doll.getImagePath());
        }
    }

    @FXML
    private void handleSaveDescription() {
        // 1. Collect data from UI (Assuming these fx:ids exist in your builder)
        String newName = detailName.getText(); // If your name is a Label, use currentDoll.getName()
        String newHint = hintField.getText();
        String newDesc = descriptionArea.getText();
        String newBrand = brandField.getText();
        String newModel = modelField.getText();

        // Convert year text to number safely
        int newYear = 0;
        try {
            newYear = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Year was not a number, defaulting to 0");
        }

        // 2. Update the Java Object in memory
        currentDoll.setName(newName);
        currentDoll.setHint(newHint);
        currentDoll.setDescription(newDesc);
        currentDoll.setBrand(newBrand);
        currentDoll.setModel(newModel);
        currentDoll.setYear(newYear);

        // 3. One single call to save everything to the closet.db
        dbManager.updateFullDollDetails(
                currentDoll.getId(),
                newName,
                newHint,
                newDesc,
                newBrand,
                newModel,
                newYear
        );

        System.out.println("Success: Entire profile updated in SQL.");
    }

    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    // HERE THIS CLEARS NOT ALL !!!! WE NEED TO BE ABLE TO CLEAR ALL FIELDS !!!!
    @FXML
    private void handleClearDescription() {
        //  ONLY clears the visual box, never updates without handleSaveDescription
        descriptionArea.clear();
        System.out.println("Visual area cleared. Object remains unchanged until Save is clicked.");
    }

    @FXML
    private void handleDeleteDoll() {
        // STEP 1: The "Simple" Yes/No Confirmation
        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Murder");
        confirmAlert.setHeaderText("Are you sure you want to KILL " + currentDoll.getName() + "????");
        confirmAlert.setContentText("This action is EXTREMELY UNETHICAL!!!!");

        // Java 8+ approach: handle the button choice
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // STEP 2: The "Hardcore" Security Code
                triggerSecurityChallenge();
            } else {
                System.out.println("Deletion cancelled at Step 1.");
            }
        });
    }

    private void triggerSecurityChallenge() {
        // Generate code
        String securityCode = String.valueOf(new java.util.Random().nextInt(900000) + 100000);

        javafx.scene.control.TextInputDialog codeDialog = new javafx.scene.control.TextInputDialog();
        codeDialog.setTitle("Final Security Protocol");
        codeDialog.setHeaderText("To finalize homicide, enter this code: " + securityCode);

        codeDialog.showAndWait().ifPresent(input -> {
            if (input.equals(securityCode)) {
                terminateDoll();
            } else {
                System.out.println("Wrong security code. Termination failed.");
            }
        });
    }

    private void terminateDoll() {
        // 1. Kill it in SQL using the Unique ID
        dbManager.deleteDollById(currentDoll.getId());

        // 2. Kill it in the UI
        parentList.remove(currentDoll);

        // 3. Close window
        javafx.stage.Stage stage = (javafx.stage.Stage) descriptionArea.getScene().getWindow();
        stage.close();
    }
}