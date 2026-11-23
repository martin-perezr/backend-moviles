# MPAZ NEB — Microservicio Spring Boot

API REST para actividades, asignaciones y validación de códigos temporales del proyecto **MPAZ NEB** (DSY1105, Duoc UC).

## Integrantes

- Hanael Echeverría
- Matías Soto
- Martín Pérez

## Tecnologías

- Java 21
- Spring Boot 4.1
- JDBC + SQLite (`mpazneb-servidor.db`)
- JaCoCo en `mvn test`

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/salud` | Estado del servicio |
| GET | `/api/actividades` | Listar actividades (Matías) |
| POST | `/api/actividades` | Crear actividad |
| PUT | `/api/actividades/{id}` | Actualizar actividad |
| DELETE | `/api/actividades/{id}` | Eliminar borrador sin asignaciones |
| POST | `/api/asignaciones` | Crear asignación + código 6 dígitos |
| POST | `/api/codigos/validar` | Validar código de estudiante |

## Ejecución

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

- **Tests:** perfil `test` → `jdbc:sqlite:mpazneb-test.db` (ver `src/test/resources/application-test.properties`).
- **Servidor:** usa `mpazneb-servidor.db` sin borrar datos al reiniciar.

Informe JaCoCo: `target/site/jacoco/index.html` tras `mvn test`.

## Repositorio relacionado

Cliente Android: repositorio `mpaz-neb` (misma organización GitHub).

Rama de integración EP4: `feature/hana-codigos-api`.

Planificación: https://trello.com/b/0EkPsNiH/mpaz-neb-dsy1105