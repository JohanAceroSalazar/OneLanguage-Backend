# Registro de rotación de credenciales — OneLanguage Backend

**Estado:** Completado  
**Fecha de documentación:** 2026-11-03  
**Hallazgo relacionado:** H-01 — Exposición de credenciales en el backend

## 1. Objetivo

Dejar constancia de la rotación de las credenciales que se encontraban expuestas en la configuración del backend, sin registrar, reproducir ni almacenar sus valores secretos en este documento.

## 2. Acciones realizadas

Se completaron las siguientes rotaciones:

- **JWT:** se reemplazó la clave utilizada para firmar los tokens por una nueva. La aplicación obtiene la clave mediante una variable de entorno (`JWT_SECRET`), en lugar de mantener el valor secreto directamente en el archivo de configuración.
- **Gmail SMTP:** se cambió la contraseña de aplicación utilizada para el envío de correos. La configuración consume la credencial desde la variable de entorno correspondiente (`MAIL_PASSWORD`).
- **PostgreSQL:** se cambió la contraseña del usuario de base de datos utilizada por el backend. La conexión obtiene este dato desde la variable de entorno (`DB_PASSWORD` o la variable equivalente definida en la configuración de despliegue).

También se ajustó la configuración versionada para que no contenga los valores reales de estas credenciales. El archivo `.env` se mantiene fuera del control de versiones y el archivo `.env.example` contiene únicamente nombres de variables y valores de ejemplo no funcionales.

## 3. Verificaciones

Como parte de la revisión se comprobó que:

- Los archivos de configuración vigentes utilizan variables de entorno para las credenciales.
- Los ejemplos de configuración no incluyen los valores secretos reales.
- El documento de esta rotación no contiene claves JWT, contraseñas SMTP ni contraseñas de PostgreSQL.
- Las credenciales anteriores fueron reemplazadas y no deben volver a utilizarse.

## 4. Resultado

**H-01: rotación de credenciales completada.**

Las credenciales JWT, Gmail SMTP y PostgreSQL fueron cambiadas. La configuración vigente utiliza variables de entorno y este registro no expone ninguno de los valores secretos.

Esta constancia documenta la remediación aplicada al backend. No afirma que se hayan eliminado todos los objetos o referencias históricas administrados por GitHub; cualquier rastro histórico debe distinguirse de la configuración vigente y de la validez de las credenciales, que fueron rotadas.

## 5. Tratamiento de información sensible

Por seguridad, no se incluyen en este documento:

- La clave JWT anterior ni la nueva.
- La contraseña de aplicación de Gmail anterior ni la nueva.
- La contraseña anterior ni la nueva de PostgreSQL.
- Archivos `.env` ni capturas que muestren valores secretos.

Los valores reales se administran únicamente mediante el mecanismo privado de configuración del entorno de ejecución.
