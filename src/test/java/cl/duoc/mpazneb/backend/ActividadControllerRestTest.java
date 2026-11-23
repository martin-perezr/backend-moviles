package cl.duoc.mpazneb.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActividadControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JSON_VALIDO = """
        {
          "nombre": "Prueba REST",
          "modulo": "LECTURA",
          "curso": 2,
          "contenido": "Contenido de prueba",
          "objetivo": "Objetivo de prueba largo",
          "dificultad": 1,
          "instrucciones": "Instrucciones claras",
          "intentosPermitidos": 2,
          "estado": "BORRADOR",
          "textoTitulo": "Prueba",
          "textoCuerpo": "Texto con más de veinte caracteres para lectura."
        }
        """;

    @Test
    void listarDevuelveJson() throws Exception {
        mockMvc.perform(get("/api/actividades"))
            .andExpect(status().isOk());
    }

    @Test
    void crearEditarYBorrarBorrador() throws Exception {
        MvcResult creada = mockMvc.perform(post("/api/actividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        String cuerpo = creada.getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(cuerpo, "$.id")).longValue();

        mockMvc.perform(put("/api/actividades/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_VALIDO))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/actividades/" + id))
            .andExpect(status().isNoContent());
    }

    @Test
    void crearRechazaNombreCorto() throws Exception {
        String invalido = JSON_VALIDO.replace("Prueba REST", "abc");
        mockMvc.perform(post("/api/actividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalido))
            .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarInexistenteDevuelve404() throws Exception {
        mockMvc.perform(delete("/api/actividades/999999"))
            .andExpect(status().isNotFound());
    }
}