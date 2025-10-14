package ejerciciosArchivos.seccion2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Act12 {
    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader(new File("src/ejerciciosArchivos/seccion2/archivo.txt"));
        BufferedReader br = new BufferedReader(fr);
        String line;
         while((line = br.readLine()) != null){
            System.out.println(line);
         }
    }
}
