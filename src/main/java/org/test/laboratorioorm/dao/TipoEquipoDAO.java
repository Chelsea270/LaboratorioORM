package org.test.laboratorioorm.dao;

import jakarta.persistence.EntityManager;
import org.test.laboratorioorm.database.JPAUtil;
import org.test.laboratorioorm.model.TipoEquipo;

import java.util.List;

public class TipoEquipoDAO {
    // Lista todos los tipos para llenar los ComboBox
    public static List<TipoEquipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoEquipo t ORDER BY t.nombre",
                    TipoEquipo.class).getResultList();
        } finally {
            em.close();
        }
    }
}

