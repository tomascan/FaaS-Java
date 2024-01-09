package FaaS;

/**
 * Interfaz FaaS.Observer para el patrón de diseño FaaS.Observer.
 * Define un método para actualizar métricas desde un objeto observado.
 */
public interface Observer {

    /**
     * Actualiza las métricas basadas en la información proporcionada por un objeto observado.
     * Este método se llama cuando un objeto observado, como un FaaS.Invoker, realiza un cambio relevante.
     *
     * @param metric La métrica que contiene la información actualizada a procesar.
     */
    void updateMetrics(Metric metric);
}
