package seccion3.Act19;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Act19 {
    public static void main(String[] args) {
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/ejerciciosArchivos/seccion3/Act1/canciones.xml"));

            Element raiz = document.getDocumentElement();
            NodeList nodeList = document.getElementsByTagName("cancion");
            System.out.println("Hay " + nodeList.getLength()+ " nodos en el archivo");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
