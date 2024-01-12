package FaaS;

import java.util.Map;

public interface IActions {

    Integer sumar(Map<String, Object> params);
    Integer restar(Map<String, Object> params);
    Integer multiplicar(Map<String, Object> params);
    Integer dividir(Map<String, Object> params);
    Integer dormir(Map<String, Object> params);
    Integer factorial(Map<String, Object> params);
    Map<String, Integer> wordCount(Map<String, Object> params);
    Integer countWords(Map<String, Object> params);
}
