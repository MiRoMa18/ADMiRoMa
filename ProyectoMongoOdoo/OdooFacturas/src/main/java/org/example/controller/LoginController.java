package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.App;
import org.example.model.Usuario;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la pantalla de login
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private AuthService authService;

    @FXML
    public void initialize() {
        authService = new AuthService();

        // Permitir login con Enter
        txtPassword.setOnAction(event -> handleLogin());

        logger.info("Pantalla de login cargada");
    }

    @FXML
    private void handleLogin() {
        // Limpiar mensaje de error
        lblError.setVisible(false);

        // Obtener credenciales
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña");
            return;
        }

        // Deshabilitar botón durante la autenticación
        btnLogin.setDisable(true);
        btnLogin.setText("Autenticando...");

        try {
            // Intentar autenticar
            Usuario usuario = authService.login(username, password);

            if (usuario != null) {
                logger.info("Login exitoso: {} ({})", usuario.getNombre(), usuario.getRol());

                // Guardar el servicio de autenticación para usarlo en otras pantallas
                SessionManager.setAuthService(authService);
                SessionManager.setUsuarioActual(usuario);

                // Ir al dashboard
                App.mostrarDashboard();

            } else {
                mostrarError("Usuario o contraseña incorrectos");
                btnLogin.setDisable(false);
                btnLogin.setText("Iniciar Sesión");
            }

        } catch (Exception e) {
            logger.error("Error durante el login", e);
            mostrarError("Error al conectar con el servidor");
            btnLogin.setDisable(false);
            btnLogin.setText("Iniciar Sesión");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}

/**
 * Clase para mantener la sesión del usuario
 */
class SessionManager {
    private static AuthService authService;
    private static Usuario usuarioActual;

    public static void setAuthService(AuthService service) {
        authService = service;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        if (authService != null) {
            authService.logout();
        }
        authService = null;
        usuarioActual = null;
    }
}