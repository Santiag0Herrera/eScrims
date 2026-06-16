package edu.adoo.escrims.patterns.state;

public interface ScrimState {
    void postular(ScrimContext context);
    void confirmar(ScrimContext context);
    void iniciar(ScrimContext context);
    void finalizar(ScrimContext context);
    void cancelar(ScrimContext context);
    String nombre();
    String canalNotificacion();
}
