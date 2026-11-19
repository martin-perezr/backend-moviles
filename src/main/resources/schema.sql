CREATE TABLE IF NOT EXISTS actividades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    modulo TEXT NOT NULL,
    curso INTEGER NOT NULL,
    contenido TEXT NOT NULL,
    objetivo TEXT NOT NULL,
    dificultad INTEGER NOT NULL,
    instrucciones TEXT NOT NULL,
    intentosPermitidos INTEGER NOT NULL,
    estado TEXT NOT NULL DEFAULT 'BORRADOR',
    textoTitulo TEXT,
    textoCuerpo TEXT,
    imagenRuta TEXT
);

CREATE TABLE IF NOT EXISTS asignaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    estudianteId INTEGER NOT NULL,
    actividadId INTEGER NOT NULL,
    asistenteId INTEGER NOT NULL,
    codigo TEXT NOT NULL,
    creadaEn INTEGER NOT NULL,
    venceEn INTEGER NOT NULL,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE'
);