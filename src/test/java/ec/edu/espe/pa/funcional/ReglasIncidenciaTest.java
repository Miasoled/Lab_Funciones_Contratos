package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ReglasIncidenciaTest {

    private static Incidencia incidencia(String estado, int prioridad) {
        return new Incidencia("INC-TEST", "Incidencia de prueba", estado, prioridad);
    }

    @ParameterizedTest
    @CsvSource({"ABIERTA, true", "EN_PROCESO, false", "CERRADA, false"})
    void determinaSiEstaAbierta(String estado, boolean esperado) {
        assertEquals(esperado, ReglasIncidencia.ESTA_ABIERTA.test(incidencia(estado, 3)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void prioridadAltaRespetaUmbral(int prioridad) {
        assertEquals(prioridad >= 4,
                ReglasIncidencia.PRIORIDAD_ALTA.test(incidencia("ABIERTA", prioridad)));
    }

    @ParameterizedTest
    @MethodSource("casosRequiereAtencion")
    void combinaEstadoYPrioridad(String estado, int prioridad, boolean esperado) {
        assertEquals(esperado,
                ReglasIncidencia.REQUIERE_ATENCION.test(incidencia(estado, prioridad)));
    }

    static Stream<Arguments> casosRequiereAtencion() {
        return Stream.of(
                Arguments.of("ABIERTA", 4, true),
                Arguments.of("ABIERTA", 5, true),
                Arguments.of("ABIERTA", 3, false),
                Arguments.of("CERRADA", 5, false),
                Arguments.of("EN_PROCESO", 4, false));
    }

    @Test
    void construyeResumenEsperado() {
        assertEquals("INC-TEST - Incidencia de prueba",
                ReglasIncidencia.RESUMEN.apply(incidencia("ABIERTA", 3)));
    }

    @ParameterizedTest
    @CsvSource({"1, BAJA", "2, MEDIA", "3, MEDIA", "4, ALTA", "5, CRITICA"})
    void clasificaTodasLasPrioridades(int prioridad, String categoria) {
        assertEquals(categoria,
                ReglasIncidencia.CATEGORIA.apply(incidencia("ABIERTA", prioridad)));
    }

    @Test
    void componePredicadosConAndOrYNegate() {
        Incidencia abiertaAlta = incidencia("ABIERTA", 4);
        Incidencia cerradaCritica = incidencia("CERRADA", 5);
        assertTrue(ReglasIncidencia.ESTA_ABIERTA.and(ReglasIncidencia.PRIORIDAD_ALTA).test(abiertaAlta));
        assertTrue(ReglasIncidencia.ESTA_ABIERTA.or(ReglasIncidencia.PRIORIDAD_ALTA).test(cerradaCritica));
        assertFalse(ReglasIncidencia.REQUIERE_ATENCION.negate().test(abiertaAlta));
        assertTrue(ReglasIncidencia.REQUIERE_ATENCION.negate().test(incidencia("ABIERTA", 3)));
    }

    @Test
    void componeFuncionesConAndThenYCompose() {
        Function<String, String> mayusculas = String::toUpperCase;
        Incidencia inc = incidencia("ABIERTA", 5);
        String esperado = "INC-TEST - INCIDENCIA DE PRUEBA";
        assertEquals(esperado, ReglasIncidencia.RESUMEN.andThen(mayusculas).apply(inc));
        assertEquals(esperado, mayusculas.compose(ReglasIncidencia.RESUMEN).apply(inc));
    }

    @Test
    void encadenaAccionesConsumer() {
        List<String> acciones = new ArrayList<>();
        Consumer<Incidencia> primero = i -> acciones.add("primero:" + i.id());
        Consumer<Incidencia> segundo = i -> acciones.add("segundo:" + i.id());
        primero.andThen(segundo).accept(incidencia("ABIERTA", 5));
        assertEquals(List.of("primero:INC-TEST", "segundo:INC-TEST"), acciones);
    }

    @Test
    void registraIncidenciasEnBitacora() {
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        registrar.accept(incidencia("ABIERTA", 5));
        assertEquals(List.of("INC-TEST"), bitacora);
    }

    @Test
    void generaIncidenciasSinteticasConIdsDistintosYPrioridadValida() {
        var generador = ReglasIncidencia.generadorSintetico();
        java.util.Set<String> identificadores = new java.util.HashSet<>();
        for (int i = 0; i < 50; i++) {
            Incidencia generada = generador.get();
            assertTrue(identificadores.add(generada.id()), "cada id sintético debe ser único");
            assertTrue(generada.prioridad() >= 1 && generada.prioridad() <= 5,
                    "prioridad sintética fuera del rango: " + generada.prioridad());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void rechazaPrioridadInvalida(int prioridad) {
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-X", "Inválida", "ABIERTA", prioridad));
    }

    @Test
    void rechazaIdNulo() {
        assertThrows(NullPointerException.class,
                () -> new Incidencia(null, "Título", "ABIERTA", 3));
    }

    @Test
    void rechazaEstadoNulo() {
        assertThrows(NullPointerException.class,
                () -> new Incidencia("INC-X", "Título", null, 3));
    }

    @Test
    void implementacionesDelContratoEvaluanLaMismaRegla() {
        ReglaIncidencia clase = new PrioridadAlta();
        ReglaIncidencia anonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia i) {
                return i.prioridad() >= 4;
            }
        };
        ReglaIncidencia lambda = i -> i.prioridad() >= 4;

        for (int prioridad = 1; prioridad <= 5; prioridad++) {
            Incidencia inc = incidencia("ABIERTA", prioridad);
            assertEquals(clase.evaluar(inc), anonima.evaluar(inc));
            assertEquals(clase.evaluar(inc), lambda.evaluar(inc));
            assertEquals(prioridad >= 4, lambda.evaluar(inc));
        }
    }
}