package org.test.laboratorioorm.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.test.laboratorioorm.database.JPAUtil;
import org.test.laboratorioorm.model.Equipo;
import org.test.laboratorioorm.model.TipoEquipo;

import java.util.List;

public class EquipoDAO {
    // CREAR
    public static void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Busca el tipo real en la base y lo vincula al equipo
            TipoEquipo tipo = em.find(TipoEquipo.class, equipo.getTipoEquipo().getId());
            equipo.setTipoEquipo(tipo);
            em.persist(equipo);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // LEER
    public static List<Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Equipo e ORDER BY e.id",
                    Equipo.class).getResultList();
        } finally {
            em.close();
        }
    }

    // ACTUALIZAR
    public static boolean actualizar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, equipo.getId());
            if (equipoBD == null) {
                tx.rollback();
                return false;
            }
            TipoEquipo tipo = em.find(TipoEquipo.class, equipo.getTipoEquipo().getId());

            equipoBD.setCodigo(equipo.getCodigo());
            equipoBD.setNombre(equipo.getNombre());
            equipoBD.setMarca(equipo.getMarca());
            equipoBD.setEstado(equipo.getEstado());
            equipoBD.setCantidad(equipo.getCantidad());
            equipoBD.setTipoEquipo(tipo);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // ELIMINAR
    public static boolean eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipoBD = em.find(Equipo.class, id);
            if (equipoBD == null) {
                tx.rollback();
                return false;
            }
            em.remove(equipoBD);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // CONSULTAR: búsqueda por nombre parcial y/o filtro por tipo
    public static List<Equipo> buscar(String nombre, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Equipo e WHERE 1 = 1";

            boolean hayNombre = nombre != null && !nombre.isBlank();
            boolean hayTipo = tipo != null;

            if (hayNombre) {
                jpql += " AND LOWER(e.nombre) LIKE :nombre";
            }
            if (hayTipo) {
                jpql += " AND e.tipoEquipo.id = :idTipo";
            }
            jpql += " ORDER BY e.id";

            TypedQuery<Equipo> query = em.createQuery(jpql, Equipo.class);

            if (hayNombre) {
                query.setParameter("nombre", "%" + nombre.trim().toLowerCase() + "%");
            }
            if (hayTipo) {
                query.setParameter("idTipo", tipo.getId());
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}


