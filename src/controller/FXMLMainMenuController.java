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
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Grupo;
import modelo.Gym;
import modelo.SesionTipo;

/**
 * FXML Controller class
 *
 * @author Utente
 */
public class FXMLMainMenuController implements Initializable {

    @FXML
    private Button addNewGroupBtn;
    @FXML
    private Button groupsStatsBtn;
    @FXML
    private Button modifyGroupBtn;
    @FXML
    private TableView<Grupo> groupTable;
    private ObservableList<Grupo> groupList;
    private ObservableList<SesionTipo> typeSessionList;
    private AccesoBD db;
    private Gym gym;
    @FXML
    private TableColumn<Grupo, String> codeColumn;
    @FXML
    private TableColumn<Grupo, Integer> completedColumn;
    @FXML
    private TableColumn<Grupo, String> descColumn;
    @FXML
    private Button deleteGroupBtn;
    @FXML
    private Button addNewSessionType;
    @FXML
    private Pagination pagination;
    @FXML
    private Button startTraining;
    @FXML
    private Label emptyLabel;
    @FXML
    private GridPane gridpane;
    @FXML
    private Label emptyGroups;
    private final ListChangeListener<? super SesionTipo> checkSessionTypeList = 
            (newVal) -> {
                pagination.setPageCount(typeSessionList.size());
                if(typeSessionList.size()<10){
                    pagination.setMaxPageIndicatorCount(typeSessionList.size());
                } else {
                    pagination.setMaxPageIndicatorCount(10);
                }
                
            };
    
    private final ListChangeListener<? super Grupo> checkEmptyGroups = 
            (newVal) -> {
                if(groupList.size()<=0){
                    emptyGroups.setText("Add a group to enable this button!");
                } else {
                    emptyGroups.setText("Have a beautiful training session!");
                }
                
            };
   

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = AccesoBD.getInstance();
        gym = db.getGym();
        setButtons();
        factoryGroups();
        factorySession();
        this.startTraining.disableProperty().bind(Bindings.size(groupList).isEqualTo(0));
        typeSessionList.addListener(checkSessionTypeList);
        groupList.addListener(checkEmptyGroups);
        if(groupList.size()<=0){
                    emptyGroups.setText("Add a group to enable this button!");
                } else {
                    emptyGroups.setText("Have a beautiful training session!");
                }
        
    }    

    @FXML
    private void groupStat(ActionEvent event) {
    }

    @FXML
    private void modifyGroup(ActionEvent event) throws IOException {
        MultipleSelectionModel<Grupo> selection = groupTable.getSelectionModel();
        launchModal(selection.getSelectedItem(), selection.getSelectedIndex());
    }

    private void setButtons() {
        // Establecer estado inicial de los botones
        addNewGroupBtn.requestFocus();
        modifyGroupBtn.setDisable(true);
        groupsStatsBtn.setDisable(true);
        deleteGroupBtn.setDisable(true);
        
        // Establecer bindings de los botones
        groupTable.getSelectionModel().selectedItemProperty().addListener((obVal, oldVal, newVal) -> {
            if (newVal != null) {
                modifyGroupBtn.setDisable(false);
                groupsStatsBtn.setDisable(false);
                deleteGroupBtn.setDisable(false);
            } else {
                modifyGroupBtn.setDisable(true);
                groupsStatsBtn.setDisable(true);
                deleteGroupBtn.setDisable(true);
            }
        });
    }
    
    private void launchModal(Grupo p, int editIndex) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLFormularioGroup.fxml"));
        Parent root = (Parent) loader.load();
        FormularioGroupController controller = loader.<FormularioGroupController>getController();
        
        controller.initialize(p, groupList, editIndex);
        
        Scene scene = new Scene(root, 450, 250);
        Stage stage = new Stage();
        stage.setScene(scene);
        if (editIndex >= 0) {
            stage.setTitle("Modify group");
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void factoryGroups() {
        groupList = FXCollections.observableArrayList(gym.getGrupos());
        groupTable.setItems(groupList);
        this.descColumn.setCellValueFactory(group -> new SimpleStringProperty(group.getValue().getDescripcion())); 
        this.codeColumn.setCellValueFactory(group -> new SimpleStringProperty(group.getValue().getCodigo()));
        this.completedColumn.setCellValueFactory(group -> new ReadOnlyObjectWrapper<>(group.getValue().getSesiones().size()));
    }

    public void factorySession() {
        typeSessionList = FXCollections.observableArrayList(gym.getTiposSesion());
        if(typeSessionList.isEmpty()){
            emptyLabel.setVisible(true);
            pagination.setVisible(false);
        } else {
            pagination.setVisible(true);
            emptyLabel.setVisible(false);
            pagination.setPageCount(typeSessionList.size());
            if(typeSessionList.size()<10){
                    pagination.setMaxPageIndicatorCount(typeSessionList.size());
                } else {
                    pagination.setMaxPageIndicatorCount(10);
                }
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
    private void onDelete(ActionEvent event) {
        int i = groupTable.getSelectionModel().getSelectedIndex();
        Grupo g= gym.getGrupos().get(i);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete group");
                alert.setHeaderText("Delete group");
                alert.setContentText("Are you sure you want to delete this group and all his completed sessions?\n\nGroup code: "
                +g.getCodigo()+"\nSession completed: "+g.getSesiones().size());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    if (i>=0) groupList.remove(i);
                    gym.getGrupos().remove(i);
                } else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
    	
    }

    @FXML
    private void addNewGroup(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewGroup.fxml"));
        Parent root = (Parent) loader.load();
        NewGroupController controller = loader.<NewGroupController>getController();
        
        controller.initialize(groupList);
        
        Scene scene = new Scene(root, 450, 250);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add new group");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void onAddNewSessionType(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewSessionType.fxml"));
        Parent root = (Parent) loader.load();
        NewSessionTypeController controller = loader.<NewSessionTypeController>getController();
        
        controller.initialize(typeSessionList, null);
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add new session type");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void onNewTraining(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLNewTrainingSession.fxml"));
        Parent root = (Parent) loader.load();
        Scene sceneTwo = new Scene(root);
        NewTrainingSessionController controller = loader.<NewTrainingSessionController>getController();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Start new training session 1/2");
        window.setScene(sceneTwo);
        window.show();
    }
    
}
