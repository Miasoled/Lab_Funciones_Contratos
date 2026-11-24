package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;

import java.util.List;

public class App {

    public static void main(String[] args) {

        List<Incidencia> datos = List.of(
            new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5),
            new Incidencia("INC-002", "Error tipográfico en el menú", "ABIERTA", 2),
            new Incidencia("INC-003", "Fallo de respaldo nocturno", "CERRADA", 5),
            new Incidencia("INC-004", "Lentitud en reportes", "ABIERTA", 3),
            new Incidencia("INC-005", "Error de acceso al sistema", "ABIERTA", 4),
            new Incidencia("INC-006", "Actualizar certificado", "EN_PROCESO", 4)
        );

        ProcesadorImperativo imperativo = new ProcesadorImperativo();
        ProcesadorFuncional funcional = new ProcesadorFuncional();

        System.out.println("== Versión imperativa ==");
        imperativo.mostrarQueRequierenAtencion(datos);

        System.out.println("== Versión funcional ==");
        funcional.ejecutar(datos, ReglasIncidencia.REQUIERE_ATENCION,
                incidencia -> System.out.println(incidencia.titulo()));

        System.out.println("== Notificaciones ==");
        funcional.ejecutar(datos, ReglasIncidencia.REQUIERE_ATENCION,
                ReglasIncidencia.NOTIFICAR);

        System.out.println("== Incidencias abiertas con su categoría ==");
        funcional.seleccionar(datos, ReglasIncidencia.ESTA_ABIERTA,
                incidencia -> ReglasIncidencia.RESUMEN.apply(incidencia)
                        + " [" + ReglasIncidencia.CATEGORIA.apply(incidencia) + "]")
                .forEach(System.out::println);
    }
}