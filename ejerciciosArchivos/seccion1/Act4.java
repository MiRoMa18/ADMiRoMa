package ejerciciosArchivos.seccion1;

import java.io.File;

public class Act4 {
    public static void main(String[] args) {
        File f = new File("src/ejerciciosArchivos/seccion1/archivo.txt");
        if(f.exists()){
            System.out.println("El archivo existe");
            System.out.println("Ruta absoluta: " + f.getAbsolutePath());
            System.out.println("Tamaño: " + f.length() + " bytes");
            System.out.println("Última modificación: " + f.lastModified());
        } else {
            System.out.println("El archivo no existe");
        }
    }
}
