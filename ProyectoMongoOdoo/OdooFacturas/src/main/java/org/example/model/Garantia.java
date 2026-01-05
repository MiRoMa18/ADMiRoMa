package org.example.model;

import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Modelo de Garantía asociada a facturas de Odoo
 */
public class Garantia {

    private ObjectId id;
    private int idFacturaOdoo;
    private String numeroFactura;
    private String cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int duracionMeses;
    private String tipo; // "1 año", "2 años", "3 años", etc.
    private String estado; // "activa", "expirada", "proxima"
    private String descripcion;
    private LocalDateTime fechaCreacion;

    // Constructores
    public Garantia() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "activa";
    }

    public Garantia(int idFacturaOdoo, String numeroFactura, String cliente,
                    LocalDate fechaInicio, LocalDate fechaFin, String tipo, String descripcion) {
        this();
        this.idFacturaOdoo = idFacturaOdoo;
        this.numeroFactura = numeroFactura;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.duracionMeses = (int) ChronoUnit.MONTHS.between(fechaInicio, fechaFin);
        actualizarEstado();
    }

    // Getters y Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getIdFacturaOdoo() {
        return idFacturaOdoo;
    }

    public void setIdFacturaOdoo(int idFacturaOdoo) {
        this.idFacturaOdoo = idFacturaOdoo;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
        if (this.fechaFin != null) {
            this.duracionMeses = (int) ChronoUnit.MONTHS.between(fechaInicio, this.fechaFin);
        }
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        if (this.fechaInicio != null) {
            this.duracionMeses = (int) ChronoUnit.MONTHS.between(this.fechaInicio, fechaFin);
        }
        actualizarEstado();
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos de utilidad

    /**
     * Actualiza el estado de la garantía según la fecha actual
     */
    public void actualizarEstado() {
        if (fechaFin == null) {
            this.estado = "activa";
            return;
        }

        LocalDate hoy = LocalDate.now();

        if (hoy.isAfter(fechaFin)) {
            this.estado = "expirada";
        } else if (hoy.plusDays(30).isAfter(fechaFin)) {
            this.estado = "proxima"; // Próxima a expirar (menos de 30 días)
        } else {
            this.estado = "activa";
        }
    }

    /**
     * Obtiene los días restantes de garantía
     */
    public long getDiasRestantes() {
        if (fechaFin == null) return 0;
        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(fechaFin)) return 0;
        return ChronoUnit.DAYS.between(hoy, fechaFin);
    }

    /**
     * Verifica si la garantía está activa
     */
    public boolean isActiva() {
        return "activa".equals(this.estado);
    }

    /**
     * Verifica si la garantía está expirada
     */
    public boolean isExpirada() {
        return "expirada".equals(this.estado);
    }

    /**
     * Verifica si la garantía está próxima a expirar
     */
    public boolean isProxima() {
        return "proxima".equals(this.estado);
    }

    @Override
    public String toString() {
        return "Garantia{" +
                "numeroFactura='" + numeroFactura + '\'' +
                ", cliente='" + cliente + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}