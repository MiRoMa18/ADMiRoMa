package org.example.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para gestionar contraseñas con BCrypt
 */
public class PasswordUtil {

    /**
     * Hashea una contraseña usando BCrypt
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash de la contraseña
     * @return true si coinciden, false en caso contrario
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Método main para generar hashes de prueba
     */
    public static void main(String[] args) {
        // Generar hash para "admin123"
        String password = "carlos123";
        String hash = hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verificación: " + checkPassword(password, hash));
    }
}