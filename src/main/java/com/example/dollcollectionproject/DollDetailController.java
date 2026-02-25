package com.example.dollcollectionproject;

import com.example.dollcollectionproject.model.Doll;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DollDetailController {

    @FXML
    private ScrollPane detailScrollPane;
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


    // This method is called by the Main Controller (HelloController) to pass selected Doll's info
    public void setDoll(Doll doll, javafx.collections.ObservableList<Doll> list) {
        this.currentDoll = doll;    // to change the doll whose details we view
        this.parentList = list;     // to change main list

        // This part makes notoriously slow ScrollPane to scroll faster
        detailScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * 7;
            double height = detailScrollPane.getContent().getBoundsInLocal().getHeight();
            detailScrollPane.setVvalue(detailScrollPane.getVvalue() - deltaY / height);
        });

        // Again we use path of folder closet to load Doll image in detail window
        String folderPath = "src/main/resources/com/example/dollcollectionproject/closet/";
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + folderPath + doll.getImagePath());
            detailImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Detail Image not found at: " + folderPath + doll.getImagePath());
        }

        // Filling all fields from the currentDoll object
        detailName.setText(doll.getName());
        hintField.setText(doll.getHint());
        descriptionArea.setText(doll.getDescription());
        brandField.setText(doll.getBrand());
        modelField.setText(doll.getModel());
        yearField.setText(String.valueOf(doll.getYear())); // Convert int to String for the UI

        // run formatter methods and set text formatter here
        detailName.setTextFormatter(getLetterFormatter());
        brandField.setTextFormatter(getLetterFormatter());
        modelField.setTextFormatter(getLetterFormatter());
        yearField.setTextFormatter(getNumberFormatter());
    }

    // This method formats numbers (for year, max 4 digits)
    private javafx.scene.control.TextFormatter<String> getNumberFormatter() {
        return new javafx.scene.control.TextFormatter<>(change ->
                (change.getText().matches("[0-9]*") && change.getControlNewText().length() <= 4) ? change : null);
    }

    // This method formats letters
    private javafx.scene.control.TextFormatter<String> getLetterFormatter() {
        return new javafx.scene.control.TextFormatter<>(change ->
                change.getText().matches("[a-zA-Z\\s]*") ? change : null);
    }

    // Method that saves fields of detail window
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

    // Method that clears fields of detail window (comment/uncomment according to your own needs)
    @FXML
    private void handleClearAllFields() {
        // Decide on what to clear (for now all except name)
        detailName.clear();
        hintField.clear();
        descriptionArea.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();

        System.out.println("Visual area cleared. Object remains unchanged until Save is clicked.");
    }

    // Doll Deleting Method (Step 1), initial selection, confirming, redirecting to security check
    @FXML
    private void handleDeleteDoll() {
        // selecting part
        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Murder");
        confirmAlert.setHeaderText("Are you sure you want to KILL " + currentDoll.getName() + "????");
        confirmAlert.setContentText("This action is EXTREMELY UNETHICAL!!!!");

        // confirming part
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                triggerSecurityChallenge();    // redirecting to security check
            } else {
                System.out.println("Deletion cancelled at Step 1.");
            }
        });
    }

    // Doll Deleting Method (Step 2), passing the 6-digit security check and verifying deletion
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

    // Doll Deleting Method (Step 3), actually deleting from everywhere
    private void terminateDoll() {
        // firstly delete IMAGE FILE from folder 'closet'
        String folderPath = "src/main/resources/com/example/dollcollectionproject/closet/";
        java.io.File fileToDelete = new java.io.File(folderPath + currentDoll.getImagePath());

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("Physical image deleted: " + currentDoll.getImagePath());
            }
        }

        // then delete it in SQL with unique ID
        dbManager.deleteDollById(currentDoll.getId());

        // then REMOVE it from list, to not display in main window
        parentList.remove(currentDoll);

        // lastly CLOSE detail window
        javafx.stage.Stage stage = (javafx.stage.Stage) descriptionArea.getScene().getWindow();
        stage.close();
    }
}