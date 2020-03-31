/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import accesoBD.AccesoBD;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import modelo.Sesion;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Utente
 */
public class NewTrainingSessionController implements Initializable {

    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    private ObservableList groupList;
    private Gym gym;
    @FXML
    private TableView<Grupo> groupTable;
    @FXML
    private TableColumn<Grupo, String> codeCol;
    @FXML
    private TableColumn<Grupo, String> descCol;
    @FXML
    private TableColumn<Grupo, Integer> sesCol;
    Utils utils;
    private Grupo grupo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gym=AccesoBD.getInstance().getGym();
        groupList = FXCollections.observableArrayList(gym.getGrupos());
        btnOk.disableProperty().bind(Bindings.size(groupList).isEqualTo(0));
        groupTable.setItems(groupList);
        groupTable.getSelectionModel().selectFirst();
        this.descCol.setCellValueFactory(group -> new SimpleStringProperty(group.getValue().getDescripcion())); 
        this.codeCol.setCellValueFactory(group -> new SimpleStringProperty(group.getValue().getCodigo()));
        this.sesCol.setCellValueFactory(group -> new ReadOnlyObjectWrapper<>(group.getValue().getSesiones().size()));
    
    }    

    @FXML
    private void onOk(ActionEvent event) throws IOException {
        this.grupo=groupTable.getSelectionModel().getSelectedItem();
       
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewTrainingSessionIIpart.fxml"));
            Parent root = (Parent) loader.load();
            NewTrainingSessionIIpartController controller = loader.<NewTrainingSessionIIpartController>getController();
            controller.initialize(grupo);

            Scene SceneTwo = new Scene(root);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setTitle("Start new training session 2/2");
            window.setScene(SceneTwo);
            window.show();
        
    }

    @FXML
    private void onCancel(ActionEvent event) throws IOException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Close window");
                alert.setHeaderText("Close window");
                alert.setContentText("Are you sure you want to close this window? All data won't be saved.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLMainMenu.fxml"));
                    Parent root = (Parent) loader.load();
                    FXMLMainMenuController controller = loader.<FXMLMainMenuController>getController();
                    Scene sceneTwo = new Scene(root);
                    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                    window.setTitle("Main Menu");
                    window.setScene(sceneTwo);
                    window.show();
                } else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
    }

    void setGroup(Grupo grupo) {
        this.groupTable.getSelectionModel().select(grupo);
    }

    
}
