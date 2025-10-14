package ejerciciosArchivos.seccion1;

import java.io.File;

public class Act7 {
    public static void main(String[] args) {
        File f = new File("src/ejerciciosArchivos/seccion1");
        if(f.exists() && f.isDirectory()){
            File[] archivos = f.listFiles();
            for(File archivo : archivos) {
                if(args.length == 0){
                    System.out.println(archivo.getName());
                }else {
                    for(String ext : args) {
                        if (archivo.isFile() && archivo.getName().endsWith(ext)) {
                            System.out.println(archivo.getName());
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("El directorio no existe o no es un directorio");
        }
    }
}
