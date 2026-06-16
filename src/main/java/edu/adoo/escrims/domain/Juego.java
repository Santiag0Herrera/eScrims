package edu.adoo.escrims.domain;

import java.util.Objects;

public class Juego {
    private final String id;
    private String nombre;
    private String descripcion;

    public Juego(String id, String nombre, String descripcion) {
        this.id = Objects.requireNonNull(id);
        this.nombre = Objects.requireNonNull(nombre);
        this.descripcion = Objects.requireNonNull(descripcion);
    }

    public boolean validarFormato(String modalidad) {
        return modalidad != null && !modalidad.isBlank();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
