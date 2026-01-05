package org.example.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a MongoDB.
 * Implementa el patrón Singleton para tener una única instancia de conexión.
 */
public class MongoDBConnection {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static Properties properties;

    // Constructor privado para evitar instanciación
    private MongoDBConnection() {}

    /**
     * Carga las propiedades del archivo application.properties
     */
    private static void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = MongoDBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")) {

                if (input == null) {
                    logger.error("No se encuentra el archivo application.properties");
                    return;
                }

                properties.load(input);
                logger.info("Propiedades de configuración cargadas correctamente");

            } catch (IOException e) {
                logger.error("Error al cargar application.properties", e);
            }
        }
    }

    /**
     * Obtiene la conexión a la base de datos MongoDB
     * @return MongoDatabase
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                loadProperties();

                String host = properties.getProperty("mongodb.host", "localhost");
                String port = properties.getProperty("mongodb.port", "27017");
                String dbName = properties.getProperty("mongodb.database", "garantias_db");
                String username = properties.getProperty("mongodb.username", "rootuser");
                String password = properties.getProperty("mongodb.password", "RootPass123!");
                String authDatabase = properties.getProperty("mongodb.auth.database", "admin");

                // Construir la cadena de conexión
                String connectionString = String.format(
                        "mongodb://%s:%s@%s:%s/%s?authSource=%s",
                        username, password, host, port, dbName, authDatabase
                );

                logger.info("Conectando a MongoDB en {}:{}", host, port);

                // Configurar el cliente de MongoDB
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build();

                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase(dbName);

                // Verificar la conexión
                database.listCollectionNames().first();
                logger.info("Conexión a MongoDB establecida correctamente");

            } catch (Exception e) {
                logger.error("Error al conectar con MongoDB", e);
                throw new RuntimeException("No se pudo conectar a MongoDB", e);
            }
        }
        return database;
    }

    /**
     * Cierra la conexión a MongoDB
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Conexión a MongoDB cerrada");
            mongoClient = null;
            database = null;
        }
    }

    /**
     * Verifica si la conexión está activa
     * @return true si está conectado, false en caso contrario
     */
    public static boolean isConnected() {
        try {
            if (database != null) {
                database.listCollectionNames().first();
                return true;
            }
        } catch (Exception e) {
            logger.error("Error al verificar conexión", e);
        }
        return false;
    }
}
