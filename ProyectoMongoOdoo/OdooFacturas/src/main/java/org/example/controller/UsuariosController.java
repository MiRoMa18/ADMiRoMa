package org.example.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.model.Usuario;
import org.example.util.MongoDBConnection;
import org.example.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * Controlador para la gestión de usuarios
 * SOLO ACCESIBLE POR ADMIN
 */
public class UsuariosController {

    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);

    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colFechaCreacion;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;
    @FXML private Button btnNuevoUsuario;
    @FXML private Button btnRefrescar;

    private ObservableList<Usuario> todosLosUsuarios;
    private ObservableList<Usuario> usuariosFiltrados;
    private MongoCollection<Document> usuariosCollection;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = SessionManager.getUsuarioActual();

        // Verificar permisos de admin
        if (!usuarioActual.isAdmin()) {
            mostrarError("Esta sección es solo para administradores");
            logger.warn("Usuario no-admin {} intentó acceder a gestión de usuarios",
                    usuarioActual.getUsername());
            return;
        }

        MongoDatabase database = MongoDBConnection.getDatabase();
        usuariosCollection = database.getCollection("usuarios");

        configurarTabla();
        configurarFiltros();
        cargarUsuarios();

        logger.info("Vista de usuarios cargada para admin: {}", usuarioActual.getNombre());
    }

    private void configurarTabla() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Rol con color
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colRol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    switch (item.toLowerCase()) {
                        case "admin":
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                            break;
                        case "comercial":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "empleado":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        // Fecha formateada
        colFechaCreacion.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaCreacion();
            String fechaStr = fecha != null ?
                    fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
            return new SimpleStringProperty(fechaStr);
        });

        // Estado
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActivo() ? "✅ Activo" : "❌ Inactivo")
        );

        configurarColumnaAcciones();

        tableUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnCambiarEstado = new Button("🔄 Estado");
            private final Button btnEliminar = new Button("🗑️ Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnCambiarEstado, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");
                btnCambiarEstado.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");

                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    editarUsuario(usuario);
                });

                btnCambiarEstado.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    cambiarEstado(usuario);
                });

                btnEliminar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    eliminarUsuario(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());

                    // No permitir eliminar al usuario actual
                    btnEliminar.setDisable(usuario.getUsername().equals(usuarioActual.getUsername()));

                    setGraphic(pane);
                }
            }
        });
    }

    private void configurarFiltros() {
        cmbRol.setItems(FXCollections.observableArrayList(
                "Todos", "Admin", "Comercial", "Empleado"
        ));
        cmbRol.setValue("Todos");

        cmbEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Activo", "Inactivo"
        ));
        cmbEstado.setValue("Todos");
    }

    @FXML
    public void cargarUsuarios() {
        lblEstado.setText("Cargando usuarios...");
        btnRefrescar.setDisable(true);

        new Thread(() -> {
            try {
                List<Usuario> usuarios = new ArrayList<>();

                for (Document doc : usuariosCollection.find()) {
                    usuarios.add(documentToUsuario(doc));
                }

                Platform.runLater(() -> {
                    todosLosUsuarios = FXCollections.observableArrayList(usuarios);
                    usuariosFiltrados = FXCollections.observableArrayList(todosLosUsuarios);
                    tableUsuarios.setItems(usuariosFiltrados);

                    lblTotal.setText("Total: " + usuarios.size() + " usuarios");
                    lblEstado.setText("Usuarios cargados correctamente");
                    lblConexion.setText("● Conectado a MongoDB");
                    lblConexion.setStyle("-fx-text-fill: #4CAF50;");
                    btnRefrescar.setDisable(false);

                    logger.info("Se cargaron {} usuarios", usuarios.size());
                });

            } catch (Exception e) {
                logger.error("Error al cargar usuarios", e);
                Platform.runLater(() -> {
                    lblEstado.setText("Error al cargar usuarios");
                    lblConexion.setText("● Error de conexión");
                    lblConexion.setStyle("-fx-text-fill: #f44336;");
                    btnRefrescar.setDisable(false);
                    mostrarError("Error al cargar usuarios: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void filtrarUsuarios() {
        if (todosLosUsuarios == null) return;

        String busqueda = txtBuscar.getText().toLowerCase();
        String rolSeleccionado = cmbRol.getValue();
        String estadoSeleccionado = cmbEstado.getValue();

        List<Usuario> filtrados = todosLosUsuarios.stream()
                .filter(u -> {
                    // Filtro de búsqueda
                    boolean coincideBusqueda = busqueda.isEmpty() ||
                            u.getUsername().toLowerCase().contains(busqueda) ||
                            u.getNombre().toLowerCase().contains(busqueda);

                    // Filtro de rol
                    boolean coincideRol = rolSeleccionado.equals("Todos") ||
                            u.getRol().equalsIgnoreCase(rolSeleccionado);

                    // Filtro de estado
                    boolean coincideEstado = estadoSeleccionado.equals("Todos") ||
                            (estadoSeleccionado.equals("Activo") && u.isActivo()) ||
                            (estadoSeleccionado.equals("Inactivo") && !u.isActivo());

                    return coincideBusqueda && coincideRol && coincideEstado;
                })
                .toList();

        usuariosFiltrados.setAll(filtrados);
        lblTotal.setText("Total: " + filtrados.size() + " usuarios");
    }

    @FXML
    private void crearUsuario() {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Usuario");
        dialog.setHeaderText("Crear un nuevo usuario del sistema");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("email@ejemplo.com");
        ComboBox<String> cmbRolNuevo = new ComboBox<>();
        cmbRolNuevo.setItems(FXCollections.observableArrayList("admin", "comercial", "empleado"));
        cmbRolNuevo.setValue("empleado");

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(new Label("Nombre:"), 0, 2);
        grid.add(txtNombre, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(txtEmail, 1, 3);
        grid.add(new Label("Rol:"), 0, 4);
        grid.add(cmbRolNuevo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(txtUsername::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                // Validar campos
                if (txtUsername.getText().trim().isEmpty() ||
                        txtPassword.getText().isEmpty() ||
                        txtNombre.getText().trim().isEmpty()) {
                    mostrarError("Todos los campos son obligatorios");
                    return null;
                }

                // Verificar si el usuario ya existe
                if (usuariosCollection.find(eq("username", txtUsername.getText().trim())).first() != null) {
                    mostrarError("El usuario ya existe");
                    return null;
                }

                // Crear usuario
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsername(txtUsername.getText().trim());
                nuevoUsuario.setPassword(PasswordUtil.hashPassword(txtPassword.getText()));
                nuevoUsuario.setNombre(txtNombre.getText().trim());
                nuevoUsuario.setEmail(txtEmail.getText().trim());
                nuevoUsuario.setRol(cmbRolNuevo.getValue());
                nuevoUsuario.setActivo(true);
                nuevoUsuario.setFechaCreacion(LocalDateTime.now());

                return nuevoUsuario;
            }
            return null;
        });

        Optional<Usuario> resultado = dialog.showAndWait();

        resultado.ifPresent(usuario -> {
            try {
                Document doc = usuarioToDocument(usuario);
                usuariosCollection.insertOne(doc);

                logger.info("Usuario creado por admin {}: {}",
                        usuarioActual.getUsername(), usuario.getUsername());
                mostrarInfo("Usuario creado exitosamente");
                cargarUsuarios();

            } catch (Exception e) {
                logger.error("Error al crear usuario", e);
                mostrarError("Error al crear usuario: " + e.getMessage());
            }
        });
    }

    private void editarUsuario(Usuario usuario) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Editar información del usuario: " + usuario.getUsername());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField(usuario.getNombre());
        TextField txtEmail = new TextField(usuario.getEmail());
        ComboBox<String> cmbRolEdit = new ComboBox<>();
        cmbRolEdit.setItems(FXCollections.observableArrayList("admin", "comercial", "empleado"));
        cmbRolEdit.setValue(usuario.getRol());
        PasswordField txtNuevaPassword = new PasswordField();
        txtNuevaPassword.setPromptText("Dejar vacío para no cambiar");

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(new Label(usuario.getUsername()), 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Rol:"), 0, 3);
        grid.add(cmbRolEdit, 1, 3);
        grid.add(new Label("Nueva Contraseña:"), 0, 4);
        grid.add(txtNuevaPassword, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                usuario.setNombre(txtNombre.getText().trim());
                usuario.setEmail(txtEmail.getText().trim());
                usuario.setRol(cmbRolEdit.getValue());

                // Solo cambiar password si se ingresó uno nuevo
                if (!txtNuevaPassword.getText().isEmpty()) {
                    usuario.setPassword(PasswordUtil.hashPassword(txtNuevaPassword.getText()));
                }

                return usuario;
            }
            return null;
        });

        Optional<Usuario> resultado = dialog.showAndWait();

        resultado.ifPresent(u -> {
            try {
                Document update = new Document()
                        .append("nombre", u.getNombre())
                        .append("email", u.getEmail())
                        .append("rol", u.getRol());

                if (!txtNuevaPassword.getText().isEmpty()) {
                    update.append("password", u.getPassword());
                }

                usuariosCollection.updateOne(
                        eq("username", u.getUsername()),
                        new Document("$set", update)
                );

                logger.info("Usuario editado por admin {}: {}",
                        usuarioActual.getUsername(), u.getUsername());
                mostrarInfo("Usuario actualizado exitosamente");
                cargarUsuarios();

            } catch (Exception e) {
                logger.error("Error al editar usuario", e);
                mostrarError("Error al editar: " + e.getMessage());
            }
        });
    }

    private void cambiarEstado(Usuario usuario) {
        boolean nuevoEstado = !usuario.isActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cambiar Estado");
        confirmacion.setHeaderText("¿" + accion.substring(0, 1).toUpperCase() + accion.substring(1) + " usuario?");
        confirmacion.setContentText("Usuario: " + usuario.getNombre() + "\n\n¿Deseas " + accion + " este usuario?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    usuariosCollection.updateOne(
                            eq("username", usuario.getUsername()),
                            new Document("$set", new Document("activo", nuevoEstado))
                    );

                    logger.info("Usuario {} {} por admin {}",
                            usuario.getUsername(), accion + "do", usuarioActual.getUsername());
                    mostrarInfo("Usuario " + accion + "do exitosamente");
                    cargarUsuarios();

                } catch (Exception e) {
                    logger.error("Error al cambiar estado", e);
                    mostrarError("Error: " + e.getMessage());
                }
            }
        });
    }

    private void eliminarUsuario(Usuario usuario) {
        if (usuario.getUsername().equals(usuarioActual.getUsername())) {
            mostrarAdvertencia("No puedes eliminar tu propio usuario");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Usuario");
        confirmacion.setHeaderText("¿Eliminar usuario permanentemente?");
        confirmacion.setContentText(
                "Usuario: " + usuario.getNombre() + "\n" +
                        "Username: " + usuario.getUsername() + "\n\n" +
                        "Esta acción NO se puede deshacer."
        );

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    usuariosCollection.deleteOne(eq("username", usuario.getUsername()));

                    logger.info("Usuario eliminado por admin {}: {}",
                            usuarioActual.getUsername(), usuario.getUsername());
                    mostrarInfo("Usuario eliminado exitosamente");
                    cargarUsuarios();

                } catch (Exception e) {
                    logger.error("Error al eliminar usuario", e);
                    mostrarError("Error: " + e.getMessage());
                }
            }
        });
    }

    private Usuario documentToUsuario(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setId(doc.getObjectId("_id"));
        usuario.setUsername(doc.getString("username"));
        usuario.setPassword(doc.getString("password"));
        usuario.setRol(doc.getString("rol"));
        usuario.setNombre(doc.getString("nombre"));
        usuario.setEmail(doc.getString("email"));
        usuario.setActivo(doc.getBoolean("activo", true));
        // Fecha de creación si existe
        return usuario;
    }

    private Document usuarioToDocument(Usuario usuario) {
        return new Document()
                .append("username", usuario.getUsername())
                .append("password", usuario.getPassword())
                .append("rol", usuario.getRol())
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("activo", usuario.isActivo())
                .append("fechaCreacion", java.util.Date.from(usuario.getFechaCreacion()
                        .atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}