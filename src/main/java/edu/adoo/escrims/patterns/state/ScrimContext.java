package edu.adoo.escrims.patterns.state;

import edu.adoo.escrims.domain.Scrim;
import edu.adoo.escrims.patterns.observer.DomainEvent;
import edu.adoo.escrims.patterns.observer.DomainEventBus;

import java.util.Map;
import java.util.Objects;

public class ScrimContext {
    private ScrimState state;
    private final Scrim scrim;
    private final DomainEventBus eventBus;

    public ScrimContext(Scrim scrim, DomainEventBus eventBus) {
        this.scrim = Objects.requireNonNull(scrim);
        this.eventBus = Objects.requireNonNull(eventBus);
        this.state = new BuscandoJugadoresState();
        this.scrim.cambiarEstado(state.nombre());
    }

    public void cambiarEstado(ScrimState nuevoEstado) {
        this.state = Objects.requireNonNull(nuevoEstado);
        scrim.cambiarEstado(state.nombre());
        eventBus.publish(new DomainEvent(
                "evt-" + scrim.getId() + "-" + state.nombre(),
                "SCRIM_" + state.nombre().toUpperCase(),
                scrim.getRegion().getNombre(),
                state.canalNotificacion(),
                Map.of("scrim", scrim.getId(), "estado", state.nombre(), "region", scrim.getRegion().getNombre())
        ));
    }

    public void postular() {
        state.postular(this);
    }

    public void confirmar() {
        state.confirmar(this);
    }

    public void iniciar() {
        state.iniciar(this);
    }

    public void finalizar() {
        state.finalizar(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }

    public Scrim getScrim() {
        return scrim;
    }

    public ScrimState getState() {
        return state;
    }
}
