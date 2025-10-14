package ejerciciosArchivos.seccion2;

import java.io.*;

public class Act14 {
    public static void main(String[] args)throws IOException {
        File f = new File("src/ejerciciosArchivos/seccion2/archivo.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter("src/ejerciciosArchivos/seccion2/archivo2.txt");

        BufferedWriter bw = new BufferedWriter(fw);
        String line;
        while((line = br.readLine()) != null){
            System.out.println(line);
            bw.write(line);
            bw.newLine();
        }
        bw.close();
        br.close();
    }
}
