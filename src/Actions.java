import java.util.Map;
import java.util.function.Function;

public class Actions {
    public static Function<Map<String, Integer>, Integer> sumar = params -> params.get("x") + params.get("y");
    public static Function<Map<String, Integer>, Integer> restar = params -> params.get("x") - params.get("y");
    public static Function<Map<String, Integer>, Integer> multiplicar = params -> params.get("x") * params.get("y");
    public static Function<Map<String, Integer>, Integer> dividir = params -> params.get("x") / params.get("y");
    public static Function<Map<String, Integer>, Integer> dormir = params -> {
        try {
            Thread.sleep(params.get("time")); // 'time' es el tiempo de dormir en milisegundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return params.get("time"); // El resultado de 'dormir' podría ser siempre 0, ya que su propósito es solo esperar
    };
}