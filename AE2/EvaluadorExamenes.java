import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EvaluadorExamenes {
    File directoryClass = new File("/Users/miquelroca/Desktop/ADMiRoMa/AE2/clase");
    Scanner sc = new Scanner(System.in);
    private static final String[] NOTAS = {"100", "66", "33", "0"};
    public void iniciar(){
        menu();
    }

    public void menu() {
        System.out.println("-----------Bienvenido al evaluador de exámenes-----------");
        File[] examanes = leerCantidadExamenes();
        if(examanes.length==0){
            System.out.println("NO hay ningún examen en el sistema para corregir");
        }else {
            System.out.println("Que examen quieres corregir? (Pulse número): ");
            AtomicInteger contadorEx = new AtomicInteger(0);
            Arrays.stream(examanes).forEach(f-> {
                int i = contadorEx.getAndIncrement();
                System.out.println((i+1)+". " + leerTituloExamen(f));
            });
            String opcionSelectedEx = "";
            do{
                opcionSelectedEx = sc.nextLine();
            }while(comprobarOpcion(opcionSelectedEx, examanes.length));

            System.out.println("--------------------------------------------");
            System.out.println("Que alumno quieres corregir? (Pulse número): ");
            ArrayList<String> alumnos = leerAlumnos();
            if(alumnos.isEmpty()){
                System.out.println("NO hay alumnos que corregir");
            }else{
                //Listar alumnos para corregir
                AtomicInteger contadorAl = new AtomicInteger(0);
                alumnos.forEach(n-> {
                    int i = contadorAl.getAndIncrement();
                    System.out.println((i+1)+". " + n);
                });
                //Comprobar opción seleccionada
                String opcionSelectedAl = "";
                do{
                    opcionSelectedAl = sc.nextLine();
                }while(comprobarOpcion(opcionSelectedAl, alumnos.size()));
                // -1 ya que en el menu se suma +1 para no mostrar un 0 en el menu y que se muestre mejor la lista
                corregirExamen(examanes[Integer.parseInt(opcionSelectedEx)-1], alumnos.get(Integer.parseInt(opcionSelectedAl)-1));
            }
        }
    }

    public void corregirExamen(File examen, String alumno){
        //Guardar notas de cada alumno
        ArrayList<String> notas = new ArrayList<String>();
        //Para saber de que es el examen y ponerlo en el .txt que guardaremos con las notas
        AtomicReference<String> tituloExamen = new AtomicReference<>("");
         try(Stream<String> lineas = Files.lines(examen.toPath())){
             //Recorrer examen
            AtomicInteger cont = new AtomicInteger(0);
            lineas.forEach(line ->{
                int i = cont.getAndIncrement();
                //Saltar titulo
                if(i != 0){
                    String[] valores = line.split(";");
                    //Dependiendo de la linea se mostrara una cosa u otra,
                    //Si estamos en la liena de la pregunta solo mostrar pregunta (Lines pares)
                    //Si estamos en linea de requisitos mostramos los requisitos y sus notas respesctivas
                    if(i % 2 != 0){
                        System.out.println(valores[0]);
                    }else{
                        IntStream.range(0, valores.length).forEach( index->{
                            System.out.println((index+1) +". "+ NOTAS[index]+" - "+ valores[index]);
                        });
                        System.out.print("Que nota le pones?(Elige número de la lista): ");
                        String opcionSelectedNota = "";
                        do{
                            opcionSelectedNota = sc.nextLine();
                        }while(comprobarOpcion(opcionSelectedNota, NOTAS.length));
                        notas.add(NOTAS[Integer.parseInt(opcionSelectedNota)-1]);
                    }
                }else{
                    tituloExamen.set(line);
                }
            });
            //Guardamos notas en nuevo archivo para cada examen
            guardarNotas(notas, alumno, tituloExamen);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void guardarNotas(ArrayList<String> notas, String alumno, AtomicReference<String> tituloExamen){
        //Abrimos el archivo donde irán las notas y el de nombres para leer los alumnos si no está creado el archivo de resultados
        // de una examen concreto
        File results = new File(directoryClass.getAbsolutePath()+"/resultados_"+tituloExamen.get().replace(" ", "_")+".txt");
        File alumnos = new File(directoryClass.getAbsolutePath()+"/nombres.txt");
        ArrayList<String> escribirLineas = new ArrayList<>();

        //Si existe leemos de otra manera que si no existiera, ya que leemos directamente el archivo de los resultados
        if(results.exists()){
            try(Stream<String> lines = Files.lines(results.toPath())){
                lines.forEach(line ->{
                    //Dividimos la linea y sacamos los 3 primeros valores que serán el nombre de cada alumno
                    String[] partes = line.split(";");
                    String nombre = String.join(" ", Arrays.copyOfRange(partes, 0, 3));
                    //Si es el alumno que estamos corrigiendo agregar las notas también
                    if(nombre.equalsIgnoreCase(alumno)){
                        //Por si acaso corregimos un alumno que ya fue corregido, para sobreescribir correctamente
                        //sacamos el nombre y luego sobreescribimos las notas
                        line = String.join(";", Arrays.copyOfRange(partes, 0, 3)) + ";"+ String.join(";", notas);
                        escribirLineas.add(line);
                    }else escribirLineas.add(line);
                });
            }catch(IOException e){
                e.printStackTrace();
            }
            //Escribir lo leído
            try(BufferedWriter bw = Files.newBufferedWriter(results.toPath())){
                for (String escribirLinea : escribirLineas) {
                    bw.write(escribirLinea);
                    bw.newLine();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            //Si no existe, como no esta lleno el archivo de resultados leeremos el archivo de nombres y rellenamos con los nombres,
            //agregando también la nota del alumno que estamos corrigiendo en ese momento
                try(Stream<String> lines = Files.lines(alumnos.toPath())){
                    lines.forEach(line ->{
                        //Dividimos la linea y sacamos los 3 primeros valores que serán el nombre de cada alumno
                        String[] partes = line.split(";");
                        String nombre = String.join(" ", Arrays.copyOfRange(partes, 0, 3));
                        //Si es el alumno que estamos corrigiendo agregar las notas también
                        if(nombre.equalsIgnoreCase(alumno)){
                            line = line+";" + String.join(";", notas);
                            escribirLineas.add(line);
                        }else escribirLineas.add(line);
                    });
                }catch(IOException e){
                    e.printStackTrace();
                }

                //Escribir en el nuevo archivo de resultados todo lo leído
                try(BufferedWriter bw = Files.newBufferedWriter(results.toPath())){
                    for (String escribirLinea : escribirLineas) {
                        bw.write(escribirLinea);
                        bw.newLine();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
        }
    }

    public boolean comprobarOpcion(String s, int limite){
        if (s == null || s.isEmpty()){ System.out.println("Número fuera del rango, entre 1 y "+ limite); return true;}
        try {
             int number = Integer.parseInt(s);
            if(number > 0 && number <= limite){
                return false;
            }else{System.out.println("Número fuera del rango, entre 1 y "+ limite); return true;}
        } catch (NumberFormatException e) {
            System.out.println("Número fuera del rango, entre 1 y "+ limite);
            return true;
        }
    }

    public ArrayList<String> leerAlumnos(){
        File alumnos = Arrays.stream(directoryClass.listFiles()).filter(f-> f.getName().contains("nombres")).findFirst().orElse(null);
        ArrayList<String> nombres = new ArrayList<>();
        //Comprobamos si hay archivo de alumnos
        if(alumnos!=null){
            try(Stream<String> lineas = Files.lines(alumnos.toPath())){
                lineas.forEach(line-> {
                    //Sacamos los nombres completos separados
                    String[] valores = line.split(";");
                    nombres.add(String.join(" ", valores));
                });
                //Devolvemos arraylist de nombres de los alumnos
                return nombres;
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    //Devolver solo los examenes
    public File[] leerCantidadExamenes(){
        //Controlamos si es un directorio
        if(directoryClass.isDirectory()){
            //Filtramos para sacar solo los examenes
            File[] examenes = Arrays.stream(directoryClass.listFiles()).filter(f-> f.getName().contains("examen")).toArray(File[]::new);
            //Comprobar si hay examenes en el directorio si no salir
            if(examenes == null) return new File[0];
            return examenes;
        }
        return new File[0];
    }

    //Leer el titulo para saber que examen se va a corregir
    public String leerTituloExamen(File f){
        try(BufferedReader bf = new BufferedReader(new FileReader(f))){
            //Leer y añadir primera línea del archivo del examen que será el título
            return bf.readLine();
        }catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
