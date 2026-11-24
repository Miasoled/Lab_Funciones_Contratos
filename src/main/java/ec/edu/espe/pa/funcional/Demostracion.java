package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.contrato.FabricaIncidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

public class Demostracion {

    public static void main(String[] args) {

        // FASE 2: INTERFACES FUNCIONALES

        Predicate<Incidencia> estaAbierta =
                i -> "ABIERTA".equals(i.estado());

        Function<Incidencia, String> obtenerResumen =
                i -> i.id() + " - " + i.titulo();

        Consumer<Incidencia> mostrar =
                i -> System.out.println(i.titulo());

        Supplier<Incidencia> crearEjemplo =
                () -> new Incidencia(
                        "INC-001", "Error de acceso", "ABIERTA", 3
                );

        Incidencia ejemplo = crearEjemplo.get();

        System.out.println("== Interfaces funcionales ==");
        System.out.println("Predicate: " + estaAbierta.test(ejemplo));
        System.out.println("Function: " + obtenerResumen.apply(ejemplo));

        System.out.print("Consumer: ");
        mostrar.accept(ejemplo);

        System.out.println("Supplier: " + ejemplo.id());

        Predicate<Incidencia> prioridadAlta =
                i -> i.prioridad() >= 4;

        Predicate<Incidencia> prioridadAltaBloque = i -> {
            int prioridad = i.prioridad();
            return prioridad >= 4;
        };

        System.out.println("== Expresiones lambda ==");
        System.out.println("Lambda simple: " + prioridadAlta.test(ejemplo));
        System.out.println("Lambda con bloque: "
                + prioridadAltaBloque.test(ejemplo));

        int umbral = 4;

        Predicate<Incidencia> superaUmbral =
                i -> i.prioridad() >= umbral;

        System.out.println("Umbral: " + superaUmbral.test(ejemplo));

        // FASE 3: CONTRATO FUNCIONAL PROPIO

        System.out.println("== Contrato funcional propio ==");

        ReglaIncidencia reglaClase = new PrioridadAlta();

        ReglaIncidencia abiertaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return "ABIERTA".equals(incidencia.estado());
            }
        };

        ReglaIncidencia abiertaLambda =
                i -> "ABIERTA".equals(i.estado());

        ReglaIncidencia requiereAtencion =
                abiertaLambda.y(new PrioridadAlta());

        Predicate<Incidencia> comoPredicado =
                requiereAtencion::evaluar;

        Incidencia incidenciaAlta = new Incidencia(
                "INC-002", "Caída del servidor", "ABIERTA", 5
        );

        System.out.println("Clase con nombre: "
                + reglaClase.evaluar(incidenciaAlta));

        System.out.println("Clase anónima: "
                + abiertaAnonima.evaluar(incidenciaAlta));

        System.out.println("Expresión lambda: "
                + abiertaLambda.evaluar(incidenciaAlta));

        System.out.println("Composición: "
                + requiereAtencion.evaluar(incidenciaAlta));

        System.out.println("Referencia a método: "
                + comoPredicado.test(incidenciaAlta));

        // FASE 5: COMPOSICIÓN DE PREDICATE

        System.out.println("== Composición de Predicate ==");

        Predicate<Incidencia> noAbierta =
                ESTA_ABIERTA.negate();

        Predicate<Incidencia> pendienteBaja =
                ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate());

        Predicate<Incidencia> revisarHoy =
                REQUIERE_ATENCION.or(i -> i.prioridad() == 5);

        System.out.println("No abierta: "
                + noAbierta.test(incidenciaAlta));

        System.out.println("Pendiente baja: "
                + pendienteBaja.test(ejemplo));

        System.out.println("Revisar hoy: "
                + revisarHoy.test(incidenciaAlta));

        // FASE 5: COMPOSICIÓN DE FUNCTION

        System.out.println("== Composición de Function ==");

        Function<String, String> aMayusculas =
                String::toUpperCase;

        Function<Incidencia, String> resumenMayusculas =
                RESUMEN.andThen(aMayusculas);

        Function<Incidencia, String> mismoResultado =
                aMayusculas.compose(RESUMEN);

        Function<Incidencia, String> etiqueta =
                CATEGORIA.andThen(c -> "[" + c + "]");

        System.out.println("andThen: "
                + resumenMayusculas.apply(incidenciaAlta));

        System.out.println("compose: "
                + mismoResultado.apply(incidenciaAlta));

        System.out.println("Etiqueta: "
                + etiqueta.apply(incidenciaAlta));

        // FASE 5: COMPOSICIÓN DE CONSUMER

        System.out.println("== Composición de Consumer ==");

        List<String> bitacora = new ArrayList<>();

        Consumer<Incidencia> registrar =
                i -> bitacora.add(i.id());

        Consumer<Incidencia> notificarYRegistrar =
                NOTIFICAR.andThen(registrar);

        notificarYRegistrar.accept(incidenciaAlta);

        System.out.println("Bitácora: " + bitacora);

        // FASE 5: REFERENCIAS A MÉTODOS

        System.out.println("== Referencias a métodos ==");

        // 1. Referencia a un método estático
        Predicate<Incidencia> esCritica =
                ReglasIncidencia::esCritica;

        System.out.println("Método estático: "
                + esCritica.test(incidenciaAlta));

        // 2. Referencia a un método de un objeto
        List<Incidencia> incidencias =
                List.of(ejemplo, incidenciaAlta);

        incidencias.forEach(System.out::println);

        // 3. Referencia a un método de instancia
        Function<Incidencia, String> titulo =
                Incidencia::titulo;

        System.out.println("Título: "
                + titulo.apply(incidenciaAlta));

        // 4. Referencias a constructores
        Supplier<List<Incidencia>> nuevaLista =
                ArrayList::new;

        FabricaIncidencia fabrica =
                Incidencia::new;

        List<Incidencia> listaNueva =
                nuevaLista.get();

        Incidencia creada = fabrica.crear(
                "INC-003",
                "Fallo de red",
                "ABIERTA",
                4
        );

        listaNueva.add(creada);

        System.out.println("Constructor: "
                + creada);

        System.out.println("Elementos en lista nueva: "
                + listaNueva.size());
    }
}
