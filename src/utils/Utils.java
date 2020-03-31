/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import accesoBD.AccesoBD;
import java.util.ArrayList;
import modelo.Grupo;
import modelo.Gym;
import modelo.SesionTipo;

/**
 *
 * @author Utente
 */
public final class Utils {

    public static boolean sessionIsPresent(ArrayList<SesionTipo> list, String text) {
        return list.stream().anyMatch((p) -> (p.getCodigo().equals(text)));
    }

    public static boolean groupIsPresent(ArrayList<Grupo> list, String text) {
        return list.stream().anyMatch((p) -> (p.getCodigo().equals(text)));
    }
    private final Gym gym=AccesoBD.getInstance().getGym();
    public int indexOfGroup(Grupo grupo){
        for (Grupo group : gym.getGrupos()){
            if(group.getCodigo().equals(grupo.getCodigo()) && (group.getDescripcion().equals(grupo.getDescripcion()))){
                 return gym.getGrupos().indexOf(group);
            }
        }
        return -1;
    }
    public int indexOfSession(SesionTipo ses) {
        for (SesionTipo sesion : gym.getTiposSesion()){
            if(ses.getCodigo().equals(sesion.getCodigo())){
                return gym.getTiposSesion().indexOf(sesion);
            }
        }
        return -1;
    }
}
