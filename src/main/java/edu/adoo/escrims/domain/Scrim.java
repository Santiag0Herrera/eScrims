package edu.adoo.escrims.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Scrim {
    private final String id;
    private final Juego juego;
    private final Region region;
    private final LocalDateTime fechaHora;
    private final int duracionMinutos;
    private final int mmrMinimo;
    private final int mmrMaximo;
    private final int latenciaMaxima;
    private final String modalidad;
    private String estadoActual;
    private final int cantidadJugadores;
    private final int cantidadEquipos;
    private final int jugadoresPorEquipo;
    private final String formato;
    private final List<Equipo> equipos = new ArrayList<>();
    private final List<Postulacion> postulaciones = new ArrayList<>();
    private final List<Confirmacion> confirmaciones = new ArrayList<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private final List<Estadistica> estadisticas = new ArrayList<>();
    private final List<ComentarioFeedback> feedback = new ArrayList<>();
    private final List<ReporteConducta> reportes = new ArrayList<>();

    public Scrim(String id, Juego juego, Region region, LocalDateTime fechaHora, int duracionMinutos,
                 String modalidad, String formato, int mmrMinimo, int mmrMaximo, int latenciaMaxima,
                 int cantidadEquipos, int jugadoresPorEquipo) {
        this.id = Objects.requireNonNull(id);
        this.juego = Objects.requireNonNull(juego);
        this.region = Objects.requireNonNull(region);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.duracionMinutos = duracionMinutos;
        this.modalidad = Objects.requireNonNull(modalidad);
        this.formato = Objects.requireNonNull(formato);
        this.mmrMinimo = mmrMinimo;
        this.mmrMaximo = mmrMaximo;
        this.latenciaMaxima = latenciaMaxima;
        this.cantidadEquipos = cantidadEquipos;
        this.jugadoresPorEquipo = jugadoresPorEquipo;
        this.cantidadJugadores = cantidadEquipos * jugadoresPorEquipo;
        this.estadoActual = "BuscandoJugadores";
        for (int i = 1; i <= cantidadEquipos; i++) {
            equipos.add(new Equipo(id + "-" + i, "Equipo " + i));
        }
    }

    public void abrirPostulaciones() {
        estadoActual = "BuscandoJugadores";
    }

    public void cerrarPostulaciones() {
        estadoActual = "LobbyArmado";
    }

    public void iniciar() {
        estadoActual = "EnJuego";
    }

    public void finalizar() {
        estadoActual = "Finalizado";
    }

    public void cancelar() {
        estadoActual = "Cancelado";
    }

    public void cambiarEstado(String nuevoEstado) {
        estadoActual = Objects.requireNonNull(nuevoEstado);
    }

    public void recibirPostulacion(Postulacion postulacion) {
        if (yaSePostulo(postulacion.getUsuario())) {
            throw new IllegalStateException("El usuario ya se postulo a este scrim");
        }
        postulaciones.add(Objects.requireNonNull(postulacion));
    }

    public void agregarConfirmacion(Confirmacion confirmacion) {
        if (usuarioYaConfirmo(confirmacion.getUsuario())) {
            throw new IllegalStateException("El usuario ya confirmo asistencia en este scrim");
        }
        confirmaciones.add(Objects.requireNonNull(confirmacion));
    }

    public void agregarEstadistica(Estadistica estadistica) {
        estadisticas.add(Objects.requireNonNull(estadistica));
    }

    public void agregarFeedback(ComentarioFeedback comentario) {
        feedback.add(Objects.requireNonNull(comentario));
    }

    public void agregarReporte(ReporteConducta reporte) {
        reportes.add(Objects.requireNonNull(reporte));
    }

    public void agregarNotificacion(Notificacion notificacion) {
        notificaciones.add(Objects.requireNonNull(notificacion));
    }

    public void reiniciarEquipos() {
        equipos.forEach(Equipo::limpiarParticipaciones);
    }

    public boolean yaTieneParticipacion(Usuario usuario) {
        return equipos.stream().anyMatch(equipo -> equipo.contieneUsuario(usuario));
    }

    public boolean yaSePostulo(Usuario usuario) {
        return postulaciones.stream().anyMatch(postulacion -> postulacion.getUsuario().equals(usuario));
    }

    public boolean usuarioYaConfirmo(Usuario usuario) {
        return confirmaciones.stream()
                .filter(Confirmacion::estaConfirmado)
                .anyMatch(confirmacion -> confirmacion.getUsuario().equals(usuario));
    }

    public int getJugadoresAsignados() {
        return equipos.stream().mapToInt(Equipo::cantidadJugadores).sum();
    }

    public boolean tieneCupoCompleto() {
        return getJugadoresAsignados() >= cantidadJugadores;
    }

    public int getCantidadEquipos() {
        return cantidadEquipos;
    }

    public int getJugadoresPorEquipo() {
        return jugadoresPorEquipo;
    }

    public String getId() {
        return id;
    }

    public Juego getJuego() {
        return juego;
    }

    public Region getRegion() {
        return region;
    }

    public int getMmrMinimo() {
        return mmrMinimo;
    }

    public int getMmrMaximo() {
        return mmrMaximo;
    }

    public int getLatenciaMaxima() {
        return latenciaMaxima;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public String getFormato() {
        return formato;
    }

    public String getModalidad() {
        return modalidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public List<Equipo> getEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public List<Postulacion> getPostulaciones() {
        return Collections.unmodifiableList(postulaciones);
    }

    public List<Confirmacion> getConfirmaciones() {
        return Collections.unmodifiableList(confirmaciones);
    }

    public List<Estadistica> getEstadisticas() {
        return Collections.unmodifiableList(estadisticas);
    }

    public List<ComentarioFeedback> getFeedback() {
        return Collections.unmodifiableList(feedback);
    }

    public List<ReporteConducta> getReportes() {
        return Collections.unmodifiableList(reportes);
    }

    @Override
    public String toString() {
        return juego + " " + formato + " en " + region;
    }
}
