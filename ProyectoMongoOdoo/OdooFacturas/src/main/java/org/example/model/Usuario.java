package org.example.model;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;

/**
 * Modelo de Usuario para el sistema
 */
public class Usuario {

    private ObjectId id;
    private String username;
    private String password;
    private String rol; // admin, comercial, empleado
    private String nombre;
    private String email;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    // Constructores
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    public Usuario(String username, String password, String rol, String nombre, String email) {
        this();
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Métodos de utilidad
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.rol);
    }

    public boolean isComercial() {
        return "comercial".equalsIgnoreCase(this.rol);
    }

    public boolean isEmpleado() {
        return "empleado".equalsIgnoreCase(this.rol);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", rol='" + rol + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }
}