package FaaS;

/**
 * Clase para representar métricas de ejecución de una acción, incluyendo el tiempo de ejecución y el uso de memoria.
 * Esta clase se utiliza para recopilar y mostrar información sobre el rendimiento de las acciones ejecutadas.
 */
public record Metric(int id, long executionTime, int memoryUsed) {
    /**
     * Constructor para crear una nueva métrica.
     *
     * @param id            El identificador del invocador que ejecutó la acción.
     * @param executionTime El tiempo de ejecución de la acción en milisegundos.
     * @param memoryUsed    La cantidad de memoria utilizada para ejecutar la acción.
     */
    public Metric {
    }


    @Override
    public String toString() {
        double executionTimeSeconds = executionTime / 1000.0; // Convierte milisegundos a segundos
        return "Metric{" +
                "invokerId='" + id + '\'' +
                ", executionTime=" + String.format("%.6f", executionTimeSeconds) +
                ", Action Memory=" + memoryUsed +
                '}';
    }
}
