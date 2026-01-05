package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Factura;
import org.example.model.Garantia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controlador para el diálogo de crear/editar garantías
 * VERSIÓN SIMPLIFICADA - Opciones claras sin duplicados
 */
public class GarantiaDialogController {

    private static final Logger logger = LoggerFactory.getLogger(GarantiaDialogController.class);

    @FXML private Label lblTitulo;
    @FXML private TextField txtNumeroFactura;
    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextField txtDuracionMeses;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Garantia garantia;
    private Factura factura;
    private boolean modoEdicion = false;
    private boolean guardado = false;

    public void initialize() {
        configurarCampos();
        configurarEventos();
    }

    private void configurarCampos() {
        // Configurar ComboBox de tipo - SIMPLIFICADO
        cmbTipo.getItems().addAll(
                "Garantía 1 año",
                "Garantía 2 años",
                "Garantía 3 años",
                "Garantía 5 años",
                "Garantía de fábrica (1 año)",
                "Garantía comercial (2 años)",
                "Otra (personalizada)"
        );
        cmbTipo.setValue("Garantía 2 años");

        // Fecha de inicio por defecto: hoy
        dpFechaInicio.setValue(LocalDate.now());

        // Fecha de fin por defecto: 2 años desde hoy
        dpFechaFin.setValue(LocalDate.now().plusYears(2));

        // Calcular duración inicial
        calcularDuracion();
    }

    private void configurarEventos() {
        // Calcular duración cuando cambian las fechas
        dpFechaInicio.valueProperty().addListener((obs, oldVal, newVal) -> calcularDuracion());
        dpFechaFin.valueProperty().addListener((obs, oldVal, newVal) -> calcularDuracion());

        // Cambiar fechas según el tipo seleccionado
        cmbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpFechaInicio.getValue() != null) {
                LocalDate inicio = dpFechaInicio.getValue();
                switch (newVal) {
                    case "Garantía 1 año":
                    case "Garantía de fábrica (1 año)":
                        dpFechaFin.setValue(inicio.plusYears(1));
                        break;
                    case "Garantía 2 años":
                    case "Garantía comercial (2 años)":
                        dpFechaFin.setValue(inicio.plusYears(2));
                        break;
                    case "Garantía 3 años":
                        dpFechaFin.setValue(inicio.plusYears(3));
                        break;
                    case "Garantía 5 años":
                        dpFechaFin.setValue(inicio.plusYears(5));
                        break;
                    // Para "Otra (personalizada)" no cambia nada, el usuario decide
                }
            }
        });
    }

    private void calcularDuracion() {
        if (dpFechaInicio.getValue() != null && dpFechaFin.getValue() != null) {
            long meses = ChronoUnit.MONTHS.between(dpFechaInicio.getValue(), dpFechaFin.getValue());
            txtDuracionMeses.setText(String.valueOf(meses));
        }
    }

    /**
     * Inicializar para CREAR garantía desde factura
     */
    public void inicializarCrear(Factura factura) {
        this.factura = factura;
        this.modoEdicion = false;

        lblTitulo.setText("Crear Nueva Garantía");
        txtNumeroFactura.setText(factura.getNumeroFactura());
        txtNumeroFactura.setEditable(false);
        txtCliente.setText(factura.getCliente());
        txtCliente.setEditable(false);

        logger.info("Diálogo de crear garantía inicializado para factura: {}", factura.getNumeroFactura());
    }

    /**
     * Inicializar para EDITAR garantía existente
     */
    public void inicializarEditar(Garantia garantia) {
        this.garantia = garantia;
        this.modoEdicion = true;

        lblTitulo.setText("Editar Garantía");
        txtNumeroFactura.setText(garantia.getNumeroFactura());
        txtNumeroFactura.setEditable(false);
        txtCliente.setText(garantia.getCliente());
        txtCliente.setEditable(false);
        cmbTipo.setValue(garantia.getTipo());
        dpFechaInicio.setValue(garantia.getFechaInicio());
        dpFechaFin.setValue(garantia.getFechaFin());
        txtDescripcion.setText(garantia.getDescripcion());

        calcularDuracion();

        logger.info("Diálogo de editar garantía inicializado para: {}", garantia.getNumeroFactura());
    }

    @FXML
    private void handleGuardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        if (modoEdicion) {
            // Actualizar garantía existente
            garantia.setTipo(cmbTipo.getValue());
            garantia.setFechaInicio(dpFechaInicio.getValue());
            garantia.setFechaFin(dpFechaFin.getValue());
            garantia.setDuracionMeses(Integer.parseInt(txtDuracionMeses.getText()));
            garantia.setDescripcion(txtDescripcion.getText());
            garantia.actualizarEstado();

        } else {
            // Crear nueva garantía
            garantia = new Garantia();
            garantia.setIdFacturaOdoo(factura.getId());
            garantia.setNumeroFactura(factura.getNumeroFactura());
            garantia.setCliente(factura.getCliente());
            garantia.setTipo(cmbTipo.getValue());
            garantia.setFechaInicio(dpFechaInicio.getValue());
            garantia.setFechaFin(dpFechaFin.getValue());
            garantia.setDuracionMeses(Integer.parseInt(txtDuracionMeses.getText()));
            garantia.setDescripcion(txtDescripcion.getText());
        }

        guardado = true;
        cerrarDialogo();
    }

    @FXML
    private void handleCancelar() {
        guardado = false;
        cerrarDialogo();
    }

    private boolean validarCampos() {
        if (cmbTipo.getValue() == null || cmbTipo.getValue().isEmpty()) {
            mostrarError("Selecciona un tipo de garantía");
            return false;
        }

        if (dpFechaInicio.getValue() == null) {
            mostrarError("Selecciona la fecha de inicio");
            return false;
        }

        if (dpFechaFin.getValue() == null) {
            mostrarError("Selecciona la fecha de fin");
            return false;
        }

        if (dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())) {
            mostrarError("La fecha de fin debe ser posterior a la fecha de inicio");
            return false;
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            mostrarError("Ingresa una descripción");
            return false;
        }

        return true;
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Getters
    public boolean isGuardado() {
        return guardado;
    }

    public Garantia getGarantia() {
        return garantia;
    }
}