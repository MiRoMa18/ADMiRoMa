package ejerciciosArchivos.seccion2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Act10 {
    public static void main(String[] args)throws IOException {
        String argsFile = args.length > 0 ? args[0] : "";
        String velocidad = args.length > 1 ? args[1] : "1";
        File f = new File(argsFile);
        int vel = Integer.parseInt(velocidad);
        FileReader fr = new FileReader(f);

        if(!f.isDirectory()){
            int c;
            while((c = fr.read()) != -1)
                try {
                    System.out.print((char) c);
                    Thread.sleep(1000/vel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }else {
            System.out.println("El archivo es un directorio.");
        }
    }
}
