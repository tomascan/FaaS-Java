package FaaS;

/**
 * Clase para representar métricas de ejecución de una acción, incluyendo el tiempo de ejecución y el uso de memoria.
 * Esta clase se utiliza para recopilar y mostrar información sobre el rendimiento de las acciones ejecutadas.
 */
public class Metric {
    private final int id;
    private final long executionTime;
    private final int memoryUsed;

    /**
     * Constructor para crear una nueva métrica.
     *
     * @param id            El identificador del invocador que ejecutó la acción.
     * @param executionTime El tiempo de ejecución de la acción en milisegundos.
     * @param memoryUsed    La cantidad de memoria utilizada para ejecutar la acción.
     */
    public Metric(int id, long executionTime, int memoryUsed) {
        this.id = id;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getId() {
        return id;
    }

    public int getMemoryUsed() {
        return memoryUsed;
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
