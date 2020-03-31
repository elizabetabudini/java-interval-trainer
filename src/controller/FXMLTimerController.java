/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import model.SessionKeeper;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

/**
 *
 * @author Utente
 */
public class FXMLTimerController implements Initializable {
    
    @FXML
    private Label group;
    @FXML
    private Label actualSerie;
    @FXML
    private Label actualEx;
    @FXML
    private Label timer;
    private Grupo actualGroup;
    private Sesion actualSession;
    private CronoService servicio;
    private final Property<Boolean> iniciado = new SimpleBooleanProperty(false);
    private boolean firstime;
    @FXML
    private Button butReset;
    @FXML
    private Button butStop;
    @FXML
    private Button butStart;
    @FXML
    private Label info;
    private SesionTipo actualType;
    private SessionKeeper sesKeeper;
    private boolean firstTime=true;
    private long startingTime;
    @FXML
    private Label warmingUp;
    @FXML
    private Button butNext;
    @FXML
    private BorderPane pane;
    @FXML
    private Button save;
    @FXML
    private Button delete;
    @FXML
    private Button menu;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menu.setDisable(true);
        save.setDisable(true);
        butNext.setDisable(true);
        butReset.setDisable(true);
    }  
    public void setButtonsSessionFinished(){
        butNext.setDisable(true);
        butStart.setDisable(true);
        butStop.setDisable(true);
        butReset.setDisable(true);
        menu.setDisable(false);
        save.setDisable(true);
        delete.setDisable(true);
    }
    public void setButtonsOnRestart(){
        butNext.setDisable(false);
        butStart.setDisable(false);
        butStop.setDisable(true);
        butReset.setDisable(false);
    }
     public void setButtonsOnPlay(){
        butNext.setDisable(false);
        butStart.setDisable(true);
        butStop.setDisable(false);
        butReset.setDisable(false);
    }
     public void setButtonsOnPause(){
        butNext.setDisable(false);
        butStart.setDisable(false);
        butStop.setDisable(true);
        butReset.setDisable(false);
    }

    public void initialize(Grupo grupo, Sesion sess) {
        this.actualGroup=grupo;
        this.actualSession=sess;
        this.actualType=sess.getTipo();
        sesKeeper=new SessionKeeper(actualType.getNum_ejercicios(), actualType.getNum_circuitos());
        this.group.setText("Group code: "+grupo.getCodigo());
        servicio = new CronoService(actualType.getT_calentamiento());
        servicio.setTiempo(timer.textProperty());
        servicio.setSerie(actualSerie.textProperty());
        servicio.setEx(actualEx.textProperty());
        
        Duration dur = Duration.ofMillis(actualType.getT_calentamiento()*1000);
        final long minutos=dur.toMinutes();
        final long segundos = dur.minusMinutes(minutos).getSeconds();
        timer.setText(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", 0));
        
        this.actualEx.setText("Exercises completed: 0/"+actualType.getNum_ejercicios());
        this.actualSerie.setText("Series completed: 0/"+actualType.getNum_circuitos());
        info.setText("Press play to start!");
        butStart.setDisable(false);
        butStop.setDisable(true);
    }

    @FXML
    private void onMenu(ActionEvent event) throws IOException {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLMainMenu.fxml"));
        Parent root = (Parent) loader.load();
        FXMLMainMenuController controller = loader.<FXMLMainMenuController>getController();
        Scene sceneTwo = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(sceneTwo);
        window.show();
                
    }

    @FXML
    private void onPlay(ActionEvent event) {
        play();
    }

    @FXML
    private void onNext(ActionEvent event) {
        servicio.cancel();
        servicio.reset();
        int time;
        sesKeeper.complete_interval();
        if(!sesKeeper.isSessionFinished()){
            if(sesKeeper.isSerieCompleted()){
                //serie rest interval
                time=actualType.getD_circuito();
                info.setText("Prepare for next serie!");
                pane.setStyle("-fx-background-color: #FFAC8F;");
                } else {
                if(sesKeeper.isRestingTime()){
                    time=actualType.getD_ejercicio();
                    info.setText("Prepare for next exercise!");
                    pane.setStyle("-fx-background-color: #FFAC8F;");
                } else {
                    time=actualType.getT_ejercicio();
                    info.setText("Workout!");
                    pane.setStyle("-fx-background-color: #92FF8F;");
                }
                
            }
            servicio = new CronoService(time);
            Duration dur = Duration.ofMillis(time*1000);
            final long minutos=dur.toMinutes();
            final long segundos = dur.minusMinutes(minutos).getSeconds();
            timer.setText(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", 0));   
            servicio.setTiempo(timer.textProperty());
            servicio.setSerie(actualSerie.textProperty());
            servicio.setEx(actualEx.textProperty());
            actualSerie.setText("Series completed: "+sesKeeper.getSeCompleted()+"/"+actualType.getNum_circuitos());
            actualEx.setText("Exercises completed: "+sesKeeper.getSeCompleted()+"/"+actualType.getNum_ejercicios());
            
            if(butStart.isDisable()){
                servicio.start();
                setButtonsOnPlay();
            }else {
                setButtonsOnPause();
            }
            
        } else {
            //session finished
           actualSerie.setText("Series completed: "+sesKeeper.getSeCompleted()+"/"+actualType.getNum_circuitos());
           actualEx.setText("Exercises completed: "+actualType.getNum_ejercicios()+"/"+actualType.getNum_ejercicios());
        
           Long elap = System.currentTimeMillis() - startingTime;
           Duration dur= Duration.ofMillis(elap);
           actualSession.setDuracion(dur);
           actualGroup.getSesiones().add(actualSession);
           actualGroup.setDefaultTipoSesion(actualType);
           info.setText("Training session completed!");
           final long min=dur.toMinutes();
           final long seg = dur.minusMinutes(min).getSeconds();
           final long cent = dur.minusSeconds(seg).toNanos() / 10000000;
           timer.setText(String.format("%02d", min) + ":" + String.format("%02d", seg) + ":" + String.format("%02d", cent)); 

           setButtonsSessionFinished();
        }
       
    }
    public void play(){
        if(firstTime){
            butReset.setDisable(true);
            butNext.setDisable(true);
            save.setDisable(false);
            delete.setDisable(false);
            this.actualSession.setFecha(LocalDateTime.now());
            startingTime= System.currentTimeMillis();
            firstTime=false;
            info.setText("Warm up!");
            pane.setStyle("-fx-background-color: #B2CFFF;");
        }
        servicio.start();
        setButtonsOnPlay();   
    }
    public void pause(){
        servicio.cancel();
        servicio.reset();
        setButtonsOnPause();
    }

    @FXML
    private void onRestart(ActionEvent event) {
        pause();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Restart session");
                alert.setHeaderText("Restart session");
                alert.setContentText("Restart this training session from the warming up?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    servicio.cancel();
                    servicio.reset();
                    setButtonsOnRestart();
                    servicio.restaurarInicio();
                    firstime = true;
                    sesKeeper.reset();
                    this.actualEx.setText("Exercises completed: 0/"+actualType.getNum_ejercicios());
                    this.actualSerie.setText("Series completed: 0/"+actualType.getNum_circuitos());
                    info.setText("Warm up");
                    pane.setStyle("-fx-background-color: #B2CFFF;");
                     Duration dur = Duration.ofMillis(actualType.getT_calentamiento()*1000);
                    final long minutos=dur.toMinutes();
                    final long segundos = dur.minusMinutes(minutos).getSeconds();
                    timer.setText(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", 0)); 

                } else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                    alert.close();
                }
        
        }

    @FXML
    private void onPause(ActionEvent event) {
        pause();
    }

    @FXML
    private void onSave(ActionEvent event) throws IOException {
        pause();
        Long elapsedTime = System.currentTimeMillis() - startingTime;
        Duration duration= Duration.ofMillis(elapsedTime);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Finish session and save");
                alert.setHeaderText("Finish session and save data?");
                alert.setContentText("Are you sure you want to finish this session? All data will be saved, but you won't"
                        + " be able to resume the session later.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                   
                    actualSession.setDuracion(duration);
                    actualGroup.getSesiones().add(actualSession);
                    actualGroup.setDefaultTipoSesion(actualType);
                    
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

    @FXML
    private void onDelete(ActionEvent event) throws IOException {
        pause();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete and close session");
                alert.setHeaderText("Delete and close session");
                alert.setContentText("All data won't be saved.");

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

    class CronoService extends Service<Void> {

    private static final int DELAY = 100;
    //tiempos
    private long lastTime = 0; // guarda la hora del ultimo instante
    private long startTime = 0;// guarda la hora del instante inicial del intervalo
    private long stoppedTime = 0;// guarda la duracion del tiempo parados

    private boolean stopped = false;//indica si se ha parado el cronometro
    private long countDownMilis;

    CronoService(int interval) {
        this.countDownMilis = interval * 1000;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (!stopped) {
                    startTime = lastTime = System.currentTimeMillis();
                } else { // estabamos detenidos y nos ponemos en marcha sin cambio de estado
                    Long elapsedTime = System.currentTimeMillis() - lastTime;
                    stoppedTime = stoppedTime + elapsedTime;
                    stopped = false;
                }
                while (true) {
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException ex) {
                        if (isCancelled()) {
                            break;
                        }
                    }
                    if (isCancelled()) {
                        break;
                    }
                    
                    if (calculaCountDown()) {
                        break;
                    }

                    
                }
                return null;
            }

            private boolean calculaCountDown() {
                lastTime = System.currentTimeMillis();
                Long totalTime = (lastTime - startTime) - stoppedTime;
                Duration duration = Duration.ofMillis(countDownMilis - totalTime);
                final long minutos = duration.toMinutes();
                final long segundos = duration.minusMinutes(minutos).getSeconds();
                final long centesimas = duration.minusSeconds(segundos).toNanos() / 10000000;
                
                if ((segundos == 3 && centesimas > 90 ) || (segundos == 2 && centesimas > 90 ) || (segundos == 1 && centesimas > 90)) {
                    Platform.runLater(() -> {
                        AudioClip plonkSound = new AudioClip(getClass().getResource("/sounds/alert.wav").toString()    );
                        plonkSound.play();
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", centesimas));
                    });
                    return false;
                }
                if (segundos == 0 && centesimas > 10 ) {
                    Platform.runLater(() -> {
                        AudioClip plonkSound = new AudioClip(getClass().getResource("/sounds/alert.wav").toString()    );
                        plonkSound.play();
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", centesimas));
                    });
                    return false;
                }

                if ((segundos == 0) && (centesimas < 10)) {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
                    });
                    return true;
                } else {
                    Platform.runLater(() -> {
                        tiempo.setValue(String.format("%02d", minutos) + ":" + String.format("%02d", segundos) + ":" + String.format("%02d", centesimas));
                    });
                    return false;
                }
            }

        };
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        lastTime = System.currentTimeMillis();
        stopped = true;
    }

    @Override
    protected void succeeded() {
        sesKeeper.complete_interval();
        if(!sesKeeper.isSessionFinished()){
            if(sesKeeper.isSerieCompleted()){
                //serie rest interval
                servicio = new CronoService(actualType.getD_circuito());
                info.setText("Prepare for next serie!");
                pane.setStyle("-fx-background-color: #FFAC8F;");
            } else {
                if(sesKeeper.isRestingTime()){
                    servicio = new CronoService(actualType.getD_ejercicio());
                    info.setText("Prepare for next exercise!");
                    pane.setStyle("-fx-background-color: #FFAC8F;");
                } else {
                    servicio = new CronoService(actualType.getT_ejercicio());
                    pane.setStyle("-fx-background-color: #92FF8F;");
                    info.setText("Workout!");
                }
                
            }
            servicio.setTiempo(timer.textProperty());
            servicio.setSerie(actualSerie.textProperty());
            servicio.setEx(actualEx.textProperty());     
            serie.setValue("Series completed: "+sesKeeper.getSeCompleted()+"/"+actualType.getNum_circuitos());
            exercise.setValue("Exercises completed: "+sesKeeper.getExCompleted()+"/"+actualType.getNum_ejercicios());
            iniciado.setValue(true);
            servicio.start();
        } else {
            //session finished
           serie.setValue("Series completed: "+sesKeeper.getSeCompleted()+"/"+actualType.getNum_circuitos());
           exercise.setValue("Exercises completed: "+actualType.getNum_ejercicios()+"/"+actualType.getNum_ejercicios());
        
           Long elap = System.currentTimeMillis() - startingTime;
           Duration dur= Duration.ofMillis(elap);
           actualSession.setDuracion(dur);
           actualGroup.getSesiones().add(actualSession);
           actualGroup.setDefaultTipoSesion(actualType);
           info.setText("Training session completed!");
           final long min=dur.toMinutes();
           final long seg = dur.minusMinutes(min).getSeconds();
           final long cent = dur.minusSeconds(seg).toNanos() / 10000000;
           timer.setText(String.format("%02d", min) + ":" + String.format("%02d", seg) + ":" + String.format("%02d", cent)); 

        }
    }
   
    private StringProperty tiempo = new SimpleStringProperty();
    private StringProperty serie = new SimpleStringProperty();
    private StringProperty exercise = new SimpleStringProperty();
    
    public String getEx() {
        return exercise.get();
    }
    public void setEx(StringProperty value) {
        exercise=value;
    }
    public StringProperty exProperty() {
        return exercise;
    }
    
    public String getSerie() {
        return serie.get();
    }
    public void setSerie(StringProperty value) {
        serie=value;
    }
    public StringProperty serieProperty() {
        return serie;
    }

    public String getTiempo() {
        return tiempo.get();
    }

    public void setTiempo(StringProperty value) {
        tiempo = value;
    }

    public StringProperty tiempoProperty() {
        return tiempo;
    }

    public void setCountDown(int seconds) {
        this.countDownMilis = seconds * 1000;
    }

    public void restaurarInicio() {
        lastTime = 0; // guarda la hora del ultimo instante
        startTime = 0;// guarda la hora del instante inicial del intervalo
        stoppedTime = 0;// guarda la duracion del tiempo parados

        //indica si se ha parado el cronometro
        stopped = false;
    }

}
    
}
