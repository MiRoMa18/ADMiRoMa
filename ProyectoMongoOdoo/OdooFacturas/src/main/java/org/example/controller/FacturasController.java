package org.example.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Factura;
import org.example.model.Garantia;
import org.example.model.Usuario;
import org.example.service.MongoService;
import org.example.service.OdooService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para la vista de facturas
 * VERSIÓN FINAL con diálogos de garantías
 */
public class FacturasController {

    private static final Logger logger = LoggerFactory.getLogger(FacturasController.class);

    @FXML private TableView<Factura> tableFacturas;
    @FXML private TableColumn<Factura, Integer> colId;
    @FXML private TableColumn<Factura, String> colNumero;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, String> colImporte;
    @FXML private TableColumn<Factura, String> colEstado;
    @FXML private TableColumn<Factura, String> colGarantia;
    @FXML private TableColumn<Factura, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;
    @FXML private Button btnRefrescar;

    private ObservableList<Factura> todasLasFacturas;
    private ObservableList<Factura> facturasFiltradas;
    private OdooService odooService;
    private MongoService mongoService;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        odooService = new OdooService();
        mongoService = new MongoService();
        usuarioActual = SessionManager.getUsuarioActual();

        configurarTabla();
        configurarFiltros();
        cargarFacturas();

        logger.info("Vista de facturas cargada para usuario: {} ({})",
                usuarioActual.getNombre(), usuarioActual.getRol());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        colFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha();
            String fechaStr = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new SimpleStringProperty(fechaStr);
        });

        colImporte.setCellValueFactory(cellData -> {
            double importe = cellData.getValue().getImporteTotal();
            return new SimpleStringProperty(String.format("%.2f €", importe));
        });

        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstadoEspanol())
        );

        colGarantia.setCellValueFactory(cellData -> {
            boolean tieneGarantia = cellData.getValue().isTieneGarantia();
            return new SimpleStringProperty(tieneGarantia ? "✅ Sí" : "❌ No");
        });

        configurarColumnaAcciones();
        tableFacturas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVerGarantia = new Button("👁️ Ver");
            private final Button btnCrearGarantia = new Button("➕ Crear");
            private final HBox pane = new HBox(5, btnVerGarantia, btnCrearGarantia);

            {
                btnVerGarantia.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                btnCrearGarantia.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");

                btnVerGarantia.setOnAction(event -> {
                    Factura factura = getTableView().getItems().get(getIndex());
                    verGarantia(factura);
                });

                btnCrearGarantia.setOnAction(event -> {
                    Factura factura = getTableView().getItems().get(getIndex());
                    crearGarantia(factura);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Factura factura = getTableView().getItems().get(getIndex());

                    // ✅ CONTROL DE PERMISOS
                    boolean puedeCrearGarantias = usuarioActual.isAdmin() || usuarioActual.isComercial();

                    btnVerGarantia.setVisible(factura.isTieneGarantia());
                    btnCrearGarantia.setVisible(!factura.isTieneGarantia() && puedeCrearGarantias);

                    setGraphic(pane);
                }
            }
        });
    }

    private void configurarFiltros() {
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Publicada", "Borrador", "Cancelada"
        ));
        cmbEstado.setValue("Todos");
    }

    @FXML
    private void cargarFacturas() {
        lblEstado.setText("Cargando facturas...");
        btnRefrescar.setDisable(true);

        new Thread(() -> {
            try {
                boolean autenticado = odooService.authenticate();

                if (!autenticado) {
                    Platform.runLater(() -> {
                        lblEstado.setText("Error: No se pudo autenticar con Odoo");
                        lblConexion.setText("● Desconectado");
                        lblConexion.setStyle("-fx-text-fill: #f44336;");
                        mostrarError("No se pudo conectar con Odoo");
                    });
                    return;
                }

                List<Factura> facturas = odooService.obtenerFacturas();

                for (Factura factura : facturas) {
                    Garantia garantia = mongoService.buscarPorFacturaOdoo(factura.getId());
                    factura.setTieneGarantia(garantia != null);
                }

                Platform.runLater(() -> {
                    todasLasFacturas = FXCollections.observableArrayList(facturas);
                    facturasFiltradas = FXCollections.observableArrayList(todasLasFacturas);
                    tableFacturas.setItems(facturasFiltradas);

                    lblTotal.setText("Total: " + facturas.size() + " facturas");
                    lblEstado.setText("Facturas cargadas correctamente");
                    lblConexion.setText("● Conectado a Odoo");
                    lblConexion.setStyle("-fx-text-fill: #4CAF50;");
                    btnRefrescar.setDisable(false);

                    logger.info("Se cargaron {} facturas", facturas.size());
                });

            } catch (Exception e) {
                logger.error("Error al cargar facturas", e);
                Platform.runLater(() -> {
                    lblEstado.setText("Error al cargar facturas");
                    lblConexion.setText("● Error de conexión");
                    lblConexion.setStyle("-fx-text-fill: #f44336;");
                    btnRefrescar.setDisable(false);
                    mostrarError("Error al cargar facturas: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void filtrarFacturas() {
        if (todasLasFacturas == null) return;

        String busqueda = txtBuscar.getText().toLowerCase();
        String estadoSeleccionado = cmbEstado.getValue();

        List<Factura> filtradas = todasLasFacturas.stream()
                .filter(f -> {
                    boolean coincideBusqueda = busqueda.isEmpty() ||
                            f.getNumeroFactura().toLowerCase().contains(busqueda) ||
                            f.getCliente().toLowerCase().contains(busqueda);

                    boolean coincideEstado = estadoSeleccionado.equals("Todos") ||
                            f.getEstadoEspanol().equals(estadoSeleccionado);

                    return coincideBusqueda && coincideEstado;
                })
                .toList();

        facturasFiltradas.setAll(filtradas);
        lblTotal.setText("Total: " + filtradas.size() + " facturas");
    }

    private void verGarantia(Factura factura) {
        try {
            Garantia garantia = mongoService.buscarPorFacturaOdoo(factura.getId());

            if (garantia == null) {
                mostrarInfo("No se encontró la garantía");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información de Garantía");
            alert.setHeaderText("Garantía para: " + factura.getNumeroFactura());

            String info = String.format(
                    "Cliente: %s\n" +
                            "Tipo: %s\n" +
                            "Fecha Inicio: %s\n" +
                            "Fecha Fin: %s\n" +
                            "Estado: %s\n" +
                            "Días Restantes: %d\n" +
                            "Descripción: %s",
                    garantia.getCliente(),
                    garantia.getTipo(),
                    garantia.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    garantia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    garantia.getEstado().toUpperCase(),
                    garantia.getDiasRestantes(),
                    garantia.getDescripcion()
            );

            alert.setContentText(info);
            alert.showAndWait();

        } catch (Exception e) {
            logger.error("Error al ver garantía", e);
            mostrarError("Error al cargar la garantía");
        }
    }

    private void crearGarantia(Factura factura) {
        // ✅ CONTROL DE PERMISOS
        if (usuarioActual.isEmpleado()) {
            mostrarAdvertencia("No tienes permisos para crear garantías.\nSolo puedes visualizar la información.");
            logger.warn("Usuario {} ({}) intentó crear una garantía sin permisos",
                    usuarioActual.getUsername(), usuarioActual.getRol());
            return;
        }

        try {
            // Cargar el diálogo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garantia-dialog.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y configurar
            GarantiaDialogController controller = loader.getController();
            controller.inicializarCrear(factura);

            // Crear y mostrar el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crear Garantía");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Si se guardó, crear en MongoDB
            if (controller.isGuardado()) {
                Garantia nuevaGarantia = controller.getGarantia();
                boolean creada = mongoService.crearGarantia(nuevaGarantia);

                if (creada) {
                    logger.info("Garantía creada por {} para factura {}",
                            usuarioActual.getUsername(), factura.getNumeroFactura());
                    mostrarInfo("Garantía creada exitosamente");
                    cargarFacturas();
                } else {
                    mostrarError("No se pudo crear la garantía");
                }
            }

        } catch (Exception e) {
            logger.error("Error al abrir diálogo de garantía", e);
            mostrarError("Error al crear la garantía: " + e.getMessage());
        }
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
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

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}