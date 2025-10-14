package ejerciciosArchivos.seccion2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class Act16 {
    public static void main(String[] args)throws IOException {
        Date date = new Date();
        String fecha = date.toString().replace(" ", "_");

        FileWriter fw = new FileWriter("src/ejerciciosArchivos/seccion2/"+fecha+".txt");
        Scanner sc = new Scanner(System.in);

        BufferedWriter bf = new BufferedWriter(fw);

        String newLine ="";
        while(newLine.toLowerCase() != "exit"){
            System.out.println("Escribe una linea (escribe 'exit' para salir): ");
            newLine = sc.nextLine();
            if(newLine.toLowerCase().equals("exit")){
                break;
            }else{
                bf.write(newLine);
                bf.newLine();
                bf.flush();
            }
        }
        bf.close();
        sc.close();
    }
}
