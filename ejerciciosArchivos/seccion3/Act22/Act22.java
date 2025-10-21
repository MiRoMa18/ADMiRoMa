package seccion3.Act22;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Act22 {
    public static void main(String[] args) {
        List<Cancion> canciones = new ArrayList<>();
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
                    int id = Integer.parseInt(el.getAttribute("id"));
                    String titulo = el.getElementsByTagName("titulo").item(0).getTextContent();
                    String artista = el.getElementsByTagName("artista").item(0).getTextContent();
                    String anyo = el.getElementsByTagName("anyo").item(0).getTextContent();
                    String formato = el.getElementsByTagName("formato").item(0).getTextContent();

                    Cancion cancion = new Cancion(id, titulo, artista, anyo, formato);
                    canciones.add(cancion);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        canciones.forEach(System.out::println);
    }

}
