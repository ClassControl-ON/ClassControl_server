-- ============================================================
--  ClassControl - Tabla de tokens de restablecimiento
--  Ejecutar en la BD railway (Railway -> MySQL).
--  Almacena el hash SHA-256 del token, de un solo uso.
-- ============================================================

USE railway;

CREATE TABLE IF NOT EXISTS restablecer_token (
    id_token INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Usuarios_id_usuarios INT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expira_en DATETIME NOT NULL,
    usado TINYINT(1) NOT NULL DEFAULT 0,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tok_usuario FOREIGN KEY (Usuarios_id_usuarios)
        REFERENCES usuarios (id_usuarios) ON DELETE CASCADE,
    INDEX idx_tok_hash (token_hash),
    INDEX idx_tok_usuario (Usuarios_id_usuarios)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;