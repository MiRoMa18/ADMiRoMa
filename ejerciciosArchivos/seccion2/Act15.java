package ejerciciosArchivos.seccion2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Act15 {
    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("src/ejerciciosArchivos/seccion2/txtTeclado.txt");
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
