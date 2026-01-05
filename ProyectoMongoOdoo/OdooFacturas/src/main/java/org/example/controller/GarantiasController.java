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
import org.example.model.Garantia;
import org.example.model.Usuario;
import org.example.service.MongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de garantías
 * VERSIÓN FINAL con diálogos
 */
public class GarantiasController {

    private static final Logger logger = LoggerFactory.getLogger(GarantiasController.class);

    @FXML private TableView<Garantia> tableGarantias;
    @FXML private TableColumn<Garantia, String> colFactura;
    @FXML private TableColumn<Garantia, String> colCliente;
    @FXML private TableColumn<Garantia, String> colTipo;
    @FXML private TableColumn<Garantia, String> colFechaInicio;
    @FXML private TableColumn<Garantia, String> colFechaFin;
    @FXML private TableColumn<Garantia, Integer> colDiasRestantes;
    @FXML private TableColumn<Garantia, String> colEstado;
    @FXML private TableColumn<Garantia, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;
    @FXML private Label lblAlerta;
    @FXML private HBox alertaBox;
    @FXML private Button btnRefrescar;

    private ObservableList<Garantia> todasLasGarantias;
    private ObservableList<Garantia> garantiasFiltradas;
    private MongoService mongoService;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        mongoService = new MongoService();
        usuarioActual = SessionManager.getUsuarioActual();

        configurarTabla();
        configurarFiltros();
        cargarGarantias();

        logger.info("Vista de garantías cargada para usuario: {} ({})",
                usuarioActual.getNombre(), usuarioActual.getRol());
    }

    private void configurarTabla() {
        colFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colFechaInicio.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaInicio()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(fecha);
        });

        colFechaFin.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaFin()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(fecha);
        });

        colDiasRestantes.setCellValueFactory(new PropertyValueFactory<>("diasRestantes"));

        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado().toUpperCase())
        );

        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "activa":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "proxima":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        case "expirada":
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        configurarColumnaAcciones();
        tableGarantias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️ Ver");
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnEliminar = new Button("🗑️ Eliminar");
            private final HBox pane = new HBox(5, btnVer, btnEditar, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");
                btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");
                btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 5px 10px;");

                btnVer.setOnAction(event -> {
                    Garantia garantia = getTableView().getItems().get(getIndex());
                    verGarantia(garantia);
                });

                btnEditar.setOnAction(event -> {
                    Garantia garantia = getTableView().getItems().get(getIndex());
                    editarGarantia(garantia);
                });

                btnEliminar.setOnAction(event -> {
                    Garantia garantia = getTableView().getItems().get(getIndex());
                    eliminarGarantia(garantia);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // ✅ CONTROL DE PERMISOS
                    btnVer.setVisible(true);
                    btnEditar.setVisible(usuarioActual.isAdmin() || usuarioActual.isComercial());
                    btnEliminar.setVisible(usuarioActual.isAdmin());

                    setGraphic(pane);
                }
            }
        });
    }

    private void configurarFiltros() {
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Activa", "Próxima", "Expirada"
        ));
        cmbEstado.setValue("Todos");
    }

    @FXML
    public void cargarGarantias() {
        lblEstado.setText("Cargando garantías...");
        btnRefrescar.setDisable(true);

        new Thread(() -> {
            try {
                List<Garantia> garantias = mongoService.obtenerTodasLasGarantias();

                long proximasExpirar = garantias.stream()
                        .filter(g -> g.getEstado().equals("proxima"))
                        .count();

                Platform.runLater(() -> {
                    todasLasGarantias = FXCollections.observableArrayList(garantias);
                    garantiasFiltradas = FXCollections.observableArrayList(todasLasGarantias);
                    tableGarantias.setItems(garantiasFiltradas);

                    lblTotal.setText("Total: " + garantias.size() + " garantías");
                    lblEstado.setText("Garantías cargadas correctamente");
                    lblConexion.setText("● Conectado a MongoDB");
                    lblConexion.setStyle("-fx-text-fill: #4CAF50;");
                    btnRefrescar.setDisable(false);

                    if (proximasExpirar > 0) {
                        alertaBox.setVisible(true);
                        lblAlerta.setText(proximasExpirar + " garantía(s) próxima(s) a expirar");
                    } else {
                        alertaBox.setVisible(false);
                    }

                    logger.info("Se cargaron {} garantías", garantias.size());
                });

            } catch (Exception e) {
                logger.error("Error al cargar garantías", e);
                Platform.runLater(() -> {
                    lblEstado.setText("Error al cargar garantías");
                    lblConexion.setText("● Error de conexión");
                    lblConexion.setStyle("-fx-text-fill: #f44336;");
                    btnRefrescar.setDisable(false);
                    mostrarError("Error al cargar garantías: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void filtrarGarantias() {
        if (todasLasGarantias == null) return;

        String busqueda = txtBuscar.getText().toLowerCase();
        String estadoSeleccionado = cmbEstado.getValue();

        List<Garantia> filtradas = todasLasGarantias.stream()
                .filter(g -> {
                    boolean coincideBusqueda = busqueda.isEmpty() ||
                            g.getNumeroFactura().toLowerCase().contains(busqueda) ||
                            g.getCliente().toLowerCase().contains(busqueda);

                    boolean coincideEstado = estadoSeleccionado.equals("Todos") ||
                            g.getEstado().equalsIgnoreCase(estadoSeleccionado);

                    return coincideBusqueda && coincideEstado;
                })
                .toList();

        garantiasFiltradas.setAll(filtradas);
        lblTotal.setText("Total: " + filtradas.size() + " garantías");
    }

    private void verGarantia(Garantia garantia) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información de Garantía");
        alert.setHeaderText("Garantía: " + garantia.getNumeroFactura());

        String info = String.format(
                "Cliente: %s\n" +
                        "Tipo: %s\n" +
                        "Fecha Inicio: %s\n" +
                        "Fecha Fin: %s\n" +
                        "Duración: %d meses\n" +
                        "Estado: %s\n" +
                        "Días Restantes: %d\n" +
                        "Descripción: %s",
                garantia.getCliente(),
                garantia.getTipo(),
                garantia.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                garantia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                garantia.getDuracionMeses(),
                garantia.getEstado().toUpperCase(),
                garantia.getDiasRestantes(),
                garantia.getDescripcion()
        );

        alert.setContentText(info);
        alert.showAndWait();
    }

    private void editarGarantia(Garantia garantia) {
        // ✅ CONTROL DE PERMISOS
        if (usuarioActual.isEmpleado()) {
            mostrarAdvertencia("No tienes permisos para editar garantías.");
            logger.warn("Usuario {} ({}) intentó editar una garantía sin permisos",
                    usuarioActual.getUsername(), usuarioActual.getRol());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/garantia-dialog.fxml"));
            Parent root = loader.load();

            GarantiaDialogController controller = loader.getController();
            controller.inicializarEditar(garantia);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Garantía");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (controller.isGuardado()) {
                Garantia garantiaEditada = controller.getGarantia();
                boolean actualizada = mongoService.actualizarGarantia(garantiaEditada);

                if (actualizada) {
                    logger.info("Garantía editada por {} : {}",
                            usuarioActual.getUsername(), garantia.getId());
                    mostrarInfo("Garantía actualizada exitosamente");
                    cargarGarantias();
                } else {
                    mostrarError("No se pudo actualizar la garantía");
                }
            }

        } catch (Exception e) {
            logger.error("Error al editar garantía", e);
            mostrarError("Error al editar: " + e.getMessage());
        }
    }

    private void eliminarGarantia(Garantia garantia) {
        // ✅ CONTROL DE PERMISOS
        if (!usuarioActual.isAdmin()) {
            mostrarAdvertencia("Solo los administradores pueden eliminar garantías.");
            logger.warn("Usuario {} ({}) intentó eliminar una garantía sin permisos",
                    usuarioActual.getUsername(), usuarioActual.getRol());
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar garantía?");
        confirmacion.setContentText(
                "¿Estás seguro de que deseas eliminar esta garantía?\n\n" +
                        "Factura: " + garantia.getNumeroFactura() + "\n" +
                        "Cliente: " + garantia.getCliente() + "\n\n" +
                        "Esta acción no se puede deshacer."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminada = mongoService.eliminarGarantia(garantia.getId());

                if (eliminada) {
                    logger.info("Garantía eliminada por admin {}: {}",
                            usuarioActual.getUsername(), garantia.getId());
                    mostrarInfo("Garantía eliminada exitosamente");
                    cargarGarantias();
                } else {
                    mostrarError("No se pudo eliminar la garantía");
                }
            } catch (Exception e) {
                logger.error("Error al eliminar garantía", e);
                mostrarError("Error al eliminar: " + e.getMessage());
            }
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