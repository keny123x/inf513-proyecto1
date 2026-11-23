-- =========================================================================
-- SCRIPT BASE DE DATOS: CONSEJO FACULTATIVO UAGRM (INF513 - GRUPO 07SC)
-- Base de Datos: db_grupo07sc
-- =========================================================================

CREATE TABLE persona (
    ci VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    celular INTEGER NOT NULL,
    email VARCHAR(150) NOT NULL
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    ci VARCHAR(20) NOT NULL,
    CONSTRAINT fk_usuario_persona FOREIGN KEY (ci) REFERENCES persona(ci) ON DELETE CASCADE
);

CREATE TABLE miembro_consejo (
    id_miembro INTEGER PRIMARY KEY,
    condicion VARCHAR(30) NOT NULL,
    tipo_estamento VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    frente_electoral VARCHAR(100) NOT NULL,
    fecha_acreditacion DATE NOT NULL,
    contador_faltas INTEGER DEFAULT 0,
    estado_mandato VARCHAR(30) NOT NULL,
    ci VARCHAR(20) NOT NULL,
    CONSTRAINT fk_miembro_persona FOREIGN KEY (ci) REFERENCES persona(ci) ON DELETE CASCADE
);

CREATE TABLE convocatoria (
    id_convocatoria INTEGER PRIMARY KEY,
    codigo_sesion VARCHAR(50) NOT NULL,
    estado_quorum VARCHAR(30) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    orden_del_dia TEXT NOT NULL,
    tipo_sesion VARCHAR(30) NOT NULL,
    aprobado_presidente BOOLEAN DEFAULT TRUE
);

CREATE TABLE sesion (
    num_sesion INTEGER PRIMARY KEY,
    id_convocatoria INTEGER NOT NULL,
    id_presidente_miembro INTEGER NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_sesion_convocatoria FOREIGN KEY (id_convocatoria) REFERENCES convocatoria(id_convocatoria) ON DELETE CASCADE,
    CONSTRAINT fk_sesion_presidente FOREIGN KEY (id_presidente_miembro) REFERENCES miembro_consejo(id_miembro)
);

CREATE TABLE correspondencia (
    id_documento INTEGER PRIMARY KEY,
    estado_flujo VARCHAR(30) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    url_archivo VARCHAR(255) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    es_urgente BOOLEAN DEFAULT FALSE,
    id_convocatoria INTEGER NULL,
    CONSTRAINT fk_correspondencia_convocatoria FOREIGN KEY (id_convocatoria) REFERENCES convocatoria(id_convocatoria) ON DELETE SET NULL
);

CREATE TABLE asistencia_excusa (
    id_asistencia INTEGER PRIMARY KEY,
    marca_biometrica VARCHAR(50) NOT NULL,
    presento_excusa BOOLEAN NOT NULL,
    motivo_excusa TEXT,
    suplente_habilitado BOOLEAN DEFAULT FALSE,
    id_miembro INTEGER NOT NULL,
    num_sesion INTEGER NOT NULL,
    CONSTRAINT fk_asistencia_miembro FOREIGN KEY (id_miembro) REFERENCES miembro_consejo(id_miembro),
    CONSTRAINT fk_asistencia_sesion FOREIGN KEY (num_sesion) REFERENCES sesion(num_sesion) ON DELETE CASCADE
);

CREATE TABLE habilitacion_suplente (
    id_suplente INTEGER PRIMARY KEY,
    id_suplenete_mimebro INTEGER NOT NULL,
    id_titular INTEGER NOT NULL,
    num_sesion INTEGER NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    CONSTRAINT fk_suplente_miembro FOREIGN KEY (id_suplenete_mimebro) REFERENCES miembro_consejo(id_miembro),
    CONSTRAINT fk_suplente_titular FOREIGN KEY (id_titular) REFERENCES miembro_consejo(id_miembro),
    CONSTRAINT fk_suplente_sesion FOREIGN KEY (num_sesion) REFERENCES sesion(num_sesion) ON DELETE CASCADE
);

CREATE TABLE votacion (
    id_votacion INTEGER PRIMARY KEY,
    id_documento INTEGER NOT NULL,
    id_miembro INTEGER NOT NULL,
    hora_voto TIME NOT NULL,
    tipo_voto VARCHAR(30) NOT NULL,
    CONSTRAINT fk_votacion_documento FOREIGN KEY (id_documento) REFERENCES correspondencia(id_documento) ON DELETE CASCADE,
    CONSTRAINT fk_votacion_miembro FOREIGN KEY (id_miembro) REFERENCES miembro_consejo(id_miembro)
);

CREATE TABLE acta (
    num_sesion INTEGER PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    estado_acta INTEGER NOT NULL,
    CONSTRAINT fk_acta_sesion FOREIGN KEY (num_sesion) REFERENCES sesion(num_sesion) ON DELETE CASCADE
);

CREATE TABLE publicacion (
    id_publicacion INTEGER PRIMARY KEY,
    fecha_publicacion DATE NOT NULL,
    numero_edicion INTEGER NOT NULL,
    url_publicacion VARCHAR(255) NOT NULL
);

CREATE TABLE resolucion (
    id_resolucion INTEGER PRIMARY KEY,
    contenido TEXT NOT NULL,
    numero_resolucion VARCHAR(50) NOT NULL,
    fecha_emision DATE NOT NULL,
    fue_vetada BOOLEAN DEFAULT FALSE,
    num_sesion INTEGER NOT NULL,
    id_publicacion INTEGER NULL,
    CONSTRAINT fk_resolucion_sesion FOREIGN KEY (num_sesion) REFERENCES sesion(num_sesion) ON DELETE CASCADE,
    CONSTRAINT fk_resolucion_publicacion FOREIGN KEY (id_publicacion) REFERENCES publicacion(id_publicacion) ON DELETE SET NULL
);
