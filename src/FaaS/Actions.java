package FaaS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Actions{
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

    public static Function<Map<String, Integer>, Integer> factorial = params -> {
        int n = params.get("number");
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    };

    private Map<String, Integer> wordCount(String text) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : text.split("\\s+")) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }
        return wordCounts;
    }

    public static Function<Map<String, Integer>, Integer> countWords = (Map<String, Integer> input) -> {
        String text = input.get("text").toString(); // Suponiendo que el texto se almacena como un entero que representa su hash
        int wordCount = text.split("\\s+").length;
        return wordCount; // Retorna el total de palabras
    };

}