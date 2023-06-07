import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class AnalizadorLexicoGUI extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JButton verificarButton;
    private JButton limpiarButton;
    private JButton TextFileCreation;
    private JButton abrirArchivoButton;
    private JTable lexemasCorrectosTable;
    private JTable lexemasIncorrectosTable;
    private DefaultTableModel correctosTableModel;
    private DefaultTableModel incorrectosTableModel;
    private static final String TOKENS_FILENAME = "tokens.txt";
    private Set<String> lexemasRegistrados;
    private Map<String, String> lexemasTokens;
    private int contadorTokenSeparador = 1;
    private int contadorTokenOperadorAritmetico = 1;
    private int contadorTokenOperadorRelacional = 1;
    private int contadorTokenOperadorAsignacion = 1;
    private int contadorTokenOperadorLogico = 1;
    private int contadorTokenIdentificador = 1;
    private int contadorTokenNumEnteros = 1;
    private int contadorTokenNumDecimal = 1;
    private int contadorTokenTipoDatos = 1;
    private int contadorTokenCondicional = 1;
    private int filaActual = 1;

    // Interfaz grafica
    public AnalizadorLexicoGUI() {
        setTitle("Analizador Léxico");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setRows(10);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        abrirArchivoButton = new JButton("Archivos");
        abrirArchivoButton.addActionListener(this);

        verificarButton = new JButton("Compilar");
        verificarButton.addActionListener(this);

        TextFileCreation = new JButton("TXT");
        TextFileCreation.addActionListener(this);

        limpiarButton = new JButton("Limpiar");
        limpiarButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(abrirArchivoButton);
        buttonPanel.add(verificarButton);
        buttonPanel.add(TextFileCreation);
        buttonPanel.add(limpiarButton);

        lexemasRegistrados = new HashSet<>();
        lexemasTokens = new HashMap<>();

        correctosTableModel = new DefaultTableModel();
        correctosTableModel.addColumn("Tipo");
        correctosTableModel.addColumn("Lexema");
        correctosTableModel.addColumn("Token");
        lexemasCorrectosTable = new JTable(correctosTableModel);

        incorrectosTableModel = new DefaultTableModel();
        incorrectosTableModel.addColumn("Error");
        incorrectosTableModel.addColumn("Lexema");
        incorrectosTableModel.addColumn("Fila");
        lexemasIncorrectosTable = new JTable(incorrectosTableModel);

        JScrollPane correctosScrollPane = new JScrollPane(lexemasCorrectosTable);
        correctosScrollPane.setPreferredSize(new Dimension(400, 300));
        correctosScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JScrollPane incorrectosScrollPane = new JScrollPane(lexemasIncorrectosTable);
        incorrectosScrollPane.setPreferredSize(new Dimension(400, 300));
        incorrectosScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(textScrollPane, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(correctosScrollPane, BorderLayout.WEST);
        panel.add(incorrectosScrollPane, BorderLayout.EAST);

        add(panel);
        setVisible(true);
    }

    private boolean evaluarSintaxis() {
        String codigo = textArea.getText();
        AnalizadorSintactico analizador = new AnalizadorSintactico(codigo);
        return analizador.analizar();
    }

    private String obtenerErrorSintactico() {
        AnalizadorSintactico analizador = new AnalizadorSintactico(textArea.getText());
        boolean resultado = analizador.analizar();
        if (!resultado) {
            return "Error en la posición: " + analizador.getPosicion();
        }
        return "";
    }

    private void guardarEnArchivo(String filename, String contenido) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.append(contenido);
            writer.newLine();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void escribirArchivo(String filename, Set<String> contenido) {
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
                textArea.setText(sb.toString().trim());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // metodo para evaluar el tipo de lexema
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == verificarButton) {
            String[] lineas = textArea.getText().split("\n");
            for (String linea : lineas) {
                String[] palabras = linea.split("\\s+");
                for (String palabra : palabras) {
                    String lexema = palabra.trim();
                    if (!lexema.isEmpty() && !lexemasRegistrados.contains(lexema)) {
                        lexemasRegistrados.add(lexema);
                        if (Separador(lexema)) {
                            String token = obtenerTokenseparador();
                            Object[] row = { "Separador", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Separador: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (OperadorAritmetico(lexema)) {
                            String token = obtenerTokenOperadorAritmetico();
                            Object[] row = { "Operador Aritmético", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Operador Aritmetico: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (OperadorRelacional(lexema)) {
                            String token = obtenerTokenOperadorRelacional();
                            Object[] row = { "Operador Relacional", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Operador Relacional: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (OperadorAsignacion(lexema)) {
                            String token = obtenerTokenOperadorAsignacion();
                            Object[] row = { "Operador Asignación", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Operador Asignación: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (OperadorLogico(lexema)) {
                            String token = obtenerTokenOperadorLogico();
                            Object[] row = { "Operador Lógico", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Operador Lógico: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (Identificador(lexema)) {
                            String token = obtenerTokenIdentificador();
                            Object[] row = { "Identificador", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Identificador: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (NumeroEntero(lexema)) {
                            String token = obtenerTokenNumEntero();
                            Object[] row = { "Numero Entero", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Numero Entero: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (NumeroDecimal(lexema)) {
                            String token = obtenerTokenNumDecimal();
                            Object[] row = { "Numero con decimal", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Numero con decimal: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (TipoDato(lexema)) {
                            String token = obtenerTokenTipoDato();
                            Object[] row = { "Tipo de dato", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Tipo de dato: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else if (Condicional(lexema)) {
                            String token = obtenerTokenCondicional();
                            Object[] row = { "Condicional", lexema, token };
                            correctosTableModel.addRow(row);
                            String contenido = "Condicional: " + lexema + ", Token: " + token;
                            guardarEnArchivo("datos.txt", contenido);
                        } else {
                            Object[] row = { "Error de lexema", lexema, String.valueOf(filaActual) };
                            incorrectosTableModel.addRow(row);
                        }
                    }
                }
                filaActual++;
            }
            filaActual = 1;

            if (evaluarSintaxis()) {
                // La sintaxis es correcta
                JOptionPane.showMessageDialog(this, "El código tiene una sintaxis válida");
            } else {
                // La sintaxis es incorrecta
                String error = obtenerErrorSintactico();
                JOptionPane.showMessageDialog(this, "El código tiene errores de sintaxis: " + error);
            }
        } else if (e.getSource() == limpiarButton) {
            textArea.setText("");
        } else if (e.getSource() == TextFileCreation) {
            escribirArchivo(TOKENS_FILENAME, lexemasRegistrados);
        } else if (e.getSource() == abrirArchivoButton) {
            abrirArchivo();
        }
    }

    // Contador de tokens para los lexemas
    private String obtenerTokenseparador() {
        String token = "DEL" + contadorTokenSeparador;
        contadorTokenSeparador++;
        return token;
    }

    private String obtenerTokenOperadorAritmetico() {
        String token = "OA" + contadorTokenOperadorAritmetico;
        contadorTokenOperadorAritmetico++;
        return token;
    }

    private String obtenerTokenOperadorRelacional() {
        String token = "OP" + contadorTokenOperadorRelacional;
        contadorTokenOperadorRelacional++;
        return token;
    }

    private String obtenerTokenOperadorAsignacion() {
        String token = "AS" + contadorTokenOperadorAsignacion;
        contadorTokenOperadorAsignacion++;
        return token;
    }

    private String obtenerTokenOperadorLogico() {
        String token = "OL" + contadorTokenOperadorLogico;
        contadorTokenOperadorLogico++;
        return token;
    }

    private String obtenerTokenIdentificador() {
        String token = "ID" + contadorTokenIdentificador;
        contadorTokenIdentificador++;
        return token;
    }

    private String obtenerTokenNumEntero() {
        String token = "NE" + contadorTokenNumEnteros;
        contadorTokenNumEnteros++;
        return token;
    }

    private String obtenerTokenNumDecimal() {
        String token = "ND" + contadorTokenNumDecimal;
        contadorTokenNumDecimal++;
        return token;
    }

    private String obtenerTokenTipoDato() {
        String token = "TD" + contadorTokenTipoDatos;
        contadorTokenTipoDatos++;
        return token;
    }

    private String obtenerTokenCondicional() {
        String token = "IC" + contadorTokenCondicional;
        contadorTokenCondicional++;
        return token;
    }

    // logica de los lexemas
    private boolean Separador(String lexema) {
        return lexema.equals("(") || lexema.equals(")") || lexema.equals("{") || lexema.equals("}")
                || lexema.equals(",") || lexema.equals(";");
    }

    private boolean OperadorAritmetico(String lexema) {
        return lexema.equals("+") || lexema.equals("-") || lexema.equals("*") || lexema.equals("/")
                || lexema.equals("%");
    }

    private boolean OperadorRelacional(String lexema) {
        return lexema.equals("<") || lexema.equals(">") || lexema.equals("<=") || lexema.equals(">=")
                || lexema.equals("==") || lexema.equals("!=");
    }

    private boolean OperadorAsignacion(String lexema) {
        return lexema.equals("=");
    }

    private boolean OperadorLogico(String lexema) {
        return lexema.equals("&&") || lexema.equals("||");
    }

    private boolean Identificador(String lexema) {
        return lexema.matches("URF_[a-zA-Z0-9]+;");
    }

    private boolean NumeroEntero(String lexema) {
        return lexema.matches("4\\d+4");
    }

    private boolean NumeroDecimal(String lexema) {
        return lexema.matches("4\\d+4\\.\\d+");
    }

    private boolean TipoDato(String lexema) {
        return lexema.matches("Zhonya-|Liandry-|Doran-");
    }

    private boolean Condicional(String lexema) {
        return lexema.matches("4(if|then|else)4");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AnalizadorLexicoGUI();
            }
        });
    }
}