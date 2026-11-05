package edu.upc.dsa.domain.models;

import edu.upc.dsa.util.RandomUtils;
import java.util.*;

public class FlightPlan {

    String id;
    String dronId;
    String pilotId;
    Date dataHora;
    double duracioHores;
    List<Posicio> posicions;

    //SI NO ÉS POSA UN ID DE FORMA MANUAL SE'N GENERA UN D'AUTOMÀTIC DONAT PER LA FUNCIÓ GETID DE RANDOMUTILS
    // 1. Constructor buit
    public FlightPlan() {
        this.setId(RandomUtils.getId());
        this.posicions = new ArrayList<>();
    }

    // 2. Constructor sense ID
    public FlightPlan(String dronId, String pilotId, Date dataHora, double duracioHores) {
        this(null, dronId, pilotId, dataHora, duracioHores);
    }

    // 3. Constructor complet
    public FlightPlan(String id, String dronId, String pilotId, Date dataHora, double duracioHores) {
        this();
        if (id != null) this.setId(id);
        this.setDronId(dronId);
        this.setPilotId(pilotId);
        this.setDataHora(dataHora);
        this.setDuracioHores(duracioHores);
    }


    // Getters i Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDronId() {
        return dronId;
    }

    public void setDronId(String dronId) {
        this.dronId = dronId;
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public double getDuracioHores() {
        return duracioHores;
    }

    public void setDuracioHores(double duracioHores) {
        this.duracioHores = duracioHores;
    }

    public List<Posicio> getPosicions() {
        return posicions;
    }

    public void setPosicions(List<Posicio> posicions) {
        this.posicions = posicions;
    }

    public void addPosicio(double latitud, double longitud) {
        this.posicions.add(new Posicio(latitud, longitud));
    }

    @Override
    public String toString() {
        return "PlaDeVol [id=" + id + ", dronId=" + dronId + ", pilotId=" + pilotId + ", horesVol=" + duracioHores + ", numPosicions=" + posicions.size() + "]";
    }
}
