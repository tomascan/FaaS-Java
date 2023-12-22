import java.util.List;
import java.util.Map;

public interface Policy {
    Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction);
}