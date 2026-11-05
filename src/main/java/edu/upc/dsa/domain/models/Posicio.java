package edu.upc.dsa.domain.models;

public class Posicio {

    double latitud;
    double longitud;

    // 1. Constructor buit
    public Posicio() {}

    // 2. Constructor complet
    public Posicio(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters i Setters
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Posició [lat=" + latitud + ", lng=" + longitud + "]";
    }
}