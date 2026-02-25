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
import java.nio.file.Path;
import java.nio.file.Paths;
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
    // Stores the path from the picker, and store last folder for next use :)
    private String selectedImagePath = "";
    private File lastDirectory = null;


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
            // Manually handle builders detail here to be able to display numbers on left. ListView builder cant arrange all we want.
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(25);    // arrange spacing as you want
            private final javafx.scene.control.Label numberLabel = new javafx.scene.control.Label();
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            private final javafx.scene.control.Label nameLabel = new javafx.scene.control.Label();
            {
                // creating container to arrange specific desired order (user needs can change)
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                container.getChildren().addAll(numberLabel, imageView, nameLabel);
            }
            @Override
            protected void updateItem(Doll doll, boolean empty) {
                // This tells parent ListCell provided by JavaFX to prepare basic setup before we start customizing
                super.updateItem(doll, empty);

                if (empty || doll == null) {
                    setGraphic(null);
                } else {
                    // arrange ordinal numbers
                    numberLabel.setText((getIndex() + 1) + ".");

                    // arrange dolls images
                    String folderPath = "src/main/resources/com/example/dollcollectionproject/closet/";
                    javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + folderPath + doll.getImagePath());
                    imageView.setImage(img);
                    imageView.setFitWidth(75);    // arrange image with as you want
                    imageView.setPreserveRatio(true);

                    // arrange hints (if exist)
                    String hintText = (doll.getHint() == null || doll.getHint().isEmpty()) ? "" : " (" + doll.getHint() + ")";
                    nameLabel.setText(doll.getName() + hintText);

                    // finally display container
                    setGraphic(container);
                }
            }
        });
    }

    // This button opens the Windows/Android file picker. It's for search, not for save!
    @FXML
    protected void onPickFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Doll Image");

        //----NEW: FOLDER MEMORY CHECKED----
        if (lastDirectory != null && lastDirectory.exists()) {
            fileChooser.setInitialDirectory(lastDirectory);
        }

        // Ensure the user only sees image folders
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );

        Stage stage = (Stage) nameInput.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            //----SAVE THE FOLDER FOR NEXT TIME----
            lastDirectory = selectedFile.getParentFile();
            // We save the path so we can use it in the Save function below
            selectedImagePath = selectedFile.getAbsolutePath();
            System.out.println("Image ready: " + selectedImagePath);

            // This is where we use imagePreview and view selected image before saving
            Image img = new Image("file:" + selectedImagePath);
            imagePreview.setImage(img);

            // SMART NAME FILL: Only fill if the user hasn't typed anything yet
            if (nameInput.getText().trim().isEmpty()) {
                nameInput.setText(selectedFile.getName().split("\\.")[0]);
            }

            System.out.println("Image ready and indicator updated: " + selectedImagePath);
        }

    }

    // This button actually talks to the SQL database. It's for actual save. addDoll()
    @FXML
    protected void onSaveClick() {
        String name = nameInput.getText();

        if (!name.isEmpty() && !selectedImagePath.isEmpty()) {
            // 1. Add doll to SQL and CATCH the new ID
            int newId = dbManager.addDoll(name, selectedImagePath);

            if (newId != -1) {
                try {
                    // 2. Prepare the new filename using the ID
                    String extension = selectedImagePath.substring(selectedImagePath.lastIndexOf("."));
                    String newFileName = "doll_" + newId + extension;

                    // 3. Define the destination inside your 'closet' resources folder
                    File sourceFile = new File(selectedImagePath);
                    Path destPath = Paths.get("src/main/resources/com/example/dollcollectionproject/closet/" + newFileName);

                    // 4. Physical Copy/Paste into the project folder
                    java.nio.file.Files.copy(sourceFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // 5. Update SQL to point to the NEW local filename instead of the old desktop path
                    dbManager.updateImagePath(newId, newFileName);

                    System.out.println("Doll added and image saved to closet folder!");
                } catch (Exception e) {
                    System.out.println("File copy failed: " + e.getMessage());
                }
            }

            // REFRESH THE LIST!!!! to show the changes instantly after save
            dollList.getItems().setAll(dbManager.getAllDolls());

            // Clearing everything for the next doll sql entry
            nameInput.clear();
            selectedImagePath = "";
            imagePreview.setImage(null);
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

            //----THE MODALITY LINE----
            // This makes the window "Modal", blocking interaction with the main list, and preventing from opening many doll detail windows
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            //----THE REFRESHING LIST LINE----
            // This makes the window refresh list every time detail window closes
            stage.setOnHiding(event -> {
                System.out.println("Detail window closed. Refreshing main list...");
                // Pull fresh data from SQL so the new Hint shows up
                dollList.getItems().setAll(dbManager.getAllDolls());
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO OPEN: Check if 'doll-detail-view.fxml' is in the right folder!");
        }
    }
}