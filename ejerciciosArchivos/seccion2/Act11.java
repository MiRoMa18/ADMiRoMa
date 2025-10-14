package ejerciciosArchivos.seccion2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Act11 {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader(new File("src/ejerciciosArchivos/seccion2/archivo.txt"));
        Scanner sc = new Scanner(System.in);
        System.out.println("Pulsa cualquier tecla para acabar de mostrar el contenido:");
        int salto = 8;
        int aux = 0;
        int c;
        while((c = fr.read()) != -1){
            System.out.print((char)c);
            if(aux == salto){
                sc.nextLine();
                aux = 0;
            }
            aux++;
        }

    }
}
