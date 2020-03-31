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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;

/**
 * FXML Controller class
 *
 * @author Utente
 */
public class FormularioGroupController implements Initializable {

    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private TextArea descField;
    @FXML
    private TextField codeField;
    
    private int editIndex;
    private ObservableList<Grupo> obsList;
    
    private ChangeListener<String> checkEnableConfirm = 
    (obVal, oldVal, newVal) -> {
        if (codeField.getText().equals("") || descField.getText().equals("")) {
            btnOk.setDisable(true);
        } else {
            btnOk.setDisable(false);
        }
    };

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnOk.setDisable(true);
        
        codeField.textProperty().addListener(checkEnableConfirm);
        descField.textProperty().addListener(checkEnableConfirm);
    }    
    
    public void initialize(Grupo g, ObservableList<Grupo> obsList, int editIndex) {
        if (g != null) {
            codeField.setText(g.getCodigo());
            descField.setText(g.getDescripcion());
        }
        this.obsList = obsList;
        this.editIndex = editIndex;
    }

    @FXML
    private void onConfirm(ActionEvent event) {
        String code = codeField.getText();
        String desc = descField.getText();
        if (code.equals("") || desc.equals(""))
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save data");
                alert.setHeaderText("Save group ghanges");
                alert.setContentText("Are you sure you want to save changes to this group?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Grupo p = new Grupo();
                    p.setCodigo(code);
                    p.setDescripcion(desc);
                    if (editIndex >= 0) {
                        obsList.set(editIndex, p);
                    } 
                    close();
                } else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
                
        
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
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
        
    }
    
    private void close() {
        Stage currentStage = (Stage) btnCancel.getScene().getWindow();
        currentStage.close();
    }
    
}
