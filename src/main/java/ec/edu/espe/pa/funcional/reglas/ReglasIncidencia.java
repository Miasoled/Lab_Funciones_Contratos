package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ReglasIncidencia {

    private ReglasIncidencia() {
    }

    public static final int UMBRAL_PRIORIDAD_ALTA = 4;

    // Predicate recibe una incidencia y devuelve un boolean.
    // Determina si la incidencia está abierta.
    public static final Predicate<Incidencia> ESTA_ABIERTA =
            incidencia -> "ABIERTA".equals(incidencia.estado());

    // Predicate recibe una incidencia y devuelve un boolean.
    // Determina si la prioridad es alta.
    public static final Predicate<Incidencia> PRIORIDAD_ALTA =
            incidencia -> incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

    // Predicate combina dos condiciones booleanas mediante and.
    // Requiere que la incidencia esté abierta y tenga prioridad alta.
    public static final Predicate<Incidencia> REQUIERE_ATENCION =
            ESTA_ABIERTA.and(PRIORIDAD_ALTA);

    // Function transforma una incidencia en una cadena de texto.
    // Construye un resumen con el identificador y el título.
    public static final Function<Incidencia, String> RESUMEN =
            incidencia -> incidencia.id() + " - " + incidencia.titulo();

    // Function recibe una incidencia y devuelve su categoría.
    // La clasificación depende de la prioridad asignada.
    public static final Function<Incidencia, String> CATEGORIA =
            incidencia -> switch (incidencia.prioridad()) {
                case 5 -> "CRITICA";
                case 4 -> "ALTA";
                case 2, 3 -> "MEDIA";
                case 1 -> "BAJA";
                default -> throw new IllegalArgumentException(
                        "Prioridad no válida: " + incidencia.prioridad()
                );
            };

    // Consumer recibe una incidencia y ejecuta una acción sin retorno.
    // Muestra una notificación con el resumen de la incidencia.
    public static final Consumer<Incidencia> NOTIFICAR =
            incidencia -> System.out.println(
                    "[ATENCION] " + RESUMEN.apply(incidencia)
            );

    // Supplier no recibe parámetros y devuelve una incidencia.
    // Genera registros sintéticos con identificadores consecutivos.
    public static Supplier<Incidencia> generadorSintetico() {

        AtomicInteger contador = new AtomicInteger(1);

        String[] estados = {
                "ABIERTA",
                "EN_PROCESO",
                "CERRADA"
        };

        return () -> {
            int n = contador.getAndIncrement();

            return new Incidencia(
                    "INC-%03d".formatted(n),
                    "Incidencia sintética " + n,
                    estados[n % estados.length],
                    (n % 5) + 1
            );
        };
    }
    public static boolean esCritica(Incidencia incidencia) {
        return incidencia.prioridad() == 5;
    }
}
