package FaaS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Actions{
        public static Function<Map<String, Object>, Object> sumar = params -> {
            // Supongamos que los parámetros vienen como Object y necesitan ser convertidos a Integer
            Integer x = (Integer) params.get("x");
            Integer y = (Integer) params.get("y");
            return x + y;
        };
    public static Function<Map<String, Object>, Object> restar = params -> {
        // Supongamos que los parámetros vienen como Object y necesitan ser convertidos a Integer
        Integer x = (Integer) params.get("x");
        Integer y = (Integer) params.get("y");
        return x - y;
    };
    public static Function<Map<String, Object>, Object> multiplicar = params -> {
        // Supongamos que los parámetros vienen como Object y necesitan ser convertidos a Integer
        Integer x = (Integer) params.get("x");
        Integer y = (Integer) params.get("y");
        return x * y;
    };
    public static Function<Map<String, Object>, Object> dividir = params -> {
        // Supongamos que los parámetros vienen como Object y necesitan ser convertidos a Integer
        Integer x = (Integer) params.get("x");
        Integer y = (Integer) params.get("y");
        return x / y;
    };
    public static Function<Map<String, Object>, Object> dormir = params -> {
        try {
            long time = ((Integer) params.get("time")).longValue();
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return params.get("time");
    };

    public static Function<Map<String, Object>, Object> factorial = params -> {
        int n = (int) params.get("number");
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    };

    public static Function<Map<String, Object>, Object> wordCount = input -> {
            Object textObj = input.get("text");
            if (textObj instanceof String text) {
                Map<String, Integer> wordCounts = new HashMap<>();
                String[] words = text.split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
                return wordCounts;
            } else {
                throw new IllegalArgumentException("wordCount espera una cadena de texto como entrada.");
            }
        };

    public static Function<Map<String, Object>, Object> countWords = (Map<String, Object> input) -> {
        String text = (String) input.get("text");
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.split("\\s+").length; // Retorna el total de palabras
    };

}