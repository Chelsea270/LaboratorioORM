# LaboratorioORM – Control de equipos de laboratorio

Aplicación de escritorio en **JavaFX** que administra el catálogo de equipos de un laboratorio de informática y los clasifica por tipo. Los datos se guardan en **PostgreSQL** mediante **Hibernate ORM**.

**Asignatura:** Programación de Aplicaciones de Escritorio  
**Autora:** Chelsea Quintanilla

---

## Requisitos

- JDK 21
- IntelliJ IDEA
- Maven (incluido con `mvnw`)
- PostgreSQL 18 (o compatible) y pgAdmin

**Dependencias principales (`pom.xml`):**
- JavaFX 21.0.6 (`javafx-controls`, `javafx-fxml`)
- Hibernate ORM 7.4.12.Final (`org.hibernate.orm:hibernate-core`)
- PostgreSQL JDBC Driver 42.7.14

---

## Configuración de PostgreSQL

1. Abrir pgAdmin y conectarse a la base `postgres`.
2. Abrir el archivo **`bd_laboratorio_orm.sql`** y ejecutar solo el **PASO 1** (crea la base `bd_laboratorio_orm`).
3. Refrescar, abrir un Query Tool sobre **`bd_laboratorio_orm`** y ejecutar desde el **PASO 2** hasta el final (crea el esquema `inventario`, las tablas, restricciones y los datos iniciales: 3 tipos y 8 equipos).

**Criterio de administración del esquema:** la estructura se creó **manualmente con SQL**. Hibernate está configurado con `hibernate.hbm2ddl.auto = validate`, por lo que no crea ni modifica tablas: solo verifica que las entidades coincidan con la base de datos.

---

## Configuración de la conexión

La unidad de persistencia está en `src/main/resources/META-INF/persistence.xml`:

| Propiedad | Valor |
|---|---|
| persistence-unit | `laboratoriopu` |
| Proveedor | `org.hibernate.jpa.HibernatePersistenceProvider` |
| URL JDBC | `jdbc:postgresql://localhost:5432/bd_laboratorio_orm` |
| Usuario | `postgres` |
| Contraseña | Variable de entorno `DB_PASSWORD` |

La contraseña **no está en el código ni en el repositorio**. La clase `JPAUtil` la lee con `System.getenv("DB_PASSWORD")` y se la pasa a Hibernate al crear el `EntityManagerFactory`.

---

## Cómo ejecutar la aplicación

1. Clonar el repositorio y abrirlo en IntelliJ IDEA.
2. Cargar las dependencias de Maven (**Load Maven Changes**).
3. Ir a **Run → Edit Configurations…**, crear o seleccionar una configuración de tipo *Application* con la clase principal **`org.test.laboratorioorm.Launcher`**.
4. En **Environment variables** agregar:
