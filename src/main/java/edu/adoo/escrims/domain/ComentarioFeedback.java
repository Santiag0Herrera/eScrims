package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ComentarioFeedback {
    private final String id;
    private final Usuario usuario;
    private final Scrim scrim;
    private final String comentario;
    private final int rating;
    private String estadoModeracion;
    private final LocalDateTime fechaCreacion;

    public ComentarioFeedback(String id, Usuario usuario, Scrim scrim, String comentario, int rating) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.scrim = Objects.requireNonNull(scrim);
        this.comentario = Objects.requireNonNull(comentario);
        this.rating = rating;
        this.estadoModeracion = "PENDIENTE";
        this.fechaCreacion = LocalDateTime.now();
    }

    public void aprobar() {
        estadoModeracion = "APROBADO";
    }

    public void rechazar() {
        estadoModeracion = "RECHAZADO";
    }
}
