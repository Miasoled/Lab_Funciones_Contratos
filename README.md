# Laboratorio 2.1: funciones y contratos funcionales en Java

Este proyecto implementa un pequeño sistema de procesamiento de incidencias para practicar programación funcional en Java 21. Cada incidencia contiene un identificador, un título, un estado y una prioridad del 1 al 5.

## ¿Qué hace el programa?

El programa identifica las incidencias que requieren atención: deben estar en estado `ABIERTA` y tener prioridad 4 o 5. Muestra el resultado mediante dos implementaciones equivalentes:

- **Imperativa:** recorre la lista con un ciclo `for` y una condición `if`.
- **Funcional:** filtra y transforma los datos con Streams y funciones de `java.util.function`.

También genera notificaciones para las incidencias seleccionadas y presenta un resumen de todas las incidencias abiertas junto con su categoría: `BAJA`, `MEDIA`, `ALTA` o `CRITICA`.

## Temas de programación funcional

El código demuestra `Predicate`, `Function`, `Consumer` y `Supplier`; expresiones lambda; composición de condiciones y funciones; referencias a métodos y constructores; y contratos funcionales propios. `ReglaIncidencia` permite combinar reglas con `y`, mientras que `FabricaIncidencia` se utiliza como referencia al constructor del record `Incidencia`.

## Estructura principal

```text
src/main/java/ec/edu/espe/pa/funcional/
├── App.java
├── Demostracion.java
├── modelo/       Incidencia.java
├── contrato/     ReglaIncidencia.java, FabricaIncidencia.java
├── reglas/       ReglasIncidencia.java, PrioridadAlta.java
├── imperativo/   ProcesadorImperativo.java
└── funcional/    ProcesadorFuncional.java

src/test/java/ec/edu/espe/pa/funcional/
├── ReglasIncidenciaTest.java
└── EquivalenciaTest.java
```

`Incidencia` es un record inmutable. Su constructor valida que el identificador y el estado no sean nulos y que la prioridad esté dentro del rango permitido. `ReglasIncidencia` centraliza los criterios de selección, el resumen, las categorías, la notificación y la generación de datos de ejemplo.

## Requisitos

- JDK 21
- Apache Maven

El proyecto usa JUnit Jupiter 5.11.4 para las pruebas y Maven Surefire 3.2.5 para ejecutarlas.

## Compilar y ejecutar

Desde la carpeta raíz del proyecto:

```powershell
mvn clean test
mvn package
java -cp target/classes ec.edu.espe.pa.funcional.App
```

Para ver los ejemplos de interfaces, composición y referencias a métodos:

```powershell
java -cp target/classes ec.edu.espe.pa.funcional.Demostracion
```

## Pruebas

Las pruebas cubren los estados y prioridades, los valores límite, las validaciones del record, la composición de funciones y reglas, el generador de incidencias y la equivalencia entre los procesadores imperativo y funcional. Para ejecutarlas se usa `mvn test`.