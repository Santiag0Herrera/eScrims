package edu.adoo.escrims.patterns.state;

public class EnJuegoState implements ScrimState {
    private final String nombre = "EnJuego";

    @Override
    public void postular(ScrimContext context) {
        throw new IllegalStateException("La partida ya comenzo");
    }

    @Override
    public void confirmar(ScrimContext context) {
        throw new IllegalStateException("La partida ya comenzo");
    }

    @Override
    public void iniciar(ScrimContext context) {
        context.getScrim().iniciar();
    }

    @Override
    public void finalizar(ScrimContext context) {
        context.cambiarEstado(new FinalizadoState());
    }

    @Override
    public void cancelar(ScrimContext context) {
        context.cambiarEstado(new CanceladoState());
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public String canalNotificacion() {
        return "PUSH";
    }
}
