package edu.adoo.escrims.patterns.state;

public class FinalizadoState implements ScrimState {
    private final String nombre = "Finalizado";

    @Override
    public void postular(ScrimContext context) {
        throw new IllegalStateException("El scrim ya finalizo");
    }

    @Override
    public void confirmar(ScrimContext context) {
        throw new IllegalStateException("El scrim ya finalizo");
    }

    @Override
    public void iniciar(ScrimContext context) {
        throw new IllegalStateException("El scrim ya finalizo");
    }

    @Override
    public void finalizar(ScrimContext context) {
        context.getScrim().finalizar();
    }

    @Override
    public void cancelar(ScrimContext context) {
        throw new IllegalStateException("No se cancela un scrim finalizado");
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public String canalNotificacion() {
        return "DISCORD";
    }
}
