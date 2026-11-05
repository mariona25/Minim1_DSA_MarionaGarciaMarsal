package edu.upc.dsa.domain.models;

import edu.upc.dsa.util.RandomUtils;
import java.util.*;

public class Pilot {

    String id;
    String nom;
    String cognom;
    List<FlightPlan> historialPlaVol;
    double horesVol;

    //SI NO ÉS POSA UN ID DE FORMA MANUAL SE'N GENERA UN D'AUTOMÀTIC DONAT PER LA FUNCIÓ GETID DE RANDOMUTILS
    // 1. Constructor buit
    public Pilot() {
        this.setId(RandomUtils.getId());
        this.historialPlaVol = new ArrayList<>();
        this.horesVol = 0.0;
    }

    // 2. Constructor sense ID
    public Pilot(String nom,String cognom) {
        this(null, nom, cognom);
    }

    // 3. Constructor complet
    public Pilot(String id, String nom, String cognom) {
        this(); // Crida al buit per generar ID per defecte si id ve null
        if (id != null) this.setId(id);
        this.setNom(nom);
    }

    // Getters i Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getCognom() {
        return cognom;
    }

    public void setApellidos(String cognom) {
        this.cognom = cognom;
    }

    public double getHoresVol() {
        return horesVol;
    }

    public void setHoresVol(double horesVol) {
        this.horesVol = horesVol;
    }

    public void addHoresVol(double h) {
        this.horesVol += h;
    }

    public List<FlightPlan> getHistorialPlaVol() {
        return historialPlaVol;
    }

    public void setHistorialPlaVol(List<FlightPlan> fp) {
        this.historialPlaVol = fp;
    }

    public void addFlightPlan(FlightPlan plan) {
        this.historialPlaVol.add(plan);
    }

    @Override
    public String toString() {
        return "Pilot [id=" + id + ", nom=" + nom + ", cognom=" + cognom + ", horesVol=" + horesVol + ", numFlightPlans=" + historialPlaVol.size() + "]";
    }

}
