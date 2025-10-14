package ejerciciosArchivos.seccion1;

import java.io.File;

public class Act5 {
    public static void main(String[] args) {
        File f = new File("src/ejerciciosArchivos/seccion1");
        String extension = ".txt";
        if(f.exists() && f.isDirectory()){
            File[] archivos = f.listFiles();
            for(File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().endsWith(extension)) {
                    System.out.println(archivo.getName());
                }
            }
        } else {
            System.out.println("El directorio no existe o no es un directorio");
        }
    }
}
