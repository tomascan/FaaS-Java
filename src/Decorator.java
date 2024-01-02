import java.util.Map;
import java.util.function.Function;
/**
 * Interfaz Decorator que extiende la interfaz Function para realizar la decoración de funciones.
 * Permite agregar funcionalidad adicional a las funciones existentes sin modificar su comportamiento principal.
 */
public interface Decorator extends Function<Map<String, Integer>, Integer> {
}
