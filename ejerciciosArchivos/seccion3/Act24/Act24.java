package seccion3.Act24;

import seccion3.Act23.Cancion;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Act24 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<seccion3.Act23.Cancion> canciones = new ArrayList<>();
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

        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFact.newDocumentBuilder();
            Document document = dBuilder.newDocument();
            Element raiz = document.createElement("canciones");
            document.appendChild(raiz);
            canciones.forEach(c ->{
                Element cancion = document.createElement("cancion");
                String id = String.valueOf(c.getId());
                cancion.setAttribute("id",id);
                raiz.appendChild(cancion);

                Element titulo = document.createElement("titulo");
                titulo.appendChild(document.createTextNode(c.getTitulo()));
                cancion.appendChild(titulo);

                Element artista = document.createElement("artista");
                artista.appendChild(document.createTextNode(c.getArtista()));
                cancion.appendChild(artista);

                Element anyo = document.createElement("anyo");
                anyo.appendChild(document.createTextNode(c.getAnyo()));
                cancion.appendChild(anyo);

                Element formato = document.createElement("formato");
                formato.appendChild(document.createTextNode(c.getFormato()));
                cancion.appendChild(formato);
            });

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            try{
                FileWriter fw = new FileWriter("/Users/miquelroca/Desktop/ADMiRoMa/ejerciciosArchivos/seccion3/Act24/canciones.xml");
                StreamResult result = new StreamResult(fw);
                transformer.transform(source, result);
                fw.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch(TransformerException ex){
            System.out.println("Error escribiendo el documento");
        }catch(ParserConfigurationException ex){
            System.out.println("Error contruyendo el documento");
        }
    }
}
