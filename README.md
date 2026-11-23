# Sistema de Automatización y Procesamiento de Datos Basado en Correo Electrónico (Email-Driven Architecture)
**Asignatura:** INF513 - Tecnología Web (Gestión 2026-2)  
**Universidad:** Universidad Autónoma Gabriel René Moreno (UAGRM) - FICCT  
**Grupo:** `grupo07sc`  

## 1. Arquitectura del Sistema
El sistema implementa una arquitectura descentralizada **Email-Driven** desarrollada en **Java** bajo el patrón de diseño **Command** y separación en capas:
- **Capa de Presentación (`presentacion`):** Demonio de ejecución continua (`Main`) y enrutador central (`MainController`).
- **Capa de Comunicación (`mail`):** Cliente POP3 para lectura periódica en `mail.tecnoweb.org.bo:110`, analizador léxico (`EmailParser`) con soporte UTF-8 y expresiones regulares, y despachador SMTP (`mail.tecnoweb.org.bo:25`).
- **Capa de Negocio (`negocio`):** Implementación de la interfaz `IComando` para cada caso de uso del Consejo Facultativo (Usuarios, Personas, Miembros, Convocatorias, Correspondencia, Asistencia/Votación, Resoluciones/Gaceta y Reportes).
- **Capa de Datos (`datos`):** Acceso a **PostgreSQL** (`db.tecnoweb.org.bo`) mediante `PreparedStatement` para prevenir inyecciones SQL y control transaccional (`commit`/`rollback`).
- **Auditoría (`util`):** Registro persistente de eventos en `access.log` y excepciones en `error.log`.

## 2. Diagrama de Flujo del Analizador de Correos
```text
[Cliente / Remitente] 
       │ (Envía correo con Subject: COMANDO["param1","param2",...])
       ▼
[Servidor Correo: mail.tecnoweb.org.bo (POP3:110)]
       │ (Hilo Demonio cada 15s lee y decodifica UTF-8)
       ▼
[EmailParser & MainController] ──(Sintaxis/Comando Inválido)──► [Correo HTML de Error + error.log]
       │ (Sintaxis Válida)
       ▼
[Capa Negocio (Command) -> Capa Datos (DAO / PreparedStatement)]
       ├── (Error SQL / Restricción) ──► [Correo HTML de Error + error.log]
       └── (Operación Exitosa) ────────► [Correo HTML de Confirmación/Tabla + access.log]
```

## 3. Estructura de la Base de Datos (PostgreSQL)
Base de datos: `db_grupo07sc` en `db.tecnoweb.org.bo:5432`. Tablas principales:
- `persona` (`ci`, `nombre`, `celular`, `email`)
- `usuario` (`id_usuario`, `nombre_usuario`, `contrasena`, `rol`, `ci`)
- `miembro_consejo` (`id_miembro`, `condicion`, `tipo_estamento`, `estado`, `frente_electoral`, `fecha_acreditacion`, `contador_faltas`, `estado_mandato`, `ci`)
- `convocatoria` y `sesion`
- `correspondencia`, `asistencia_excusa`, `habilitacion_suplente`, `votacion`, `acta`, `resolucion` y `publicacion`

## 4. Instalación y Ejecución
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/keny123x/inf513-proyecto1
   ```
2. Asegurarse de incluir las librerías en `/lib`:
   - `jakarta.mail-2.0.1.jar`
   - `jakarta.activation-api-2.1.3.jar`
   - `postgresql-42.7.14.jar`
3. Ejecutar el script SQL `proyecto_consejo.sql` en PostgreSQL (`db_grupo07sc`).
4. Compilar y ejecutar la clase principal `presentacion.Main`:
   ```bash
   javac -cp "lib/*:src" -d bin src/presentacion/Main.java
   java -cp "lib/*:bin" presentacion.Main
   ```
## 5. Catálogo de Comandos Soportados
- **Personas (CU1):** `INSPER`, `UPDPER`, `DELPER`, `GETPER`, `LISPER["*"]`
- **Usuarios (CU1):** `INSUSR`, `UPDUSR`, `DELUSR`, `GETUSR`, `LISUSR["*"]`
- **Miembros y Sesiones (CU2):** `INSMC`, `LISMC["*"]`, `INSSES`, `UPDQUO`
- **Convocatorias y Suplentes (CU3):** `INSCON`, `GETCON`, `UPDCON`, `DELCON`, `INSEXC`, `LISSUP["*"]`
- **Correspondencia (CU4):** `INSDOC`, `LISPEN["*"]`, `AGEDOC`
- **Asistencia, Quórum y Votación (CU5):** `INSASI`, `GETQUO`, `INSVOT`, `LISVOT["*"]`
- **Actas, Resoluciones y Gaceta (CU6-7):** `INSACT`, `INSRES`, `VETRES`, `INSPUB`, `AUDIT`
- **Reportes y Ayuda (CU8):** `ESTEST["*"]`, `GETREP`, `HELP["*"]`

