package proyectoGuardias;

import java.io.*;
import java.util.*;

public class SistemaGuardias {

    Scanner sc = new Scanner(System.in);
    private File packageProfes = new File("src/proyectoGuardias/profes");
    private File sustitucionesFile = new File("src/proyectoGuardias/sustituciones/sustituciones.csv");
    private ArrayList<Profesor> profes;

    public void iniciar() {
        System.out.println("Sistema de Guardias iniciado.");
        menu();
    }

    public void menu(){
        while(true) {
            System.out.println("----- Menú -----");
            System.out.println("1. Asignar sustitución");
            System.out.println("2. Ranking de profesores y sus sustituciones");
            System.out.println("3. Salir");
            String opcion = sc.nextLine();
            switch (opcion) {
                case "1":
                    asignarSustitucionMenu();
                    break;
                case "2":
                    System.out.println("Ranking de profesores por número de sustituciones:");
                    ArrayList<Profesor> profesRanking = cargarProfesores(packageProfes);
                    profesRanking.sort(Comparator.comparingInt(Profesor::getNumSustituciones).reversed());
                    for (Profesor p : profesRanking) {
                        System.out.println(" - " + putMayuscula(p.getNombre()) + ": " + p.getNumSustituciones() + " sustituciones");
                    }
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción del 1 al 3.");
            }
        }

    }

    public void asignarSustitucionMenu(){
        String nombreProfe ="";
        String dia ="";
        String hora ="";
        boolean profeCorrecto = false;
        boolean diaCorrecto = false;
        boolean horaCorrecta = false;
        String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes"};
        String[] horas = {"8-9", "9-10", "10-11", "11-12", "12-13", "13-14"};
        if(packageProfes.isDirectory()){
            //profesor
            while(!profeCorrecto) {
                System.out.print("Que profesor ha faltado?: ");
                mostrarProfesores(packageProfes);
                nombreProfe = sc.nextLine().toLowerCase();
                profeCorrecto = comprobarProfesor(nombreProfe, packageProfes);
            }
            //dia
            while(!diaCorrecto) {
                System.out.print("Que día?: ");
                mostrar(dias);
                dia = sc.nextLine().toLowerCase();
                diaCorrecto = comprobarDia(dia, dias);
            }
            //hora
            while(!horaCorrecta) {
                System.out.print("Que hora?: ");
                mostrar(horas);
                hora = sc.nextLine().toLowerCase();
                horaCorrecta = comprobarHora(hora, horas);
            }
        }else{
            System.out.println("El archivo " + packageProfes.getName() + "no es una carpeta");
        }
        System.out.println("Profesores libres para sustituir:");
        cargarProfesores(packageProfes);
        buscarProfesoresLibres(dia, hora, nombreProfe);


        ArrayList<Profesor> profeLibres = buscarProfesoresLibres(dia, hora,nombreProfe);
        if(profeLibres.isEmpty()){
            System.out.println("No hay profesores libres para sustituir.");
        }else {
            for (Profesor p : profeLibres) {
                System.out.println(" - " + putMayuscula(p.getNombre()) + " (" + p.getNumSustituciones() + " sustituciones)");
            }
        }

        boolean asignado = false;
        while(!asignado && !profeLibres.isEmpty()) {
            System.out.print("Qué profesor va a sustituir?: ");
            String profeSustituto = sc.nextLine().toLowerCase();
            asignado = asignarSustitucion(profeSustituto);
            if (!asignado) {
                System.out.println("El profesor introducido no es correcto o no está libre para sustituir.");
            }
        }
    }
    //poner mayuscula al principio
    public String putMayuscula(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    //mostrar profesores para introducirlo correctamente
    public void mostrarProfesores(File packageProfes){
        File[] profes = packageProfes.listFiles();
        for(int i = 0; i < profes.length; i++){
            String nombreProfe = profes[i].getName().replace(".csv", "");
            if(i == profes.length-1) {
                System.out.println(" - " + putMayuscula(nombreProfe));
            }else if(i == 0){
                System.out.print(putMayuscula(nombreProfe));
            }else {
                System.out.print(" - " + putMayuscula(nombreProfe));
            }

        }
    }

    //mostrar dias y horas para introducirlo correctamente
    public void mostrar(String[] array){
        for(int i = 0; i < array.length; i++){
            if(i == array.length-1){
                System.out.println(" - " + array[i]);
            }else if(i == 0){
                System.out.print(array[i]);
            }else{
                System.out.print(" - " + array[i]);
            }
        }
    }

    //comprobacion de datos introducidos
    public boolean comprobarProfesor(String nombreProfe, File packageProfes){
        File[] profes = packageProfes.listFiles();
        boolean profeCorrecto = false;

        for(File profe : profes){
            String nombreArchivo = profe.getName().replace(".csv", "").toLowerCase();
            if(nombreArchivo.equals(nombreProfe)){
                profeCorrecto = true;
                break;
            }
        }

        if(!profeCorrecto){
            System.out.println("El profesor introducido no es correcto.");
        }

        return profeCorrecto;
    }

    public boolean comprobarDia(String dia, String[] dias){
        boolean diaCorrecto = false;

        for(String d : dias){
            if(d.toLowerCase().equals(dia)){
                diaCorrecto = true;
                break;
            }
        }

        if(!diaCorrecto){
            System.out.println("El día introducido no es correcto.");
        }

        return diaCorrecto;
    }

    public boolean comprobarHora(String hora, String[] horas){
        boolean horaCorrecta = false;

        for(String h : horas){
            if(h.toLowerCase().equals(hora)){
                horaCorrecta = true;
                break;
            }
        }

        if(!horaCorrecta){
            System.out.println("La hora introducida no es correcta.");
        }

        return horaCorrecta;
    }

    public ArrayList<Profesor> cargarProfesores(File packageProfes){
        profes = new ArrayList<>();
        File[] archivos = packageProfes.listFiles();
        String nombre ="";

        for(File archivo : archivos){
            //estructura: Map<DIA, Map<HORA, MATERIA>>
            Map<String , Map<String, String>> horario = new LinkedHashMap<>();

            try(BufferedReader br = new BufferedReader(new FileReader(archivo))){
                String linea;
                nombre = archivo.getName().replace(".csv", "");

                int row = 0;
                while((linea = br.readLine()) != null){
                    //separar linea del csv por ;
                    String[] valores = linea.split(";");
                    if(row == 0){
                        //poner dias en el primer String del map
                        for(int i = 1; i < valores.length; i++){
                            horario.put(valores[i], new LinkedHashMap<>());
                        }
                    }
                    if(row != 0){
                        //poner horas y materias en el segundo map interno
                        //sacar claves para poner en cada dia la hora y materia correspondiente
                        List<String> lista = new ArrayList<>(horario.keySet());
                        int i = 0;
                        for(int col = 1; col < valores.length; col++){
                            //añadir a cada dia su hora y materia
                            horario.get(lista.get(i)).put(valores[0], valores[col]);
                            i++;
                        }
                    }
                    row++;
                }

            }catch (IOException e){
                System.out.println("Error al leer el archivo: " + archivo.getName());
            }

            //crear instnancia a cada profesor con su nombre y horario personalizado
            Profesor profesor = new Profesor(nombre, horario);
            profes.add(profesor);
        }

        try(BufferedReader br = new BufferedReader(new FileReader(sustitucionesFile))){
            String linea;

            int fila = 0;
            while((linea = br.readLine()) != null){
               if(fila != 0){
                   String[] valores = linea.split(";");
                   String nombreProfe = valores[0].toLowerCase();
                   int numSustituciones = Integer.parseInt(valores[1]);

                   //buscar profesor y poner las sustituciones que lleva
                   for(Profesor p : profes){
                       if(p.getNombre().toLowerCase().equals(nombreProfe)){
                           p.setNumSustituciones(numSustituciones);
                       }
                   }
               }
               fila++;
            }
        }catch (IOException e){
            System.out.println("Error al leer el archivo: " + sustitucionesFile.getName());
        }

        return profes;
    }

    public ArrayList<Profesor> buscarProfesoresLibres(String dia, String hora, String nombreProfe) {
        ArrayList<Profesor> profeLibres = new ArrayList<>();
        for (Profesor p : profes) {
            if (!p.getNombre().equals(nombreProfe)) {
                Map<String, Map<String, String>> horario = p.getHorario();
                if (horario.containsKey(dia)) {
                    Map<String, String> horasMap = horario.get(dia);
                    if (horasMap.containsKey(hora)) {
                        String materia = horasMap.get(hora);
                        if (materia.equals("GUARDIA")) {
                            profeLibres.add(p);
                        }
                    }
                }
            }
        }
        //ordenar por numero de sustituciones de menor a mayor
        profeLibres.sort(Comparator.comparingInt(Profesor::getNumSustituciones));

        return profeLibres;
    }

    public boolean asignarSustitucion(String nombreProfe) {
        boolean asignado = false;
        for (Profesor p : profes) {
            if (p.getNombre().equals(nombreProfe)) {
                p.incrementarSustituciones();
                asignado = true;
                break;
            }
        }
        if (asignado) {
            actualizarSustitucionesFile(nombreProfe);
        }
        return asignado;
    }

    public void actualizarSustitucionesFile(String nombreProfe) {
        ArrayList<String> line = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(sustitucionesFile))){
            String linea;
            while((linea = br.readLine()) != null){
                String[] valores = linea.split(";");
                if(valores[0].equalsIgnoreCase(nombreProfe)){
                    valores[1] = String.valueOf(Integer.parseInt(valores[1]) + 1);
                    linea = String.join(";",valores);
                }
                line.add(linea);
            }
        }catch(IOException e){
            System.out.println("Error al leer el archivo de sustituciones: " + sustitucionesFile.getName());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sustitucionesFile))) {
            for(String l : line){
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo de sustituciones: " + sustitucionesFile.getName());
        }
        System.out.println("Sustitución asignada a " + putMayuscula(nombreProfe) + " correctamente.");
    }

}
