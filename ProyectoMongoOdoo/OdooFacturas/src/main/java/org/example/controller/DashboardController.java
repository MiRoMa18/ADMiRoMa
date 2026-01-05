package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.example.App;
import org.example.model.Usuario;
import org.example.service.MongoService;
import org.example.service.OdooService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador del dashboard principal
 */
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblStats;
    @FXML private Button btnFacturas;
    @FXML private Button btnGarantias;
    @FXML private Button btnUsuarios;
    @FXML private StackPane contentArea;

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        // Obtener usuario actual de la sesión
        usuarioActual = SessionManager.getUsuarioActual();

        if (usuarioActual != null) {
            lblUsuario.setText(usuarioActual.getNombre());
            lblRol.setText(usuarioActual.getRol().toUpperCase());

            // Mostrar botón de usuarios solo para admins
            if (usuarioActual.isAdmin()) {
                btnUsuarios.setVisible(true);
            }

            logger.info("Dashboard cargado para usuario: {} ({})",
                    usuarioActual.getNombre(), usuarioActual.getRol());
        }

        // Cargar estadísticas en segundo plano
        cargarEstadisticas();

        // Estilo hover para botones del menú
        aplicarEstilosMenu();
    }

    @FXML
    private void mostrarFacturas() {
        cargarVista("/fxml/facturas.fxml", "Facturas");
        resaltarBoton(btnFacturas);
    }

    @FXML
    private void mostrarGarantias() {
        cargarVista("/fxml/garantias.fxml", "Garantías");
        resaltarBoton(btnGarantias);
    }

    @FXML
    private void mostrarUsuarios() {
        if (!usuarioActual.isAdmin()) {
            mostrarAlerta("Acceso Denegado", "No tienes permisos para acceder a esta sección");
            return;
        }
        cargarVista("/fxml/usuarios.fxml", "Usuarios");
        resaltarBoton(btnUsuarios);
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Estás seguro de que deseas cerrar sesión?");
        alert.setContentText("Tendrás que iniciar sesión nuevamente para acceder al sistema");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                SessionManager.cerrarSesion();
                logger.info("Usuario cerró sesión");
                App.mostrarLogin();
            }
        });
    }

    private void cargarVista(String fxmlPath, String nombreVista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

            logger.info("Vista cargada: {}", nombreVista);

        } catch (Exception e) {
            logger.error("Error al cargar vista: " + nombreVista, e);
            mostrarError("No se pudo cargar la vista: " + nombreVista);
        }
    }

    private void cargarEstadisticas() {
        new Thread(() -> {
            try {
                // Obtener estadísticas de forma asíncrona
                MongoService mongoService = new MongoService();
                OdooService odooService = new OdooService();

                int totalGarantias = mongoService.obtenerTodasLasGarantias().size();

                odooService.authenticate();
                int totalFacturas = odooService.obtenerFacturas().size();

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    String stats = String.format(
                            "📊 Estadísticas:\n" +
                                    "• %d Facturas\n" +
                                    "• %d Garantías",
                            totalFacturas, totalGarantias
                    );
                    lblStats.setText(stats);
                });

            } catch (Exception e) {
                logger.error("Error al cargar estadísticas", e);
                Platform.runLater(() -> lblStats.setText("Error al cargar estadísticas"));
            }
        }).start();
    }

    private void resaltarBoton(Button botonActivo) {
        // Resetear todos los botones
        btnFacturas.setStyle(btnFacturas.getStyle().replace("-fx-background-color: #37474F;",
                "-fx-background-color: transparent;"));
        btnGarantias.setStyle(btnGarantias.getStyle().replace("-fx-background-color: #37474F;",
                "-fx-background-color: transparent;"));
        btnUsuarios.setStyle(btnUsuarios.getStyle().replace("-fx-background-color: #37474F;",
                "-fx-background-color: transparent;"));

        // Resaltar el botón activo
        botonActivo.setStyle(botonActivo.getStyle() + "-fx-background-color: #37474F;");
    }

    private void aplicarEstilosMenu() {
        String hoverStyle = "-fx-background-color: #37474F;";

        btnFacturas.setOnMouseEntered(e -> {
            if (!btnFacturas.getStyle().contains("#37474F")) {
                btnFacturas.setStyle(btnFacturas.getStyle() + hoverStyle);
            }
        });
        btnFacturas.setOnMouseExited(e -> {
            // Solo quitar el hover si no es el botón activo
            resaltarBoton(btnFacturas); // Esto se manejará mejor con CSS classes
        });

        btnGarantias.setOnMouseEntered(e -> {
            if (!btnGarantias.getStyle().contains("#37474F")) {
                btnGarantias.setStyle(btnGarantias.getStyle() + hoverStyle);
            }
        });

        btnUsuarios.setOnMouseEntered(e -> {
            if (!btnUsuarios.getStyle().contains("#37474F")) {
                btnUsuarios.setStyle(btnUsuarios.getStyle() + hoverStyle);
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
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
}