package io.github.agus5534.googleocrtelegramas.utils.texts;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase para convertir cadenas de texto en números según un mapeo predefinido.
 */
public class StringToNumberConverter {

    /**
     * Convierte una cadena de texto en un número utilizando un mapeo predefinido.
     *
     * @param value Cadena de texto a convertir.
     * @return El número convertido, o -1 si la cadena está vacía o nula.
     */
    public static int convert(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        return customStringToNumber(value);
    }

    /**
     * Convierte una cadena de texto en un número utilizando un mapeo predefinido.
     *
     * @param value Cadena de texto a convertir.
     * @return El número convertido, o -1 si no se puede convertir.
     */
    private static int customStringToNumber(String value) {
        String lowerCaseValue = value.toLowerCase();
        StringBuilder result = new StringBuilder();

        for (char character : lowerCaseValue.toCharArray()) {
            if (Character.isDigit(character)) {
                result.append(character);
            } else {
                Integer mappedValue = charToNumberMap.get(character);
                if (mappedValue != null) {
                    result.append(mappedValue);
                } else {
                    return -1;
                }
            }
        }

        try {
            return Integer.parseInt(result.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Mapeo de caracteres a números.
     */
    private static final Map<Character, Integer> charToNumberMap = new HashMap<>();
    static {
        charToNumberMap.put('o', 0);
        charToNumberMap.put('c', 0);
        charToNumberMap.put('l', 1);
        charToNumberMap.put('n', 1);
        charToNumberMap.put('i', 1);
        charToNumberMap.put('|', 1);
        charToNumberMap.put('(', 1);
        charToNumberMap.put('z', 2);
        charToNumberMap.put('e', 6);
        charToNumberMap.put('b', 6);
        charToNumberMap.put('G', 6);
        charToNumberMap.put('a', 4);
        charToNumberMap.put('y', 4);
        charToNumberMap.put('v', 4);
        charToNumberMap.put('h', 4);
        charToNumberMap.put('u', 4);
        charToNumberMap.put('k', 4);
        charToNumberMap.put('\u0446', 4);
        charToNumberMap.put('\u0438', 4);
        charToNumberMap.put('s', 5);
        charToNumberMap.put('g', 9);
        charToNumberMap.put('q', 9);
        charToNumberMap.put('p', 9);
        charToNumberMap.put('B', 13);
        charToNumberMap.put('?', 7);
        charToNumberMap.put('\u0E32', 7);
        charToNumberMap.put('\u3131', 7);
        charToNumberMap.put('+', 7);
        charToNumberMap.put('&', 8);
    }
}
