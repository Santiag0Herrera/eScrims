package edu.adoo.escrims.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PerfilJuego {
    private final String id;
    private final Juego juego;
    private String rango;
    private int mmr;
    private final List<String> rolesPreferidos = new ArrayList<>();

    public PerfilJuego(String id, Juego juego, String rango, int mmr, List<String> rolesPreferidos) {
        this.id = Objects.requireNonNull(id);
        this.juego = Objects.requireNonNull(juego);
        this.rango = Objects.requireNonNull(rango);
        this.mmr = mmr;
        this.rolesPreferidos.addAll(rolesPreferidos);
    }

    public void actualizarRango(String nuevoRango) {
        this.rango = Objects.requireNonNull(nuevoRango);
    }

    public void actualizarMMR(int nuevoMMR) {
        if (nuevoMMR < 0) {
            throw new IllegalArgumentException("El MMR no puede ser negativo");
        }
        this.mmr = nuevoMMR;
    }

    public String getId() {
        return id;
    }

    public Juego getJuego() {
        return juego;
    }

    public String getRango() {
        return rango;
    }

    public int getMmr() {
        return mmr;
    }

    public List<String> getRolesPreferidos() {
        return Collections.unmodifiableList(rolesPreferidos);
    }
}
