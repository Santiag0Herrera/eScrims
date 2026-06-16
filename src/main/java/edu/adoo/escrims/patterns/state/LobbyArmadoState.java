package edu.adoo.escrims.patterns.state;

public class LobbyArmadoState implements ScrimState {
    private final String nombre = "LobbyArmado";

    @Override
    public void postular(ScrimContext context) {
        throw new IllegalStateException("El lobby ya esta armado");
    }

    @Override
    public void confirmar(ScrimContext context) {
        context.cambiarEstado(new ConfirmadoState());
    }

    @Override
    public void iniciar(ScrimContext context) {
        throw new IllegalStateException("Faltan confirmaciones");
    }

    @Override
    public void finalizar(ScrimContext context) {
        throw new IllegalStateException("No se puede finalizar un lobby");
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
        return "DISCORD";
    }
}
