package edu.adoo.escrims.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Equipo {
    private final String id;
    private final String lado;
    private final List<Participacion> participaciones = new ArrayList<>();

    public Equipo(String id, String lado) {
        this.id = Objects.requireNonNull(id);
        this.lado = Objects.requireNonNull(lado);
    }

    public Participacion agregarJugador(Usuario usuario, String rol) {
        if (contieneUsuario(usuario)) {
            throw new IllegalStateException("El usuario ya pertenece a este equipo");
        }
        Participacion participacion = new Participacion("part-" + id + "-" + usuario.getId(), usuario, rol, false);
        participaciones.add(participacion);
        return participacion;
    }

    public void quitarJugador(Participacion participacion) {
        participacion.abandonar();
        participaciones.remove(participacion);
    }

    public void asignarCapitan(Participacion participacion) {
        if (!participaciones.contains(participacion)) {
            throw new IllegalArgumentException("La participacion no pertenece al equipo");
        }
        participacion.asignarCapitan();
    }

    public String getLado() {
        return lado;
    }

    public boolean contieneUsuario(Usuario usuario) {
        return participaciones.stream().anyMatch(participacion -> participacion.getUsuario().equals(usuario));
    }

    public void limpiarParticipaciones() {
        participaciones.clear();
    }

    public int cantidadJugadores() {
        return participaciones.size();
    }

    public List<Participacion> getParticipaciones() {
        return Collections.unmodifiableList(participaciones);
    }

    @Override
    public String toString() {
        return lado + " (" + participaciones.size() + " jugadores)";
    }
}
