package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.util.MongoDBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aplicación principal JavaFX
 */
public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // Cargar la pantalla de login
            mostrarLogin();

            // Configurar cierre de la aplicación
            stage.setOnCloseRequest(event -> {
                MongoDBConnection.close();
                logger.info("Aplicación cerrada");
            });

        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación", e);
            e.printStackTrace();
        }
    }

    /**
     * Muestra la pantalla de login
     */
    public static void mostrarLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema de Gestión - Login");
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            logger.error("Error al cargar pantalla de login", e);
            e.printStackTrace();
        }
    }

    /**
     * Muestra el dashboard principal
     */
    public static void mostrarDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema de Gestión - Dashboard");
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            logger.error("Error al cargar dashboard", e);
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}