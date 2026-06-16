package edu.adoo.escrims.patterns.state;

public class BuscandoJugadoresState implements ScrimState {
    private final String nombre = "BuscandoJugadores";

    @Override
    public void postular(ScrimContext context) {
        context.getScrim().abrirPostulaciones();
    }

    @Override
    public void confirmar(ScrimContext context) {
        if (!context.getScrim().tieneCupoCompleto()) {
            throw new IllegalStateException("No puedes cerrar postulaciones hasta completar todos los jugadores requeridos");
        }
        context.cambiarEstado(new LobbyArmadoState());
    }

    @Override
    public void iniciar(ScrimContext context) {
        throw new IllegalStateException("No se puede iniciar sin lobby armado");
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
