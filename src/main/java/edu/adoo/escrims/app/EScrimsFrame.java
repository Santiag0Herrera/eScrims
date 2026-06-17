package edu.adoo.escrims.app;

import edu.adoo.escrims.domain.ComentarioFeedback;
import edu.adoo.escrims.domain.Confirmacion;
import edu.adoo.escrims.domain.Equipo;
import edu.adoo.escrims.domain.Estadistica;
import edu.adoo.escrims.domain.Juego;
import edu.adoo.escrims.domain.Participacion;
import edu.adoo.escrims.domain.PerfilJuego;
import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Region;
import edu.adoo.escrims.domain.ReporteConducta;
import edu.adoo.escrims.domain.Scrim;
import edu.adoo.escrims.domain.Usuario;
import edu.adoo.escrims.patterns.adapter.DiscordAPI;
import edu.adoo.escrims.patterns.adapter.DiscordAdapter;
import edu.adoo.escrims.patterns.adapter.FirebaseAPI;
import edu.adoo.escrims.patterns.adapter.FirebaseAdapter;
import edu.adoo.escrims.patterns.adapter.SendGridAPI;
import edu.adoo.escrims.patterns.adapter.SendGridAdapter;
import edu.adoo.escrims.patterns.factory.ChannelNotifierFactory;
import edu.adoo.escrims.patterns.observer.DomainEvent;
import edu.adoo.escrims.patterns.observer.DomainEventBus;
import edu.adoo.escrims.patterns.observer.NotificationSubscriber;
import edu.adoo.escrims.patterns.state.ScrimContext;
import edu.adoo.escrims.patterns.strategy.ByHistoryStrategy;
import edu.adoo.escrims.patterns.strategy.ByLatencyStrategy;
import edu.adoo.escrims.patterns.strategy.ByMMRStrategy;
import edu.adoo.escrims.patterns.strategy.MatchmakingService;
import edu.adoo.escrims.patterns.strategy.MatchmakingStrategy;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EScrimsFrame extends JFrame {
    private static final Color BG = new Color(10, 12, 18);
    private static final Color PANEL = new Color(20, 24, 34);
    private static final Color PANEL_ALT = new Color(25, 31, 43);
    private static final Color FIELD = new Color(12, 16, 24);
    private static final Color TEXT = new Color(232, 238, 247);
    private static final Color MUTED = new Color(143, 154, 174);
    private static final Color ACCENT = new Color(0, 229, 255);
    private static final Color ACCENT_2 = new Color(63, 255, 159);
    private static final Color DANGER = new Color(255, 78, 112);
    private static final Color LINE = new Color(45, 56, 76);
    private static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font UI_FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font MONO_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private static final Dimension CARD_SIZE = new Dimension(360, 220);

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Juego> juegos = new ArrayList<>();
    private final List<Region> regiones = new ArrayList<>();
    private final List<Scrim> scrims = new ArrayList<>();
    private final Map<Scrim, ScrimContext> contexts = new HashMap<>();
    private final AtomicInteger ids = new AtomicInteger(1);

    private final JTextArea eventLog = new JTextArea(8, 80);
    private final DefaultListModel<String> usersModel = new DefaultListModel<>();
    private final DefaultListModel<String> scrimsModel = new DefaultListModel<>();
    private final DefaultListModel<Postulacion> postulacionesModel = new DefaultListModel<>();
    private final DefaultListModel<String> equiposModel = new DefaultListModel<>();
    private final DefaultListModel<String> postScrimModel = new DefaultListModel<>();
    private final JPanel usersGrid = cardsGrid();
    private final JPanel scrimsGrid = cardsGrid();
    private final JPanel postulacionesGrid = cardsGrid();
    private final JPanel equiposGrid = cardsGrid();
    private final JPanel postScrimGrid = cardsGrid();

    private final JComboBox<Usuario> userPostCombo = new JComboBox<>();
    private final JComboBox<Usuario> feedbackUserCombo = new JComboBox<>();
    private final JComboBox<Usuario> reportanteCombo = new JComboBox<>();
    private final JComboBox<Usuario> reportadoCombo = new JComboBox<>();
    private final JComboBox<Scrim> scrimPostCombo = new JComboBox<>();
    private final JComboBox<Scrim> scrimMatchCombo = new JComboBox<>();
    private final JComboBox<Scrim> scrimPostActionsCombo = new JComboBox<>();
    private final JComboBox<Juego> juegoUserCombo = new JComboBox<>();
    private final JComboBox<Juego> juegoScrimCombo = new JComboBox<>();
    private final JComboBox<Region> regionUserCombo = new JComboBox<>();
    private final JComboBox<Region> regionScrimCombo = new JComboBox<>();

    private DomainEventBus eventBus;

    public EScrimsFrame() {
        super("eScrims - Plataforma de scrims");
        applyGlobalTheme();
        seedCatalogs();
        seedInitialUsers();
        configureEvents();
        configureWindow();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        eventLog.setEditable(false);
        eventLog.setFont(MONO_FONT);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Usuarios", buildUsersPanel());
        tabs.addTab("Scrims", buildScrimsPanel());
        tabs.addTab("Postulaciones", buildPostulacionesPanel());
        tabs.addTab("Emparejamiento", buildMatchmakingPanel());
        tabs.addTab("Posterior al scrim", buildPostScrimPanel());
        styleTabbedPane(tabs);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LINE), "EVENTOS | OBSERVADOR | FABRICA | ADAPTADORES", 0, 0, UI_FONT_BOLD, ACCENT_2));
        logPanel.setBackground(PANEL);
        logPanel.add(darkScroll(eventLog), BorderLayout.CENTER);

        add(buildHeader(), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        styleTree(this);
        refreshAll();
    }

    private JPanel buildHeader() {
        JPanel header = new RoundedPanel(BG, BG, 0, 0);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                BorderFactory.createEmptyBorder(18, 22, 16, 22)
        ));

        JLabel title = new JLabel("eScrims");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Consola de gestion competitiva de scrims");
        subtitle.setFont(UI_FONT);
        subtitle.setForeground(MUTED);

        JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        copy.add(title, c);
        c.gridy = 1;
        copy.add(subtitle, c);

        JLabel status = new JLabel("OPERACION ACTIVA");
        status.setOpaque(true);
        status.setBackground(new Color(11, 48, 47));
        status.setForeground(ACCENT_2);
        status.setFont(UI_FONT_BOLD);
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(26, 135, 112)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));

        header.add(copy, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);
        return header;
    }

    private JPanel buildUsersPanel() {
        JButton crear = new JButton("Crear usuario");
        crear.addActionListener(event -> showCreateUserDialog());
        return boardPanel("Usuarios", "Gestion de jugadores registrados", crear, usersGrid);
    }

    private JPanel buildScrimsPanel() {
        JButton crear = new JButton("Crear scrim");
        crear.addActionListener(event -> showCreateScrimDialog());
        JButton probarDiscord = new JButton("Probar Discord");
        probarDiscord.addActionListener(event -> testDiscordNotification());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(probarDiscord);
        actions.add(crear);
        return boardPanel("Scrims", "Cada scrim muestra la siguiente accion disponible en su ciclo de vida", actions, scrimsGrid);
    }

    private JPanel buildPostulacionesPanel() {
        JButton crear = new JButton("Crear postulacion");
        crear.addActionListener(event -> showCreatePostulacionDialog());
        return boardPanel("Postulaciones", "Solicitudes pendientes y resueltas", crear, postulacionesGrid);
    }

    private JPanel buildMatchmakingPanel() {
        JComboBox<String> strategy = new JComboBox<>(new String[]{
                "Equilibrar por MMR",
                "Priorizar baja latencia",
                "Priorizar historial"
        });
        scrimMatchCombo.addActionListener(event -> refreshEquiposGrid());
        JButton ejecutar = new JButton("Armar equipos automaticamente");
        ejecutar.addActionListener(event -> {
            Scrim scrim = selected(scrimMatchCombo);
            if (scrim.getPostulaciones().isEmpty()) {
                showWarning("Ese scrim todavia no tiene postulaciones para armar equipos");
                return;
            }
            MatchmakingService service = new MatchmakingService(strategyFor((String) strategy.getSelectedItem(), scrim));
            List<Postulacion> seleccionados = service.ejecutarMatchmaking(scrim);
            log("Equipos armados con criterio '" + strategy.getSelectedItem() + "': " + seleccionados.size() + " jugadores asignados");
            refreshAll();
        });

        JPanel form = formPanel();
        addRow(form, 0, "Scrim", scrimMatchCombo, "Selecciona el scrim sobre el que quieres armar los equipos.");
        addWide(form, 1, sectionIntro("Que hace esta accion", "Toma las postulaciones del scrim seleccionado, arma sus equipos y actualiza sus cards en la pestaña Scrims."));
        addRow(form, 2, "Criterio", strategy, "Define como se priorizan los jugadores al momento de armar los equipos.");
        addWide(form, 3, strategyHelp());
        addWide(form, 4, ejecutar);

        return splitWithComponent(form, "Equipos", darkScroll(equiposGrid));
    }

    private JPanel buildPostScrimPanel() {
        JButton comentario = new JButton("Dejar comentario");
        comentario.addActionListener(event -> showComentarioDialog());

        JButton reporte = new JButton("Crear reporte");
        reporte.addActionListener(event -> showReporteDialog());

        JPanel actions = formPanel();
        addRow(actions, 0, "Scrim", scrimPostActionsCombo, "Selecciona el scrim sobre el que quieres operar.");
        addWide(actions, 1, sectionIntro("Comentarios", "Carga feedback posterior para evaluar la experiencia del scrim."));
        addWide(actions, 2, comentario);
        addWide(actions, 3, sectionIntro("Reportes", "Registra y resuelve conductas inadecuadas reportadas por jugadores."));
        addWide(actions, 4, reporte);

        return splitWithComponent(actions, "Actividad posterior al scrim", darkScroll(postScrimGrid));
    }

    private void showEstadisticaDialog(Scrim preselectedScrim) {
        if (scrims.isEmpty() || usuarios.isEmpty()) {
            showWarning("Necesitas al menos un scrim y un usuario para cargar estadisticas");
            return;
        }
        JComboBox<Scrim> scrim = comboFor(scrims);
        scrim.setSelectedItem(preselectedScrim != null ? preselectedScrim : scrimPostActionsCombo.getSelectedItem());
        JComboBox<Usuario> usuario = comboFor(usuarios);
        JSpinner kills = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
        JSpinner deaths = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        JSpinner assists = new JSpinner(new SpinnerNumberModel(8, 0, 100, 1));
        JComboBox<String> mvp = new JComboBox<>(new String[]{"NO", "SI"});
        JTextField resultado = new JTextField(12);
        JTextField observaciones = new JTextField(18);

        JPanel form = formPanel();
        addRow(form, 0, "Scrim", scrim);
        addRow(form, 1, "Jugador", usuario);
        addRow(form, 2, "Bajas / Muertes / Asistencias", compact(kills, deaths, assists));
        addRow(form, 3, "Jugador destacado", mvp);
        addRow(form, 4, "Resultado", resultado, "Resultado individual o del equipo. Ejemplo: Victoria");
        addRow(form, 5, "Observaciones", observaciones, "Detalle breve de la actuacion del jugador.");

        showFormDialog("Cargar estadistica", form, () -> {
            if (!requireText(resultado, observaciones)) {
                return false;
            }
            Estadistica stat = new Estadistica(nextId("stat"), selected(usuario), selected(scrim), (Integer) kills.getValue(),
                    (Integer) deaths.getValue(), (Integer) assists.getValue(), "SI".equals(mvp.getSelectedItem()),
                    observaciones.getText(), resultado.getText());
            selected(scrim).agregarEstadistica(stat);
            log("Estadistica registrada: " + stat);
            refreshAll();
            return true;
        });
    }

    private void showComentarioDialog() {
        if (scrims.isEmpty() || usuarios.isEmpty()) {
            showWarning("Necesitas al menos un scrim y un usuario para dejar un comentario");
            return;
        }
        JComboBox<Scrim> scrim = comboFor(scrims);
        scrim.setSelectedItem(scrimPostActionsCombo.getSelectedItem());
        JComboBox<Usuario> usuario = comboFor(usuarios);
        JTextField comentario = new JTextField(24);
        JSpinner rating = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));

        JPanel form = formPanel();
        addRow(form, 0, "Scrim", scrim);
        addRow(form, 1, "Usuario", usuario);
        addRow(form, 2, "Comentario", comentario, "Feedback posterior al scrim. Ejemplo: Buena comunicacion.");
        addRow(form, 3, "Puntuacion", rating);

        showFormDialog("Dejar comentario", form, () -> {
            if (!requireText(comentario)) {
                return false;
            }
            ComentarioFeedback item = new ComentarioFeedback(nextId("fb"), selected(usuario), selected(scrim), comentario.getText(), (Integer) rating.getValue());
            item.aprobar();
            selected(scrim).agregarFeedback(item);
            log("Comentario registrado y aprobado");
            refreshAll();
            return true;
        });
    }

    private void showReporteDialog() {
        if (scrims.isEmpty() || usuarios.size() < 2) {
            showWarning("Necesitas al menos un scrim y dos usuarios para crear un reporte");
            return;
        }
        JComboBox<Scrim> scrim = comboFor(scrims);
        scrim.setSelectedItem(scrimPostActionsCombo.getSelectedItem());
        JComboBox<Usuario> reportante = comboFor(usuarios);
        JComboBox<Usuario> reportado = comboFor(usuarios);
        JTextField motivo = new JTextField(24);

        JPanel form = formPanel();
        addRow(form, 0, "Scrim", scrim);
        addRow(form, 1, "Reportante", reportante);
        addRow(form, 2, "Reportado", reportado);
        addRow(form, 3, "Motivo", motivo, "Descripcion del comportamiento reportado.");

        showFormDialog("Crear reporte", form, () -> {
            if (!requireText(motivo)) {
                return false;
            }
            if (selected(reportante).equals(selected(reportado))) {
                showWarning("El reportante y el reportado no pueden ser la misma persona");
                return false;
            }
            ReporteConducta item = selected(reportante).reportarConducta(selected(reportado), selected(scrim), motivo.getText());
            item.procesar();
            item.resolver("Advertencia");
            log("Reporte resuelto: " + item.obtenerReportante() + " -> " + item.obtenerReportado());
            refreshAll();
            return true;
        });
    }

    private void showCreateUserDialog() {
        JTextField username = new JTextField(18);
        JTextField email = new JTextField(22);
        JTextField password = new JTextField(18);
        JTextField disponibilidad = new JTextField(24);
        JTextField rango = new JTextField(14);
        JSpinner mmr = new JSpinner(new SpinnerNumberModel(2300, 0, 10000, 50));
        JTextField roles = new JTextField(24);
        JComboBox<Region> region = comboFor(regiones);
        JComboBox<Juego> juego = comboFor(juegos);

        JPanel form = formPanel();
        addRow(form, 0, "Nombre de usuario", username, "Alias publico del jugador. Ejemplo: santiMain");
        addRow(form, 1, "Correo electronico", email, "Direccion de contacto. Ejemplo: jugador@correo.com");
        addRow(form, 2, "Hash de contrasena", password, "Valor ya cifrado o identificador simulado para el TP.");
        addRow(form, 3, "Disponibilidad", disponibilidad, "Dias y horarios disponibles. Ejemplo: Lun a Vie 20:00-23:00");
        addRow(form, 4, "Region principal", region);
        addRow(form, 5, "Juego", juego);
        addRow(form, 6, "Rango", rango, "Rango competitivo en el juego seleccionado. Ejemplo: Diamante");
        addRow(form, 7, "MMR", mmr);
        addRow(form, 8, "Roles preferidos", roles, "Separar roles con coma. Ejemplo: Duelista, Controlador");

        showFormDialog("Crear usuario", form, () -> {
            if (!requireText(username, email, password, disponibilidad, rango, roles)) {
                return false;
            }
            Usuario usuario = new Usuario(nextId("usr"), username.getText(), email.getText(), password.getText());
            usuario.registrarse();
            usuario.actualizarPerfil(username.getText(), email.getText(), disponibilidad.getText(), selected(region));
            usuario.agregarPerfilJuego(new PerfilJuego(nextId("perfil"), selected(juego), rango.getText(), (Integer) mmr.getValue(), parseRoles(roles.getText())));
            usuarios.add(usuario);
            log("Usuario registrado: " + usuario + " | perfil " + selected(juego).getNombre() + " MMR " + mmr.getValue());
            refreshAll();
            return true;
        });
    }

    private void showEditUserDialog(Usuario usuario) {
        JTextField username = new JTextField(usuario.getUsername(), 18);
        JTextField email = new JTextField(usuario.getEmail(), 22);
        JTextField disponibilidad = new JTextField(usuario.getDisponibilidadHoraria(), 24);
        JComboBox<Region> region = comboFor(regiones);
        region.setSelectedItem(usuario.getRegionPrincipal());

        PerfilJuego perfilActual = usuario.getPerfiles().isEmpty() ? null : usuario.getPerfiles().get(0);
        JComboBox<Juego> juego = comboFor(juegos);
        JTextField rango = new JTextField(14);
        JSpinner mmr = new JSpinner(new SpinnerNumberModel(2300, 0, 10000, 50));
        JTextField roles = new JTextField(24);

        if (perfilActual != null) {
            juego.setSelectedItem(perfilActual.getJuego());
            rango.setText(perfilActual.getRango());
            mmr.setValue(perfilActual.getMmr());
            roles.setText(String.join(", ", perfilActual.getRolesPreferidos()));
        }

        JPanel form = formPanel();
        addRow(form, 0, "Nombre de usuario", username, "Alias publico del jugador. Ejemplo: santiMain");
        addRow(form, 1, "Correo electronico", email, "Direccion de contacto. Ejemplo: jugador@correo.com");
        addRow(form, 2, "Disponibilidad", disponibilidad, "Dias y horarios disponibles. Ejemplo: Lun a Vie 20:00-23:00");
        addRow(form, 3, "Region principal", region);
        addRow(form, 4, "Juego", juego);
        addRow(form, 5, "Rango", rango, "Rango competitivo en el juego seleccionado. Ejemplo: Diamante");
        addRow(form, 6, "MMR", mmr);
        addRow(form, 7, "Roles preferidos", roles, "Separar roles con coma. Ejemplo: Duelista, Controlador");

        showFormDialog("Editar usuario", form, () -> {
            if (!requireText(username, email, disponibilidad, rango, roles)) {
                return false;
            }
            usuario.actualizarPerfil(username.getText(), email.getText(), disponibilidad.getText(), selected(region));
            PerfilJuego nuevoPerfil = new PerfilJuego(
                    perfilActual != null ? perfilActual.getId() : nextId("perfil"),
                    selected(juego),
                    rango.getText(),
                    (Integer) mmr.getValue(),
                    parseRoles(roles.getText())
            );
            if (perfilActual == null) {
                usuario.agregarPerfilJuego(nuevoPerfil);
            } else {
                usuario.reemplazarPerfilJuego(perfilActual, nuevoPerfil);
            }
            log("Usuario actualizado: " + usuario.getUsername());
            refreshAll();
            return true;
        });
    }

    private void showCreateScrimDialog() {
        JComboBox<Juego> juego = comboFor(juegos);
        JComboBox<Region> region = comboFor(regiones);
        JSpinner dias = new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));
        JSpinner duracion = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15));
        JTextField modalidad = new JTextField(18);
        JTextField formato = new JTextField(12);
        JSpinner mmrMin = new JSpinner(new SpinnerNumberModel(1800, 0, 10000, 50));
        JSpinner mmrMax = new JSpinner(new SpinnerNumberModel(2600, 0, 10000, 50));
        JSpinner latencia = new JSpinner(new SpinnerNumberModel(80, 0, 300, 5));
        JSpinner equipos = new JSpinner(new SpinnerNumberModel(2, 2, 8, 1));
        JSpinner jugadoresPorEquipo = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

        JPanel form = formPanel();
        addRow(form, 0, "Juego", juego);
        addRow(form, 1, "Region", region);
        addRow(form, 2, "Fecha en dias", dias);
        addRow(form, 3, "Duracion", duracion);
        addRow(form, 4, "Modalidad", modalidad, "Tipo de partida. Ejemplo: Competitiva, Practica, Torneo");
        addRow(form, 5, "Formato", formato, "Cantidad y estructura de jugadores. Ejemplo: 5v5");
        addRow(form, 6, "MMR minimo", mmrMin);
        addRow(form, 7, "MMR maximo", mmrMax);
        addRow(form, 8, "Latencia maxima", latencia);
        addRow(form, 9, "Cantidad de equipos", equipos, "Define cuantos equipos tendra el scrim. Minimo: 2");
        addRow(form, 10, "Jugadores por equipo", jugadoresPorEquipo, "La cantidad total de jugadores se calcula automaticamente.");

        showFormDialog("Crear scrim", form, () -> {
            if (!requireText(modalidad, formato)) {
                return false;
            }
            Scrim scrim = new Scrim(nextId("scrim"), selected(juego), selected(region),
                    LocalDateTime.now().plusDays((Integer) dias.getValue()), (Integer) duracion.getValue(),
                    modalidad.getText(), formato.getText(), (Integer) mmrMin.getValue(), (Integer) mmrMax.getValue(),
                    (Integer) latencia.getValue(), (Integer) equipos.getValue(), (Integer) jugadoresPorEquipo.getValue());
            scrims.add(scrim);
            contexts.put(scrim, new ScrimContext(scrim, eventBus));
            log("Scrim creado: " + scrim + " | estado " + scrim.getEstadoActual() + " | equipos=" + scrim.getCantidadEquipos() + " | jugadores=" + scrim.getCantidadJugadores());
            refreshAll();
            return true;
        });
    }

    private void showCreatePostulacionDialog() {
        if (usuarios.isEmpty() || scrims.isEmpty()) {
            showWarning("Necesitas al menos un usuario y un scrim para crear una postulacion");
            return;
        }
        JComboBox<Usuario> usuario = comboFor(usuarios);
        JComboBox<Scrim> scrim = comboFor(scrims);
        JTextField rol = new JTextField(18);

        JPanel form = formPanel();
        addRow(form, 0, "Usuario", usuario);
        addRow(form, 1, "Scrim", scrim);
        addRow(form, 2, "Rol deseado", rol, "Rol que quiere ocupar el jugador. Ejemplo: Soporte");

        showFormDialog("Crear postulacion", form, () -> {
            if (!requireText(rol)) {
                return false;
            }
            try {
                Postulacion postulacion = selected(usuario).postularse(selected(scrim), rol.getText());
                log("Postulacion creada: " + postulacion);
                refreshAll();
                return true;
            } catch (RuntimeException ex) {
                showWarning(ex.getMessage());
                return false;
            }
        });
    }

    private void showFormDialog(String title, JPanel form, DialogAction action) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(BG);

        JLabel heading = new JLabel(title.toUpperCase());
        heading.setFont(UI_FONT_BOLD);
        heading.setForeground(ACCENT);
        heading.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));

        JButton create = new JButton(title);
        JButton cancel = new JButton("Cancelar");
        create.addActionListener(event -> {
            if (action.run()) {
                dialog.dispose();
            }
        });
        cancel.addActionListener(event -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(PANEL);
        actions.add(cancel);
        actions.add(create);

        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        styleTree(dialog);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(460, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel boardPanel(String title, String subtitle, JComponent action, JPanel grid) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel(title);
        heading.setFont(TITLE_FONT);
        heading.setForeground(TEXT);
        JLabel copy = new JLabel(subtitle);
        copy.setFont(UI_FONT);
        copy.setForeground(MUTED);

        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        text.add(heading, c);
        c.gridy = 1;
        text.add(copy, c);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(text, BorderLayout.WEST);
        top.add(action, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(darkScroll(grid), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel cardsGrid() {
        JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 14));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return grid;
    }

    private JPanel card(String title, String detail, String meta) {
        return card(title, detail, meta, ACCENT_2);
    }

    private JPanel card(String title, String detail, String meta, Color metaColor) {
        return card(title, detail, meta, metaColor, CARD_SIZE);
    }

    private JPanel card(String title, String detail, String meta, Color metaColor, Dimension size) {
        JPanel card = new RoundedPanel(PANEL, new Color(62, 73, 96), 18, 1);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(62, 73, 96), 18, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(size);
        card.setMinimumSize(size);
        card.setMaximumSize(size);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setForeground(MUTED);
        detailLabel.setFont(UI_FONT);
        detailLabel.setVerticalAlignment(JLabel.TOP);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setForeground(metaColor);
        metaLabel.setFont(MONO_FONT);
        metaLabel.setVerticalAlignment(JLabel.TOP);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(detailLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(metaLabel);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private Dimension scrimCardSize(Scrim scrim) {
        int teamLines = Math.max(scrim.getCantidadEquipos(), 1);
        int playerLines = scrim.getEquipos().stream()
                .mapToInt(equipo -> Math.max(equipo.getParticipaciones().size(), 1))
                .sum();
        int height = 230 + (teamLines * 28) + (playerLines * 22);
        return new Dimension(CARD_SIZE.width, Math.max(CARD_SIZE.height, height));
    }

    private String userCardDetail(Usuario usuario) {
        StringBuilder detail = new StringBuilder();
        detail.append(usuario.getEmail());
        detail.append("<br>Disponibilidad: ").append(usuario.getDisponibilidadHoraria());
        if (usuario.getRegionPrincipal() != null) {
            detail.append("<br>Region: ").append(usuario.getRegionPrincipal().getNombre());
        }
        return detail.toString();
    }

    private String userCardMeta(Usuario usuario) {
        if (usuario.getPerfiles().isEmpty()) {
            return "Sin perfil de juego configurado";
        }
        PerfilJuego perfil = usuario.getPerfiles().get(0);
        return perfil.getJuego().getNombre() + " | " + perfil.getRango() + " | MMR " + perfil.getMmr();
    }

    private String scrimCardDetail(Scrim scrim) {
        return "Estado: " + scrim.getEstadoActual()
                + "<br>Equipos: " + scrim.getCantidadEquipos()
                + " | Jugadores por equipo: " + scrim.getJugadoresPorEquipo()
                + "<br>Cupos cubiertos: " + scrim.getJugadoresAsignados() + "/" + scrim.getCantidadJugadores()
                + " | Postulaciones: " + scrim.getPostulaciones().size();
    }

    private String scrimTeamsMeta(Scrim scrim) {
        StringBuilder meta = new StringBuilder();
        int numeroEquipo = 1;
        for (Equipo equipo : scrim.getEquipos()) {
            if (meta.length() > 0) {
                meta.append("<br><br>");
            }
            meta.append("<b>Equipo ").append(numeroEquipo).append("</b>")
                    .append(" (").append(equipo.getParticipaciones().size()).append("/").append(scrim.getJugadoresPorEquipo()).append(")");
            if (equipo.getParticipaciones().isEmpty()) {
                meta.append("<br>Sin integrantes");
                numeroEquipo++;
                continue;
            }
            for (Participacion participacion : equipo.getParticipaciones()) {
                meta.append("<br>- <span style='font-size:13px'><b>")
                        .append(participacion.getUsuario().getUsername())
                        .append("</b></span>")
                        .append("<br>&nbsp;&nbsp;")
                        .append(playerMetaFor(participacion, scrim));
            }
            numeroEquipo++;
        }
        return meta.toString();
    }

    private String playerMetaFor(Participacion participacion, Scrim scrim) {
        try {
            PerfilJuego perfil = participacion.getUsuario().perfilPara(scrim.getJuego());
            return perfil.getRango() + " | " + participacion.getRol() + " | MMR " + perfil.getMmr();
        } catch (IllegalStateException ex) {
            return participacion.getRol() + " | sin perfil para " + scrim.getJuego().getNombre();
        }
    }

    private <T> JComboBox<T> comboFor(List<T> values) {
        JComboBox<T> combo = new JComboBox<>();
        setModel(combo, values);
        styleCombo(combo);
        return combo;
    }

    private interface DialogAction {
        boolean run();
    }

    private void configureEvents() {
        SendGridAdapter sendGrid = new SendGridAdapter(new SendGridAPI("sendgrid-key", this::log));
        FirebaseAdapter firebase = new FirebaseAdapter(new FirebaseAPI("escrims-app", this::log));
        DiscordAdapter discord = new DiscordAdapter(new DiscordAPI(System.getenv("DISCORD_WEBHOOK_URL"), this::log));
        ChannelNotifierFactory factory = new ChannelNotifierFactory(sendGrid, firebase, discord);
        eventBus = new DomainEventBus();
        eventBus.subscribe(new NotificationSubscriber(factory));
    }

    private void testDiscordNotification() {
        eventBus.publish(new DomainEvent(
                "evt-discord-test-" + ids.getAndIncrement(),
                "PRUEBA_DISCORD",
                "DISCORD",
                Map.of("mensaje", "Discord conectado correctamente desde eScrims")
        ));
        log("Prueba de Discord enviada al adaptador");
    }

    private void seedCatalogs() {
        juegos.add(new Juego("game-valorant", "Valorant", "FPS tactico"));
        juegos.add(new Juego("game-lol", "League of Legends", "MOBA competitivo"));
        juegos.add(new Juego("game-cs2", "CS2", "FPS competitivo"));
        regiones.add(new Region("reg-las", "LAS", "Santiago"));
        regiones.add(new Region("reg-lan", "LAN", "Miami"));
        regiones.add(new Region("reg-br", "BR", "Sao Paulo"));
    }

    private void seedInitialUsers() {
        if (!usuarios.isEmpty()) {
            return;
        }

        Juego cs2 = juegos.stream()
                .filter(juego -> "CS2".equalsIgnoreCase(juego.getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontro el juego CS2 en el catalogo"));
        Region las = regiones.stream()
                .filter(region -> "LAS".equalsIgnoreCase(region.getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontro la region LAS en el catalogo"));

        usuarios.add(createSeedUser("Santi Herrera", "santi.herrera@escrims.gg", "hash-santi", "Lun a Dom 19:00-01:00", las, cs2, "Global Elite", 2600, List.of("Rifler", "IGL")));
        usuarios.add(createSeedUser("Seba Tomas", "seba.tomas@escrims.gg", "hash-seba", "Lun a Vie 20:00-00:00", las, cs2, "Supreme", 2300, List.of("Entry", "AWPer")));
        usuarios.add(createSeedUser("Santi Albui", "santi.albui@escrims.gg", "hash-albui", "Mar a Dom 18:00-23:30", las, cs2, "Global Elite", 2600, List.of("Rifler", "Lurker")));
        usuarios.add(createSeedUser("Agus Mollo", "agus.mollo@escrims.gg", "hash-agus", "Lun a Sab 21:00-01:00", las, cs2, "Supreme", 2300, List.of("Support", "Anchor")));
        usuarios.add(createSeedUser("Nacho", "nacho@escrims.gg", "hash-nacho", "Lun a Vie 19:00-23:00", las, cs2, "Legendary Eagle", 2400, List.of("Entry", "Support")));
        usuarios.add(createSeedUser("Tizi", "tizi@escrims.gg", "hash-tizi", "Mar a Dom 20:00-00:30", las, cs2, "Legendary Eagle Master", 2450, List.of("AWPer", "Lurker")));
        usuarios.add(createSeedUser("Nico", "nico@escrims.gg", "hash-nico", "Lun a Dom 18:00-22:30", las, cs2, "Supreme", 2350, List.of("IGL", "Anchor")));
    }

    private Usuario createSeedUser(String username, String email, String passwordHash, String disponibilidad,
                                   Region region, Juego juego, String rango, int mmr, List<String> roles) {
        Usuario usuario = new Usuario(nextId("usr"), username, email, passwordHash);
        usuario.registrarse();
        usuario.actualizarPerfil(username, email, disponibilidad, region);
        usuario.agregarPerfilJuego(new PerfilJuego(nextId("perfil"), juego, rango, mmr, roles));
        return usuario;
    }

    private void refreshAll() {
        refreshCombos();
        refreshLists();
    }

    private void refreshCombos() {
        setModel(juegoUserCombo, juegos);
        setModel(juegoScrimCombo, juegos);
        setModel(regionUserCombo, regiones);
        setModel(regionScrimCombo, regiones);
        setModel(userPostCombo, usuarios);
        setModel(feedbackUserCombo, usuarios);
        setModel(reportanteCombo, usuarios);
        setModel(reportadoCombo, usuarios);
        setModel(scrimPostCombo, scrims);
        setModel(scrimMatchCombo, scrims);
        setModel(scrimPostActionsCombo, scrims);
    }

    private void refreshLists() {
        usersModel.clear();
        usersGrid.removeAll();
        for (Usuario usuario : usuarios) {
            usersModel.addElement(usuario.getUsername() + " | " + usuario.getEmail() + " | " + usuario.getDisponibilidadHoraria());
            JPanel userCard = card(
                    usuario.getUsername(),
                    "<html><body style='width:300px'>" + userCardDetail(usuario) + "</body></html>",
                    userCardMeta(usuario)
            );
            userCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            userCard.setToolTipText("Haz click para editar este usuario");
            userCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    showEditUserDialog(usuario);
                }
            });
            usersGrid.add(userCard);
        }
        scrimsModel.clear();
        scrimsGrid.removeAll();
        for (Scrim scrim : scrims) {
            scrimsModel.addElement(scrim + " | estado=" + scrim.getEstadoActual() + " | postulaciones=" + scrim.getPostulaciones().size());
            JPanel scrimCard = card(
                    scrim.toString(),
                    "<html><body style='width:300px'>" + scrimCardDetail(scrim) + "</body></html>",
                    "<html><body style='width:300px'>" + scrimTeamsMeta(scrim) + "</body></html>",
                    colorEstadoScrim(scrim.getEstadoActual()),
                    scrimCardSize(scrim)
            );
            String nextActionLabel = nextActionLabel(scrim);
            if (nextActionLabel != null) {
                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                actions.setOpaque(false);
                JButton avanzar = new JButton(nextActionLabel);
                avanzar.putClientProperty("compact", Boolean.TRUE);
                avanzar.addActionListener(event -> avanzarEstadoScrim(scrim));
                actions.add(avanzar);
                scrimCard.add(actions, BorderLayout.SOUTH);
            } else if ("Finalizado".equalsIgnoreCase(scrim.getEstadoActual())) {
                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                actions.setOpaque(false);
                JButton estadistica = new JButton("Cargar estadistica");
                JButton verEstadisticas = new JButton("Ver estadisticas");
                estadistica.putClientProperty("compact", Boolean.TRUE);
                verEstadisticas.putClientProperty("compact", Boolean.TRUE);
                estadistica.addActionListener(event -> showEstadisticaDialog(scrim));
                verEstadisticas.addActionListener(event -> showEstadisticasViewer(scrim));
                actions.add(estadistica);
                actions.add(verEstadisticas);
                scrimCard.add(actions, BorderLayout.SOUTH);
            }
            scrimCard.setToolTipText("Estado actual: " + scrim.getEstadoActual());
            styleTree(scrimCard);
            scrimsGrid.add(scrimCard);
        }
        postulacionesModel.clear();
        postulacionesGrid.removeAll();
        for (Scrim scrim : scrims) {
            for (Postulacion postulacion : scrim.getPostulaciones()) {
                postulacionesModel.addElement(postulacion);
                String estado = postulacion.getEstado();
                String estadoVisible = estado;
                if ("ACEPTADA".equalsIgnoreCase(estado) && postulacion.getScrim().usuarioYaConfirmo(postulacion.getUsuario())) {
                    estadoVisible = "ACEPTADA | ASISTENCIA CONFIRMADA";
                }
                JPanel card = card(
                        postulacion.getUsuario().getUsername(),
                        postulacion.getRolDeseado() + " | " + scrim,
                        estadoVisible,
                        colorEstadoPostulacion(estado)
                );
                if ("PENDIENTE".equalsIgnoreCase(estado)) {
                    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                    actions.setOpaque(false);
                    JButton aceptar = new JButton("Aceptar");
                    JButton rechazar = new JButton("Rechazar");
                    aceptar.putClientProperty("compact", Boolean.TRUE);
                    rechazar.putClientProperty("compact", Boolean.TRUE);
                    aceptar.addActionListener(event -> changePostulacion(postulacion, true));
                    rechazar.addActionListener(event -> changePostulacion(postulacion, false));
                    actions.add(rechazar);
                    actions.add(aceptar);
                    card.add(actions, BorderLayout.SOUTH);
                } else if ("ACEPTADA".equalsIgnoreCase(estado) && !postulacion.getScrim().usuarioYaConfirmo(postulacion.getUsuario())) {
                    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                    actions.setOpaque(false);
                    JButton confirmarAsistencia = new JButton("Confirmar asistencia");
                    confirmarAsistencia.putClientProperty("compact", Boolean.TRUE);
                    confirmarAsistencia.addActionListener(event -> confirmPostulacion(postulacion));
                    actions.add(confirmarAsistencia);
                    card.add(actions, BorderLayout.SOUTH);
                }
                styleTree(card);
                postulacionesGrid.add(card);
            }
        }
        refreshEquiposGrid();
        postScrimModel.clear();
        postScrimGrid.removeAll();
        for (Scrim scrim : scrims) {
            postScrimModel.addElement(scrim + " | confirmaciones=" + scrim.getConfirmaciones().size()
                    + " | estadisticas=" + scrim.getEstadisticas().size()
                    + " | comentarios=" + scrim.getFeedback().size()
                    + " | reportes=" + scrim.getReportes().size());
            postScrimGrid.add(card(scrim.toString(), "Confirmaciones: " + scrim.getConfirmaciones().size() + " | Estadisticas: " + scrim.getEstadisticas().size(),
                    "Comentarios: " + scrim.getFeedback().size() + " | Reportes: " + scrim.getReportes().size()));
        }
        usersGrid.revalidate();
        usersGrid.repaint();
        scrimsGrid.revalidate();
        scrimsGrid.repaint();
        postulacionesGrid.revalidate();
        postulacionesGrid.repaint();
        equiposGrid.revalidate();
        equiposGrid.repaint();
        postScrimGrid.revalidate();
        postScrimGrid.repaint();
    }

    private void refreshEquiposGrid() {
        equiposModel.clear();
        equiposGrid.removeAll();
        Scrim scrimSeleccionado = (Scrim) scrimMatchCombo.getSelectedItem();
        if (scrimSeleccionado == null) {
            equiposGrid.revalidate();
            equiposGrid.repaint();
            return;
        }

        equiposModel.addElement(scrimSeleccionado + " | " + scrimSeleccionado.getEstadoActual());
        for (Equipo equipo : scrimSeleccionado.getEquipos()) {
            equiposModel.addElement("  " + equipo);
            JPanel equipoCard = card(
                    equipo.getLado(),
                    scrimSeleccionado.toString(),
                    "Jugadores en este equipo: " + equipo.getParticipaciones().size() + "/" + scrimSeleccionado.getJugadoresPorEquipo()
            );
            for (Participacion participacion : equipo.getParticipaciones()) {
                equiposModel.addElement("    " + participacion.getUsuario() + " - " + participacion.getRol());
            }
            equiposGrid.add(equipoCard);
        }
        equiposGrid.revalidate();
        equiposGrid.repaint();
    }

    private void showEstadisticasViewer(Scrim scrim) {
        JDialog dialog = new JDialog(this, "Estadisticas del scrim", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(BG);
        dialog.setLayout(new BorderLayout(0, 12));

        JLabel heading = new JLabel("ESTADISTICAS | " + scrim);
        heading.setFont(UI_FONT_BOLD);
        heading.setForeground(ACCENT);
        heading.setBorder(BorderFactory.createEmptyBorder(14, 16, 0, 16));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        content.add(buildStatsSummaryCard(scrim));
        content.add(Box.createVerticalStrut(12));

        if (scrim.getEstadisticas().isEmpty()) {
            content.add(emptyStateCard("Todavia no hay estadisticas cargadas para este scrim."));
        } else {
            for (Equipo equipo : scrim.getEquipos()) {
                content.add(buildTeamStatsSection(scrim, equipo));
                content.add(Box.createVerticalStrut(12));
            }
        }

        JButton close = new JButton("Cerrar");
        close.addActionListener(event -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(PANEL);
        actions.add(close);

        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(darkScroll(content), BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        styleTree(dialog);
        dialog.setSize(960, 720);
        dialog.setMinimumSize(new Dimension(860, 620));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildStatsSummaryCard(Scrim scrim) {
        JPanel summary = new RoundedPanel(PANEL_ALT, new Color(62, 73, 96), 20, 1);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(62, 73, 96), 20, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Resumen del scrim");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel body = new JLabel("<html><body style='width:800px'>"
                + "Juego: " + scrim.getJuego().getNombre()
                + "<br>Estado: " + scrim.getEstadoActual()
                + "<br>Equipos: " + scrim.getCantidadEquipos()
                + " | Jugadores por equipo: " + scrim.getJugadoresPorEquipo()
                + "<br>Estadisticas cargadas: " + scrim.getEstadisticas().size()
                + " | Confirmaciones: " + scrim.getConfirmaciones().size()
                + "</body></html>");
        body.setForeground(MUTED);
        body.setFont(UI_FONT);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);

        summary.add(title);
        summary.add(Box.createVerticalStrut(8));
        summary.add(body);
        return summary;
    }

    private JPanel buildTeamStatsSection(Scrim scrim, Equipo equipo) {
        JPanel section = new RoundedPanel(PANEL, new Color(58, 70, 95), 20, 1);
        section.setLayout(new BorderLayout(0, 12));
        section.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(58, 70, 95), 20, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(equipo.getLado());
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(ACCENT);

        JLabel subtitle = new JLabel(equipo.getParticipaciones().size() + "/" + scrim.getJugadoresPorEquipo() + " jugadores con estadisticas vinculadas al scrim");
        subtitle.setFont(UI_FONT);
        subtitle.setForeground(MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        JPanel grid = cardsGrid();
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        if (equipo.getParticipaciones().isEmpty()) {
            grid.add(emptyStateCard("Este equipo todavia no tiene integrantes asignados."));
        } else {
            for (Participacion participacion : equipo.getParticipaciones()) {
                grid.add(buildPlayerStatCard(scrim, participacion));
            }
        }

        section.add(heading, BorderLayout.NORTH);
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildPlayerStatCard(Scrim scrim, Participacion participacion) {
        Estadistica estadistica = findStat(scrim, participacion.getUsuario());
        String detail = "<html><body style='width:280px'>"
                + "<span style='font-size:13px'><b>" + participacion.getUsuario().getUsername() + "</b></span>"
                + "<br>" + playerMetaFor(participacion, scrim)
                + "</body></html>";

        String meta;
        Color metaColor;
        if (estadistica == null) {
            meta = "<html><body style='width:280px'>Sin estadisticas cargadas todavia.</body></html>";
            metaColor = MUTED;
        } else {
            meta = "<html><body style='width:280px'>"
                    + "K " + estadistica.getKills()
                    + " | D " + estadistica.getDeaths()
                    + " | A " + estadistica.getAssists()
                    + "<br>KDA " + String.format("%.2f", estadistica.calcularKDA())
                    + " | " + estadistica.getResultado()
                    + (estadistica.isMvp() ? " | MVP" : "")
                    + "<br>" + estadistica.getObservaciones()
                    + "</body></html>";
            metaColor = colorResultadoEstadistica(estadistica);
        }

        return card("Jugador", detail, meta, metaColor, new Dimension(320, estadistica == null ? 170 : 196));
    }

    private JPanel emptyStateCard(String message) {
        return card("Sin datos", "<html><body style='width:280px'>" + message + "</body></html>", "", MUTED, new Dimension(320, 120));
    }

    private Estadistica findStat(Scrim scrim, Usuario usuario) {
        for (Estadistica estadistica : scrim.getEstadisticas()) {
            if (estadistica.getUsuario().equals(usuario)) {
                return estadistica;
            }
        }
        return null;
    }

    private Color colorResultadoEstadistica(Estadistica estadistica) {
        String resultado = estadistica.getResultado().toLowerCase();
        if (estadistica.isMvp()) {
            return new Color(255, 210, 72);
        }
        if (resultado.contains("victoria") || resultado.contains("gan")) {
            return ACCENT_2;
        }
        if (resultado.contains("derrota") || resultado.contains("perd")) {
            return DANGER;
        }
        return ACCENT;
    }

    private void changePostulacion(Postulacion postulacion, boolean aceptar) {
        if (postulacion == null) {
            showWarning("Selecciona una postulacion");
            return;
        }
        if (aceptar) {
            postulacion.aceptar();
        } else {
            postulacion.rechazar();
        }
        log("Postulacion actualizada: " + postulacion);
        refreshAll();
    }

    private void confirmPostulacion(Postulacion postulacion) {
        try {
            Confirmacion confirmacion = postulacion.confirmarAsistencia();
            log("Asistencia confirmada desde postulacion: " + confirmacion.getUsuario());
            refreshAll();
        } catch (RuntimeException ex) {
            showWarning(ex.getMessage());
        }
    }

    private Color colorEstadoPostulacion(String estado) {
        if ("ACEPTADA".equalsIgnoreCase(estado)) {
            return ACCENT_2;
        }
        if ("RECHAZADA".equalsIgnoreCase(estado)) {
            return DANGER;
        }
        return new Color(255, 210, 72);
    }

    private Color colorEstadoScrim(String estado) {
        if ("Finalizado".equalsIgnoreCase(estado) || "Confirmado".equalsIgnoreCase(estado)) {
            return ACCENT_2;
        }
        if ("Cancelado".equalsIgnoreCase(estado)) {
            return DANGER;
        }
        if ("EnJuego".equalsIgnoreCase(estado) || "LobbyArmado".equalsIgnoreCase(estado)) {
            return new Color(255, 210, 72);
        }
        return ACCENT;
    }

    private String nextActionLabel(Scrim scrim) {
        return switch (scrim.getEstadoActual()) {
            case "BuscandoJugadores" -> "Cerrar postulaciones";
            case "LobbyArmado" -> "Confirmar";
            case "Confirmado" -> "Iniciar";
            case "EnJuego" -> "Finalizar";
            default -> null;
        };
    }

    private void avanzarEstadoScrim(Scrim scrim) {
        String action = switch (scrim.getEstadoActual()) {
            case "BuscandoJugadores", "LobbyArmado" -> "confirmar";
            case "Confirmado" -> "iniciar";
            case "EnJuego" -> "finalizar";
            default -> null;
        };
        if (action == null) {
            return;
        }
        runStateAction(action, scrim);
    }

    private void runStateAction(String action, Scrim scrim) {
        try {
            ScrimContext context = contexts.get(scrim);
            if (context == null) {
                showWarning("Selecciona un scrim");
                return;
            }
            switch (action) {
                case "postular" -> context.postular();
                case "confirmar" -> context.confirmar();
                case "iniciar" -> context.iniciar();
                case "finalizar" -> context.finalizar();
                case "cancelar" -> context.cancelar();
                default -> throw new IllegalArgumentException("Accion desconocida");
            }
            log("Estado actualizado: " + scrim.getEstadoActual());
            refreshAll();
        } catch (RuntimeException ex) {
            showWarning(ex.getMessage());
        }
    }

    private MatchmakingStrategy strategyFor(String name, Scrim scrim) {
        return switch (name) {
            case "Priorizar baja latencia" -> new ByLatencyStrategy(scrim.getLatenciaMaxima());
            case "Priorizar historial" -> new ByHistoryStrategy(5);
            default -> new ByMMRStrategy(250);
        };
    }

    private JPanel formPanel() {
        JPanel panel = new RoundedPanel(PANEL, new Color(53, 64, 88), 18, 1);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(53, 64, 88), 18, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JPanel splitWithList(JPanel left, String title, JList<?> list) {
        list.setVisibleRowCount(18);
        return splitWithComponent(left, title, new JScrollPane(list));
    }

    private JPanel splitWithComponent(JPanel left, String title, java.awt.Component component) {
        JPanel right = new RoundedPanel(PANEL_ALT, new Color(52, 63, 86), 20, 1);
        right.setLayout(new BorderLayout());
        right.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(52, 63, 86), 20, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        right.add(component, BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, java.awt.Component component) {
        GridBagConstraints labelConstraints = constraints(0, row);
        labelConstraints.anchor = GridBagConstraints.EAST;
        JLabel text = new JLabel(label);
        text.setFont(UI_FONT_BOLD);
        text.setForeground(MUTED);
        panel.add(text, labelConstraints);
        GridBagConstraints fieldConstraints = constraints(1, row);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;
        panel.add(component, fieldConstraints);
    }

    private void addRow(JPanel panel, int row, String label, java.awt.Component component, String hint) {
        component.setName(label);
        if (component instanceof JComponent jComponent) {
            jComponent.setToolTipText(hint);
        }
        addRow(panel, row, label, withHint(component, hint));
    }

    private JPanel withHint(java.awt.Component component, String hint) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);
        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hintLabel.setForeground(MUTED);
        wrapper.add(component, BorderLayout.CENTER);
        wrapper.add(hintLabel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void addWide(JPanel panel, int row, java.awt.Component component) {
        GridBagConstraints c = constraints(0, row);
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, c);
    }

    private GridBagConstraints constraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(4, 4, 4, 4);
        return c;
    }

    private JPanel compact(java.awt.Component first, java.awt.Component second, java.awt.Component third) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(first);
        panel.add(second);
        panel.add(third);
        return panel;
    }

    private JPanel sectionIntro(String title, String description) {
        JPanel panel = new RoundedPanel(PANEL_ALT, new Color(52, 63, 86), 16, 1);
        panel.setLayout(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(52, 63, 86), 16, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel heading = new JLabel(title);
        heading.setFont(UI_FONT_BOLD);
        heading.setForeground(TEXT);
        JLabel copy = new JLabel("<html><body style='width:260px'>" + description + "</body></html>");
        copy.setFont(UI_FONT);
        copy.setForeground(MUTED);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(copy, BorderLayout.CENTER);
        return panel;
    }

    private JPanel strategyHelp() {
        JPanel panel = new RoundedPanel(PANEL_ALT, new Color(52, 63, 86), 16, 1);
        panel.setLayout(new GridLayout(0, 1, 0, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(52, 63, 86), 16, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        panel.add(helpLine("Equilibrar por MMR", "Intenta juntar jugadores con nivel competitivo similar."));
        panel.add(helpLine("Priorizar baja latencia", "Favorece mejores condiciones tecnicas de conexion."));
        panel.add(helpLine("Priorizar historial", "Ordena la seleccion segun experiencia previa."));
        return panel;
    }

    private JLabel helpLine(String title, String text) {
        JLabel label = new JLabel("<html><b>" + title + ":</b> " + text + "</html>");
        label.setForeground(MUTED);
        label.setFont(UI_FONT);
        return label;
    }

    @SuppressWarnings("unchecked")
    private <T> void setModel(JComboBox<T> combo, List<T> values) {
        T selected = (T) combo.getSelectedItem();
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        for (T value : values) {
            model.addElement(value);
        }
        combo.setModel(model);
        if (selected != null && values.contains(selected)) {
            combo.setSelectedItem(selected);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T selected(JComboBox<T> combo) {
        T item = (T) combo.getSelectedItem();
        if (item == null) {
            throw new IllegalStateException("Faltan datos para completar la accion");
        }
        return item;
    }

    private List<String> parseRoles(String raw) {
        List<String> roles = new ArrayList<>();
        for (String role : raw.split(",")) {
            if (!role.isBlank()) {
                roles.add(role.trim());
            }
        }
        return roles;
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private boolean requireText(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().isBlank()) {
                field.requestFocusInWindow();
                showWarning("Completa todos los campos requeridos");
                return false;
            }
        }
        return true;
    }

    private String nextId(String prefix) {
        return prefix + "-" + ids.getAndIncrement();
    }

    private void log(String message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> log(message));
            return;
        }
        eventLog.append(message + System.lineSeparator());
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "eScrims", JOptionPane.WARNING_MESSAGE);
    }

    private void applyGlobalTheme() {
        UIManager.put("TabbedPane.selected", PANEL);
        UIManager.put("TabbedPane.contentAreaColor", BG);
        UIManager.put("TabbedPane.focus", ACCENT);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.focus", ACCENT);
        UIManager.put("ComboBox.background", FIELD);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", new Color(0, 83, 103));
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("ComboBox.disabledBackground", FIELD);
        UIManager.put("ComboBox.disabledForeground", MUTED);
        UIManager.put("ComboBox.buttonBackground", FIELD);
        UIManager.put("ComboBox.buttonDarkShadow", LINE);
        UIManager.put("ComboBox.buttonHighlight", ACCENT);
        UIManager.put("ComboBox.buttonShadow", LINE);
    }

    private void styleTree(Component component) {
        styleComponent(component);
        if (component instanceof java.awt.Container container) {
            for (Component child : container.getComponents()) {
                styleTree(child);
            }
        }
    }

    private void styleComponent(Component component) {
        if (component instanceof JPanel panel) {
            if (panel.getBackground().equals(new JPanel().getBackground())) {
                panel.setBackground(PANEL);
            }
        } else if (component instanceof JLabel label) {
            if (label.getFont().getSize() < 18) {
                label.setFont(UI_FONT);
            }
            if (!ACCENT.equals(label.getForeground()) && !ACCENT_2.equals(label.getForeground())) {
                label.setForeground(TEXT);
            }
        } else if (component instanceof JButton button) {
            styleButton(button);
        } else if (component instanceof JTextField field) {
            styleTextField(field);
        } else if (component instanceof JTextArea area) {
            styleTextArea(area);
        } else if (component instanceof JComboBox<?> combo) {
            styleCombo(combo);
        } else if (component instanceof JSpinner spinner) {
            styleSpinner(spinner);
        } else if (component instanceof JList<?> list) {
            styleList(list);
        } else if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBorder(new RoundedBorder(new Color(48, 59, 80), 18, 1));
            scrollPane.getViewport().setBackground(FIELD);
            scrollPane.setBackground(PANEL_ALT);
        }
    }

    private void styleTabbedPane(JTabbedPane tabs) {
        tabs.setBackground(BG);
        tabs.setForeground(TEXT);
        tabs.setFont(UI_FONT_BOLD);
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));
    }

    private void styleButton(JButton button) {
        button.setFont(UI_FONT_BOLD);
        button.setBackground(isDangerAction(button.getText()) ? new Color(72, 28, 43) : new Color(20, 61, 75));
        button.setForeground(isDangerAction(button.getText()) ? DANGER : ACCENT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(isDangerAction(button.getText()) ? DANGER : new Color(22, 155, 178), 16, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        boolean compact = Boolean.TRUE.equals(button.getClientProperty("compact"));
        button.setPreferredSize(new Dimension(Math.max(button.getPreferredSize().width, compact ? 108 : 180), compact ? 32 : 36));
    }

    private boolean isDangerAction(String text) {
        String lower = text.toLowerCase();
        return lower.contains("rechazar") || lower.contains("cancelar") || lower.contains("reporte");
    }

    private void styleTextField(JTextField field) {
        field.setFont(UI_FONT);
        field.setBackground(FIELD);
        field.setForeground(TEXT);
        field.setCaretColor(ACCENT);
        field.setSelectionColor(new Color(0, 103, 125));
        field.setSelectedTextColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(55, 65, 86), 14, 1),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(MONO_FONT);
        area.setBackground(new Color(7, 10, 15));
        area.setForeground(new Color(190, 255, 220));
        area.setCaretColor(ACCENT_2);
        area.setSelectionColor(new Color(20, 82, 70));
        area.setSelectedTextColor(TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(FIELD);
        combo.setForeground(TEXT);
        combo.setFont(UI_FONT);
        combo.setOpaque(true);
        combo.setBorder(new RoundedBorder(new Color(55, 65, 86), 14, 1));
        combo.setRenderer(new DarkComboRenderer());
        combo.setMaximumRowCount(8);
        Component editor = combo.getEditor().getEditorComponent();
        if (editor instanceof JComponent editorComponent) {
            editorComponent.setBackground(FIELD);
            editorComponent.setForeground(TEXT);
            editorComponent.setFont(UI_FONT);
            editorComponent.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setBorder(new RoundedBorder(new Color(55, 65, 86), 14, 1));
        Component editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            styleTextField(defaultEditor.getTextField());
        }
    }

    private void styleList(JList<?> list) {
        list.setBackground(FIELD);
        list.setForeground(TEXT);
        list.setSelectionBackground(new Color(0, 83, 103));
        list.setSelectionForeground(TEXT);
        list.setFixedCellHeight(28);
        list.setFont(MONO_FONT);
        list.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private JScrollPane darkScroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(new RoundedBorder(new Color(48, 59, 80), 18, 1));
        scrollPane.getViewport().setBackground(FIELD);
        scrollPane.setBackground(PANEL_ALT);
        if (component instanceof JPanel) {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return scrollPane;
    }

    private static class DarkComboRenderer extends BasicComboBoxRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setOpaque(true);
            setFont(UI_FONT);
            setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
            list.setBackground(FIELD);
            list.setForeground(TEXT);
            list.setSelectionBackground(new Color(0, 83, 103));
            list.setSelectionForeground(TEXT);
            if (isSelected) {
                setBackground(new Color(0, 83, 103));
                setForeground(TEXT);
            } else {
                setBackground(FIELD);
                setForeground(TEXT);
            }
            if (cellHasFocus) {
                setBorder(BorderFactory.createCompoundBorder(new LineBorder(ACCENT, 1), BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            }
            return this;
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;
        private final int strokeWidth;

        RoundedPanel(Color fill, Color stroke, int radius, int strokeWidth) {
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
            this.strokeWidth = strokeWidth;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (strokeWidth > 0) {
                g.setColor(stroke);
                for (int i = 0; i < strokeWidth; i++) {
                    g.drawRoundRect(i, i, getWidth() - 1 - (i * 2), getHeight() - 1 - (i * 2), radius, radius);
                }
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedBorder implements Border {
        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            for (int i = 0; i < thickness; i++) {
                g.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), radius, radius);
            }
            g.dispose();
        }
    }

    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= getHgap() + 1;
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (getHgap() * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dimension = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int members = target.getComponentCount();
                for (int i = 0; i < members; i++) {
                    Component component = target.getComponent(i);
                    if (!component.isVisible()) {
                        continue;
                    }

                    Dimension componentDimension = preferred ? component.getPreferredSize() : component.getMinimumSize();

                    if (rowWidth + componentDimension.width > maxWidth) {
                        addRow(dimension, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0) {
                        rowWidth += getHgap();
                    }

                    rowWidth += componentDimension.width;
                    rowHeight = Math.max(rowHeight, componentDimension.height);
                }

                addRow(dimension, rowWidth, rowHeight);
                dimension.width += horizontalInsetsAndGap;
                dimension.height += insets.top + insets.bottom + (getVgap() * 2);

                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
                if (scrollPane != null) {
                    dimension.width -= getHgap() + 1;
                }

                return dimension;
            }
        }

        private void addRow(Dimension dimension, int rowWidth, int rowHeight) {
            dimension.width = Math.max(dimension.width, rowWidth);
            if (dimension.height > 0) {
                dimension.height += getVgap();
            }
            dimension.height += rowHeight;
        }
    }

    public static void showUI() {
        SwingUtilities.invokeLater(() -> new EScrimsFrame().setVisible(true));
    }
}
