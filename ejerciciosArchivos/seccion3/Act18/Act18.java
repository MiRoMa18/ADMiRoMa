package seccion3.Act18;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Act18 {
    public static void main(String[] args) {
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/ejerciciosArchivos/seccion3/Act1/canciones.xml"));

            Element raiz = document.getDocumentElement();
            NodeList nodeList = document.getElementsByTagName("cancion");
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;
                    System.out.println("Id canción: " + el.getAttribute("id"));
                    System.out.println("Titulo: " + el.getElementsByTagName("titulo").item(0).getTextContent());
                    System.out.println("Artista: " + el.getElementsByTagName("artista").item(0).getTextContent());
                    System.out.println("Año: " + el.getElementsByTagName("anyo").item(0).getTextContent());
                    System.out.println("Formato: " + el.getElementsByTagName("formato").item(0).getTextContent());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }
}
