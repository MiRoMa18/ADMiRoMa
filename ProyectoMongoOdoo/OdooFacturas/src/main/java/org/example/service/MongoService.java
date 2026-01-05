package org.example.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.example.model.Garantia;
import org.example.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * Servicio para gestionar Garantías en MongoDB
 */
public class MongoService {

    private static final Logger logger = LoggerFactory.getLogger(MongoService.class);
    private final MongoCollection<Document> garantiasCollection;

    public MongoService() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.garantiasCollection = database.getCollection("garantias");
    }

    /**
     * Crea una nueva garantía
     */
    public boolean crearGarantia(Garantia garantia) {
        try {
            Document doc = garantiaToDocument(garantia);
            garantiasCollection.insertOne(doc);
            garantia.setId(doc.getObjectId("_id"));

            logger.info("Garantía creada para factura: {}", garantia.getNumeroFactura());
            return true;

        } catch (Exception e) {
            logger.error("Error al crear garantía", e);
            return false;
        }
    }

    /**
     * Obtiene todas las garantías
     */
    public List<Garantia> obtenerTodasLasGarantias() {
        List<Garantia> garantias = new ArrayList<>();
        try {
            for (Document doc : garantiasCollection.find()) {
                garantias.add(documentToGarantia(doc));
            }
            logger.info("Se obtuvieron {} garantías", garantias.size());
        } catch (Exception e) {
            logger.error("Error al obtener garantías", e);
        }
        return garantias;
    }

    /**
     * Busca una garantía por ID de factura de Odoo
     */
    public Garantia buscarPorFacturaOdoo(int idFacturaOdoo) {
        try {
            Document doc = garantiasCollection.find(eq("idFacturaOdoo", idFacturaOdoo)).first();
            if (doc != null) {
                return documentToGarantia(doc);
            }
        } catch (Exception e) {
            logger.error("Error al buscar garantía por factura", e);
        }
        return null;
    }

    /**
     * Obtiene garantías por estado
     */
    public List<Garantia> obtenerPorEstado(String estado) {
        List<Garantia> garantias = new ArrayList<>();
        try {
            for (Document doc : garantiasCollection.find(eq("estado", estado))) {
                garantias.add(documentToGarantia(doc));
            }
            logger.info("Se obtuvieron {} garantías con estado: {}", garantias.size(), estado);
        } catch (Exception e) {
            logger.error("Error al obtener garantías por estado", e);
        }
        return garantias;
    }

    /**
     * Actualiza una garantía existente
     */
    public boolean actualizarGarantia(Garantia garantia) {
        try {
            Document doc = garantiaToDocument(garantia);
            garantiasCollection.replaceOne(
                    eq("_id", garantia.getId()),
                    doc
            );
            logger.info("Garantía actualizada: {}", garantia.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar garantía", e);
            return false;
        }
    }

    /**
     * Elimina una garantía
     */
    public boolean eliminarGarantia(ObjectId id) {
        try {
            garantiasCollection.deleteOne(eq("_id", id));
            logger.info("Garantía eliminada: {}", id);
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar garantía", e);
            return false;
        }
    }

    /**
     * Actualiza el estado de todas las garantías
     */
    public void actualizarEstadosGarantias() {
        logger.info("Actualizando estados de garantías...");
        List<Garantia> garantias = obtenerTodasLasGarantias();
        for (Garantia g : garantias) {
            g.actualizarEstado();
            actualizarGarantia(g);
        }
        logger.info("Estados actualizados");
    }

    /**
     * Convierte una Garantia a Document de MongoDB
     */
    private Document garantiaToDocument(Garantia garantia) {
        Document doc = new Document();

        if (garantia.getId() != null) {
            doc.append("_id", garantia.getId());
        }

        doc.append("idFacturaOdoo", garantia.getIdFacturaOdoo())
                .append("numeroFactura", garantia.getNumeroFactura())
                .append("cliente", garantia.getCliente())
                .append("fechaInicio", localDateToDate(garantia.getFechaInicio()))
                .append("fechaFin", localDateToDate(garantia.getFechaFin()))
                .append("duracionMeses", garantia.getDuracionMeses())
                .append("tipo", garantia.getTipo())
                .append("estado", garantia.getEstado())
                .append("descripcion", garantia.getDescripcion())
                .append("fechaCreacion", Date.from(garantia.getFechaCreacion()
                        .atZone(ZoneId.systemDefault()).toInstant()));

        return doc;
    }

    /**
     * Convierte un Document de MongoDB a Garantia
     */
    private Garantia documentToGarantia(Document doc) {
        Garantia garantia = new Garantia();

        garantia.setId(doc.getObjectId("_id"));
        garantia.setIdFacturaOdoo(doc.getInteger("idFacturaOdoo"));
        garantia.setNumeroFactura(doc.getString("numeroFactura"));
        garantia.setCliente(doc.getString("cliente"));
        garantia.setFechaInicio(dateToLocalDate(doc.getDate("fechaInicio")));
        garantia.setFechaFin(dateToLocalDate(doc.getDate("fechaFin")));
        garantia.setDuracionMeses(doc.getInteger("duracionMeses", 0));
        garantia.setTipo(doc.getString("tipo"));
        garantia.setEstado(doc.getString("estado"));
        garantia.setDescripcion(doc.getString("descripcion"));

        // Actualizar estado según fechas
        garantia.actualizarEstado();

        return garantia;
    }

    /**
     * Convierte LocalDate a Date
     */
    private Date localDateToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convierte Date a LocalDate
     */
    private LocalDate dateToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}