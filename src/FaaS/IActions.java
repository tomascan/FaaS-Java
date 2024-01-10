package FaaS;

import java.util.Map;

public interface IActions {

    Integer sumar(Map<String, Integer> params);
    Integer restar(Map<String, Integer> params);
    Integer multiplicar(Map<String, Integer> params);
    Integer dividir(Map<String, Integer> params);
    Integer dormir(Map<String, Integer> params);
    Integer factorial(Map<String, Integer> params);
    Map<String, Integer> wordCount(Map<String, Object> params);
    Integer countWords(Map<String, Object> params);
}
