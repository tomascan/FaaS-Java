package FaaS;

import java.util.Map;
import java.util.function.Function;
/**
 * Decorador que añade memoización a una función.
 * Almacena los resultados de las invocaciones previas de la función y los reutiliza cuando se encuentran parámetros idénticos.
 */
public class MemoizationDecorator implements Decorator {
    private final String actionName;
    private final Function<Map<String, Object>, Object> function;
    private final Controller controller;

    /**
     * Constructor para FaaS.MemoizationDecorator.
     *
     * @param actionName Nombre de la función
     * @param function   La función a la cual se le añadirá la funcionalidad de memoización.
     * @param controller El controlador utilizado para almacenar y recuperar resultados memorizados.
     */
    public MemoizationDecorator(String actionName, Function<Map<String, Object>, Object> function, Controller controller) {
        this.actionName = actionName;
        this.function = function;
        this.controller = controller;
    }

    /**
     * Ejecuta la función decorada con memoización.
     * Si el resultado ya está memorizado para los parámetros dados, lo retorna; de lo contrario, ejecuta la función y almacena su resultado.
     *
     * @param params Los parámetros para la función.
     * @return El resultado de la función, ya sea recuperado de la memoria o calculado.
     */
    @Override
    public Object apply(Map<String, Object> params) {
        String action = actionName;
        Integer cachedResult = controller.getCachedResult(action, params);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Integer result = (Integer) function.apply(params);
            controller.cacheResult(action, params, result);
            return result;
        }
    }
}