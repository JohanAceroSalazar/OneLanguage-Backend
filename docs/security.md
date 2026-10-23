# Seguridad y CI

Configure en un `.env` privado `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `MAIL_USERNAME` y `MAIL_PASSWORD`. `JWT_SECRET` debe tener al menos 32 caracteres; su rotación invalida los JWT emitidos con la clave anterior.

Gitleaks analiza todo el historial Git. Una exposición histórica puede hacer fallar CI incluso si el árbol actual está limpio: rote primero la credencial y coordine después el saneamiento de historial, clones, forks y caches.

Las pruebas estándar usan H2 en memoria desde `src/test/resources/application.properties`; por eso el job `test` no inyecta variables `SPRING_DATASOURCE_*`. La prueba `AuthServicePostgresConcurrencyTest` existe para PostgreSQL real, pero solo se habilita con `-Dconcurrency.integration=true` y requiere una base migrada. No se ejecuta en el workflow actual ni se considera una integración PostgreSQL aprobada.
