import java.util.List;

public class PolicyManager {
    private Policy policy;
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public List<Integer> distributeFunctions(int totalFunctions, List<Integer> availableInvokers) {
        return policy.distributeFunctions(totalFunctions, availableInvokers);
    }
}
