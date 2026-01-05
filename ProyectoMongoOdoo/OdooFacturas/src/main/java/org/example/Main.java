package org.example;

import org.example.model.Factura;
import org.example.model.Garantia;
import org.example.model.Usuario;
import org.example.service.AuthService;
import org.example.service.MongoService;
import org.example.service.OdooService;
import org.example.util.MongoDBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Clase principal para probar las conexiones y servicios
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== INICIANDO PRUEBAS DEL SISTEMA ===");

        // Probar MongoDB
        probarMongoDB();

        // Probar Autenticación
        probarAutenticacion();

        // Probar Odoo
        probarOdoo();

        // Probar Garantías
        probarGarantias();

        logger.info("=== PRUEBAS COMPLETADAS ===");
    }

    private static void probarMongoDB() {
        logger.info("\n--- PROBANDO MONGODB ---");
        try {
            boolean conectado = MongoDBConnection.isConnected();
            if (conectado) {
                logger.info("✅ MongoDB: CONECTADO");
            } else {
                logger.error("❌ MongoDB: NO CONECTADO");
            }
        } catch (Exception e) {
            logger.error("❌ Error al conectar con MongoDB", e);
        }
    }

    private static void probarAutenticacion() {
        logger.info("\n--- PROBANDO AUTENTICACIÓN ---");
        try {
            AuthService authService = new AuthService();

            // Probar login exitoso
            Usuario usuario = authService.login("admin", "admin123");
            if (usuario != null) {
                logger.info("✅ Login exitoso: {} ({})", usuario.getNombre(), usuario.getRol());
            } else {
                logger.error("❌ Login fallido");
            }

            // Probar login fallido
            Usuario usuarioFail = authService.login("admin", "wrongpassword");
            if (usuarioFail == null) {
                logger.info("✅ Login fallido correctamente rechazado");
            }

            authService.logout();

        } catch (Exception e) {
            logger.error("❌ Error en autenticación", e);
        }
    }

    private static void probarOdoo() {
        logger.info("\n--- PROBANDO ODOO ---");
        try {
            OdooService odooService = new OdooService();

            // Probar conexión
            boolean conexion = odooService.testConnection();
            if (conexion) {
                logger.info("✅ Odoo: CONECTADO");
            } else {
                logger.error("❌ Odoo: NO CONECTADO");
                return;
            }

            // Autenticar
            boolean auth = odooService.authenticate();
            if (auth) {
                logger.info("✅ Odoo: AUTENTICADO (UID: {})", odooService.getUid());
            } else {
                logger.error("❌ Odoo: AUTENTICACIÓN FALLIDA");
                return;
            }

            // Obtener facturas
            List<Factura> facturas = odooService.obtenerFacturas();
            logger.info("✅ Se obtuvieron {} facturas de Odoo", facturas.size());

            // Mostrar las primeras 3 facturas
            int count = Math.min(3, facturas.size());
            for (int i = 0; i < count; i++) {
                Factura f = facturas.get(i);
                logger.info("   Factura {}: {} - {} - {}€",
                        f.getId(), f.getNumeroFactura(), f.getCliente(), f.getImporteTotal());
            }

        } catch (Exception e) {
            logger.error("❌ Error con Odoo", e);
        }
    }

    private static void probarGarantias() {
        logger.info("\n--- PROBANDO GARANTÍAS ---");
        try {
            MongoService mongoService = new MongoService();

            // Obtener todas las garantías
            List<Garantia> garantias = mongoService.obtenerTodasLasGarantias();
            logger.info("✅ Se obtuvieron {} garantías de MongoDB", garantias.size());

            // Mostrar garantías
            for (Garantia g : garantias) {
                logger.info("   Garantía: {} - {} - Estado: {}",
                        g.getNumeroFactura(), g.getCliente(), g.getEstado());
            }

        } catch (Exception e) {
            logger.error("❌ Error con garantías", e);
        }
    }
}