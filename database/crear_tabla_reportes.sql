-- Tabla de reportes de incidencias (instructor -> administrador).
USE railway;

CREATE TABLE IF NOT EXISTS reportes (
    id_reportes INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(30) NOT NULL DEFAULT 'ambiente',
    Usuarios_id_usuarios INT NOT NULL COMMENT 'Quien reporta',
    Ambientes_id_ambientes INT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    respuesta_admin VARCHAR(255) NULL,
    atendido_por INT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_atencion DATETIME NULL,
    CONSTRAINT fk_rep_usuario FOREIGN KEY (Usuarios_id_usuarios)
        REFERENCES usuarios (id_usuarios) ON DELETE CASCADE,
    CONSTRAINT fk_rep_ambiente FOREIGN KEY (Ambientes_id_ambientes)
        REFERENCES ambientes (id_ambientes) ON DELETE SET NULL,
    CONSTRAINT fk_rep_atendido FOREIGN KEY (atendido_por)
        REFERENCES usuarios (id_usuarios) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
