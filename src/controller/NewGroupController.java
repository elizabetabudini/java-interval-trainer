/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import accesoBD.AccesoBD;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Utente
 */
public class NewGroupController implements Initializable {
    
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOK;
    
    private ObservableList<Grupo> obsList;
    @FXML
    private TextField codeField;
    @FXML
    private TextArea descField;
    
    private AccesoBD db;
    private Gym gym;

    @FXML
    private Label alert;
    private boolean noemptyID;
    private boolean existingID;
    
    private final ChangeListener<String> checkId = 
    (obVal, oldVal, newVal) -> {
        if(Utils.groupIsPresent(gym.getGrupos(), codeField.getText())){
            alert.setVisible(true);
            existingID=false;
        } else {
            alert.setVisible(false);
            existingID=true;
        }
        
        noemptyID = !codeField.getText().equals("") && !descField.getText().equals("");
        btnOK.setDisable(!(noemptyID && existingID));

           
    };
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnOK.setDisable(true);
        alert.setVisible(false);
        db = AccesoBD.getInstance();
        gym = db.getGym();
        codeField.textProperty().addListener(checkId);
        descField.textProperty().addListener(checkId);
    }    

    void initialize(ObservableList<Grupo> groupList) {
        this.obsList = groupList;
    }

    @FXML
    private void onCancel(ActionEvent event) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Close window");
                alert.setHeaderText("Close window");
                alert.setContentText("Are you sure you want to close this window? All data won't be saved.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    close();
                } else {
                    event.consume();
                    alert.close();
                }
    }
    
    private void close() {
        Stage currentStage = (Stage) btnCancel.getScene().getWindow();
        currentStage.close();
    }
    
    @FXML
    private void onSave(ActionEvent event) {
        String code = codeField.getText();
        String desc = descField.getText();
        if (code.equals("") || desc.equals(""))
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save data");
                alert.setHeaderText("New group");
                alert.setContentText("Are you sure you want to save this group?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Grupo p = new Grupo();
                    p.setCodigo(code);
                    p.setDescripcion(desc);
                    if(!gym.getTiposSesion().isEmpty()){
                        p.setDefaultTipoSesion(gym.getTiposSesion().get(0));
                    }
                    obsList.add(p);
                    gym.getGrupos().add(p);
                    close();
                } else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
    }
    
}
