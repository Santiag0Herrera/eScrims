package edu.adoo.escrims.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Usuario {
    private final String id;
    private String username;
    private String email;
    private String passwordHash;
    private String estadoVerificacion;
    private String disponibilidadHoraria;
    private Region regionPrincipal;
    private final List<PerfilJuego> perfiles = new ArrayList<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();

    public Usuario(String id, String username, String email, String passwordHash) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.estadoVerificacion = "PENDIENTE";
        this.disponibilidadHoraria = "Sin configurar";
    }

    public void registrarse() {
        estadoVerificacion = "REGISTRADO";
    }

    public void actualizarPerfil(String username, String email, String disponibilidadHoraria, Region regionPrincipal) {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.disponibilidadHoraria = Objects.requireNonNull(disponibilidadHoraria);
        this.regionPrincipal = Objects.requireNonNull(regionPrincipal);
    }

    public void agregarPerfilJuego(PerfilJuego perfil) {
        perfiles.add(Objects.requireNonNull(perfil));
    }

    public void reemplazarPerfilJuego(PerfilJuego perfilActual, PerfilJuego perfilNuevo) {
        int index = perfiles.indexOf(Objects.requireNonNull(perfilActual));
        if (index < 0) {
            throw new IllegalArgumentException("El perfil actual no pertenece al usuario");
        }
        perfiles.set(index, Objects.requireNonNull(perfilNuevo));
    }

    public Postulacion postularse(Scrim scrim, String rolDeseado) {
        if (scrim.yaSePostulo(this)) {
            throw new IllegalStateException("El usuario ya se postulo a este scrim");
        }
        Postulacion postulacion = new Postulacion("post-" + id + "-" + scrim.getId(), this, scrim, rolDeseado);
        scrim.recibirPostulacion(postulacion);
        return postulacion;
    }

    public Confirmacion confirmarParticipacion(Scrim scrim) {
        Confirmacion confirmacion = new Confirmacion("conf-" + id + "-" + scrim.getId(), this, scrim);
        confirmacion.confirmar();
        scrim.agregarConfirmacion(confirmacion);
        return confirmacion;
    }

    public ReporteConducta reportarConducta(Usuario usuarioReportado, Scrim scrim, String motivo) {
        ReporteConducta reporte = new ReporteConducta("rep-" + id + "-" + usuarioReportado.id, scrim, this, usuarioReportado, motivo);
        scrim.agregarReporte(reporte);
        return reporte;
    }

    public void recibirNotificacion(Notificacion notificacion) {
        notificaciones.add(Objects.requireNonNull(notificacion));
    }

    public PerfilJuego perfilPara(Juego juego) {
        return perfiles.stream()
                .filter(perfil -> perfil.getJuego().equals(juego))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene perfil para " + juego.getNombre()));
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisponibilidadHoraria() {
        return disponibilidadHoraria;
    }

    public Region getRegionPrincipal() {
        return regionPrincipal;
    }

    public List<PerfilJuego> getPerfiles() {
        return Collections.unmodifiableList(perfiles);
    }

    public List<Notificacion> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    @Override
    public String toString() {
        return username;
    }
}
