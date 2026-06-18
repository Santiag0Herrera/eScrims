package edu.adoo.escrims.patterns.state;

import edu.adoo.escrims.domain.Scrim;
import edu.adoo.escrims.patterns.observer.DomainEvent;
import edu.adoo.escrims.patterns.observer.DomainEventBus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        String emailsJugadores = scrim.getEquipos().stream()
                .flatMap(equipo -> equipo.getParticipaciones().stream())
                .map(participacion -> participacion.getUsuario().getEmail())
                .collect(Collectors.joining(","));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("scrim", scrim.getId());
        payload.put("estado", state.nombre());
        payload.put("juego", scrim.getJuego().getNombre());
        payload.put("formato", scrim.getFormato());
        payload.put("modalidad", scrim.getModalidad());
        payload.put("region", scrim.getRegion().getNombre());
        payload.put("fecha", scrim.getFechaHora().toString());
        payload.put("equipos", String.valueOf(scrim.getCantidadEquipos()));
        payload.put("jugadoresPorEquipo", String.valueOf(scrim.getJugadoresPorEquipo()));
        if (!emailsJugadores.isBlank()) {
            payload.put("jugadores", emailsJugadores);
        }

        eventBus.publish(new DomainEvent(
                "evt-" + scrim.getId() + "-" + state.nombre(),
                "SCRIM_" + state.nombre().toUpperCase(),
                scrim.getRegion().getNombre(),
                state.canalNotificacion(),
                payload
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
