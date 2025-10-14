package ejerciciosArchivos.seccion1;

import java.io.File;

public class Act2 {
    public static void main(String[] args) {
        File f = new File("src/ejerciciosArchivos/seccion1/archivo.txt");
        System.out.println("Tamaño: " + f.length() + " bytes");
        System.out.println("¿Se puede leer? " + f.canRead());
        System.out.println("¿Se puede escribir? " + f.canWrite());
        System.out.println("¿Es un directorio? " + f.isDirectory());
        System.out.println("¿Es un archivo? " + f.isFile());
        System.out.println("¿Está oculto? " + f.isHidden());
        System.out.println("Última modificación: " + f.lastModified());
    }
}
