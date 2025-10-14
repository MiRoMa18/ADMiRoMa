package ejerciciosArchivos.seccion2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Act9 {
    public static void main(String[] args) throws IOException {

        String argsFile = args.length > 0 ? args[0] : "";
        File f = new File(argsFile);

        if(!f.isDirectory()){
            FileReader fr = new FileReader(f);
            int c;
            while((c = fr.read()) != -1) {
                System.out.print((char) c);
            }
        }else{
            System.out.println("El archivo es un directorio.");
        }


    }
}
