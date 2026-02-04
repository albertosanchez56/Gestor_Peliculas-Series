# Usar config local (sin GitHub)

Por defecto el Config Server lee la configuración desde **Git** (GitHub).  
Los cambios que hagas en la carpeta `config-data` del repo **no** se aplican hasta que los subas a GitHub y reinicies el Config Server.

## Opción 1: Perfil `native` (recomendado en desarrollo)

Arranca el Config Server con el perfil **native** para que lea la carpeta `config-data` local:

```bash
--spring.profiles.active=native
```

Ejecuta el Config Server **desde la carpeta config-service3** (para que `file:../config-data` apunte bien).

Así los cambios en `config-data/` (p. ej. `gateway-service3.yaml`) se aplican al reiniciar el Config Server y luego el Gateway, **sin subir nada a GitHub**.

## Opción 2: Seguir usando Git

Si arrancas el Config Server **sin** el perfil `native`, usa la config del repo de GitHub.  
Para que los cambios de `config-data` se apliquen:

1. Sube los cambios a GitHub (rama `main`, carpeta `config-data`).
2. Reinicia el Config Server.
3. Reinicia el Gateway (y los servicios que usen esa config).
