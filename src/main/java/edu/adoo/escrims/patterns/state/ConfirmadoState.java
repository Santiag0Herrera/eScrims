package edu.adoo.escrims.patterns.state;

public class ConfirmadoState implements ScrimState {
    private final String nombre = "Confirmado";

    @Override
    public void postular(ScrimContext context) {
        throw new IllegalStateException("El scrim ya esta confirmado");
    }

    @Override
    public void confirmar(ScrimContext context) {
        context.getScrim().cambiarEstado(nombre);
    }

    @Override
    public void iniciar(ScrimContext context) {
        if (context.getScrim().getConfirmaciones().size() < context.getScrim().getCantidadJugadores()) {
            throw new IllegalStateException("No puedes iniciar hasta que todos los jugadores confirmen asistencia");
        }
        context.cambiarEstado(new EnJuegoState());
    }

    @Override
    public void finalizar(ScrimContext context) {
        throw new IllegalStateException("No se puede finalizar antes de iniciar");
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
