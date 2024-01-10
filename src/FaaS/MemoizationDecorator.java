package FaaS;

import java.util.Map;
import java.util.function.Function;
/**
 * Decorador que añade memoización a una función.
 * Almacena los resultados de las invocaciones previas de la función y los reutiliza cuando se encuentran parámetros idénticos.
 */
public class MemoizationDecorator implements Decorator {
    private final Function<Map<String, Object>, Object> function;
    private final Controller controller;

    /**
     * Constructor para FaaS.MemoizationDecorator.
     *
     * @param function   La función a la cual se le añadirá la funcionalidad de memoización.
     * @param controller El controlador utilizado para almacenar y recuperar resultados memorizados.
     */
    public MemoizationDecorator(Function<Map<String, Object>, Object> function, Controller controller) {
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
        String actionName = function.toString();
        Integer cachedResult = controller.getCachedResult(actionName, params);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Integer result = (Integer) function.apply(params);
            controller.cacheResult(actionName, params, result);
            return result;
        }
    }
}