package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Participacion {
    private final String id;
    private final Usuario usuario;
    private String rol;
    private boolean esCapitan;
    private boolean esSuplente;
    private String estado;
    private final LocalDateTime fechaIngreso;

    public Participacion(String id, Usuario usuario, String rol, boolean esSuplente) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.rol = Objects.requireNonNull(rol);
        this.esSuplente = esSuplente;
        this.estado = "ACTIVA";
        this.fechaIngreso = LocalDateTime.now();
    }

    public void cambiarRol(String nuevoRol) {
        this.rol = Objects.requireNonNull(nuevoRol);
    }

    public void abandonar() {
        estado = "ABANDONO";
    }

    public void asignarCapitan() {
        esCapitan = true;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    public boolean isEsCapitan() {
        return esCapitan;
    }
}
