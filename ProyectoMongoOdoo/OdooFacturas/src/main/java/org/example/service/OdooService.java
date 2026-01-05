package org.example.service;

import org.example.model.Factura;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio para interactuar con la API de Odoo
 */
public class OdooService {

    private static final Logger logger = LoggerFactory.getLogger(OdooService.class);

    private String url;
    private String database;
    private String username;
    private String password;
    private Integer uid; // User ID después de autenticación

    private XmlRpcClient commonClient;
    private XmlRpcClient objectClient;

    public OdooService() {
        loadConfiguration();
        try {
            initializeClients();
        } catch (MalformedURLException e) {
            logger.error("Error al inicializar clientes Odoo", e);
        }
    }

    /**
     * Carga la configuración desde application.properties
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                logger.error("No se encuentra application.properties");
                return;
            }

            properties.load(input);

            this.url = properties.getProperty("odoo.url", "http://localhost:8069");
            this.database = properties.getProperty("odoo.database", "odoo_db");
            this.username = properties.getProperty("odoo.username", "admin@empresa.com");
            this.password = properties.getProperty("odoo.password", "admin");

            logger.info("Configuración de Odoo cargada: {}, DB: {}", url, database);

        } catch (IOException e) {
            logger.error("Error al cargar configuración", e);
        }
    }

    /**
     * Inicializa los clientes XML-RPC
     */
    private void initializeClients() throws MalformedURLException {
        // Cliente para autenticación
        XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
        commonConfig.setServerURL(new URL(url + "/xmlrpc/2/common"));
        commonClient = new XmlRpcClient();
        commonClient.setConfig(commonConfig);

        // Cliente para operaciones de objetos
        XmlRpcClientConfigImpl objectConfig = new XmlRpcClientConfigImpl();
        objectConfig.setServerURL(new URL(url + "/xmlrpc/2/object"));
        objectClient = new XmlRpcClient();
        objectClient.setConfig(objectConfig);
    }

    /**
     * Autentica con Odoo y obtiene el UID
     */
    public boolean authenticate() {
        try {
            logger.info("Autenticando con Odoo...");

            Object result = commonClient.execute("authenticate", Arrays.asList(
                    database, username, password, Collections.emptyMap()
            ));

            if (result instanceof Integer) {
                this.uid = (Integer) result;
                logger.info("Autenticación exitosa. UID: {}", uid);
                return true;
            } else {
                logger.error("Autenticación fallida");
                return false;
            }

        } catch (XmlRpcException e) {
            logger.error("Error al autenticar con Odoo", e);
            return false;
        }
    }

    /**
     * Obtiene la lista de facturas de clientes (out_invoice)
     */
    public List<Factura> obtenerFacturas() {
        List<Factura> facturas = new ArrayList<>();

        if (uid == null && !authenticate()) {
            logger.error("No se pudo autenticar");
            return facturas;
        }

        try {
            logger.info("Obteniendo facturas de Odoo...");

            // Filtro para obtener solo facturas de cliente confirmadas
            List<Object> domain = Arrays.asList(
                    Arrays.asList("move_type", "=", "out_invoice"),
                    Arrays.asList("state", "=", "posted")
            );

            // Campos a obtener
            Map<String, Object> kwargs = new HashMap<>();
            kwargs.put("fields", Arrays.asList(
                    "id", "name", "partner_id", "invoice_date",
                    "amount_total", "state"
            ));

            // Ejecutar búsqueda
            Object[] result = (Object[]) objectClient.execute("execute_kw", Arrays.asList(
                    database, uid, password,
                    "account.move", "search_read",
                    Arrays.asList(domain),
                    kwargs
            ));

            logger.info("Se obtuvieron {} facturas de Odoo", result.length);

            // Convertir resultados a objetos Factura
            for (Object obj : result) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) obj;
                    Factura factura = mapToFactura(map);
                    facturas.add(factura);
                }
            }

        } catch (XmlRpcException e) {
            logger.error("Error al obtener facturas", e);
        }

        return facturas;
    }

    /**
     * Busca una factura específica por ID
     */
    public Factura obtenerFacturaPorId(int facturaId) {
        if (uid == null && !authenticate()) {
            return null;
        }

        try {
            List<Object> domain = Arrays.asList(
                    Arrays.asList("id", "=", facturaId)
            );

            Map<String, Object> kwargs = new HashMap<>();
            kwargs.put("fields", Arrays.asList(
                    "id", "name", "partner_id", "invoice_date",
                    "amount_total", "state"
            ));

            Object[] result = (Object[]) objectClient.execute("execute_kw", Arrays.asList(
                    database, uid, password,
                    "account.move", "search_read",
                    Arrays.asList(domain),
                    kwargs
            ));

            if (result.length > 0 && result[0] instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result[0];
                return mapToFactura(map);
            }

        } catch (XmlRpcException e) {
            logger.error("Error al obtener factura por ID", e);
        }

        return null;
    }

    /**
     * Convierte un Map de Odoo a objeto Factura
     */
    private Factura mapToFactura(Map<String, Object> map) {
        Factura factura = new Factura();

        // ID
        factura.setId((Integer) map.get("id"));

        // Número de factura
        factura.setNumeroFactura((String) map.get("name"));

        // Cliente (partner_id es un array [id, nombre])
        Object[] partnerId = (Object[]) map.get("partner_id");
        if (partnerId != null && partnerId.length >= 2) {
            factura.setClienteId((Integer) partnerId[0]);
            factura.setCliente((String) partnerId[1]);
        }

        // Fecha
        String fechaStr = (String) map.get("invoice_date");
        if (fechaStr != null && !fechaStr.isEmpty()) {
            try {
                factura.setFecha(LocalDate.parse(fechaStr, DateTimeFormatter.ISO_DATE));
            } catch (Exception e) {
                logger.warn("Error al parsear fecha: {}", fechaStr);
            }
        }

        // Importe total
        Object amountObj = map.get("amount_total");
        if (amountObj instanceof Double) {
            factura.setImporteTotal((Double) amountObj);
        } else if (amountObj instanceof Integer) {
            factura.setImporteTotal(((Integer) amountObj).doubleValue());
        }

        // Estado
        factura.setEstado((String) map.get("state"));

        return factura;
    }

    /**
     * Verifica la conexión con Odoo
     */
    public boolean testConnection() {
        try {
            Object version = commonClient.execute("version", Collections.emptyList());
            logger.info("Versión de Odoo: {}", version);
            return true;
        } catch (XmlRpcException e) {
            logger.error("Error al conectar con Odoo", e);
            return false;
        }
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public String getDatabase() {
        return database;
    }

    public Integer getUid() {
        return uid;
    }
}