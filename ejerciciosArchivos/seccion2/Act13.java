package ejerciciosArchivos.seccion2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Act13 {
    public static void main(String[] args) throws IOException {
        String argsFile = args.length > 0 ? args[0] : "";
        String velocidad = args.length > 1 ? args[1] : "1";
        File f = new File(argsFile);
        int vel = Integer.parseInt(velocidad);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        String line;
        while((line = br.readLine()) != null){
            try {
                System.out.println(line);
                Thread.sleep(1000/vel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
