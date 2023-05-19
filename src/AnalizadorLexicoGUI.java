import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnalizadorLexicoGUI extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JButton verificarButton;
    private JTable lexemasCorrectosTable;
    private JTable lexemasIncorrectosTable;
    private DefaultTableModel correctosTableModel;
    private DefaultTableModel incorrectosTableModel;
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

    //Interfaz grafica
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

        verificarButton = new JButton("Verificar");
        verificarButton.addActionListener(this);

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(verificarButton);

        panel.add(textScrollPane, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(correctosScrollPane, BorderLayout.WEST);
        panel.add(incorrectosScrollPane, BorderLayout.EAST);

        add(panel);
        setVisible(true);
    }

    //metodo para evaluar el tipo de lexema
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == verificarButton) {
            String[] lineas = textArea.getText().split("\n");
            for (String linea : lineas) {
                String[] palabras = linea.split("\\s+"); // Separar la línea en palabras usando espacios como delimitadores
                for (String palabra : palabras) {
                    String lexema = palabra.trim();
                    if (!lexema.isEmpty() && !lexemasRegistrados.contains(lexema)) {
                        lexemasRegistrados.add(lexema);
                        if  (Separador(lexema)) {
                            String token = obtenerTokenseparador();
                            Object[] row = {"Separador", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (OperadorAritmetico(lexema)) {
                            String token = obtenerTokenOperadorAritmetico();
                            Object[] row = {"Operador Aritmético", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (OperadorRelacional(lexema)){
                            String token = obtenerTokenOperadorRelacional();
                            Object[] row = {"Operador Relacional", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (OperadorAsignacion(lexema)) {
                            String token = obtenerTokenOperadorAsignacion();
                            Object[] row = {"Operador Asignación", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (OperadorLogico(lexema)) {
                            String token = obtenerTokenOperadorLogico();
                            Object[] row = {"Operador Lógico", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (Identificador(lexema)) {
                            String token = obtenerTokenIdentificador();
                            Object[] row = {"Identificador", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (NumeroEntero(lexema)){
                            String token = obtenerTokenNumEntero();
                            Object[] row = {"Numero Entero", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (NumeroDecimal(lexema)){
                            String token = obtenerTokenNumDecimal();
                            Object[] row = {"Numero con decimal", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (TipoDato(lexema)){
                            String token = obtenerTokenTipoDato();
                            Object[] row = {"Tipo de dato", lexema, token};
                            correctosTableModel.addRow(row);
                        } else if (Condicional(lexema)){
                            String token = obtenerTokenCondicional();
                            Object[] row = {"Condicional", lexema, token};
                            correctosTableModel.addRow(row);
                        } else {
                            Object[] row = {"Error de sintaxis", lexema, "-"};
                            incorrectosTableModel.addRow(row);
                        }
                    }
                }
            }
            textArea.setText("");
        }
    }
    //Contador de tokens para los lexemas
    private String obtenerTokenseparador() {
        String token = "TOKEN_Sep_" + contadorTokenSeparador;
        contadorTokenSeparador++;
        return token;
    }

    private String obtenerTokenOperadorAritmetico() {
        String token = "TOKEN_OP_ARIT_" + contadorTokenOperadorAritmetico;
        contadorTokenOperadorAritmetico++;
        return token;
    }

    private String obtenerTokenOperadorRelacional() {
        String token = "TOKEN_OP_Relaci_" + contadorTokenOperadorRelacional;
        contadorTokenOperadorRelacional++;
        return token;
    }

    private String obtenerTokenOperadorAsignacion() {
        String token = "TOKEN_OP_ASIG" + contadorTokenOperadorAsignacion;
        contadorTokenOperadorAsignacion++;
        return token;
    }

    private String obtenerTokenOperadorLogico() {
        String token = "TOKEN_OP_LOG" + contadorTokenOperadorLogico;
        contadorTokenOperadorLogico++;
        return token;
    }

    private String obtenerTokenIdentificador() {
        String token = "TOKEN_ID" + contadorTokenIdentificador;
        contadorTokenIdentificador++;
        return token;
    }
    private String obtenerTokenNumEntero() {
        String token = "TOKEN_Num_Entero_" + contadorTokenNumEnteros;
        contadorTokenNumEnteros++;
        return token;
    }
    private String obtenerTokenNumDecimal() {
        String token = "TOKEN_Num_Decimal_" + contadorTokenNumDecimal;
        contadorTokenNumDecimal++;
        return token;
    }
    private String obtenerTokenTipoDato() {
        String token = "TOKEN_Tip_Dato_" + contadorTokenTipoDatos;
        contadorTokenTipoDatos++;
        return token;
    }

    private String obtenerTokenCondicional() {
        String token = "TOKEN_Condicional_" + contadorTokenCondicional;
        contadorTokenCondicional++;
        return token;
    }
    //logica de los lexemas
    private boolean Separador(String lexema){
        return lexema.equals("(") || lexema.equals(")") || lexema.equals("{") || lexema.equals("}") || lexema.equals(",") || lexema.equals(";");
    }

    private boolean OperadorAritmetico(String lexema) {
        return lexema.equals("+") || lexema.equals("-") || lexema.equals("*") || lexema.equals("/");
    }

    private boolean OperadorRelacional(String lexema) {
        return lexema.equals("<") || lexema.equals(">") || lexema.equals("<=") || lexema.equals(">=") || lexema.equals("==") || lexema.equals("!=");
    }
    private boolean OperadorAsignacion(String lexema) {
        return lexema.equals("=");
    }
    private boolean OperadorLogico(String lexema) {
        return lexema.equals("&&") || lexema.equals("||");
    }

    private boolean Identificador(String lexema) {
        return lexema.matches("Ufr_(\\d|[a-zA-Z])*U");
    }
    private boolean NumeroEntero(String lexema){
        return lexema.matches("4[0-9]*4");
    }
    private boolean NumeroDecimal(String lexema){
        return lexema.matches("4[0-9]*\\.[0-9]*4");
    }
    private boolean TipoDato(String lexema){
        return lexema.matches("(Zhonya-|Liandry-|Doran-).*");
    }
    private boolean Condicional(String lexema){
        return lexema.matches("4\\[(if|then|else)\\]4");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AnalizadorLexicoGUI();
            }
        });
    }
}