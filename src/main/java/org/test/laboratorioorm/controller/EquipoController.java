package org.test.laboratorioorm.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.test.laboratorioorm.dao.EquipoDAO;
import org.test.laboratorioorm.dao.TipoEquipoDAO;
import org.test.laboratorioorm.model.Equipo;
import org.test.laboratorioorm.model.TipoEquipo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EquipoController {

    @FXML private Button btnActualizar;
    @FXML private Button btnBuscar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnMostrarTodos;

    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;
    @FXML private ComboBox<TipoEquipo> cmbTipo;

    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colTipo;

    @FXML private TableView<Equipo> tblEquipos;

    @FXML private TextField txtBuscar;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtNombre;

    private static final List<String> ESTADOS =
            List.of("Disponible", "En mantenimiento", "Fuera de servicio");

    private Long idSeleccionado = null;

    @FXML
    public void initialize() {
        // Columnas simples: usan los getters de Equipo
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipoEquipo().getNombre()));
        cmbEstado.setItems(FXCollections.observableArrayList(ESTADOS));
        cargarTipos();

        tblEquipos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, equipo) -> {
            if (equipo != null) {
                idSeleccionado = equipo.getId();
                txtCodigo.setText(equipo.getCodigo());
                txtNombre.setText(equipo.getNombre());
                txtMarca.setText(equipo.getMarca());
                txtCantidad.setText(String.valueOf(equipo.getCantidad()));
                cmbTipo.setValue(equipo.getTipoEquipo());
                cmbEstado.setValue(equipo.getEstado());
            }
            actualizarBotones();
        });

        mostrarTodos();
        actualizarBotones();
    }

    private void cargarTipos() {
        try {
            List<TipoEquipo> tipos = TipoEquipoDAO.listar();
            cmbTipo.setItems(FXCollections.observableArrayList(tipos));
            cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los tipos de equipo.\n" + e.getMessage());
        }
    }

    // Recarga la tabla respetando la búsqueda y el filtro actuales
    private void refrescarTabla() {
        try {
            List<Equipo> equipos = EquipoDAO.buscar(txtBuscar.getText(), cmbFiltroTipo.getValue());
            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los equipos.\n" + e.getMessage());
        }
    }

    @FXML
    private void guardarEquipo() {
        Equipo equipo = obtenerEquipoFormulario();
        if (equipo == null) {
            return; // la validación ya mostró el Alert
        }
        try {
            EquipoDAO.guardar(equipo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo registrado correctamente.");
            limpiarCampos();
            refrescarTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar", traducirError(e));
        }
    }

    @FXML
    private void actualizarEquipo() {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un equipo de la tabla.");
            return;
        }
        Equipo equipo = obtenerEquipoFormulario();
        if (equipo == null) {
            return;
        }
        equipo.setId(idSeleccionado);
        try {
            boolean actualizado = EquipoDAO.actualizar(equipo);
            if (!actualizado) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El equipo ya no existe en la base de datos.");
                return;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo actualizado correctamente.");
            limpiarCampos();
            refrescarTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar", traducirError(e));
        }
    }

    @FXML
    private void eliminarEquipo() {
        Equipo seleccionado = tblEquipos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un equipo de la tabla.");
            return;
        }

        // Confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar el equipo \""
                + seleccionado.getNombre() + "\" (" + seleccionado.getCodigo() + ")?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminado = EquipoDAO.eliminar(seleccionado.getId());
            if (!eliminado) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El equipo ya no existe en la base de datos.");
                return;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Equipo eliminado correctamente.");
            limpiarCampos();
            refrescarTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar", traducirError(e));
        }
    }

    @FXML
    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        tblEquipos.getSelectionModel().clearSelection();
        idSeleccionado = null;
        actualizarBotones();
    }

    @FXML
    private void aplicarFiltros() {
        refrescarTabla();
    }

    @FXML
    private void mostrarTodos() {
        txtBuscar.clear();
        cmbFiltroTipo.setValue(null);
        try {
            List<Equipo> equipos = EquipoDAO.listar();
            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los equipos.\n" + e.getMessage());
        }
    }

    // Lee el formulario. Si algo es inválido, muestra un Alert y devuelve null.
    private Equipo obtenerEquipoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        String cantidadTexto = txtCantidad.getText().trim();
        TipoEquipo tipo = cmbTipo.getValue();
        String estado = cmbEstado.getValue();

        if (codigo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "El código es obligatorio.");
            return null;
        }
        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "El nombre es obligatorio.");
            return null;
        }
        if (marca.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La marca es obligatoria.");
            return null;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La cantidad debe ser un número entero.");
            return null;
        }
        if (cantidad < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La cantidad no puede ser negativa.");
            return null;
        }

        if (tipo == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Seleccione un tipo de equipo.");
            return null;
        }
        if (estado == null || !ESTADOS.contains(estado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Seleccione un estado válido.");
            return null;
        }

        return new Equipo(cantidad, estado, marca, nombre, codigo, tipo);
    }

    // Convierte el error técnico de PostgreSQL en un mensaje entendible
    private String traducirError(Exception e) {
        Throwable causa = e;
        while (causa != null) {
            if (causa instanceof SQLException sql) {
                if ("23505".equals(sql.getSQLState())) {
                    return "Ya existe un equipo con ese código. No se guardaron cambios.";
                }
                if ("23514".equals(sql.getSQLState())) {
                    return "Los datos no cumplen una regla de la base de datos (cantidad o estado). No se guardaron cambios.";
                }
                if ("23503".equals(sql.getSQLState())) {
                    return "El tipo de equipo seleccionado no existe. No se guardaron cambios.";
                }
            }
            causa = causa.getCause();
        }
        return "Ocurrió un error inesperado. No se guardaron cambios.\n" + e.getMessage();
    }


    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnActualizar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
        btnGuardar.setDisable(!sinSeleccion);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}