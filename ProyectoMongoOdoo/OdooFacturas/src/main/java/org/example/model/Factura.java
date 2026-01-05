package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de Factura obtenida desde Odoo
 */
public class Factura {

    private int id;
    private String numeroFactura; // name en Odoo
    private String cliente; // partner_id[1] en Odoo
    private int clienteId; // partner_id[0] en Odoo
    private LocalDate fecha; // invoice_date
    private double importeTotal; // amount_total
    private String estado; // state: draft, posted, cancel
    private List<LineaFactura> lineas;
    private boolean tieneGarantia; // Campo calculado

    // Constructor
    public Factura() {
        this.lineas = new ArrayList<>();
        this.tieneGarantia = false;
    }

    public Factura(int id, String numeroFactura, String cliente, LocalDate fecha, double importeTotal) {
        this();
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importeTotal = importeTotal;
        this.estado = "posted";
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<LineaFactura> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaFactura> lineas) {
        this.lineas = lineas;
    }

    public void addLinea(LineaFactura linea) {
        this.lineas.add(linea);
    }

    public boolean isTieneGarantia() {
        return tieneGarantia;
    }

    public void setTieneGarantia(boolean tieneGarantia) {
        this.tieneGarantia = tieneGarantia;
    }

    // Métodos de utilidad

    /**
     * Verifica si la factura está confirmada (posted)
     */
    public boolean isConfirmada() {
        return "posted".equals(this.estado);
    }

    /**
     * Verifica si la factura está en borrador
     */
    public boolean isBorrador() {
        return "draft".equals(this.estado);
    }

    /**
     * Obtiene el estado en español
     */
    public String getEstadoEspanol() {
        return switch (estado) {
            case "draft" -> "Borrador";
            case "posted" -> "Publicada";
            case "cancel" -> "Cancelada";
            default -> estado;
        };
    }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", cliente='" + cliente + '\'' +
                ", fecha=" + fecha +
                ", importeTotal=" + importeTotal +
                ", estado='" + estado + '\'' +
                ", tieneGarantia=" + tieneGarantia +
                '}';
    }

    /**
     * Clase interna para representar líneas de factura
     */
    public static class LineaFactura {
        private String producto;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;

        public LineaFactura() {}

        public LineaFactura(String producto, int cantidad, double precioUnitario) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }

        // Getters y Setters
        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
            this.subtotal = cantidad * precioUnitario;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }

        @Override
        public String toString() {
            return "LineaFactura{" +
                    "producto='" + producto + '\'' +
                    ", cantidad=" + cantidad +
                    ", precioUnitario=" + precioUnitario +
                    ", subtotal=" + subtotal +
                    '}';
        }
    }
}