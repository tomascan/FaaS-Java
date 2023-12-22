import java.util.Map;

public class AddAction implements Action<Map<Object, Object>, Object> {

    @Override
    public Object run(Map<Object, Object> args) {
        String operation = args.get("operation_name").toString().toLowerCase();

        switch (operation) {
            case "sum":
                return (Integer) args.get("x") + (Integer) args.get("y");
            case "subtract":
                return (Integer) args.get("x") - (Integer) args.get("y");
            case "multiply":
                return (Integer) args.get("x") * (Integer) args.get("y");
            case "sleep":
                // Dormir durante 'x' segundos
                try {
                    Thread.sleep((Integer) args.get("x") * 1000L);
                    return args.get("y"); // Retorna 'y' después de dormir
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
