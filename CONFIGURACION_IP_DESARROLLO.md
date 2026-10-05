# Configuración de IP para desarrollo local e intranet

## One Language - Backend

Este documento explica qué se debe actualizar cuando cambia la dirección IPv4 del equipo donde se ejecuta **One Language** durante el desarrollo.

La IP es utilizada principalmente para permitir el acceso al frontend desde la red local y para generar correctamente los enlaces enviados por correo durante el proceso de **recuperación de contraseña**.

> Esta configuración corresponde al entorno de desarrollo. No debe utilizarse `mkcert` como solución de certificados para producción.

---

## 1. ¿Cuándo es necesario realizar este procedimiento?

Este procedimiento debe realizarse cuando la dirección IPv4 utilizada para acceder a One Language desde la red local haya cambiado.

Por ejemplo, si anteriormente se utilizaba:

```text
192.168.56.1
```

y posteriormente el equipo recibe otra dirección, será necesario actualizar la configuración.

Algunos síntomas de una IP desactualizada pueden ser:

- El frontend abre en `localhost`, pero no desde otro equipo de la red.
- El enlace de recuperación enviado al correo apunta a una IP antigua.
- El navegador muestra un error relacionado con el certificado HTTPS al acceder mediante la nueva IP.
- El backend rechaza solicitudes del frontend por CORS.
- El enlace recibido por correo no permite abrir correctamente la página de restablecimiento de contraseña.

---

## 2. Identificar la IP actual

En Windows, abrir PowerShell y ejecutar:

```powershell
ipconfig
```

También se pueden visualizar únicamente las IPv4 con:

```powershell
ipconfig | Select-String "IPv4"
```

Si aparecen varias direcciones, no se debe asumir automáticamente que cualquiera de ellas es la correcta.

Para identificar los adaptadores de red puede utilizarse:

```powershell
Get-NetIPConfiguration | Where-Object {$_.IPv4Address} | Format-List InterfaceAlias,InterfaceDescription,IPv4Address,IPv4DefaultGateway
```

Se debe identificar la IPv4 correspondiente a la interfaz desde la cual se está accediendo a One Language.

---

# 3. Configuración del Backend

## Archivo `.env`

En el repositorio del backend existe un archivo local:

```text
.env
```

Este archivo contiene la configuración real utilizada durante el desarrollo y **no debe subirse al repositorio**.

Cuando cambie la IP se deben revisar principalmente estas variables:

```env
CORS_ALLOWED_ORIGINS=https://localhost:5173,https://127.0.0.1:5173,https://<NUEVA_IP>:5173
APP_FRONTEND_BASE_URL=https://<NUEVA_IP>:5173
```

Por ejemplo:

```env
CORS_ALLOWED_ORIGINS=https://localhost:5173,https://127.0.0.1:5173,https://192.168.56.1:5173
APP_FRONTEND_BASE_URL=https://192.168.56.1:5173
```

### `CORS_ALLOWED_ORIGINS`

Esta variable determina qué orígenes del frontend pueden realizar solicitudes al backend.

Cuando cambie la IP, se debe reemplazar la IP anterior por la nueva:

```env
https://<NUEVA_IP>:5173
```

No utilizar:

```env
*
```

El proyecto mantiene una lista explícita de orígenes permitidos.

### `APP_FRONTEND_BASE_URL`

Esta variable es especialmente importante para la recuperación de contraseña.

El backend utiliza esta dirección para construir el enlace enviado por correo.

Por ejemplo:

```env
APP_FRONTEND_BASE_URL=https://192.168.56.1:5173
```

produce enlaces similares a:

```text
https://192.168.56.1:5173/reset-password?id=<UUID>&token=<TOKEN>
```

Si esta variable continúa apuntando a una IP antigua o a `localhost`, los nuevos correos de recuperación utilizarán esa dirección incorrecta.

---

# 4. Archivo `.env.example`

También se debe revisar:

```text
.env.example
```

Este archivo sirve únicamente como **documentación/plantilla de configuración**.

Puede mantenerse de esta forma:

```env
SERVER_PORT=8084
SERVER_ADDRESS=0.0.0.0

CORS_ALLOWED_ORIGINS=https://localhost:5173,https://127.0.0.1:5173,https://<HOST_LAN>:5173

# URL donde se abre el frontend para los enlaces enviados por correo.
APP_FRONTEND_BASE_URL=https://<HOST_LAN>:5173

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
MAIL_USERNAME=
MAIL_PASSWORD=
```

> No colocar contraseñas, JWT secrets, credenciales de correo ni otras credenciales reales en `.env.example`.

El archivo `.env.example` puede estar versionado porque únicamente contiene nombres de variables y valores de ejemplo.

El archivo `.env` real debe permanecer fuera de Git.

---

# 5. Actualizar el certificado HTTPS del Frontend

El frontend utiliza HTTPS durante el desarrollo.

Vite obtiene las rutas del certificado mediante las variables:

```env
VITE_DEV_HTTPS_CERT
VITE_DEV_HTTPS_KEY
```

Actualmente los certificados del proyecto se encuentran en:

```text
web/certs/onelanguage-cert.pem
web/certs/onelanguage-key.pem
```

Antes de reemplazarlos se recomienda crear una copia de seguridad:

```powershell
Copy-Item ".\certs\onelanguage-cert.pem" ".\certs\onelanguage-cert.backup.pem"
Copy-Item ".\certs\onelanguage-key.pem" ".\certs\onelanguage-key.backup.pem"
```

---

## 6. Regenerar el certificado con mkcert

Primero comprobar que `mkcert` esté disponible:

```powershell
mkcert -version
```

Si está correctamente configurado, asegurarse de que su CA de desarrollo esté instalada:

```powershell
mkcert -install
```

Después, desde la carpeta `web` del frontend, generar un certificado que incluya:

- `localhost`
- `127.0.0.1`
- la IP LAN actual

Ejemplo:

```powershell
mkcert -key-file ".\certs\onelanguage-key.pem" -cert-file ".\certs\onelanguage-cert.pem" localhost 127.0.0.1 <NUEVA_IP>
```

Por ejemplo:

```powershell
mkcert -key-file ".\certs\onelanguage-key.pem" -cert-file ".\certs\onelanguage-cert.pem" localhost 127.0.0.1 192.168.56.1
```

Si temporalmente se necesita conservar compatibilidad con más de una IP de desarrollo, se pueden incluir varias:

```powershell
mkcert -key-file ".\certs\onelanguage-key.pem" -cert-file ".\certs\onelanguage-cert.pem" localhost 127.0.0.1 192.168.56.1 192.168.1.26
```

> Nunca compartir ni subir a Git las claves privadas generadas por `mkcert`.

---

## 7. Si `mkcert` está instalado pero PowerShell no lo reconoce

Puede ocurrir que:

```powershell
winget list --id FiloSottile.mkcert
```

muestre que `mkcert` está instalado, pero:

```powershell
mkcert -version
```

indique que el comando no existe.

Se puede localizar el ejecutable con:

```powershell
Get-ChildItem "$env:LOCALAPPDATA\Microsoft\WinGet\Packages" -Recurse -Filter "mkcert.exe" -ErrorAction SilentlyContinue | Select-Object FullName
```

Después se puede utilizar directamente su ruta.

Ejemplo:

```powershell
$mkcert = "RUTA_COMPLETA_A_MKCERT.EXE"
```

Comprobar:

```powershell
& $mkcert -version
```

Instalar la CA:

```powershell
& $mkcert -install
```

Y generar el certificado:

```powershell
& $mkcert -key-file ".\certs\onelanguage-key.pem" -cert-file ".\certs\onelanguage-cert.pem" localhost 127.0.0.1 <NUEVA_IP>
```

---

# 8. Reiniciar el Frontend

Después de reemplazar los certificados, Vite debe reiniciarse para utilizar los nuevos archivos.

Detener el proceso anterior con:

```text
Ctrl + C
```

y volver a ejecutar:

```powershell
npm run dev
```

Comprobar que Vite muestre una dirección de red similar a:

```text
Network: https://<NUEVA_IP>:5173/
```

Después probar:

```text
https://localhost:5173
```

y:

```text
https://<NUEVA_IP>:5173
```

---

# 9. Reiniciar Docker / Backend

**Este paso es obligatorio después de modificar las variables del `.env` del backend.**

Los contenedores que ya están ejecutándose pueden continuar utilizando las variables anteriores.

Desde el directorio que contiene el archivo `docker-compose.yml`, ejecutar:

```powershell
docker compose down
docker compose up -d --build
```

De esta forma los contenedores se vuelven a crear utilizando la configuración actualizada.

Se puede comprobar la configuración procesada por Docker Compose con:

```powershell
docker compose config | Select-String "APP_FRONTEND_BASE_URL|CORS_ALLOWED_ORIGINS"
```

Debe aparecer la nueva IP.

Por ejemplo:

```text
APP_FRONTEND_BASE_URL=https://192.168.56.1:5173
```

---

# 10. Probar nuevamente la recuperación de contraseña

Después de actualizar la IP y reiniciar el backend, se debe solicitar **un nuevo correo de recuperación de contraseña**.

No se recomienda reutilizar un enlace generado antes del cambio de configuración.

El nuevo enlace debe comenzar con:

```text
https://<NUEVA_IP>:5173/reset-password
```

Por ejemplo:

```text
https://192.168.56.1:5173/reset-password?id=<UUID>&token=<TOKEN>
```

Comprobar el flujo completo:

```text
Solicitar recuperación
        ↓
Recibir correo
        ↓
Abrir enlace HTTPS
        ↓
Página /reset-password
        ↓
Establecer nueva contraseña
        ↓
Iniciar sesión con la nueva contraseña
```

---

# 11. Acceso desde otro dispositivo

Que el certificado sea válido para:

```text
<NUEVA_IP>
```

no significa que otro computador o celular confíe automáticamente en él.

`mkcert` utiliza una autoridad certificadora local de desarrollo.

Por tanto, para utilizar HTTPS sin advertencias desde otro dispositivo, ese dispositivo debe confiar explícitamente en la CA de desarrollo correspondiente.

> Nunca copiar ni compartir `rootCA-key.pem`.

La clave privada de la CA debe permanecer protegida en el equipo donde fue creada.

Esta configuración es exclusivamente para desarrollo. En producción deben utilizarse certificados emitidos mediante una solución de confianza apropiada.

---

# 12. Resumen rápido cuando cambie la IP

Cuando cambie la IP utilizada por One Language:

1. Identificar la nueva IPv4.
2. Actualizar `CORS_ALLOWED_ORIGINS` en el `.env` del backend.
3. Actualizar `APP_FRONTEND_BASE_URL` en el `.env` del backend.
4. Revisar/actualizar `.env.example` solamente si la documentación necesita cambiar.
5. Regenerar el certificado HTTPS del frontend incluyendo la nueva IP.
6. Reiniciar Vite con `npm run dev`.
7. Reiniciar Docker/Backend:
   ```powershell
   docker compose down
   docker compose up -d --build
   ```
8. Verificar las variables cargadas por Docker.
9. Abrir `https://<NUEVA_IP>:5173`.
10. Solicitar un nuevo correo de recuperación.
11. Verificar que el enlace recibido utilice la nueva IP.
12. Probar el restablecimiento de contraseña y posteriormente el inicio de sesión.

---

## Archivos involucrados

### Backend

```text
.env
.env.example
docker-compose.yml
```

Normalmente, cuando solo cambia la IP, **no debería ser necesario modificar `docker-compose.yml`** si este ya consume correctamente:

```text
CORS_ALLOWED_ORIGINS
APP_FRONTEND_BASE_URL
```

Tampoco debería ser necesario modificar código Java.

### Frontend Web

```text
web/.env
web/vite.config.js
web/certs/onelanguage-cert.pem
web/certs/onelanguage-key.pem
```

Normalmente `vite.config.js` **no necesita modificarse**. Este ya obtiene las rutas del certificado mediante:

```text
VITE_DEV_HTTPS_CERT
VITE_DEV_HTTPS_KEY
```

Lo que cambia al cambiar la IP es principalmente el certificado generado por `mkcert`.

---

## Seguridad

Nunca subir al repositorio:

- `.env` con credenciales reales.
- `JWT_SECRET`.
- contraseñas de PostgreSQL.
- contraseñas de aplicación del correo.
- claves privadas de certificados.
- `rootCA-key.pem`.

Antes de hacer commit, verificar:

```powershell
git status
```

y:

```powershell
git diff --check
```

Los secretos deben mantenerse fuera del control de versiones.