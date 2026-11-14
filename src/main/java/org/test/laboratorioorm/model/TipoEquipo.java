package org.test.laboratorioorm.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="tipo_equipo", schema = "inventario")
public class TipoEquipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @OneToMany(mappedBy = "tipoEquipo", fetch = FetchType.LAZY)
    private  List<Equipo> equipos =  new ArrayList<>();

    public TipoEquipo() {
    }

    public TipoEquipo(Long id, List<Equipo> equipos, String descripcion, String nombre) {
        this.id = id;
        this.equipos = equipos;
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public TipoEquipo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }


    public List<Equipo> getEquipos() {
        return equipos;
    }

    // El ComboBox muestra lo que devuelve este método
    @Override
    public String toString() {
        return nombre;
    }

    // Dos tipos son iguales si tienen el mismo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoEquipo)) return false;
        TipoEquipo otro = (TipoEquipo) o;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
