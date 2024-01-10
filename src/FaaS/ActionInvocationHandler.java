package FaaS;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * Manejador de invocación para el proxy de acciones.
 * Intercepta las llamadas a métodos realizadas a través de un proxy y las redirige al controlador apropiado.
 */
public class ActionInvocationHandler implements InvocationHandler {
    private final Controller controller;

    /**
     * Constructor de FaaS.ActionInvocationHandler.
     *
     * @param controller El controlador al cual redirigir las llamadas de método.
     */
    public ActionInvocationHandler(Controller controller) {
        this.controller = controller;
    }

    /**
     * Método interceptado para cada invocación a través del proxy.
     * Delega la ejecución de la acción al controlador basándose en el nombre del método y los parámetros proporcionados.
     *
     * @param proxy  El proxy a través del cual se realiza la llamada.
     * @param method El método que se está invocando.
     * @param args   Los argumentos del método invocado.
     * @return El resultado de la acción ejecutada por el controlador.
     * @throws Throwable Si ocurre un error durante la invocación.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Obtiene el nombre del método que fue llamado en el proxy
        String actionName = method.getName();

        // Asume que el primer argumento es un Map<String, Integer>
        // Verifica si el método existe en FaaS.Actions y lo invoca, de lo contrario devuelve null
        if (Actions.class.getDeclaredField(actionName) != null) {
            Function<Map<String, Integer>, Integer> action = (Function<Map<String, Integer>, Integer>) Actions.class.getDeclaredField(actionName).get(null);
            return controller.invoke(actionName, (Map<String, Object>) args[0]);
        }

        return null;
    }
}
