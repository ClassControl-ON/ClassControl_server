-- Corrige los indices unicos heredados del modelo inicial.
-- Ejecutar una vez en la base de datos ClassControl ya creada.
USE ClassControl;

DROP INDEX IF EXISTS fecha_inicial_Prog_UNIQUE ON Programacion_Instructores;
DROP INDEX IF EXISTS fecha_fin_Prog_UNIQUE ON Programacion_Instructores;
DROP INDEX IF EXISTS diasSemana_UNIQUE ON Programacion_Instructores;
DROP INDEX IF EXISTS Ambientes_id_ambientes_UNIQUE ON Programacion_Instructores;
DROP INDEX IF EXISTS hora_inicio_UNIQUE ON Programacion_Instructores;
DROP INDEX IF EXISTS hora_fin_UNIQUE ON Programacion_Instructores;

-- Un mismo ambiente puede tener varias programaciones; la aplicacion valida
-- conflictos reales de horario antes de guardar.
