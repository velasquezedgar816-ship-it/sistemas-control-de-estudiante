import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ====================================================================
// 1. SISTEMA DE AUDIO INTERACTIVO (SINTETIZADOR DINÁMICO TÁCTIL)
// ====================================================================

class GestorAudio {
    public static void reproducirClickSincronizado() {
        new Thread(() -> {
            try {
                float sampleRate = 44100F;
                int duracionMs = 22;
                int numSamples = (int) (sampleRate * duracionMs / 1000);
                byte[] buffer = new byte[numSamples];

                AudioFormat formato = new AudioFormat(sampleRate, 8, 1, true, false);

                for (int i = 0; i < numSamples; i++) {
                    double t = i / sampleRate;
                    double envolvente = Math.exp(-t * 220);
                    double onda = Math.sin(2.0 * Math.PI * 1400.0 * t);
                    buffer[i] = (byte) (onda * 90 * envolvente);
                }

                SourceDataLine line = AudioSystem.getSourceDataLine(formato);
                line.open(formato, buffer.length);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {
                Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }
}

// ====================================================================
// 2. COMPONENTES VISUALES PERSONALIZADOS
// ====================================================================

class BotonCurvo extends JButton {
    private Color colorBase;
    private Color colorHover;
    private Color colorPress;
    private boolean esEspecial;

    public BotonCurvo(String texto, Color colorBase) {
        this(texto, colorBase, false);
    }

    public BotonCurvo(String texto, Color colorBase, boolean esEspecial) {
        super(texto);
        this.colorBase = colorBase;
        this.colorHover = colorBase.brighter();
        this.colorPress = colorBase.darker();
        this.esEspecial = esEspecial;

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, esEspecial ? 13 : 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(8, 14, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c1 = colorBase;

        if (getModel().isPressed()) {
            c1 = colorPress;
        } else if (getModel().isRollover()) {
            c1 = colorHover;
        }

        if (esEspecial) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(251, 191, 36), getWidth(), getHeight(), new Color(217, 119, 6));
            g2.setPaint(gp);
        } else {
            g2.setColor(c1);
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));

        if (esEspecial) {
            g2.setColor(new Color(254, 243, 199));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

class CampoTextoCurvo extends JTextField {
    public CampoTextoCurvo() {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(15, 23, 42));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        g2.setColor(hasFocus() ? new Color(56, 189, 248) : new Color(51, 65, 85));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        g2.dispose();
        super.paintComponent(g);
    }
}

class PanelContenedor extends JPanel {
    private String titulo;

    public PanelContenedor(String titulo) {
        this.titulo = titulo;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(30, 41, 59));
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);

        g2.setColor(new Color(51, 65, 85));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);

        if (titulo != null && !titulo.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(148, 163, 184));
            g2.drawString(titulo.toUpperCase(), 14, 20);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

// ====================================================================
// NUEVA CLASE: PANEL DE DATOS CON DEGRADADO MORADO PÚRPURA Y RESPLANDOR
// ====================================================================

class PanelIndicadoresMorado extends JPanel {
    private String titulo;

    public PanelIndicadoresMorado(String titulo) {
        this.titulo = titulo;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fondo Degradado Púrpura Elegante
        GradientPaint degradadoPurpura = new GradientPaint(
                0, 0, new Color(46, 16, 101),       // Púrpura Profundo Superior
                0, getHeight(), new Color(76, 29, 149) // Violeta Sombra Inferior
        );
        g2.setPaint(degradadoPurpura);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);

        // 2. Efecto de Luz Interna Superior (Glow/Cristal)
        GradientPaint luzInterna = new GradientPaint(
                0, 0, new Color(192, 132, 252, 60),
                0, 60, new Color(192, 132, 252, 0)
        );
        g2.setPaint(luzInterna);
        g2.fillRoundRect(1, 1, getWidth() - 4, 60, 18, 18);

        // 3. Borde Púrpura Neón Suave
        g2.setColor(new Color(124, 58, 237, 180));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);

        // Título de la sección
        if (titulo != null && !titulo.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(233, 213, 255)); // Texto Violeta Claro
            g2.drawString(titulo.toUpperCase(), 14, 20);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

// ====================================================================
// 3. GRÁFICO ESTADÍSTICO DE BARRAS
// ====================================================================

class PanelGraficoBarras extends JPanel {
    private int diamantes = 0, platas = 0, bronces = 0;

    public void actualizarDatos(int diamantes, int platas, int bronces) {
        this.diamantes = diamantes;
        this.platas = platas;
        this.bronces = bronces;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int max = Math.max(1, Math.max(diamantes, Math.max(platas, bronces)));
        int altoDisponible = getHeight() - 40;
        int xInicial = 18;
        int separacion = 55;

        dibujarBarra(g2, xInicial, diamantes, max, altoDisponible, new Color(56, 189, 248), "💎");
        dibujarBarra(g2, xInicial + separacion, platas, max, altoDisponible, new Color(203, 213, 225), "🥈");
        dibujarBarra(g2, xInicial + (separacion * 2), bronces, max, altoDisponible, new Color(251, 146, 60), "🥉");

        g2.dispose();
    }

    private void dibujarBarra(Graphics2D g2, int x, int valor, int max, int altoMax, Color color, String etiqueta) {
        int anchoBarra = 28;
        int altoBarra = (int) (((double) valor / max) * (altoMax - 20));
        if (altoBarra < 6 && valor > 0) altoBarra = 6;

        int y = getHeight() - 25 - altoBarra;

        g2.setColor(color);
        g2.fillRoundRect(x, y, anchoBarra, altoBarra, 8, 8);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString(String.valueOf(valor), x + 9, y - 5);
        g2.drawString(etiqueta, x + 5, getHeight() - 6);
    }
}

// ====================================================================
// 4. INTERFAZ Y LÓGICA PRINCIPAL
// ====================================================================

public class InterfazEstudiantes extends JFrame {

    private List<Estudiante> listaEstudiantes;

    private JTextField txtNombre, txtEdad, txtMatricula, txtNuevaMateria, txtNota, txtBuscar;
    private JComboBox<Estudiante> comboEstudiantesMateria, comboEstudiantesNota;
    private JComboBox<Materia> comboMateriasNota;

    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> ordenadorFiltro;

    // Indicadores superiores
    private JLabel lblTotalEstudiantes, lblPromedioGeneral, lblTasaRiesgo;
    private PanelGraficoBarras panelGrafico;

    private JLabel lblTarjetaNombre, lblTarjetaDetalle, lblTarjetaInsignia;
    private JTextArea txtAreaMateriasDetalle;

    public InterfazEstudiantes() {
        listaEstudiantes = new ArrayList<>();

        setTitle("EagleAcademic — Sistema Intuitivo de Gestión Académica");
        setSize(1200, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBackground(new Color(15, 23, 42));
        panelPrincipal.setBorder(new EmptyBorder(12, 15, 15, 15));
        setContentPane(panelPrincipal);

        // Paleta Corporativa
        Color colAzul = new Color(14, 165, 233);
        Color colMorado = new Color(139, 92, 246);
        Color colVerde = new Color(16, 185, 129);
        Color colRojo = new Color(239, 68, 68);
        Color colNaranja = new Color(245, 158, 11);

        // --- ENCABEZADO CON BOTÓN DESTACADO EN LA ESQUINA SUPERIOR DERECHA ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new Dimension(1180, 42));

        JLabel lblTitulo = new JLabel("SISTEMA DE CONTROL DE RENDIMIENTO ACADÉMICO", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(241, 245, 249));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        BotonCurvo btnAbanderados = new BotonCurvo("🏅 POSIBLES ABANDERADOS", new Color(245, 158, 11), true);
        btnAbanderados.addActionListener(e -> {
            GestorAudio.reproducirClickSincronizado();
            mostrarTopAbanderados();
        });
        panelHeader.add(btnAbanderados, BorderLayout.EAST);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // --- IZQUIERDA: FORMULARIOS ---
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));

        // Form 1: Registro
        PanelContenedor panelCrear = new PanelContenedor("1. Registro de Estudiante");
        JPanel gridCrear = new JPanel(new GridLayout(4, 2, 6, 6));
        gridCrear.setOpaque(false);
        gridCrear.setBorder(new EmptyBorder(22, 0, 0, 0));

        gridCrear.add(crearLabel("Nombre:"));
        txtNombre = new CampoTextoCurvo(); gridCrear.add(txtNombre);
        gridCrear.add(crearLabel("Edad:"));
        txtEdad = new CampoTextoCurvo(); gridCrear.add(txtEdad);
        gridCrear.add(crearLabel("Matrícula:"));
        txtMatricula = new CampoTextoCurvo(); gridCrear.add(txtMatricula);

        BotonCurvo btnGuardar = new BotonCurvo("Registrar", colAzul);
        btnGuardar.addActionListener(e -> { GestorAudio.reproducirClickSincronizado(); registrarEstudiante(); });
        gridCrear.add(new JLabel());
        gridCrear.add(btnGuardar);
        panelCrear.add(gridCrear, BorderLayout.CENTER);

        // Form 2: Materia
        PanelContenedor panelMateria = new PanelContenedor("2. Inscripción de Asignatura");
        JPanel gridMateria = new JPanel(new GridLayout(3, 2, 6, 6));
        gridMateria.setOpaque(false);
        gridMateria.setBorder(new EmptyBorder(22, 0, 0, 0));

        gridMateria.add(crearLabel("Estudiante:"));
        comboEstudiantesMateria = new JComboBox<>(); gridMateria.add(comboEstudiantesMateria);
        gridMateria.add(crearLabel("Materia:"));
        txtNuevaMateria = new CampoTextoCurvo(); gridMateria.add(txtNuevaMateria);

        BotonCurvo btnMateria = new BotonCurvo("Inscribir", colMorado);
        btnMateria.addActionListener(e -> { GestorAudio.reproducirClickSincronizado(); agregarMateriaEstudiante(); });
        gridMateria.add(new JLabel());
        gridMateria.add(btnMateria);
        panelMateria.add(gridMateria, BorderLayout.CENTER);

        // Form 3: Nota
        PanelContenedor panelNotas = new PanelContenedor("3. Asignación de Calificación");
        JPanel gridNotas = new JPanel(new GridLayout(4, 2, 6, 6));
        gridNotas.setOpaque(false);
        gridNotas.setBorder(new EmptyBorder(22, 0, 0, 0));

        gridNotas.add(crearLabel("Estudiante:"));
        comboEstudiantesNota = new JComboBox<>();
        comboEstudiantesNota.addActionListener(e -> actualizarComboMaterias());
        gridNotas.add(comboEstudiantesNota);

        gridNotas.add(crearLabel("Materia:"));
        comboMateriasNota = new JComboBox<>();
        gridNotas.add(comboMateriasNota);

        gridNotas.add(crearLabel("Nota (0-100):"));
        txtNota = new CampoTextoCurvo(); gridNotas.add(txtNota);

        BotonCurvo btnNota = new BotonCurvo("Guardar Nota", colVerde);
        btnNota.addActionListener(e -> { GestorAudio.reproducirClickSincronizado(); agregarNotaEstudiante(); });
        gridNotas.add(new JLabel());
        gridNotas.add(btnNota);
        panelNotas.add(gridNotas, BorderLayout.CENTER);

        panelIzquierdo.add(panelCrear);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelIzquierdo.add(panelMateria);
        panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelIzquierdo.add(panelNotas);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

        // --- CENTRO: TABLA DE REGISTROS ---
        PanelContenedor panelCentral = new PanelContenedor("Resumen Académico General");
        JPanel contCentral = new JPanel(new BorderLayout(8, 8));
        contCentral.setOpaque(false);
        contCentral.setBorder(new EmptyBorder(22, 0, 0, 0));

        JPanel panelBusqueda = new JPanel(new BorderLayout(8, 8));
        panelBusqueda.setOpaque(false);
        panelBusqueda.add(crearLabel("🔍 Buscar Registro: "), BorderLayout.WEST);
        txtBuscar = new CampoTextoCurvo();
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarTabla(txtBuscar.getText());
            }
        });
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        contCentral.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"Matrícula", "Nombre", "Materias", "Promedio", "Rango"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaEstudiantes = new JTable(modeloTabla);
        ordenadorFiltro = new TableRowSorter<>(modeloTabla);
        tablaEstudiantes.setRowSorter(ordenadorFiltro);
        tablaEstudiantes.setRowHeight(28);
        tablaEstudiantes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaEstudiantes.setBackground(new Color(15, 23, 42));
        tablaEstudiantes.setForeground(Color.WHITE);
        tablaEstudiantes.setSelectionBackground(new Color(56, 189, 248));
        tablaEstudiantes.setSelectionForeground(Color.BLACK);

        JTableHeader headerTabla = tablaEstudiantes.getTableHeader();
        headerTabla.setBackground(new Color(51, 65, 85));
        headerTabla.setForeground(Color.WHITE);
        headerTabla.setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i : new int[]{0, 2, 3, 4}) {
            tablaEstudiantes.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleEstudianteSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaEstudiantes);
        scrollTabla.getViewport().setBackground(new Color(15, 23, 42));
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));

        // Tarjeta de Detalle Inferior
        PanelContenedor panelTarjetaResumen = new PanelContenedor("Expediente Seleccionado");
        JPanel contResumen = new JPanel(new BorderLayout(4, 4));
        contResumen.setOpaque(false);
        contResumen.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel panelInfoEncabezado = new JPanel(new GridLayout(1, 3));
        panelInfoEncabezado.setOpaque(false);

        lblTarjetaNombre = crearLabel("Seleccione un alumno de la tabla...");
        lblTarjetaNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTarjetaDetalle = crearLabel("");
        lblTarjetaInsignia = crearLabel("");

        panelInfoEncabezado.add(lblTarjetaNombre);
        panelInfoEncabezado.add(lblTarjetaDetalle);
        panelInfoEncabezado.add(lblTarjetaInsignia);

        txtAreaMateriasDetalle = new JTextArea("Desglose detallado de materias y calificaciones...");
        txtAreaMateriasDetalle.setEditable(false);
        txtAreaMateriasDetalle.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtAreaMateriasDetalle.setBackground(new Color(15, 23, 42));
        txtAreaMateriasDetalle.setForeground(new Color(226, 232, 240));

        JScrollPane scrollDetalle = new JScrollPane(txtAreaMateriasDetalle);
        scrollDetalle.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));

        contResumen.add(panelInfoEncabezado, BorderLayout.NORTH);
        contResumen.add(scrollDetalle, BorderLayout.CENTER);
        panelTarjetaResumen.add(contResumen, BorderLayout.CENTER);

        JPanel panelCentroContenedor = new JPanel(new BorderLayout(8, 8));
        panelCentroContenedor.setOpaque(false);
        panelCentroContenedor.add(scrollTabla, BorderLayout.CENTER);
        panelCentroContenedor.add(panelTarjetaResumen, BorderLayout.SOUTH);
        panelTarjetaResumen.setPreferredSize(new Dimension(0, 145));

        contCentral.add(panelCentroContenedor, BorderLayout.CENTER);

        // --- BOTONES INFERIORES DE ACCIÓN ---
        JPanel panelAccionesInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelAccionesInferior.setOpaque(false);

        BotonCurvo btnEliminar = new BotonCurvo("🗑️ Eliminar Registro", colRojo);
        btnEliminar.addActionListener(e -> { GestorAudio.reproducirClickSincronizado(); eliminarEstudianteSeleccionado(); });

        BotonCurvo btnExportar = new BotonCurvo("📄 Exportar Reporte", colNaranja);
        btnExportar.addActionListener(e -> { GestorAudio.reproducirClickSincronizado(); exportarReporteTXT(); });

        panelAccionesInferior.add(btnEliminar);
        panelAccionesInferior.add(btnExportar);
        contCentral.add(panelAccionesInferior, BorderLayout.SOUTH);

        panelCentral.add(contCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // --- DERECHA: PANEL DE DATOS MORADO PÚRPURA DEGRADADO ---
        PanelIndicadoresMorado panelEstadisticas = new PanelIndicadoresMorado("Indicadores");
        JPanel contEst = new JPanel();
        contEst.setOpaque(false);
        contEst.setLayout(new BoxLayout(contEst, BoxLayout.Y_AXIS));
        contEst.setBorder(new EmptyBorder(22, 0, 0, 0));

        lblTotalEstudiantes = crearLabel("👥 Total Alumnos: 0");
        lblPromedioGeneral = crearLabel("📊 Promedio Global: 0.0 pts");
        lblTasaRiesgo = crearLabel("⚠️ Riesgo Alumnos: 0.0%");

        lblTotalEstudiantes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPromedioGeneral.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTasaRiesgo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTasaRiesgo.setForeground(new Color(252, 165, 165)); // Tono rojizo pastel sobre morado

        contEst.add(lblTotalEstudiantes);
        contEst.add(Box.createRigidArea(new Dimension(0, 6)));
        contEst.add(lblPromedioGeneral);
        contEst.add(Box.createRigidArea(new Dimension(0, 6)));
        contEst.add(lblTasaRiesgo);
        contEst.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel lblGraficoTitulo = crearLabel("DISTRIBUCIÓN DE RANGOS:");
        lblGraficoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblGraficoTitulo.setForeground(new Color(216, 180, 254)); // Púrpura suave
        contEst.add(lblGraficoTitulo);
        contEst.add(Box.createRigidArea(new Dimension(0, 10)));

        panelGrafico = new PanelGraficoBarras();
        panelGrafico.setOpaque(false);
        panelGrafico.setPreferredSize(new Dimension(190, 200));
        contEst.add(panelGrafico);

        panelEstadisticas.add(contEst, BorderLayout.CENTER);
        panelEstadisticas.setPreferredSize(new Dimension(240, 0));
        panelPrincipal.add(panelEstadisticas, BorderLayout.EAST);
    }

    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(new Color(243, 232, 255)); // Tono blanco-púrpura legible
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private void mostrarTopAbanderados() {
        if (listaEstudiantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen alumnos registrados para evaluar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Estudiante> top4 = listaEstudiantes.stream()
                .sorted(Comparator.comparingDouble(Estudiante::calcularPromedioGeneral).reversed())
                .limit(4)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 POSIBLES ABANDERADOS DE LA INSTITUCIÓN 🏆\n");
        sb.append("----------------------------------------------------------------------\n");

        String[] medallas = {"🥇 1er Lugar", "🥈 2do Lugar", "🥉 3er Lugar", "🏅 4to Lugar"};

        for (int i = 0; i < top4.size(); i++) {
            Estudiante e = top4.get(i);
            sb.append(String.format("%s: %s (Matrícula: %s)\n", medallas[i], e.getNombre(), e.getMatricula()));
            sb.append(String.format("    Promedio: %.2f pts | Rango: %s\n\n", e.calcularPromedioGeneral(), e.getInsigniaRango()));
        }

        JTextArea areaTexto = new JTextArea(sb.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaTexto.setBackground(new Color(15, 23, 42));
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(420, 240));

        JOptionPane.showMessageDialog(this, scroll, "Cuadro de Honor - Abanderados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDetalleEstudianteSeleccionado() {
        int filaSel = tablaEstudiantes.getSelectedRow();
        if (filaSel != -1) {
            int filaModelo = tablaEstudiantes.convertRowIndexToModel(filaSel);
            Estudiante est = listaEstudiantes.get(filaModelo);

            lblTarjetaNombre.setText("👤 " + est.getNombre() + " (" + est.getEdad() + " años)");
            lblTarjetaDetalle.setText("🆔 " + est.getMatricula() + " | Prom: " + String.format("%.2f", est.calcularPromedioGeneral()));
            lblTarjetaInsignia.setText("Rango: " + est.getInsigniaRango());

            StringBuilder sb = new StringBuilder();
            if (est.getMaterias().isEmpty()) {
                sb.append(" Sin materias inscritas actualmente.");
            } else {
                for (Materia m : est.getMaterias()) {
                    sb.append(" 📌 ").append(m.getNombreMateria())
                            .append(" -> Calificaciones: [").append(m.getNotasFormateadas())
                            .append("] | Promedio: ").append(String.format("%.2f", m.calcularPromedioMateria()))
                            .append("\n");
                }
            }
            txtAreaMateriasDetalle.setText(sb.toString());
        } else {
            lblTarjetaNombre.setText("Seleccione un estudiante...");
            lblTarjetaDetalle.setText("");
            lblTarjetaInsignia.setText("");
            txtAreaMateriasDetalle.setText("Desglose detallado de materias y calificaciones...");
        }
    }

    private void registrarEstudiante() {
        try {
            String nombre = txtNombre.getText().trim();
            String matricula = txtMatricula.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());

            if (nombre.isEmpty() || matricula.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos solicitados.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Estudiante nuevo = new Estudiante(nombre, edad, matricula);
            listaEstudiantes.add(nuevo);
            comboEstudiantesMateria.addItem(nuevo);
            comboEstudiantesNota.addItem(nuevo);

            txtNombre.setText(""); txtEdad.setText(""); txtMatricula.setText("");
            actualizarTablaYEstadisticas();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una edad numérica válida.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarMateriaEstudiante() {
        Estudiante seleccionado = (Estudiante) comboEstudiantesMateria.getSelectedItem();
        String materiaStr = txtNuevaMateria.getText().trim();

        if (seleccionado == null || materiaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante e ingrese el nombre de la materia.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        seleccionado.agregarMateria(materiaStr);
        txtNuevaMateria.setText("");
        actualizarComboMaterias();
        actualizarTablaYEstadisticas();
        mostrarDetalleEstudianteSeleccionado();
    }

    private void actualizarComboMaterias() {
        comboMateriasNota.removeAllItems();
        Estudiante seleccionado = (Estudiante) comboEstudiantesNota.getSelectedItem();
        if (seleccionado != null) {
            for (Materia m : seleccionado.getMaterias()) {
                comboMateriasNota.addItem(m);
            }
        }
    }

    private void agregarNotaEstudiante() {
        Estudiante estSeleccionado = (Estudiante) comboEstudiantesNota.getSelectedItem();
        Materia materiaSeleccionada = (Materia) comboMateriasNota.getSelectedItem();

        if (estSeleccionado == null || materiaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Asegúrese de seleccionar un estudiante y una materia.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double nota = Double.parseDouble(txtNota.getText().trim());
            if (nota < 0 || nota > 100) {
                JOptionPane.showMessageDialog(this, "La nota debe ser un número entre 0 y 100.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            materiaSeleccionada.agregarNota(nota);
            txtNota.setText("");
            actualizarComboMaterias();
            actualizarTablaYEstadisticas();
            mostrarDetalleEstudianteSeleccionado();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una calificación numérica válida.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudianteSeleccionado() {
        int filaSeleccionada = tablaEstudiantes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaEstudiantes.convertRowIndexToModel(filaSeleccionada);
        Estudiante estAEliminar = listaEstudiantes.get(filaModelo);

        int resp = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el registro de " + estAEliminar.getNombre() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            listaEstudiantes.remove(estAEliminar);
            comboEstudiantesMateria.removeItem(estAEliminar);
            comboEstudiantesNota.removeItem(estAEliminar);
            actualizarTablaYEstadisticas();
            mostrarDetalleEstudianteSeleccionado();
        }
    }

    private void exportarReporteTXT() {
        if (listaEstudiantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros disponibles para exportar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (FileWriter writer = new FileWriter("Reporte_Estudiantes.txt")) {
            writer.write("==============================================\n");
            writer.write("       REPORTE ACADÉMICO GENERAL             \n");
            writer.write("==============================================\n\n");

            for (Estudiante est : listaEstudiantes) {
                writer.write("Matrícula: " + est.getMatricula() + "\n");
                writer.write("Nombre:    " + est.getNombre() + " (" + est.getEdad() + " años)\n");
                writer.write("Insignia:  " + est.getInsigniaRango() + "\n");
                writer.write("Promedio:  " + String.format("%.2f", est.calcularPromedioGeneral()) + "\n");
                writer.write("Materias y Calificaciones:\n");
                for (Materia m : est.getMaterias()) {
                    writer.write("  - " + m.getNombreMateria() + ": [" + m.getNotasFormateadas() + "] -> Prom: " + String.format("%.2f", m.calcularPromedioMateria()) + "\n");
                }
                writer.write("----------------------------------------------\n");
            }
            JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente como 'Reporte_Estudiantes.txt'.", "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al intentar guardar el reporte.", "Error de E/S", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarTabla(String consulta) {
        if (consulta.trim().length() == 0) {
            ordenadorFiltro.setRowFilter(null);
        } else {
            ordenadorFiltro.setRowFilter(RowFilter.regexFilter("(?i)" + consulta));
        }
    }

    private void actualizarTablaYEstadisticas() {
        modeloTabla.setRowCount(0);
        double sumaPromedios = 0;
        int diamantes = 0, platas = 0, bronces = 0, estudiantesEnRiesgo = 0;

        for (Estudiante est : listaEstudiantes) {
            double promGen = est.calcularPromedioGeneral();
            sumaPromedios += promGen;

            String rango = est.getInsigniaRango();
            if (rango.contains("Diamante")) diamantes++;
            else if (rango.contains("Plata")) platas++;
            else if (rango.contains("Bronce")) bronces++;

            if (promGen < 62.0) {
                estudiantesEnRiesgo++;
            }

            modeloTabla.addRow(new Object[]{
                    est.getMatricula(),
                    est.getNombre(),
                    est.getMaterias().size() + " materias",
                    String.format("%.2f", promGen),
                    rango
            });
        }

        int total = listaEstudiantes.size();
        double porcentajeRiesgo = total > 0 ? ((double) estudiantesEnRiesgo / total) * 100.0 : 0.0;

        lblTotalEstudiantes.setText("👥 Total Alumnos: " + total);
        lblPromedioGeneral.setText(String.format("📊 Promedio Global: %.2f pts", total > 0 ? (sumaPromedios / total) : 0.0));
        lblTasaRiesgo.setText(String.format("⚠️ Riesgo Alumnos: %.1f%% (%d/%d)", porcentajeRiesgo, estudiantesEnRiesgo, total));

        panelGrafico.actualizarDatos(diamantes, platas, bronces);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazEstudiantes().setVisible(true));
    }
}