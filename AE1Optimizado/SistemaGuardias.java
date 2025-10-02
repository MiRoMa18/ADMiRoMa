
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SistemaGuardias {
    Scanner sc = new Scanner(System.in);
    ArrayList<Profesor> profesores = new ArrayList<>();
    public void iniciar(){
        System.out.println("Sistema de Gestión de Guardias Optimizado");
        cargarProfesores();
        cargarSustituciones();
        menu();
    }

    public void menu(){
        System.out.println("Menú Principal");
        System.out.println("1. Gestionar Sustituciones");
        System.out.println("2. Lista sustituciones de cada profesor");
        System.out.println("3. Ranking de profesores con más guardias");
        System.out.println("4. Salir");
        int opcion = comprobacionOpcion();
        switch (opcion){
            case 1:
                gestionarSustituciones();
                menu();
                break;
            case 2:
                listaSustituciones();
                menu();
                break;
            case 3:
                rankingSustituciones();
                menu();
                break;
            case 4:
                System.out.println("Saliendo del sistema. ¡Hasta luego!");
                break;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
                menu();
                break;
        }
    }

    public void gestionarSustituciones(){
        System.out.println("----------Gestión de Sustituciones----------");
        System.out.println("Que profesor ha faltado");

        System.out.println("---------------------------------------------");
    }

    public void rankingSustituciones(){
        System.out.println("----------Ranking de profesores con más guardias:----------");
        profesores.stream().sorted(Comparator.comparing(Profesor::getNumSustituciones).reversed())
                .forEach(p -> System.out.println("Profesor: " + p.getNombre() + " - Sustituciones: " + p.getNumSustituciones()));
        System.out.println("-----------------------------------------------------------");
    }

    public void listaSustituciones(){
        System.out.println("----------Lista de sustituciones por profesor:----------");
        profesores.forEach(p -> {
            System.out.println("Profesor: " + p.getNombre() + " - Sustituciones: " + p.getNumSustituciones());
        });
        System.out.println("--------------------------------------------------------");
    }

    public int comprobacionOpcion(){
        int numero = -1;
        boolean valido = false;

        while (!valido) {
            System.out.print("Introduce un número entre 1 y 4: ");
            String entrada = sc.nextLine();

            try {
                numero = Integer.parseInt(entrada);
                if (numero >= 1 && numero <= 4) {
                    valido = true;
                } else {
                    System.out.println("Opción no válida. Debe ser un número entre 1 y 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debes introducir un número entero válido.");
            }
        }
        return numero;
    }

    public void cargarProfesores(){
        File carpeta = new File("profes");
        File[] archivos = carpeta.listFiles();
        if(archivos != null){
            Arrays.stream(archivos).forEach(p -> {
                String nombreProfesor = p.getName().replace(".csv", "");
                Map<String, Map<String, String>> horario = new LinkedHashMap<>();
                System.out.println("Cargando datos del profesor: " + nombreProfesor);
                try(Stream<String> lines = Files.lines(Path.of(p.getPath()))){
                    AtomicInteger lineNumber = new AtomicInteger(0);
                    lines.forEach( line -> {
                        String[] datos = line.split(";");
                        if(lineNumber.getAndIncrement() == 0) {
                            Arrays.stream(datos).skip(1).forEach(dia -> {
                                horario.put(dia, new LinkedHashMap<>());
                            });
                        }else{
                            String hora = datos[0];
                            AtomicInteger colIndex = new AtomicInteger(1);
                            horario.keySet()
                                    .forEach(dia -> {
                                        String asignatura = datos[colIndex.getAndIncrement()];
                                        horario.get(dia).put(hora, asignatura);
                                    });
                        }
                    });
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Profesor profesor = new Profesor(nombreProfesor, horario);
                profesores.add(profesor);
            });
        }else{
            System.out.println("No se encontraron archivos de profesores.");
        }
    }

    public void cargarSustituciones(){
        File archivo = new File("sustituciones/sustituciones.csv");
        if(archivo.exists()){
            try(Stream<String> lines = Files.lines(Path.of(archivo.getPath()))){
                lines.skip(1).forEach( line -> {
                    String[] datos = line.split(";");
                    profesores.stream().filter(p -> p.getNombre().equalsIgnoreCase(datos[0])).findFirst().ifPresent(profesor -> {
                        profesor.setNumSustituciones(Integer.parseInt(datos[1]));
                    });
                });
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("No se encontró el archivo de sustituciones.");
        }
    }
}
