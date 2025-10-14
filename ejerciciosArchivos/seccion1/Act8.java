package ejerciciosArchivos.seccion1;

import java.io.File;
import java.io.IOException;

public class Act8 {
    public static void main(String[] args) throws IOException {
        File f = new File("src/ejerciciosArchivos/seccion1/archivo.txt");
        System.out.println("Archivo: " + f.getName());
        File fAux = new File(f.getAbsolutePath().replace("archivo.txt", "archivoAux.txt"));
        System.out.println("Archivo Aux: " + fAux.getName());
        System.out.println("Crear archivo aux para que pueda ser borrado: " + fAux.createNewFile());

        System.out.println("Eliminar archivo aux");
        if(fAux.delete()){
            System.out.println("Archivo aux eliminado");
        } else {
            System.out.println("No se pudo eliminar el archivo aux");
        }

    }
}
