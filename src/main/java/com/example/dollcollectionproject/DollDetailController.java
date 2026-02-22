package com.example.dollcollectionproject;

import com.example.dollcollectionproject.model.Doll;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DollDetailController {

    @FXML
    private ImageView detailImage;

    @FXML
    private Label detailName;

    @FXML
    private TextArea descriptionArea;

    private Doll currentDoll;

    private javafx.collections.ObservableList<Doll> parentList;

    // This method will be called by the Main Controller to "send" the doll data here
    public void setDoll(Doll doll, javafx.collections.ObservableList<Doll> list) {
        this.currentDoll = doll;
        this.parentList = list;   // to change main list
        detailName.setText(doll.getName());
        descriptionArea.setText(doll.getDescription());

        try {
            Image img = new Image(getClass().getResourceAsStream(doll.getImagePath()));
            detailImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Image not found in closet!");
        }
    }

    @FXML
    private void handleSaveDescription() {
        // ONLY here we SAVE description
        currentDoll.setDescription(descriptionArea.getText());
        System.out.println("Persistent state updated for: " + currentDoll.getName());
    }

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
        // remove from the ObservableList (which automatically updates the UI)
        parentList.remove(currentDoll);

        // kill the window after deleting object, to avoid button clicks etc.
        javafx.stage.Stage stage = (javafx.stage.Stage) descriptionArea.getScene().getWindow();
        stage.close();
        System.out.println("Object destroyed and window closed.");
    }
}