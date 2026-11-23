package cl.duoc.mpazneb.backend.repository;

import cl.duoc.mpazneb.backend.dto.AsignacionDto;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AsignacionRepositorio {
    private static final String COLUMNAS =
        "id, estudianteId, actividadId, asistenteId, codigo, creadaEn, venceEn, estado";

    private static final RowMapper<AsignacionDto> MAPPER = (rs, i) -> new AsignacionDto(
        rs.getLong("id"),
        rs.getLong("estudianteId"),
        rs.getLong("actividadId"),
        rs.getLong("asistenteId"),
        rs.getString("codigo"),
        rs.getLong("creadaEn"),
        rs.getLong("venceEn"),
        rs.getString("estado"));

    private final JdbcTemplate jdbc;

    public AsignacionRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean codigoVigenteEnUso(String codigo, long ahora) {
        Integer cuenta = jdbc.queryForObject(
            "SELECT COUNT(*) FROM asignaciones WHERE codigo = ? AND estado != 'FINALIZADA' AND venceEn > ?",
            Integer.class,
            codigo,
            ahora);
        return cuenta != null && cuenta > 0;
    }

    public boolean estudianteConSesionAbierta(long estudianteId, long ahora) {
        Integer cuenta = jdbc.queryForObject(
            "SELECT COUNT(*) FROM asignaciones WHERE estudianteId = ? AND estado != 'FINALIZADA' AND venceEn > ?",
            Integer.class,
            estudianteId,
            ahora);
        return cuenta != null && cuenta > 0;
    }

    public AsignacionDto insertar(
            long estudianteId,
            long actividadId,
            long asistenteId,
            String codigo,
            long creadaEn,
            long venceEn,
            String estado) {
        return jdbc.queryForObject(
            "INSERT INTO asignaciones (estudianteId, actividadId, asistenteId, codigo, creadaEn, venceEn, estado) " +
            "VALUES (?,?,?,?,?,?,?) RETURNING " + COLUMNAS,
            MAPPER,
            estudianteId,
            actividadId,
            asistenteId,
            codigo,
            creadaEn,
            venceEn,
            estado);
    }

    public Optional<AsignacionDto> buscarPorCodigo(String codigo) {
        return jdbc.query(
            "SELECT " + COLUMNAS + " FROM asignaciones WHERE codigo = ? ORDER BY creadaEn DESC LIMIT 1",
            MAPPER,
            codigo)
            .stream()
            .findFirst();
    }
}