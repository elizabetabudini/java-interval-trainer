/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import accesoBD.AccesoBD;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import modelo.SesionTipo;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Utente
 */
public class NewSessionTypeController implements Initializable {

    @FXML
    private Label totalTime;
    @FXML
    private Spinner<Integer> warmingUpTime;
    @FXML
    private Spinner<Integer> seriesRestTime;
    @FXML
    private Spinner<Integer> esRestTime;
    @FXML
    private Spinner<Integer> exDuration;
    @FXML
    private Spinner<Integer> n_series;
    @FXML
    private Spinner<Integer> n_exercises;
    @FXML
    private Button onOk;
    @FXML
    private Button onCancel;

    
    private AccesoBD db;
    private Gym gym;
   
    private final ChangeListener<? super Integer> totTimeListener= 
    (obVal, oldVal, newVal) -> {
        Integer pTime=(esRestTime.getValue()*n_exercises.getValue()
                +this.exDuration.getValue()*n_exercises.getValue()
                +this.seriesRestTime.getValue())*n_series.getValue()+this.warmingUpTime.getValue();

       this.totalTime.setText(String.format("%02d:%02d", pTime / 60, pTime % 60));
        
    };
            
    private ObservableList<SesionTipo> typeSessionList;
    @FXML
    private TextField codeField;
    private Grupo mode;
    @FXML
    private Label alert;
    private boolean existingID;
    private boolean noemptyID;
    
    private final ChangeListener<String> checkId = 
    (obVal, oldVal, newVal) -> {
        if(Utils.sessionIsPresent(gym.getTiposSesion(), codeField.getText())){
            alert.setVisible(true);
            existingID=false;
        } else {
            alert.setVisible(false);
            existingID=true;
        }
        
        noemptyID = !codeField.getText().equals("");
        onOk.setDisable(!(noemptyID && existingID));

           
    };
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alert.setVisible(false);
        onOk.setDisable(true);
        codeField.textProperty().addListener(checkId);
        this.esRestTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 900, 0, 5));
        this.exDuration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 900, 0, 5));
        this.n_exercises.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
        this.n_series.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
        this.seriesRestTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 900, 0, 5));
        this.warmingUpTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 900, 0, 5));
        
        this.esRestTime.valueProperty().addListener(totTimeListener);
        this.exDuration.valueProperty().addListener(totTimeListener);
        this.n_exercises.valueProperty().addListener(totTimeListener);
        this.n_series.valueProperty().addListener(totTimeListener);
        this.seriesRestTime.valueProperty().addListener(totTimeListener);
        this.warmingUpTime.valueProperty().addListener(totTimeListener);
        
        db = AccesoBD.getInstance();
        gym = db.getGym();
    }   
    
    public void initialize(ObservableList<SesionTipo> typeSessionList, Grupo mode) {
        this.typeSessionList = typeSessionList;
        this.mode=mode;
    }

    @FXML
    private void onOk(ActionEvent event) throws IOException {
        Integer n_ex = this.n_exercises.getValue();
        Integer n_serie = this.n_series.getValue();
        String code = this.codeField.getText();
        Integer rest_exerc_sec = this.esRestTime.getValue();
        Integer rest_series_sec = this.seriesRestTime.getValue();
        Integer ex_time = this.exDuration.getValue();
        Integer warmup = this.warmingUpTime.getValue();
        if (code.equals(""))
            return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save data");
                alert.setHeaderText("New session type");
                alert.setContentText("Are you sure you want to save changes to this session type?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    SesionTipo p = new SesionTipo();
                    p.setCodigo(code);
                    p.setD_circuito(rest_series_sec);
                    p.setD_ejercicio(rest_exerc_sec);
                    p.setT_calentamiento(warmup);
                    p.setT_ejercicio(ex_time);
                    p.setNum_circuitos(n_serie);
                    p.setNum_ejercicios(n_ex);
                    
                    typeSessionList.add(p);
                    gym.getTiposSesion().add(p);
                    if(mode != null){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewTrainingSessionIIpart.fxml"));
                        Parent root = (Parent) loader.load();
                        NewTrainingSessionIIpartController controller = loader.<NewTrainingSessionIIpartController>getController();
                        mode.setDefaultTipoSesion(p);
                        controller.initialize(mode);
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
                    event.consume();
                    alert.close();
                }
    }
    
     private void close() {
        Stage currentStage = (Stage) onCancel.getScene().getWindow();
        currentStage.close();
    }
    
    
}
