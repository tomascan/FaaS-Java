/**
 * Interfaz Observer para el patrón de diseño Observer.
 * Define un método para actualizar métricas desde un objeto observado.
 */
public interface Observer {

    /**
     * Actualiza las métricas basadas en la información proporcionada por un objeto observado.
     * Este método se llama cuando un objeto observado, como un Invoker, realiza un cambio relevante.
     *
     * @param metric La métrica que contiene la información actualizada a procesar.
     */
    void updateMetrics(Metric metric);
}
