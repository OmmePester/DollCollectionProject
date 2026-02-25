package com.example.dollcollectionproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        //----DELETE EVERYTHING STARTS----
//        DatabaseManager dbDebug = new DatabaseManager();
//        dbDebug.fullWipeOut();
//        //----DELETE EVERYTHING ENDS----
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 800);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}