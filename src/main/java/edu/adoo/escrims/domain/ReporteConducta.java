package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReporteConducta {
    private final String id;
    private final Scrim scrim;
    private final Usuario reportante;
    private final Usuario reportado;
    private final String motivo;
    private String estado;
    private String sancion;
    private final LocalDateTime fechaCreacion;

    public ReporteConducta(String id, Scrim scrim, Usuario reportante, Usuario reportado, String motivo) {
        this.id = Objects.requireNonNull(id);
        this.scrim = Objects.requireNonNull(scrim);
        this.reportante = Objects.requireNonNull(reportante);
        this.reportado = Objects.requireNonNull(reportado);
        this.motivo = Objects.requireNonNull(motivo);
        this.estado = "CREADO";
        this.sancion = "SIN_SANCION";
        this.fechaCreacion = LocalDateTime.now();
    }

    public void procesar() {
        estado = "EN_REVISION";
    }

    public void resolver(String sancion) {
        this.sancion = Objects.requireNonNull(sancion);
        estado = "RESUELTO";
    }

    public Usuario obtenerReportante() {
        return reportante;
    }

    public Usuario obtenerReportado() {
        return reportado;
    }
}
