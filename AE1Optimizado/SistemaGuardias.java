
import java.io.BufferedWriter;
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
        System.out.print("Que profesor ha faltado: ");
        listaProfesores();
        String nombreProfesor = comprobarProfesor();
        String dia = comprobarDia();
        String hora = comprobarHora();
        System.out.print("Elige un profesor para la sustitución: ");
        listaProfesoresLibres(nombreProfesor, dia, hora);
        String profesorSustituto = comprobarProfesor();
        registrarSustitucion(profesorSustituto, dia, hora);

        System.out.println("---------------------------------------------");
    }

    public void registrarSustitucion(String nombreProfesor, String dia, String hora){
        Profesor profesor = profesores.stream().filter(p -> p.getNombre().equalsIgnoreCase(nombreProfesor)).findFirst().orElse(null);
        if(profesor != null){
            profesor.incrementarSustituciones();
            actualizarArchivoSustituciones(nombreProfesor);
            System.out.println("Sustitución registrada: " + nombreProfesor + " - Día: " + dia + " - Hora: " + hora);
        }else {
            System.out.println("Error: Profesor no encontrado.");
        }
    }

    public void actualizarArchivoSustituciones(String nombreProfesor){
        File archivo = new File("sustituciones/sustituciones.csv");
        ArrayList<String> sobreescribirLineas = new ArrayList<>();
        try(Stream<String> lines = Files.lines(Path.of(archivo.getPath()))){
            lines.forEach(line ->{
                String[] datos = line.split(";");
                if(datos[0].equalsIgnoreCase(nombreProfesor)){
                    datos[1] = String.valueOf(Integer.parseInt(datos[1]) + 1);
                    String newLine = String.join(";", datos);
                    sobreescribirLineas.add(newLine);
                }else{
                    sobreescribirLineas.add(line);
                }
            });
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        try(BufferedWriter bf = Files.newBufferedWriter(Path.of(archivo.getPath()))){
            for(String linea : sobreescribirLineas){
                bf.write(linea);
                bf.newLine();
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }


    public String comprobarHora(){
        String[] horas = profesores.get(0).getHorario().get(profesores.get(0).getHorario().keySet().toArray(new String[0])[0]).keySet().toArray(new String[0]);
        return comprobarOpcion("hora", horas);
    }
    public String comprobarDia(){
        String[] dias = profesores.get(0).getHorario().keySet().toArray(new String[0]);
        return comprobarOpcion("día", dias);
    }
    public String comprobarOpcion(String tipo, String[] opciones){
        String eleccion;
        while(true){
            System.out.print("Qué " + tipo + "?: ");
            mostrarOpciones(opciones);
            eleccion = sc.nextLine().trim();
            String finalEleccion = eleccion;
            if(Arrays.stream(opciones).anyMatch(d -> d.equalsIgnoreCase(finalEleccion))) break;
            System.out.println(eleccion + " no es un " + tipo + " válido. Inténtalo de nuevo.");
        }
        return eleccion;
    }

    public void mostrarOpciones(String[] opciones){
        Arrays.stream(opciones).forEach(d -> System.out.print( d + " / ") );
        System.out.println();
    }

    public void listaProfesoresLibres(String profesorSustituido, String dia, String hora){
        profesores.stream().filter(p-> !p.getNombre().equalsIgnoreCase(profesorSustituido) &&
                p.getHorario().get(dia).get(hora).equalsIgnoreCase("GUARDIA")).forEach(
                p -> System.out.print(p.getNombre() + " - ")
        );
    }

    public void listaProfesores() {
        profesores.stream().forEach(p -> System.out.print(p.getNombre()+ " - "));
    }

    public String comprobarProfesor(){
        while(true){
            String nombre = sc.nextLine();
            Optional<Profesor> profesorOpt = profesores.stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst();
            if(profesorOpt.isPresent()) {
                return nombre;
            }else{
                System.out.print("Profesor no encontrado. Inténtalo de nuevo: ");
                continue;
            }
        }
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
