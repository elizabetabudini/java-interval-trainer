/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import accesoBD.AccesoBD;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Utente
 */
public class ProyectoIPC2 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/FXMLMainMenu.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        AccesoBD db = AccesoBD.getInstance();
        stage.setOnCloseRequest((WindowEvent event) ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close application");
            alert.setHeaderText("Close application");
            alert.setContentText("Are you sure you want to exit the application?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
            
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Save DB");
                alert1.setHeaderText("Saving data in DB");
                alert1.setContentText("The application is saving the changes in the data into the database. This actioncan expend some minutes.");
                alert1.show();
                db.salvar();
            } else {
                event.consume();
                alert.close();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
