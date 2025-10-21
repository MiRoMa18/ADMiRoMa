package seccion3.Act23;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Act23 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Cancion> canciones = new ArrayList<>();
        String salir = "";
        do{
            System.out.println("Introduzca los siguientes datos para guardar canciones:");
            System.out.print("Titulo: ");
            String titulo = sc.nextLine();
            System.out.print("\nArtista: ");
            String artista = sc.nextLine();
            System.out.print("\nAño: ");
            String anyo = sc.nextLine();
            System.out.print("\nFormato: ");
            String formato = sc.nextLine();

            canciones.add(new Cancion(titulo, artista, anyo, formato));

            System.out.print("\nQuieres salir? (s/n): ");
            salir = sc.nextLine();
        }while(!salir.equalsIgnoreCase("s"));

        canciones.forEach(System.out::println);
    }
}
