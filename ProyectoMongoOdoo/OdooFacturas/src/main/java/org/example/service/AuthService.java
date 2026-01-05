package org.example.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.example.model.Usuario;
import org.example.util.MongoDBConnection;
import org.example.util.PasswordUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;

/**
 * Servicio de Autenticación
 */
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MongoCollection<Document> usuariosCollection;
    private Usuario usuarioActual;

    public AuthService() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.usuariosCollection = database.getCollection("usuarios");
    }

    /**
     * Intenta autenticar un usuario
     * @param username nombre de usuario
     * @param password contraseña en texto plano
     * @return Usuario si la autenticación es exitosa, null en caso contrario
     */
    public Usuario login(String username, String password) {
        try {
            logger.info("Intentando autenticar usuario: {}", username);

            // Buscar usuario en MongoDB
            Document userDoc = usuariosCollection.find(eq("username", username)).first();

            if (userDoc == null) {
                logger.warn("Usuario no encontrado: {}", username);
                return null;
            }

            // Verificar si el usuario está activo
            boolean activo = userDoc.getBoolean("activo", true);
            if (!activo) {
                logger.warn("Usuario inactivo: {}", username);
                return null;
            }

            // Verificar contraseña
            String hashedPassword = userDoc.getString("password");
            if (!PasswordUtil.checkPassword(password, hashedPassword)) {
                logger.warn("Contraseña incorrecta para usuario: {}", username);
                return null;
            }

            // Crear objeto Usuario
            Usuario usuario = documentToUsuario(userDoc);
            this.usuarioActual = usuario;

            logger.info("Autenticación exitosa para usuario: {} (rol: {})",
                    username, usuario.getRol());

            return usuario;

        } catch (Exception e) {
            logger.error("Error durante la autenticación", e);
            return null;
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        if (usuarioActual != null) {
            logger.info("Usuario {} ha cerrado sesión", usuarioActual.getUsername());
            usuarioActual = null;
        }
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean isAutenticado() {
        return usuarioActual != null;
    }

    /**
     * Convierte un Document de MongoDB a un objeto Usuario
     */
    private Usuario documentToUsuario(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setId(doc.getObjectId("_id"));
        usuario.setUsername(doc.getString("username"));
        usuario.setPassword(doc.getString("password"));
        usuario.setRol(doc.getString("rol"));
        usuario.setNombre(doc.getString("nombre"));
        usuario.setEmail(doc.getString("email"));
        usuario.setActivo(doc.getBoolean("activo", true));

        // Fecha de creación (puede ser Date o String)
        Object fechaObj = doc.get("fechaCreacion");
        if (fechaObj != null) {
            // Implementar conversión si es necesario
        }

        return usuario;
    }
}