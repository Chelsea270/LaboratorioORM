package org.test.laboratorioorm.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory; // puede ser un metodo sincronized.

    // metodo declarado de variables de entorno. buenas practicas

    public static synchronized void iniciar() {
        if (entityManagerFactory == null) {
            String password = System.getenv("DB_PASSWORD");
            // en caso de null se devuelve invalido.

            if (password == null || password.isEmpty()) {
                throw new RuntimeException("BD PASSWORD invalidoo");
            }
            // hashmap = llave valor.
            // metodo usado por la llave de entorno, llave valor = mas 1 valores

            Map<String, Object> propiedades = new HashMap<String, Object>();
            propiedades.put("jakarta.persistence.jdbc.password", password);
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    "laboratoriopu", propiedades);
        }
    }

    public static EntityManager getEntityManager() {
        iniciar();
        return entityManagerFactory.createEntityManager();
    }

    public static void fecharEntityManager() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
