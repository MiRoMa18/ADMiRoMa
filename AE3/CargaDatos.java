import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CargaDatos {

    //LinkedHashmap porque vamos a cargar dos veces, uno para cargar los objetos con los datos básicos y la otra carga para cargar las relaciones entre ellos
    private Map<Integer, Alumno> alumnos = new LinkedHashMap<>();
    private Map<Integer, Asignatura> asignaturas = new LinkedHashMap<>();
    private Map<Integer, Ausencia> ausencias = new LinkedHashMap<>();
    private Map<Integer, Profesor> profesores = new LinkedHashMap<>();

    public Map<Integer, Profesor> getProfesores() {
        return new HashMap<>(profesores);
    }

    public Map<Integer, Alumno> getAlumnos() {
        return new HashMap<>(alumnos);
    }

    public Map<Integer, Ausencia> getAusencias() {
        return new HashMap<>(ausencias);
    }

    public Map<Integer, Asignatura> getAsignaturas() {
        return new HashMap<>(asignaturas);
    }

    public void cargarDatos(){
        cargarXML();
        cargarRelaciones();
    }

    public void cargarXML(){
        cargarXMLAlumno();
        cargarXMLAsignatura();
        cargarXMLProfesor();
        cargarXMLAusencia();
    }

    public void cargarRelaciones(){
        cargarRelacionesProfesor();
        cargarRelacionesAlumno();
        cargarRelacionesAsignaturas();
        cargarRelacionesAusencias();
    }

    public void cargarRelacionesProfesor(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/profesores.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;

                    int profesorId = 0;
                    NodeList hijos = el.getChildNodes();
                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            if(elHijo.getNodeName().equals("id")){
                                profesorId = Integer.parseInt(elHijo.getTextContent().trim());
                                break;
                            }
                        }
                    }

                    Profesor profesor = profesores.get(profesorId);
                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            if (elHijo.getNodeName().equals("asignaturas")) {
                                NodeList listAsignaturas = elHijo.getChildNodes();
                                for (int x = 0; x < listAsignaturas.getLength(); x++) {
                                    Node nodeAsignatura = listAsignaturas.item(x);
                                    if (nodeAsignatura.getNodeType() == Node.ELEMENT_NODE) {
                                        Element elAsignatura = (Element) nodeAsignatura;
                                        profesor.agregarAsignatura(asignaturas.get(Integer.parseInt(elAsignatura.getTextContent().trim())));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void cargarRelacionesAusencias() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/ausencias.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;

                    int ausenciaId = 0;
                    NodeList hijos = el.getChildNodes();
                    for (int j = 0; j < hijos.getLength(); j++) {
                        Node hijo = hijos.item(j);
                        if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                            Element elHijo = (Element) hijo;
                            if (elHijo.getNodeName().equals("id")) {
                                ausenciaId = Integer.parseInt(elHijo.getTextContent().trim());
                                break;
                            }
                        }
                    }

                    Ausencia ausencia = ausencias.get(ausenciaId);
                    for (int j = 0; j < hijos.getLength(); j++) {
                        Node hijo = hijos.item(j);
                        if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                            Element elHijo = (Element) hijo;
                            switch (elHijo.getNodeName()) {
                                case "alumnoId" ->
                                        ausencia.agregarAlumno(alumnos.get(Integer.parseInt(elHijo.getTextContent().trim())));
                                case "asignaturaId" ->
                                        ausencia.agregarAsignatura(asignaturas.get(Integer.parseInt(elHijo.getTextContent().trim())));
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarRelacionesAsignaturas(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/asignaturas.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;

                    int asignaturaId = 0;
                    NodeList hijos = el.getChildNodes();
                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            if(elHijo.getNodeName().equals("id")){
                                asignaturaId = Integer.parseInt(elHijo.getTextContent().trim());
                                break;
                            }
                        }
                    }

                    Asignatura asignatura = asignaturas.get(asignaturaId);
                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            if (elHijo.getNodeName().equals("alumnos")) {
                                NodeList listAsignaturas = elHijo.getChildNodes();
                                for (int x = 0; x < listAsignaturas.getLength(); x++) {
                                    Node nodeAsignatura = listAsignaturas.item(x);
                                    if (nodeAsignatura.getNodeType() == Node.ELEMENT_NODE) {
                                        Element elAsignatura = (Element) nodeAsignatura;
                                        asignatura.agregarAlumnos(alumnos.get(Integer.parseInt(elAsignatura.getTextContent().trim())));
                                    }
                                }
                            }else if(elHijo.getNodeName().equals("profesorId")){
                                asignatura.agregarProfesor(profesores.get(Integer.parseInt(elHijo.getTextContent().trim())));
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarRelacionesAlumno(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/alumnos.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;

                    int alumnoId = 0;
                    NodeList hijos = el.getChildNodes();
                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            if(elHijo.getNodeName().equals("id")){
                                alumnoId = Integer.parseInt(elHijo.getTextContent().trim());
                                break;
                            }
                        }
                    }

                    Alumno alumno = alumnos.get(alumnoId);

                    for(int j = 0; j < hijos.getLength(); j++){
                        Node hijo = hijos.item(j);
                        if(hijo.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijo = (Element) hijo;
                            switch (elHijo.getNodeName()){
                                case "asignaturas" ->{
                                    NodeList listAsignaturasProgenitores = elHijo.getChildNodes();
                                    for(int x = 0; x < listAsignaturasProgenitores.getLength(); x++){
                                        Node nodeAsignatura = listAsignaturasProgenitores.item(x);
                                        if(nodeAsignatura.getNodeType()== Node.ELEMENT_NODE){
                                            Element elAsignatura = (Element) nodeAsignatura;
                                            alumno.agregarAsignatura(asignaturas.get(Integer.parseInt(elAsignatura.getTextContent().trim())));
                                        }
                                    }
                                }
                                case "ausencias" -> {
                                    NodeList listAusencias = elHijo.getChildNodes();
                                    for(int x = 0; x < listAusencias.getLength(); x++){
                                        Node nodeAusencia = listAusencias.item(x);
                                        if(nodeAusencia.getNodeType()== Node.ELEMENT_NODE){
                                            Element elAusencia = (Element) nodeAusencia;
                                            alumno.agregarAusencia(ausencias.get(Integer.parseInt(elAusencia.getTextContent().trim())));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarXMLAlumno(){

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/alumnos.xml"));
            Element root = document.getDocumentElement();

            // Por si no se saben los datos que se guardan el archivo getChildNodes()
            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                String nombre = "", curso="", dni="", fecha_nacimiento="";
                int id = 0;
                ArrayList<String> nombresProgenitores = new ArrayList<>();
                ArrayList<String> correoProgenitores = new ArrayList<>();
                Node alumno = nodeList.item(i);
                if(alumno.getNodeType()== Node.ELEMENT_NODE){
                    Element alumnoHijos = (Element) alumno;
                    NodeList hijosAlumno = alumnoHijos.getChildNodes();
                    for(int j = 0; j < hijosAlumno.getLength(); j++){
                        Node hijoAlumno = hijosAlumno.item(j);
                        if(hijoAlumno.getNodeType()== Node.ELEMENT_NODE){
                            Element elHijoAlumno = (Element) hijoAlumno;
                            switch (elHijoAlumno.getNodeName()){
                                case "nombre" -> nombre = elHijoAlumno.getTextContent().trim();
                                case "curso" -> curso = elHijoAlumno.getTextContent().trim();
                                case "dni" -> dni = elHijoAlumno.getTextContent().trim();
                                case "fechaNacimiento" -> fecha_nacimiento = elHijoAlumno.getTextContent().trim();
                                case "id" -> id = Integer.parseInt(elHijoAlumno.getTextContent().trim());
                                case "nombresProgenitores" ->{
                                    NodeList listNombresProgenitores = elHijoAlumno.getChildNodes();
                                    for(int x = 0; x < listNombresProgenitores.getLength(); x++){
                                        Node nodeNombreProgenitor = listNombresProgenitores.item(x);
                                        if(nodeNombreProgenitor.getNodeType()== Node.ELEMENT_NODE){
                                            Element elNombreProgenitor = (Element) nodeNombreProgenitor;
                                            nombresProgenitores.add(elNombreProgenitor.getTextContent().trim());
                                        }
                                    }
                                }
                                case "correoProgenitores" -> {
                                    NodeList listCorreosProgenitores = elHijoAlumno.getChildNodes();
                                    for(int x = 0; x < listCorreosProgenitores.getLength(); x++){
                                        Node nodeCorreoProgenitor = listCorreosProgenitores.item(x);
                                        if(nodeCorreoProgenitor.getNodeType()== Node.ELEMENT_NODE){
                                            Element elNombreProgenitor = (Element) nodeCorreoProgenitor;
                                            correoProgenitores.add(elNombreProgenitor.getTextContent().trim());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    alumnos.put(id, new Alumno(correoProgenitores, nombresProgenitores, fecha_nacimiento, dni, curso, nombre, id));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarXMLAsignatura(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/asignaturas.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                String nombre ="";
                int id = 0,cantHoras= 0;
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;
                    NodeList nodeListHijos = el.getChildNodes();
                    for(int j = 0; j < nodeListHijos.getLength(); j++){
                        Node nodeHijos = nodeListHijos.item(j);
                        if(nodeHijos.getNodeType()== Node.ELEMENT_NODE) {
                            Element elHijos = (Element) nodeHijos;
                            switch (elHijos.getNodeName()){
                                case "nombre"-> nombre = elHijos.getTextContent().trim();
                                case "id" -> id = Integer.parseInt(elHijos.getTextContent().trim());
                                case "cantHoras" -> cantHoras = Integer.parseInt(elHijos.getTextContent().trim());
                            }
                        }
                    }
                    asignaturas.put(id, new Asignatura(nombre, id, cantHoras));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarXMLAusencia(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/ausencias.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                boolean justificada = false;
                int id = 0;
                LocalDateTime horaAusencia = LocalDateTime.now();
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;
                    NodeList nodeListHijos = el.getChildNodes();
                    for(int j = 0; j < nodeListHijos.getLength(); j++){
                        Node nodeHijos = nodeListHijos.item(j);
                        if(nodeHijos.getNodeType()== Node.ELEMENT_NODE) {
                            Element elHijos = (Element) nodeHijos;
                            switch (elHijos.getNodeName()){
                                case "horaAusencia"-> horaAusencia = LocalDateTime.parse(elHijos.getTextContent().trim());
                                case "id" -> id = Integer.parseInt(elHijos.getTextContent().trim());
                                case "justificada" -> justificada = Boolean.parseBoolean(elHijos.getTextContent().trim());
                            }
                        }
                    }
                    ausencias.put(id, new Ausencia(horaAusencia, justificada, id));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cargarXMLProfesor(){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File("/Users/miquelroca/Desktop/ADMiRoMa/AE3/datos/profesores.xml"));
            Element root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for(int i = 0; i < nodeList.getLength(); i++){
                int id = 0;
                String nombre = "",contrasena="", correo="", dni="", numTelefono="";
                Node node = nodeList.item(i);
                if(node.getNodeType()== Node.ELEMENT_NODE){
                    Element el = (Element) node;
                    NodeList nodeListHijos = el.getChildNodes();
                    for(int j = 0; j < nodeListHijos.getLength(); j++){
                        Node nodeHijos = nodeListHijos.item(j);
                        if(nodeHijos.getNodeType()== Node.ELEMENT_NODE) {
                            Element elHijos = (Element) nodeHijos;
                            switch (elHijos.getNodeName()){
                                case "nombre"-> nombre = elHijos.getTextContent().trim();
                                case "id" -> id = Integer.parseInt(elHijos.getTextContent().trim());
                                case "contrasena" -> contrasena = elHijos.getTextContent().trim();
                                case "dni" -> dni = elHijos.getTextContent().trim();
                                case "correo" -> correo = elHijos.getTextContent().trim();
                                case "numTelefono" -> numTelefono = elHijos.getTextContent().trim();
                            }
                        }
                    }
                    profesores.put(id, new Profesor(id, dni, numTelefono, contrasena, correo, nombre));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
