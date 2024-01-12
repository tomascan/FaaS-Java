package FaaS;

/**
 * Interfaz Observer para el patrón de diseño FaaS.Observer.
 * Define un método para actualizar métricas desde un objeto observado.
 */
public interface Observer {

    /**
     * Actualiza las métricas basadas en la información proporcionada por un objeto observado.
     * Este método se llama cuando un objeto observado,un Invoker, realiza un cambio relevante.
     * Se actualiza el Observer, en estre proyecto, el Controller.
     *
     * @param metric La métrica que contiene la información actualizada a procesar.
     */
    void updateMetrics(Metric metric);
}
