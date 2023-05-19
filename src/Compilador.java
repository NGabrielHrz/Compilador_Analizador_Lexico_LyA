import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

public class Compilador extends JFrame implements ActionListener {
    // -------------------------------------------------------------------------------------------------
    private static final String SEPARADORES_PATTERN = "[\\(\\)\\{\\}\\[\\]]";
    private static final String OPERADORES_ARITMETICOS_PATTERN = "[\\+\\-\\*/%]";
    private static final String MISCELANEOS_PATTERN = "[\\:\\,;]";
    private static final String OPERADORES_RELACIONALES_PATTERN = "[=!><]=|<>|<|>";
    private static final String OPERADOR_ASIGNACION_PATTERN = "=";
    private static final String OPERADORES_LOGICOS_PATTERN = "&&|\\|\\|";
    private static final String IDENTIFICADORES_PATTERN = "[a-zA-Z]+\\w*";
    private static final String IDENTIFICADORES_PATTERN_E4 = "URF_[a-zA-Z0-9]+";
    private static final String NUMEROS_ENTEROS_PATTERN = "\\d+";
    private static final String NUMEROS_ENTEROS_PATTERN_E4 = "4\\d+4";
    private static final String NUMEROS_DECIMALES_PATTERN = "\\d+\\.\\d+";
    private static final String NUMEROS_DECIMALES_PATTERN_E4 = "4\\d+\\.\\d+4";
    private static final String TIPOS_DE_DATOS_PATTERN = "int|float|double|long|boolean|char|string";
    private static final String TIPOS_DE_DATOS_PATTERN_E4 = "Zhonya-|Liandry-|Doran-";
    private static final String INSTRUCIONES_CICLICAS_PATTERN = "while|for|do";
    private static final String INSTRUCIONES_CONDICIONALES_PATTERN = "if|else|switch|case|default";
    private static final String EQUIPO_4_PATTERN = "4(if|then|else)4";
    // -------------------------------------------------------------------------------------------------
    private static final Map<String, String> TOKENS = new HashMap<String, String>() {
        {
            put(SEPARADORES_PATTERN, "DEL");/// 1
            put(OPERADORES_ARITMETICOS_PATTERN, "OA");// 2
            put(MISCELANEOS_PATTERN, "SEP");// 3
            put(OPERADORES_RELACIONALES_PATTERN, "OR");// 4
            put(OPERADOR_ASIGNACION_PATTERN, "AS");// 5
            put(OPERADORES_LOGICOS_PATTERN, "OL");
            put(IDENTIFICADORES_PATTERN, "ID");/// 6
            put(IDENTIFICADORES_PATTERN_E4, "IDe4");
            put(NUMEROS_ENTEROS_PATTERN, "NE");
            put(NUMEROS_ENTEROS_PATTERN_E4, "NEe4");
            put(NUMEROS_DECIMALES_PATTERN, "ND");
            put(NUMEROS_DECIMALES_PATTERN_E4, "NDe4");
            put(TIPOS_DE_DATOS_PATTERN, "TD");// 7
            put(TIPOS_DE_DATOS_PATTERN_E4, "TDe4");
            put(INSTRUCIONES_CICLICAS_PATTERN, "IR");// 8
            put(INSTRUCIONES_CONDICIONALES_PATTERN, "IC");// 9
            put(EQUIPO_4_PATTERN, "E4P");
        }
    };
    // =================================================================================================
    private static final String ERROR_TOKEN = "ERROR";

    private static final String SIMBOLOS_FILENAME = "simbolos.txt";
    private static final String ERRORES_FILENAME = "errores.txt";
    private static final String TOKENS_FILENAME = "tokens.txt";

    private ArrayList<String> simbolos = new ArrayList<String>();
    private ArrayList<String> errores = new ArrayList<String>();
    private ArrayList<String> tokens = new ArrayList<String>();

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;

    public Compilador() {
        setTitle("Compilador");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea();
        inputPanel.add(inputTextArea, BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputPanel.add(outputTextArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton compilarButton = new JButton("Compilar");
        compilarButton.addActionListener(this);
        buttonPanel.add(compilarButton);

        JButton abrirArchivoButton = new JButton("Abrir archivo");
        abrirArchivoButton.addActionListener(this);
        buttonPanel.add(abrirArchivoButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Compilador();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Compilar")) {
            compilar();
        } else if (e.getActionCommand().equals("Abrir archivo")) {
            abrirArchivo();
        }
    }

    private void compilar() {
        String input = inputTextArea.getText();
        simbolos.clear();
        errores.clear();
        tokens.clear();

        String[] lines = input.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] lexemes = line.split("\\s+");

            for (String lexeme : lexemes) {
                if (lexeme.isEmpty()) {
                    continue;
                }

                boolean matchFound = false;

                for (String pattern : TOKENS.keySet()) {
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(lexeme);

                    if (m.matches()) {
                        matchFound = true;
                        String token = TOKENS.get(pattern);
                        simbolos.add(token + "\t" + lexeme);
                        tokens.add(token);
                        break;
                    }
                }

                if (!matchFound) {
                    errores.add(ERROR_TOKEN + "\t" + lexeme + "\t" + (i + 1) + "\tToken no reconocido");
                    tokens.add(ERROR_TOKEN);
                }
            }
        }

        escribirTabla(SIMBOLOS_FILENAME, simbolos);
        escribirTabla(ERRORES_FILENAME, errores);
        escribirArchivo(TOKENS_FILENAME, tokens);
        mostrarTabla();
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto", "txt");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

                br.close();
                inputTextArea.setText(sb.toString().trim());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void escribirTabla(String filename, ArrayList<String> tabla) {
        try {
            FileWriter fw = new FileWriter(filename);

            for (String linea : tabla) {
                fw.write(linea + "\n");
            }

            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void escribirArchivo(String filename, ArrayList<String> contenido) {
        try {
            FileWriter fw = new FileWriter(filename);

            for (String linea : contenido) {
                fw.write(linea + "\n");
            }

            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarTabla() {
        HashMap<String, Integer> lexemaCount = new HashMap<String, Integer>(); // Para realizar el conteo de lexemas
        HashMap<String, String> lexemaTokenMap = new HashMap<String, String>(); // Mapa de lexema a token único
        ArrayList<String[]> tablaSimbolos = new ArrayList<String[]>(); // Tabla de símbolos modificada sin repeticiones
        int tokenCounter = 1; // Contador para generar identificadores numéricos de los tokens
        for (int i = 0; i < simbolos.size(); i++) {
            String[] parts = simbolos.get(i).split("\\t");
            String token = parts[0];
            String lexema = parts[1];
            if (!lexemaCount.containsKey(lexema)) {
                lexemaCount.put(lexema, 1);
            } else {
                int count = lexemaCount.get(lexema) + 1;
                lexemaCount.put(lexema, count);
            }
            String tokenIdentifier;
            if (!lexemaTokenMap.containsKey(lexema)) {
                tokenIdentifier = token + tokenCounter;
                lexemaTokenMap.put(lexema, tokenIdentifier);
                tokenCounter++;
            } else {
                tokenIdentifier = lexemaTokenMap.get(lexema);
            }
            String[] symbolEntry = { tokenIdentifier, lexema };
            tablaSimbolos.add(symbolEntry);
        }
        String[] columnNames = { "Token", "Lexema" };
        String[][] data = new String[tablaSimbolos.size()][2];
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            data[i] = tablaSimbolos.get(i);
        }
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Tabla de símbolos", JOptionPane.PLAIN_MESSAGE);
        if (!errores.isEmpty()) {
            String[] errorColumnNames = { "Token de error", "Lexema", "Línea", "Descripción" };
            String[][] errorData = new String[errores.size()][4];
            for (int i = 0; i < errores.size(); i++) {
                String[] parts = errores.get(i).split("\\t");
                errorData[i][0] = parts[0];
                errorData[i][1] = parts[1];
                errorData[i][2] = parts[2];
                errorData[i][3] = parts[3];
            }
            JTable errorTable = new JTable(errorData, errorColumnNames);
            errorTable.setEnabled(false);
            JScrollPane errorScrollPane = new JScrollPane(errorTable);
            errorScrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(this, errorScrollPane, "Tabla de errores", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void analisisSintactico() {
        String input = inputTextArea.getText();
        String[] lines = input.split("\\n");
        boolean sintaxisCorrecta = true;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            sintaxisCorrecta = analizarLinea(line);

            if (!sintaxisCorrecta) {
                break;
            }
        }

        if (sintaxisCorrecta) {
            outputTextArea.append("El análisis sintáctico fue exitoso. La sintaxis es correcta.\n");
        } else {
            outputTextArea.append("Se encontraron errores en el análisis sintáctico. La sintaxis es incorrecta.\n");
        }
    }

    private boolean analizarLinea(String line) {
        String noTerminalActual = "S";
        noTerminalActual = noTerminalActual.replace("S", "FP");
        noTerminalActual = noTerminalActual.replace("X", "Zhonya- | Liandry- | Doran-");
        noTerminalActual = noTerminalActual.replace("Y", "UFR_J");
        noTerminalActual = noTerminalActual.replace("J", "∈ | WJ | DJ | EJ");
        noTerminalActual = noTerminalActual.replace("W", "0 | 1 | 2 | … | 9");
        noTerminalActual = noTerminalActual.replace("D", "a | b | c | … | z");
        noTerminalActual = noTerminalActual.replace("E", "A | B | C | … | Z");
        noTerminalActual = noTerminalActual.replace("F", "XY");
        noTerminalActual = noTerminalActual.replace("P", "∈ |,∅YP");
        boolean sintaxisCorrecta = line.equals(noTerminalActual);
        if (sintaxisCorrecta) {
            outputTextArea.append("La línea '" + line + "' es sintácticamente correcta.\n");
        } else {
            outputTextArea.append("Error de sintaxis en la línea '" + line + "'.\n");
        }
        return sintaxisCorrecta;
    }
}