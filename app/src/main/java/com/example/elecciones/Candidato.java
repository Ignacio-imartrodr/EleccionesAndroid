package com.example.elecciones;

public class Candidato {
    private int idCandidato;
    private String nombre;
    private Partido partido;
    private int numVotos;

    public Candidato(int idCandidato, String nombre, Partido partido, int numVotos) {
        this.idCandidato = idCandidato;
        this.nombre = nombre;
        this.partido = partido;
        this.numVotos = numVotos;
    }

    public Candidato(int idCandidato, String nombre, Partido partido) {
        this.idCandidato = idCandidato;
        this.nombre = nombre;
        this.partido = partido;
        this.numVotos = 0;
    }

    public int getId() {
        return idCandidato;
    }

    public String getNombre() {
        return nombre;
    }

    public Partido getPartido() {
        return partido;
    }

    public int getNumVotos() {
        return numVotos;
    }

    public void addVoto() {
        this.numVotos++;
    }

    @Override
    public String toString() {
        return partido != null ? nombre + " - " + partido : nombre;
    }
}
