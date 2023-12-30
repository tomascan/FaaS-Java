import java.util.Map;
import java.util.function.Function;
public class TimerDecorator implements Decorator {
    private final Function<Map<String, Integer>, Integer> function;

    public TimerDecorator(Function<Map<String, Integer>, Integer> function) {
        this.function = function;
    }

    @Override
    public Integer apply(Map<String, Integer> params) {
        long startTime = System.currentTimeMillis();
        Integer result = function.apply(params);
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
        return result;
    }
}
