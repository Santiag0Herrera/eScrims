package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Confirmacion {
    private final String id;
    private final Usuario usuario;
    private final Scrim scrim;
    private boolean confirmado;
    private LocalDateTime fechaConfirmacion;

    public Confirmacion(String id, Usuario usuario, Scrim scrim) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.scrim = Objects.requireNonNull(scrim);
    }

    public void confirmar() {
        confirmado = true;
        fechaConfirmacion = LocalDateTime.now();
    }

    public void revocar() {
        confirmado = false;
        fechaConfirmacion = LocalDateTime.now();
    }

    public boolean estaConfirmado() {
        return confirmado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Scrim getScrim() {
        return scrim;
    }
}
