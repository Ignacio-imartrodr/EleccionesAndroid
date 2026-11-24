package com.example.elecciones;

public class Usuario {
    public static final int NUMERO_VOTOS = 3;
    private int idUsuario;
    private String NIF;
    private String password;
    private boolean haVotado;

    public Usuario(String NIF, String password) {
        this.NIF = NIF;
        this.password = password;
        this.haVotado = false;
    }

    public Usuario(int idUsuario, String NIF, String password, boolean haVotado) {
        this.idUsuario = idUsuario;
        this.NIF = NIF;
        this.password = password;
        this.haVotado = haVotado;
    }

    public int getId() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNIF() {
        return NIF;
    }

    public void setHaVotado(boolean haVotado) {
        this.haVotado = haVotado;
    }

    public boolean haVotado() {
        return haVotado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return (idUsuario == usuario.idUsuario || NIF.equals(usuario.NIF));
    }
}
