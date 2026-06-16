package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Postulacion {
    private final String id;
    private final Usuario usuario;
    private final Scrim scrim;
    private final String rolDeseado;
    private String estado;
    private final LocalDateTime fechaPostulacion;

    public Postulacion(String id, Usuario usuario, Scrim scrim, String rolDeseado) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.scrim = Objects.requireNonNull(scrim);
        this.rolDeseado = Objects.requireNonNull(rolDeseado);
        this.estado = "PENDIENTE";
        this.fechaPostulacion = LocalDateTime.now();
    }

    public void aceptar() {
        estado = "ACEPTADA";
    }

    public void rechazar() {
        estado = "RECHAZADA";
    }

    public Confirmacion confirmarAsistencia() {
        if (!"ACEPTADA".equalsIgnoreCase(estado)) {
            throw new IllegalStateException("Solo una postulacion aceptada puede confirmar asistencia");
        }
        return usuario.confirmarParticipacion(scrim);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getId() {
        return id;
    }

    public Scrim getScrim() {
        return scrim;
    }

    public String getRolDeseado() {
        return rolDeseado;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return usuario + " como " + rolDeseado + " (" + estado + ")";
    }
}
