package edu.adoo.escrims.patterns.state;

public class CanceladoState implements ScrimState {
    private final String nombre = "Cancelado";

    @Override
    public void postular(ScrimContext context) {
        throw new IllegalStateException("El scrim esta cancelado");
    }

    @Override
    public void confirmar(ScrimContext context) {
        throw new IllegalStateException("El scrim esta cancelado");
    }

    @Override
    public void iniciar(ScrimContext context) {
        throw new IllegalStateException("El scrim esta cancelado");
    }

    @Override
    public void finalizar(ScrimContext context) {
        throw new IllegalStateException("El scrim esta cancelado");
    }

    @Override
    public void cancelar(ScrimContext context) {
        context.getScrim().cancelar();
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public String canalNotificacion() {
        return "EMAIL";
    }
}
