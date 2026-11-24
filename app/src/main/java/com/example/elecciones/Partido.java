package com.example.elecciones;

public class Partido {
    private int idPartido;
    private String nombre;
    private Color color;
    private String siglas;

    public Partido(int idPartido, String nombre, String siglas, Color color) {
        this.idPartido = idPartido;
        this.nombre = nombre.toUpperCase();
        this.siglas = siglas;
        this.color = color;
    }

    public String getSiglas() {
        return siglas;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }

    public int getIdPartido() {
        return idPartido;
    }
    @Override
    public String toString() {
        return nombre + " (" + siglas + ')';
    }
}
