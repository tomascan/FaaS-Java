package FaaS;

import java.util.List;
import java.util.Map;

public interface Policy {
    /**
     * Método para distribuir una lista de acciones entre un conjunto de invocadores.
     * La implementación de este método deberá decidir cómo asignar las acciones a los invocadores
     * basándose en la memoria requerida por acción y otros criterios definidos en la política concreta.
     *
     * @param actions         Lista de mapas representando las acciones a distribuir, donde cada mapa contiene los parámetros necesarios para una acción.
     * @param invokers        Lista de invocadores disponibles para ejecutar las acciones.
     * @param memoryPerAction Cantidad de memoria requerida por cada acción.
     * @return                Un mapa que asocia cada invocador con una lista de acciones (y sus parámetros) que se les ha asignado.
     */
    Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction);
}