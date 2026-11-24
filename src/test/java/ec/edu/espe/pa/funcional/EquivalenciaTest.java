package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EquivalenciaTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("escenarios")
    void ambasImplementacionesProducenLosMismosTitulosEnElMismoOrden(
            String nombre, List<Incidencia> datos, List<String> esperados) {
        List<Incidencia> antes = List.copyOf(datos);
        List<String> resultadoImperativo = new ProcesadorImperativo()
                .titulosQueRequierenAtencion(datos);
        List<String> resultadoFuncional = new ProcesadorFuncional()
                .titulosQueRequierenAtencion(datos);

        assertEquals(esperados, resultadoImperativo, "referencia imperativa: " + nombre);
        assertEquals(resultadoImperativo, resultadoFuncional, "equivalencia: " + nombre);
        assertEquals(antes, datos, "los datos de entrada deben conservarse: " + nombre);
        assertTrue(datos.equals(antes));
        if (esperados.isEmpty()) {
            assertTrue(resultadoFuncional.isEmpty());
        } else {
            assertFalse(resultadoFuncional.isEmpty());
        }
        assertThrows(UnsupportedOperationException.class,
                () -> datos.add(new Incidencia("INC-X", "No agregar", "ABIERTA", 5)));
    }

    static Stream<Arguments> escenarios() {
        Supplier<List<Incidencia>> cincuenta = () -> {
            var generador = ReglasIncidencia.generadorSintetico();
            List<Incidencia> lista = new ArrayList<>();
            for (int i = 0; i < 50; i++) lista.add(generador.get());
            return List.copyOf(lista);
        };
        Incidencia abiertaAlta = inc("1", "ABIERTA", 5);
        Incidencia abiertaBaja = inc("2", "ABIERTA", 2);
        Incidencia cerradaAlta = inc("3", "CERRADA", 5);
        Incidencia enProcesoAlta = inc("4", "EN_PROCESO", 4);
        Incidencia prioridad3 = inc("5", "ABIERTA", 3);
        Incidencia prioridad4 = inc("6", "ABIERTA", 4);
        Incidencia prioridad5 = inc("7", "ABIERTA", 5);
        List<Incidencia> mixta = List.of(abiertaAlta, abiertaBaja, cerradaAlta,
                enProcesoAlta, prioridad3, prioridad4);
        return Stream.of(
                Arguments.of("lista vacía", List.of(), List.of()),
                Arguments.of("una incidencia abierta de prioridad alta", List.of(abiertaAlta), List.of("Título 1")),
                Arguments.of("abiertas de prioridad baja", List.of(abiertaBaja), List.<String>of()),
                Arguments.of("cerradas de prioridad alta", List.of(cerradaAlta), List.<String>of()),
                Arguments.of("incidencias en proceso", List.of(enProcesoAlta), List.<String>of()),
                Arguments.of("prioridad 3 debajo del umbral", List.of(prioridad3), List.<String>of()),
                Arguments.of("prioridad 4 en el umbral", List.of(prioridad4), List.of("Título 6")),
                Arguments.of("prioridad 5 máxima", List.of(prioridad5), List.of("Título 7")),
                Arguments.of("lista mixta conserva orden", mixta, List.of("Título 1", "Título 6")),
                Arguments.of("50 incidencias sintéticas", cincuenta.get(), expectedSyntheticTitles()));
    }

    private static List<String> expectedSyntheticTitles() {
        List<String> expected = new ArrayList<>();
        for (int n = 1; n <= 50; n++) {
            if (n % 3 == 0 && (n % 5) + 1 >= 4) {
                expected.add("Incidencia sintética " + n);
            }
        }
        return expected;
    }

    private static Incidencia inc(String id, String estado, int prioridad) {
        return new Incidencia("INC-" + id, "Título " + id, estado, prioridad);
    }
}