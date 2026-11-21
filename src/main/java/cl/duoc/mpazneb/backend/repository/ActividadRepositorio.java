package cl.duoc.mpazneb.backend.repository;

import cl.duoc.mpazneb.backend.dto.ActividadDto;
import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import java.util.List;
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
}