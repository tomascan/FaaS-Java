import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MemoizationDecorator implements Decorator {
    private Function<Map<String, Integer>, Integer> function;
    private Controller controller;

    public MemoizationDecorator(Function<Map<String, Integer>, Integer> function, Controller controller) {
        this.function = function;
        this.controller = controller;
    }

    @Override
    public Integer apply(Map<String, Integer> params) {
        String actionName = function.toString();
        Integer cachedResult = controller.getCachedResult(actionName, params);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            Integer result = function.apply(params);
            controller.cacheResult(actionName, params, result);
            return result;
        }
    }
}