package edu.upc.dsa.domain.models;

import edu.upc.dsa.util.RandomUtils;

import java.util.*;

public class Dron {

    String id;
    String nom;
    String fabricant;
    String model;
    List<FlightPlan> historialPlaVol;
    boolean enManteniment;
    double horesVol;

    //SI NO ÉS POSA UN ID DE FORMA MANUAL SE'N GENERA UN D'AUTOMÀTIC DONAT PER LA FUNCIÓ GETID DE RANDOMUTILS
    // 1. Constructor buit
    public Dron() {
        this.setId(RandomUtils.getId());
        this.horesVol = 0.0;
        this.enManteniment = false;
        this.historialPlaVol = new ArrayList<>();
    }

    // 2. Constructor sense ID
    public Dron(String nom, String fabricant, String model) {
        this(null, nom, fabricant, model);
    }

    // 3. Constructor complet
    public Dron(String id, String nom, String fabricant, String model) {
        this(); // Crida al buit per generar ID per defecte i inicialitzar llistes
        if (id != null) this.setId(id);
        this.setNom(nom);
        this.setFabricant(fabricant);
        this.setModel(model);
    }

    // Getters i Setters

    public String getId() {
        return this.id;
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

    public String getFabricant() {
        return fabricant;
    }

    public void setFabricant(String fabricant) {
        this.fabricant = fabricant;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getHoresVol() {
        return horesVol;
    }

    public void setHoresVol(double h) {
        this.horesVol = h;
    }

    public void addHoresVol(double h) {
        this.horesVol += h;
    }

    public boolean isEnManteniment() {
        return enManteniment;
    }

    public void setEnManteniment(boolean enManteniment) {
        this.enManteniment = enManteniment;
    }

    public List<FlightPlan> getHistorialPlaVol() {
        return historialPlaVol;
    }

    public void setHistorialPlaVol(List<FlightPlan> fp) {
        this.historialPlaVol = fp;
    }

    public void addHistorialPlaVol(FlightPlan plan) {
        this.historialPlaVol.add(plan);
    }

    @Override
    public String toString() {
        return "Drone [id=" + id + ", nom=" + nom + ", fabricant=" + fabricant + ", model=" + model + ", horesVol=" + horesVol + ", enMantenimient=" + enManteniment + "]";
    }

}