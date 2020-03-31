package model;

import java.util.Objects;

/**
 *
 * @author Utente
 */
public class SessionKeeper {
    private int ex_completed;
    private final int total_ex;
    private int series_completed;
    private final int total_ser;
    private boolean ser_restTime;
    private boolean sessionFinished;
    private boolean restingTime=true;
    
    public SessionKeeper(int total_ex, int total_ser){
        this.total_ex=total_ex;
        this.total_ser=total_ser;
        this.ex_completed=0;
        this.series_completed=0;
    }
    public int getExCompleted(){
        return this.ex_completed;
    }
    public int getSeCompleted(){
        return this.series_completed;
    }
    
    public void complete_interval(){
        if(!restingTime){
            this.ex_completed++;
        }
        restingTime= !restingTime;
        if(this.total_ex == this.ex_completed){
            series_completed++;
            this.ex_completed=0;
            ser_restTime=true;
            sessionFinished = this.total_ser == this.series_completed;
        } else {
            ser_restTime=false;
        }
    }
    public void reset(){
        this.series_completed=0;
        this.ex_completed=0;
        restingTime=true;
        sessionFinished=false;
    }
    
    public boolean isSessionFinished(){
        return this.sessionFinished;
    }
    
    public boolean isSerieCompleted(){
        return this.ser_restTime;
    }
    public boolean isRestingTime(){
        return restingTime;
    }
    
}