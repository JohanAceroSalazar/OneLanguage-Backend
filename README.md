# OneLanguage Backend

Backend del proyecto **One Language**, desarrollado con Spring Boot y organizado por módulos funcionales. Proporciona autenticación, usuarios, preferencias de accesibilidad, permisos funcionales, traducciones e historial con grabaciones opcionales.

## Tecnologías

- Java 17.
- Spring Boot 4.0.7.
- Spring Web, Validation, Security, Data JPA, Actuator y Mail.
- PostgreSQL, Hibernate y ddl-auto=validate.
- JWT con JJWT 0.12.7.
- MapStruct, Lombok, OpenFeign y Springdoc OpenAPI.
- Maven y Docker.

## Estructura

~~~text
OneLanguage-Backend/
├── Backend-OneLanguage/
│   ├── pom.xml
│   └── src/main/java/com/sena/Backend_OneLanguage/
│       ├── auth, users, passwordreset
│       ├── permissions, accessibility, translation
│       ├── security, email y config
│       └── src/test/
├── Dockerfile
├── docker-compose.yml
└── .env.example
~~~

Los módulos separan controladores, DTO, servicios, repositorios y entidades cuando aplica. GlobalExceptionHandler centraliza errores; JwtAuthenticationFilter, JwtService y CustomUserDetailsService implementan la autenticación.

Los módulos de autenticación, usuarios, recuperación de contraseña, permisos, accesibilidad y traducción ya tienen código. La evolución prevista puede ampliar history, roles, notifications y otras capacidades de negocio; no se documentan como endpoints disponibles mientras no exista implementación.

## Requisitos y configuración

Se requiere JDK 17, Maven (o mvnw), PostgreSQL migrado por OneLanguage-Database y un servidor SMTP para recuperación de contraseña.

Copiar .env.example a .env y definir valores reales:

~~~dotenv
SERVER_PORT=8084
SERVER_ADDRESS=0.0.0.0
CORS_ALLOWED_ORIGINS=https://localhost:5173,https://127.0.0.1:5173
APP_FRONTEND_BASE_URL=https://localhost:5173
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5438/proyect_onelanguage
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
MAIL_USERNAME=
MAIL_PASSWORD=
APP_MEDIA_STORAGE_PATH=uploads/translation-recordings
~~~

No publicar contraseñas, tokens ni credenciales. Las grabaciones se almacenan fuera de PostgreSQL en APP_MEDIA_STORAGE_PATH.

## Ejecución y pruebas

~~~bash
cd Backend-OneLanguage
./mvnw spring-boot:run
./mvnw test
./mvnw clean package
~~~

En Windows:

~~~powershell
mvnw.cmd spring-boot:run
mvnw.cmd test
mvnw.cmd clean package
~~~

Con Docker, PostgreSQL debe estar disponible primero:

~~~bash
docker compose up --build
~~~

El contenedor publica el puerto 8084 y monta el volumen translation_media en /app/uploads.

## API implementada

Las rutas protegidas requieren Authorization: Bearer <token>, salvo registro, login y recuperación.

| Método | Ruta | Propósito |
|---|---|---|
| POST | /api/users | Registrar usuario |
| GET | /api/users/me | Obtener el usuario autenticado |
| POST | /auth/login | Iniciar sesión y obtener JWT |
| POST | /auth/forgot-password | Solicitar recuperación |
| POST | /auth/reset-password | Restablecer contraseña |
| GET | /api/feature-permissions | Consultar permisos |
| PUT | /api/feature-permissions | Actualizar un permiso |
| GET | /api/accessibility-settings | Consultar accesibilidad |
| PUT | /api/accessibility-settings | Actualizar accesibilidad |
| POST | /api/translations | Crear traducción |
| POST | /api/translations/with-recording | Crear traducción multipart con grabación |
| GET | /api/translations | Listar traducciones propias |
| GET | /api/translations/{translationId}/recording | Obtener grabación propia |
| DELETE | /api/translations/{translationId} | Eliminar una traducción |
| DELETE | /api/translations | Eliminar todas las traducciones |

La operación multipart usa recording, translatedText y confidence opcional. Las grabaciones solo son accesibles para su propietario.

### Permisos funcionales

GET y PUT /api/feature-permissions trabajan únicamente con camera y audio. El permiso files fue eliminado y no debe enviarse ni documentarse como disponible.

## Seguridad, correo y persistencia

Spring Security valida JWT, con expiración configurada por jwt.expiration. CORS se controla con CORS_ALLOWED_ORIGINS. La recuperación usa APP_FRONTEND_BASE_URL y SMTP. Las entidades principales cubren usuarios, permisos, accesibilidad, traducciones y tokens de recuperación; las grabaciones se sirven desde almacenamiento externo.

## Estado

Está implementado el flujo de autenticación, perfil, recuperación, accesibilidad, permisos camera/audio, traducciones, historial, grabaciones opcionales, validación y pruebas automatizadas.

La disponibilidad de reconocimiento depende del servicio de IA; el despliegue requiere SMTP válido, PostgreSQL migrado y configuración coordinada con los frontends.

