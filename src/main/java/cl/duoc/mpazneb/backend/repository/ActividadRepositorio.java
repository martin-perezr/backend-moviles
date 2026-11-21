package cl.duoc.mpazneb.backend.repository;

import cl.duoc.mpazneb.backend.dto.ActividadDto;
import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ActividadRepositorio {
    private static final String COLUMNAS =
        "id, nombre, modulo, curso, contenido, objetivo, dificultad, instrucciones, " +
        "intentosPermitidos, estado, textoTitulo, textoCuerpo, imagenRuta";

    private static final RowMapper<ActividadDto> MAPPER = (rs, i) -> new ActividadDto(
        rs.getLong("id"),
        rs.getString("nombre"),
        rs.getString("modulo"),
        rs.getInt("curso"),
        rs.getString("contenido"),
        rs.getString("objetivo"),
        rs.getInt("dificultad"),
        rs.getString("instrucciones"),
        rs.getInt("intentosPermitidos"),
        rs.getString("estado"),
        rs.getString("textoTitulo"),
        rs.getString("textoCuerpo"),
        rs.getString("imagenRuta"));

    private final JdbcTemplate jdbc;

    public ActividadRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ActividadDto> listar() {
        return jdbc.query("SELECT " + COLUMNAS + " FROM actividades ORDER BY id", MAPPER);
    }

    public ActividadDto crear(ActividadRequest r) {
        return jdbc.queryForObject(
            "INSERT INTO actividades (nombre, modulo, curso, contenido, objetivo, dificultad, " +
            "instrucciones, intentosPermitidos, estado, textoTitulo, textoCuerpo, imagenRuta) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?) RETURNING " + COLUMNAS,
            MAPPER,
            r.nombre().trim(), r.modulo(), r.curso(), r.contenido().trim(), r.objetivo().trim(),
            r.dificultad(), r.instrucciones().trim(), r.intentosPermitidos(), r.estado(),
            r.textoTitulo(), r.textoCuerpo(), r.imagenRuta());
    }

        public Optional<ActividadDto> buscarPorId(long id) {
        return jdbc.query(
            "SELECT " + COLUMNAS + " FROM actividades WHERE id = ?", MAPPER, id)
            .stream().findFirst();
    }

    public void actualizar(long id, ActividadRequest r) {
        jdbc.update(
            "UPDATE actividades SET nombre = ?, modulo = ?, curso = ?, contenido = ?, " +
            "objetivo = ?, dificultad = ?, instrucciones = ?, intentosPermitidos = ?, " +
            "estado = ?, textoTitulo = ?, textoCuerpo = ?, imagenRuta = ? WHERE id = ?",
            r.nombre().trim(), r.modulo(), r.curso(), r.contenido().trim(), r.objetivo().trim(),
            r.dificultad(), r.instrucciones().trim(), r.intentosPermitidos(), r.estado(),
            r.textoTitulo(), r.textoCuerpo(), r.imagenRuta(), id);
    }

    /** Borra solo si es BORRADOR y no tiene asignaciones. Devuelve filas borradas (0 o 1). */
    public int eliminarBorradorLibre(long id) {
        return jdbc.update(
            "DELETE FROM actividades WHERE id = ? AND estado = 'BORRADOR' " +
            "AND NOT EXISTS (SELECT 1 FROM asignaciones WHERE actividadId = actividades.id)",
            id);
    }
}