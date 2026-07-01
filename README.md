# OneLanguage-Backend

Backend del proyecto **OneLanguage**, desarrollado en **Spring Boot** y orientado a una arquitectura **by module**.
Este repositorio se encarga de la lógica del servidor, la conexión con PostgreSQL y la preparación de la base para trabajar con el módulo de usuarios.

## Objetivo del backend

El backend centraliza:

- La conexión con la base de datos.
- La persistencia de entidades con JPA/Hibernate.
- La estructura por módulos del negocio.
- La futura exposición de endpoints REST para autenticación, usuarios y demás funcionalidades del proyecto.

## Arquitectura by module

La organización del backend está pensada por módulos funcionales, no por capas mezcladas en un solo paquete grande.
Eso hace más fácil crecer el proyecto sin desordenarlo.

La idea es que cada módulo represente una parte clara del negocio de OneLanguage.

### Módulos actuales

- `config`

  - Contiene configuraciones transversales del backend.
  - Actualmente incluye un verificador de conexión con la base de datos.
- `users`

  - Contiene todo lo relacionado con el módulo de usuarios.
  - Incluye la entidad JPA y el repositorio para acceder a la tabla `users`.

### Módulos planificados

Estos módulos todavía no tienen código, pero ya están pensados para el crecimiento del proyecto:

- `auth`

  - Se encargará de inicio de sesión, registro, cierre de sesión y tokens JWT.
  - También puede manejar recuperación de contraseña y renovación de sesión.
  - En un proyecto como OneLanguage, este módulo es clave para identificar quién entra a la plataforma y qué permisos tiene.
- `history`

  - Guardará el historial de uso de la plataforma.
  - Puede registrar acciones como inicios de sesión, traducciones realizadas, intentos fallidos, cambios de perfil o actividad del usuario.
  - Sirve para auditoría, trazabilidad y seguimiento del uso del sistema.
- `translations`

  - Se orientará a la lógica principal del proyecto: traducción de señas.
  - Puede manejar la entrada de datos, interpretación de señas, resultados traducidos y almacenamiento de traducciones realizadas.
  - Este módulo sería uno de los más importantes porque representa la funcionalidad central de OneLanguage.
- `gestures`

  - Puede almacenar catálogos de señas, movimientos o patrones reconocidos.
  - También puede servir para clasificar señas por categorías, letras, palabras o frases.
  - Es útil si después quieres entrenar, consultar o mejorar el reconocimiento de señas.
- `roles`

  - Administrará los tipos de acceso del sistema.
  - Por ejemplo: administrador, usuario, moderador o docente.
  - Permite controlar qué puede hacer cada persona dentro de la aplicación.
- `permissions`

  - Complementa a `roles` para definir acciones más específicas.
  - Por ejemplo: crear usuario, editar traducciones, ver historial o administrar contenido.
  - Es útil si quieres una seguridad más fina dentro del backend.
- `notifications`

  - Puede manejar mensajes internos, alertas o notificaciones por correo.
  - Sirve para avisos como registro exitoso, cambios de contraseña o confirmaciones.

### Cómo se divide internamente

Cada módulo puede crecer con sus propias partes:

- `entity`

  - Representa las tablas de la base de datos.
- `repository`

  - Permite consultar y guardar datos en PostgreSQL.
- `service`

  - Aquí iría la lógica de negocio.
- `controller`

  - Aquí irían los endpoints REST.
- `dto`

  - Sirve para recibir y enviar datos sin exponer directamente las entidades.

En este momento el proyecto ya tiene:

- `entity`
- `repository`
- `config`

y más adelante se pueden agregar `service`, `controller` y `dto` por cada módulo.

## Estructura del repositorio

```text
OneLanguage-Backend/
├── Backend-OneLanguage/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sena/Backend_OneLanguage/
│   │   │   │   ├── BackendOneLanguageApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── DatabaseConnectionVerifier.java
│   │   │   │   └── users/
│   │   │   │       ├── entity/
│   │   │   │       │   └── UserEntity.java
│   │   │   │       └── repository/
│   │   │   │           └── UserRepository.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/com/sena/Backend_OneLanguage/
│   │           └── BackendOneLanguageApplicationTests.java
│   └── docker-compose.yml
├── Dockerfile
└── README.md
```

## Tecnologías usadas

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Web
- PostgreSQL
- Liquibase en el repositorio de base de datos
- Docker

## Base de datos

El backend se conecta a PostgreSQL usando estas credenciales por defecto:

- Base de datos: `proyect_onelanguage`
- Usuario: `postgres`
- Contraseña: `Johan2509`
- Puerto local expuesto: `5438`

La tabla principal que ya está creada es:

- `users`

### Mapeo de la entidad `users`

La entidad `UserEntity` representa la tabla `users`:

- `id_user`
- `email`
- `full_name`
- `password_hash`
- `user_status`
- `failed_attempts`
- `locked_until`
- `last_access`
- `created_at`
- `updated_at`
- `deleted_at`

## Configuración del backend

### `application.properties`

Archivo:

- [`Backend-OneLanguage/src/main/resources/application.properties`](./Backend-OneLanguage/src/main/resources/application.properties)

Responsabilidades:

- Define el nombre de la aplicación.
- Expone el puerto `8084`.
- Configura el datasource de PostgreSQL.
- Activa `ddl-auto=validate` para que Hibernate verifique que la estructura de la tabla coincide con la entidad.
- Configura la zona horaria en UTC.

### Verificador de conexión

Archivo:

- [`Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/config/DatabaseConnectionVerifier.java`](./Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/config/DatabaseConnectionVerifier.java)

Este componente se ejecuta al iniciar el backend y consulta el conteo de registros de `users`.
Sirve para confirmar que la conexión con la base quedó bien configurada.

## Clase principal

Archivo:

- [`Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/BackendOneLanguageApplication.java`](./Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/BackendOneLanguageApplication.java)

Es el punto de entrada de Spring Boot.

## Módulo de usuarios

### Entidad

Archivo:

- [`Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/users/entity/UserEntity.java`](./Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/users/entity/UserEntity.java)

Esta clase mapea la tabla `users` y define:

- la clave primaria UUID,
- el correo,
- el nombre completo,
- el hash de contraseña,
- el estado del usuario,
- intentos fallidos,
- bloqueo temporal,
- último acceso,
- fecha de creación,
- fecha de actualización,
- eliminación lógica.

Además:

- genera un UUID si no viene uno asignado,
- inicializa campos base en `@PrePersist`,
- actualiza `updatedAt` en `@PreUpdate`.

### Repositorio

Archivo:

- [`Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/users/repository/UserRepository.java`](./Backend-OneLanguage/src/main/java/com/sena/Backend_OneLanguage/users/repository/UserRepository.java)

Permite:

- consultar usuarios por `id`,
- buscar usuarios por `email`.

## Docker

### Dockerfile

Archivo:

- [`Dockerfile`](./Dockerfile)

El `Dockerfile` usa dos etapas:

1. Compila el proyecto con Maven dentro de un contenedor.
2. Ejecuta el `.jar` generado en una imagen ligera de Java 17.

Esto evita depender de un JAR compilado manualmente en tu máquina.

### docker-compose del backend

Archivo:

- [`docker-compose.yml`](./docker-compose.yml)

Este archivo levanta solo el backend y espera conectarse a la base de datos que corre en el otro repositorio.

#### Qué hace

- Construye la imagen del backend con `build: .`
- Expone el puerto `8084`
- Inyecta las variables de entorno de Spring:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

#### Punto importante

La URL apunta a:

```text
jdbc:postgresql://host.docker.internal:5438/proyect_onelanguage
```

Eso significa que:

- el contenedor del backend se conecta a un Postgres que ya debe estar arriba,
- ese Postgres está expuesto en el puerto `5438` en tu máquina local,
- en Windows normalmente `host.docker.internal` funciona bien para llegar al host desde Docker.

## Docker Compose de la base de datos

Ese archivo está en el repositorio:

- `OneLanguage-Database/docker-compose.yml`

Allí se levanta:

- `postgres`
- `liquibase`

### Qué hace Postgres

- crea la base `proyect_onelanguage`,
- expone el puerto `5438`,
- conserva los datos en un volumen llamado `postgres_data`.

### Qué hace Liquibase

- aplica los scripts de migración,
- crea extensiones,
- crea tablas, vistas, funciones y demás objetos del esquema.

### Ajuste importante

Se agregó `healthcheck` para que Liquibase espere a que PostgreSQL esté listo antes de correr.
Eso evita errores de arranque por carreras de inicio entre contenedores.

## Cómo ejecutar

### 1. Levantar la base de datos

En el repositorio `OneLanguage-Database`:

```bash
docker compose up -d
```

### 2. Levantar el backend

En este repositorio `OneLanguage-Backend`:

```bash
docker compose up --build
```

### 3. Verificar conexión

Si todo queda bien, el backend debería iniciar sin errores y registrar en consola que la conexión a la base de datos fue exitosa.

## Pruebas

Archivo:

- [`Backend-OneLanguage/src/test/java/com/sena/Backend_OneLanguage/BackendOneLanguageApplicationTests.java`](./Backend-OneLanguage/src/test/java/com/sena/Backend_OneLanguage/BackendOneLanguageApplicationTests.java)

Actualmente solo valida que el contexto de Spring arranque correctamente.

## Estado actual del backend

Lo que ya está listo:

- estructura base del proyecto,
- conexión a PostgreSQL,
- entidad `users`,
- repositorio de usuarios,
- verificación de conexión,
- configuración para Docker.

Lo que todavía falta construir:

- controladores REST,
- servicios de negocio,
- autenticación y autorización completa,
- DTOs,
- validaciones de entrada,
- endpoints para registro, login y gestión de usuarios.

También faltan por crear los módulos planificados como `auth`, `history`, `translations`, `gestures`, `roles`, `permissions` y `notifications`.

## Siguiente paso recomendado

Lo más lógico ahora es continuar con:

1. `service` del módulo `users`
2. `controller` del módulo `users`
3. endpoints de registro y consulta
4. login con JWT

