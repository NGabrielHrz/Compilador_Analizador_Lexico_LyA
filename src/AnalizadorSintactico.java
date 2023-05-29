public class AnalizadorSintactico {
  private String codigo;
    private int posicion;

    public AnalizadorSintactico(String codigo) {
        this.codigo = codigo;
        this.posicion = 0;
    }

    public boolean analizar() {
        // Lógica para analizar la sintaxis del código
        boolean resultado = S(); // Llamada inicial al símbolo inicial S

        // Verificar si se analizó todo el código
        if (resultado && posicion == codigo.length()) {
            return true; // La sintaxis es correcta
        } else {
            return false; // La sintaxis es incorrecta
        }
    }

    // Regla S: S -> FL;
    private boolean S() {
        return F() && L() && match(";"); // Llamada a F, L y verificación de ;
    }

    // Regla F: F -> XY
    private boolean F() {
        return X() && Y(); // Llamada a X y Y
    }

    // Regla X: X -> Zhonya- | Liandry- | Doran-
    private boolean X() {
        return match("Zhonya-") || match("Liandry-") || match("Doran-"); // Verificación de las palabras clave
    }

    // Regla Y: Y -> UFR_J
    private boolean Y() {
        return match("UFR_") && J(); // Verificación de UFR_J
    }

    // Regla J: J -> ∈ | WJ | DJ | EJ
    private boolean J() {
        if (match("\u2205")) {
            return true; // ∈ se cumple
        } else if (W() && J()) {
            return true; // WJ se cumple
        } else if (D() && J()) {
            return true; // DJ se cumple
        } else if (E() && J()) {
            return true; // EJ se cumple
        } else {
            return false; // Ninguna de las opciones se cumple
        }
    }

    // Regla W: W -> 0 | 1 | 2 | ... | 9
    private boolean W() {
        return matchDigit(); // Verificación de un dígito
    }

    // Regla D: D -> a | b | c | ... | z
    private boolean D() {
        return matchLetter(); // Verificación de una letra
    }

    // Regla E: E -> A | B | C | ... | Z
    private boolean E() {
        return matchLetter(); // Verificación de una letra
    }

    // Regla L: L -> ∈ | ∅YL
    private boolean L() {
        while (posicion < codigo.length()) {
            char currentChar = codigo.charAt(posicion);
            if (Character.isWhitespace(currentChar)) {
                return Y() && L();
            }
            else if (currentChar == '\u2205'){
                posicion++;
            }
            else {
                break; // Salir del bucle al encontrar un carácter distinto de espacio en blanco o caracter vacío
            }
        }
        return true; // Se considera válido si se encontraron espacios en blanco o caracteres vacíos
    }

    // Método auxiliar para verificar un dígito
    private boolean matchDigit() {
        if (posicion < codigo.length() && Character.isDigit(codigo.charAt(posicion))) {
            posicion++;
            return true; // El siguiente caracter es un dígito
        } else {
            return false; // No se cumple el dígito
        }
    }

    // Método auxiliar para verificar una letra
    private boolean matchLetter() {
        if (posicion < codigo.length() && Character.isLetter(codigo.charAt(posicion))) {
            posicion++;
            return true; // El siguiente caracter es una letra
        } else {
            return false; // No se cumple la letra
        }
    }

    // Método auxiliar para verificar un token específico
    private boolean match(String token) {
        if (posicion + token.length() <= codigo.length() && codigo.substring(posicion, posicion + token.length()).equals(token)) {
            posicion += token.length();
            return true; // Se cumple el token
        } else {
            return false; // No se cumple el token
        }
    }
    public int getPosicion() {
        return posicion;
    }
}