import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class ProgramaArchivos {
    File f;
    Scanner sc = new Scanner(System.in);
    ArrayList<String> tree = new ArrayList<>();

    public ProgramaArchivos(String[] args){
        this.f = new File(args.length > 0 ? args[0] : "");
    }
    public void iniciar(){
        if(comprobarRutaIntroducida()) {
            menu();
        }
    }

    public void menu(){
        System.out.println("---------------- Menú Archivos ----------------");
        System.out.println("1. Mostrar información");
        System.out.println("2. Crear carpeta");
        System.out.println("3. Crear archivo");
        System.out.println("4. Eliminar carpeta/archivo");
        System.out.println("5. Actualizar nombre carpeta/archivo");
        System.out.println("6. Salir");
        System.out.print("Que desea hacer? ");
        int opcion = comprobarOpcion();
        File fileSelect = null;
        if(opcion!=6){
            System.out.println("Donde lo desea hacer? Copia y pega la ruta directamente:");
            System.out.println("-----------------------------------------------------");
            tree.clear();
            tree = mostrarTree(f);
            tree.forEach(System.out::println);
            System.out.println("-----------------------------------------------------");
            fileSelect = comprobarRutaIntroducida(tree);
        }
        switch (opcion){
            case 1 -> {
                getInformacion(fileSelect);
                menu();
            }
            case 2 -> {
                createElement(fileSelect, "directorio");
                menu();
            }
            case 3 -> {
                createElement(fileSelect, "archivo");
                menu();
            }
            case 4 -> {
                deleteElement(fileSelect);
                menu();
            }
            case 5 -> {
                updateElement(fileSelect);
                menu();
            }
            case 6 -> {
                System.out.println("Saliendo del sistema. Adios...");
            }
        }
    }

    public void updateElement(File auxF){
        System.out.println("Introduce el nuevo nombre");
        String newName = sc.nextLine();
        File newElement = new File(auxF.getParentFile()+"/"+newName);
        if(auxF.renameTo(newElement)) System.out.println("Nombre cambiado con éxito");
        else System.out.println("No se ha podido cambiar el nombre");
    }

    public void deleteElement(File auxF){
        if(auxF.delete()) System.out.println("Eliminado correctamente");
        else System.out.println("No se ha podido eliminar");
    }

    public void createElement(File auxF, String elemento){
        if(auxF.isFile()) System.out.println("La ruta introducida es un archivo, no se puede crear el " + elemento);
        else{
            System.out.println("Como quieres que se llame el " + elemento);
            String element = sc.nextLine();
            File newElement = new File(auxF.getAbsolutePath()+"/"+element);
            if(newElement.exists()) System.out.println("El " + elemento +" ya existe");
            else {
                if(elemento.equals("archivo")){
                    try{
                        System.out.println((newElement.createNewFile() ? "Archivo creado con éxito" : "No se ha podido crear el archivo"));
                    }catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if(elemento.equals("directorio")) System.out.println((newElement.mkdir() ? "Directorio creado con éxito":"No se ha podido crear el directorio"));
            }
        }
    }


    public void getInformacion(File auxF){
        System.out.println("Nombre: " + auxF.getName());
        System.out.println("Tipo: " + ((auxF.isDirectory()) ? "carpeta": "archivo"));
        System.out.println("Ubicación: " + auxF.getAbsolutePath());
        System.out.println("Última modificación: " + new Date(auxF.lastModified()));
        System.out.println("Está oculto?: " + ((auxF.isHidden()) ? "Si": "No"));
        if(auxF.isDirectory()){
            System.out.println("Numero elementos que contiene: " + auxF.listFiles().length);
            System.out.println("Espacio Libre: " + auxF.getFreeSpace());
            System.out.println("Espacio Total: " + auxF.getTotalSpace());
        }else{
            System.out.println("Bytes: " + auxF.length());
        }
    }

    public ArrayList<String> mostrarTree(File f1){
        tree.add(f1.getAbsolutePath());
        for(File elemento : f1.listFiles()){
            if(elemento.isDirectory()){
                mostrarTree(elemento);
            }else{
                tree.add(elemento.getAbsolutePath());
            }
        }
        return tree;
    }

    public int comprobarOpcion(){
        while(true){
            try{
                String opcion = sc.nextLine();
                int numOpcion = Integer.parseInt(opcion);
                if(numOpcion>0&&numOpcion<7) return numOpcion;
                System.out.print("Introduce un numero entero entre 1 y 6. Vuelvelo a introducir: ");
            }catch(NumberFormatException e){
                System.out.print("No has introducido un numero entero. Vuelvelo a introducir: ");
            }
        }
    }

    public File comprobarRutaIntroducida(ArrayList<String> tree){
        String rutaFinal = "";
        do{
            System.out.println("Introduce la ruta:");
            String ruta = sc.nextLine();
            rutaFinal = tree.stream().filter(t -> t.equalsIgnoreCase(ruta.trim())).findFirst().orElse(null);

            if(rutaFinal==null){
                System.out.println("La ruta introducida no coincide con ninguna mostrada anteriormente");
            }
        }while(rutaFinal==null);

        return new File(rutaFinal);
    }

    public boolean comprobarRutaIntroducida(){
        if(f.exists()){
            if(f.isDirectory()){
                int contF= 0, contD=0;
                for(File auxF : f.listFiles()){
                    if(auxF.isDirectory()) contD++;
                    else if(auxF.isFile()) contF++;

                    if(contF>0&&contD>0) break;
                }
                if(contF==0 || contD==0){
                    System.out.println("Debe haber al menos un archivo y una carpeta en el directorio introducido");
                    return false;
                }
                return true;
            }else{
                System.out.println("No es un directorio");
                return false;
            }
        }else{
            System.out.println("No existe nada con esa ruta introducida");
            return false;
        }
    }
}
