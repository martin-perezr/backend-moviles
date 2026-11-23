package cl.duoc.mpazneb.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import cl.duoc.mpazneb.backend.dto.ActividadRequest;
import cl.duoc.mpazneb.backend.dto.AsignacionDto;
import cl.duoc.mpazneb.backend.dto.AsignacionRequest;
import cl.duoc.mpazneb.backend.dto.ValidarCodigoRequest;
import cl.duoc.mpazneb.backend.repository.ActividadRepositorio;
import cl.duoc.mpazneb.backend.service.AsignacionServicio;
import cl.duoc.mpazneb.backend.service.Reloj;
import cl.duoc.mpazneb.backend.service.RespuestaServicio;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AsignacionServicioTest {

  @Autowired
  private AsignacionServicio asignacionServicio;

  @Autowired
  private ActividadRepositorio actividades;

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private RelojFijo reloj;

  private long actividadDisponibleId;

  @BeforeEach
  void preparar() throws Exception {
    exigirBaseDePruebas();
    reloj.fijar(1_700_000_000_000L);
    jdbc.update("DELETE FROM asignaciones");
    var actividad =
        actividades.crear(
            new ActividadRequest(
                "Actividad de prueba",
                "LECTURA",
                2,
                "Contenido",
                "Objetivo",
                1,
                "Instrucciones",
                2,
                "DISPONIBLE",
                "Título",
                "Cuerpo largo de prueba para lectura",
                null));
    actividadDisponibleId = actividad.id();
  }

  @Test
  void crearAsignacionConCodigoDeSeisDigitos() {
    var resultado =
        asignacionServicio.crear(new AsignacionRequest(1L, actividadDisponibleId, 2L));
    assertEquals(HttpStatus.CREATED, resultado.estado());
    AsignacionDto dto = resultado.cuerpo();
    assertNotNull(dto);
    assertEquals(6, dto.codigo().length());
    assertTrue(dto.codigo().chars().allMatch(Character::isDigit));
    assertEquals("PENDIENTE", dto.estado());
    assertEquals(reloj.ahora() + AsignacionServicio.DURACION_MS, dto.venceEn());
  }

  @Test
  void rechazaIdsInvalidosYActividadInexistente() {
    assertEquals(
        HttpStatus.BAD_REQUEST,
        asignacionServicio.crear(new AsignacionRequest(0L, actividadDisponibleId, 2L)).estado());
    assertEquals(
        HttpStatus.NOT_FOUND,
        asignacionServicio.crear(new AsignacionRequest(1L, 9_999L, 2L)).estado());
  }

  @Test
  void rechazaSesionDuplicada() {
    assertEquals(
        HttpStatus.CREATED,
        asignacionServicio.crear(new AsignacionRequest(1L, actividadDisponibleId, 2L)).estado());
    assertEquals(
        HttpStatus.CONFLICT,
        asignacionServicio.crear(new AsignacionRequest(1L, actividadDisponibleId, 2L)).estado());
  }

  @Test
  void validarCodigoVigenteYVencido() {
    var creada =
        asignacionServicio.crear(new AsignacionRequest(5L, actividadDisponibleId, 2L)).cuerpo();
    assertNotNull(creada);

    assertEquals(
        HttpStatus.OK,
        asignacionServicio.validarCodigo(new ValidarCodigoRequest(creada.codigo())).estado());

    reloj.fijar(creada.venceEn());
    assertEquals(
        HttpStatus.OK,
        asignacionServicio.validarCodigo(new ValidarCodigoRequest(creada.codigo())).estado());

    reloj.fijar(creada.venceEn() + 1);
    assertEquals(
        HttpStatus.GONE,
        asignacionServicio.validarCodigo(new ValidarCodigoRequest(creada.codigo())).estado());
  }

  /** Evita que un DELETE de test borre la base usada por spring-boot:run. */
  private void exigirBaseDePruebas() throws Exception {
    try (var conexion = dataSource.getConnection()) {
      String url = conexion.getMetaData().getURL();
      if (!url.contains("mpazneb-test.db")) {
        fail("Los tests deben usar mpazneb-test.db, pero la URL es: " + url);
      }
    }
  }

  @TestConfiguration
  static class RelojPruebaConfig {
    @Bean
    @Primary
    RelojFijo relojFijo() {
      return new RelojFijo();
    }
  }

  static class RelojFijo implements Reloj {
    private final AtomicLong valor = new AtomicLong();

    void fijar(long instante) {
      valor.set(instante);
    }

    @Override
    public long ahora() {
      return valor.get();
    }
  }
}