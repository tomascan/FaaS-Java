package FaaS;

import java.util.Map;
import java.util.function.Function;

/**
 * Decorador que añade la funcionalidad de cronometraje a una función.
 * Mide el tiempo de ejecución de la función y lo imprime.
 */
public class TimerDecorator implements Decorator {
    private final Function<Map<String, Integer>, Integer> function;


    /**
     * Constructor para FaaS.TimerDecorator.
     *
     * @param function La función a la cual se le añadirá la funcionalidad de cronometraje.
     */
    public TimerDecorator(Function<Map<String, Integer>, Integer> function) {
        this.function = function;
    }

    /**
     * Ejecuta la función decorada, midiendo y mostrando su tiempo de ejecución.
     *
     * @param params Los parámetros para la función.
     * @return El resultado de la función decorada.
     */
    @Override
    public Integer apply(Map<String, Integer> params) {
        long startTime = System.nanoTime();
        Integer result = function.apply(params);
        long endTime = System.nanoTime();
        System.out.println("Tiempo de ejecución: " + ((endTime - startTime) / 1.0e6) + " ms");
        return result;
    }
}
