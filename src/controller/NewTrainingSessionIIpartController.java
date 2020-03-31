/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import accesoBD.AccesoBD;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import modelo.Sesion;
import modelo.SesionTipo;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Utente
 */

public class NewTrainingSessionIIpartController implements Initializable {

    @FXML
    private Label groupCOde;
    @FXML
    private Pagination pagination;
    @FXML
    private Button newSessionType;
    private Grupo grupo;
    private ObservableList<SesionTipo> typeSessionList;
    private Gym gym;
    @FXML
    private Label emptySessionType;
    private Utils util;
    private Sesion sess;
    private final ListChangeListener<? super SesionTipo> check2 = 
            (newVal) -> {
                pagination.setPageCount(typeSessionList.size());
                if(typeSessionList.size()<10){
                    pagination.setMaxPageIndicatorCount(typeSessionList.size());
                } else {
                    pagination.setMaxPageIndicatorCount(10);
                }
                
            };
    @FXML
    private Button back;
    @FXML
    private Button start;
    

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gym=AccesoBD.getInstance().getGym();
        util= new Utils();
 
    }    

    void initialize(Grupo grupo) {
        this.grupo=grupo;
        this.groupCOde.setText("Group code: "+grupo.getCodigo());
        factorySession();
        if(typeSessionList.isEmpty()){
            this.emptySessionType.setVisible(true);
        } else {
            emptySessionType.setVisible(false);
        }
        
    }
    
     public void factorySession() {
        typeSessionList = FXCollections.observableArrayList(gym.getTiposSesion());
        if(typeSessionList.isEmpty()){
            emptySessionType.setVisible(true);
            pagination.setVisible(false);
        } else {
            emptySessionType.setVisible(false);
            pagination.setVisible(true);
            
            pagination.setPageCount(typeSessionList.size());
            if(typeSessionList.size()<10){
                    pagination.setMaxPageIndicatorCount(typeSessionList.size());
                } else {
                    pagination.setMaxPageIndicatorCount(10);
                }
            if(grupo.getDefaultTipoSesion()!=null){
                    if(util.indexOfSession(grupo.getDefaultTipoSesion())>= 0){
                        setIndex();
                    }
            }
            typeSessionList.addListener(check2);
            pagination.setPageFactory((Integer pageIndex)-> createPage(pageIndex));
        }
    }
    public VBox createPage(Integer pageIndex){
        VBox pageBox = new VBox();
        pageIndex++;
        Label title= new Label("Session Type "+ pageIndex);
        Label code =new Label("Code: "+typeSessionList.get(pageIndex-1).getCodigo());
        Label number_ex=new Label("Number of exercises:"+typeSessionList.get(pageIndex-1).getNum_ejercicios());
        Label number_se=new Label("Number of series: "+typeSessionList.get(pageIndex-1).getNum_circuitos());
        Label rest_se=new Label("Rest between series: "+typeSessionList.get(pageIndex-1).getD_circuito()+" sec");
        Label rest_ex=new Label("Rest between exercises: "+typeSessionList.get(pageIndex-1).getD_ejercicio()+" sec");
        Label warmup=new Label("Warmup duration: "+typeSessionList.get(pageIndex-1).getT_calentamiento()+" sec");
        Label time_ex=new Label("Exercises duration: "+typeSessionList.get(pageIndex-1).getT_ejercicio()+" sec");
        pageBox.getChildren().addAll(title, code, warmup, number_ex, time_ex, rest_ex, number_se, rest_se);
        return pageBox;
    }

    @FXML
    private void onNewSessionType(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewSessionType.fxml"));
        Parent root = (Parent) loader.load();
        NewSessionTypeController controller = loader.<NewSessionTypeController>getController();
        
        controller.initialize(typeSessionList, grupo);
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add new session type");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        if(grupo.getDefaultTipoSesion()!=null){
            if(util.indexOfSession(grupo.getDefaultTipoSesion())>= 0){
                setIndex();
            }
        }
    }
    
    public void setIndex(){
        pagination.setCurrentPageIndex(util.indexOfSession(grupo.getDefaultTipoSesion()));
    }

    @FXML
    private void onBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewTrainingSession.fxml"));
        Parent root = (Parent) loader.load();
        NewTrainingSessionController controller = loader.<NewTrainingSessionController>getController();
        controller.setGroup(grupo);
        Scene SceneTwo = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Start new training session 1/2");
        window.setScene(SceneTwo);
        window.show();
    }

    @FXML
    private void onStart(ActionEvent event) throws IOException {
        sess = new Sesion();
        sess.setFecha(LocalDateTime.now());
        SesionTipo type= gym.getTiposSesion().get(pagination.getCurrentPageIndex());
        sess.setTipo(type);
        grupo.setDefaultTipoSesion(type);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLTimer.fxml"));
        Parent root = (Parent) loader.load();
        FXMLTimerController controller = loader.<FXMLTimerController>getController();
        controller.initialize(grupo, sess);
        
        Scene SceneTwo = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Interval Timer");
        window.setScene(SceneTwo);
        window.show();
    }
    
}
