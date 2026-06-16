package edu.adoo.escrims.domain;

import java.util.Objects;

public class Region {
    private final String id;
    private String nombre;
    private String servidor;

    public Region(String id, String nombre, String servidor) {
        this.id = Objects.requireNonNull(id);
        this.nombre = Objects.requireNonNull(nombre);
        this.servidor = Objects.requireNonNull(servidor);
    }

    public boolean validarLatencia(int latencia) {
        return latencia >= 0 && latencia <= 180;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getServidor() {
        return servidor;
    }

    @Override
    public String toString() {
        return nombre + " (" + servidor + ")";
    }
}
