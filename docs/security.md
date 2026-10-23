# Seguridad y CI

Configure en un `.env` privado `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `MAIL_USERNAME` y `MAIL_PASSWORD`. `JWT_SECRET` debe tener al menos 32 caracteres; su rotación invalida los JWT emitidos con la clave anterior.

Gitleaks analiza todo el historial Git. Una exposición histórica puede hacer fallar CI incluso si el árbol actual está limpio: rote primero la credencial y coordine después el saneamiento de historial, clones, forks y caches.
