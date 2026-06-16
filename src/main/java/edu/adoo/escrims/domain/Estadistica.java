package edu.adoo.escrims.domain;

import java.util.Objects;

public class Estadistica {
    private final String id;
    private final Usuario usuario;
    private final Scrim scrim;
    private final int kills;
    private final int deaths;
    private final int assists;
    private final boolean mvp;
    private final String observaciones;
    private final String resultado;

    public Estadistica(String id, Usuario usuario, Scrim scrim, int kills, int deaths, int assists, boolean mvp, String observaciones, String resultado) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.scrim = Objects.requireNonNull(scrim);
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.mvp = mvp;
        this.observaciones = Objects.requireNonNull(observaciones);
        this.resultado = Objects.requireNonNull(resultado);
    }

    public double calcularKDA() {
        return (kills + assists) / (double) Math.max(1, deaths);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Scrim getScrim() {
        return scrim;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public boolean isMvp() {
        return mvp;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getResultado() {
        return resultado;
    }

    @Override
    public String toString() {
        return usuario + " KDA=" + String.format("%.2f", calcularKDA()) + " " + resultado + (mvp ? " MVP" : "");
    }
}
