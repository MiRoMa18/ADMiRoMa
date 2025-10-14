package ejerciciosArchivos.seccion1;

import java.io.File;

public class Act3 {
    public static void main(String[] args) {
        File f = new File("src/ejerciciosArchivos/seccion1/archivo.txt");
        if(f.exists()){
            System.out.println("El archivo existe");
        } else {
            System.out.println("El archivo no existe");
        }
    }
}
