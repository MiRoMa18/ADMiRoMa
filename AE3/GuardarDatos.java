import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.util.Map;

public class GuardarDatos {

    public void guardarDatos(Map<Integer, Profesor> profesores, Map<Integer, Alumno> alumnos, Map<Integer, Ausencia> ausencias, Map<Integer, Asignatura> asignaturas){

        guardarAlumnos(new ArrayList<>(alumnos.values()));
        guardarProfesores(new ArrayList<>(profesores.values()));
        guardarAsignaturas(new ArrayList<>(asignaturas.values()));
        guardarAusencias(new ArrayList<>(ausencias.values()));

    }

    public void guardarAlumnos(ArrayList<Alumno> alumnos){
        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFact.newDocumentBuilder();
            Document document = dBuilder.newDocument();
            Element raiz = document.createElement("alumnos");
            document.appendChild(raiz);
            alumnos.forEach(c ->{
                Element alumno = document.createElement("alumno");

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(c.getId())));
                alumno.appendChild(id);

                Element nombre = document.createElement("nombre");
                nombre.appendChild(document.createTextNode(c.getNombre()));
                alumno.appendChild(nombre);

                Element curso = document.createElement("curso");
                curso.appendChild(document.createTextNode(c.getCurso()));
                alumno.appendChild(curso);

                Element dni = document.createElement("dni");
                dni.appendChild(document.createTextNode(c.getDni()));
                alumno.appendChild(dni);

                Element fechaNacimiento = document.createElement("fechaNacimiento");
                fechaNacimiento.appendChild(document.createTextNode(c.getFecha_nacimiento()));
                alumno.appendChild(fechaNacimiento);

                Element nombresProgenitores = document.createElement("nombresProgenitores");
                c.getNombresProgenitores().forEach(nb-> {
                    Element progenitor = document.createElement("progenitor");
                    progenitor.appendChild(document.createTextNode(nb));
                    nombresProgenitores.appendChild(progenitor);
                });
                alumno.appendChild(nombresProgenitores);

                Element correoProgenitores = document.createElement("correoProgenitores");
                c.getCorreoProgenitores().forEach(cp-> {
                    Element correo = document.createElement("correo");
                    correo.appendChild(document.createTextNode(cp));
                    correoProgenitores.appendChild(correo);
                });
                alumno.appendChild(correoProgenitores);

                Element asignaturas = document.createElement("asignaturas");
                c.getAsignaturas().forEach(as-> {
                    Element asignaturaId = document.createElement("asignaturaId");
                    asignaturaId.appendChild(document.createTextNode(String.valueOf(as.getId())));
                    asignaturas.appendChild(asignaturaId);
                });
                alumno.appendChild(asignaturas);

                Element ausencias = document.createElement("ausencias");
                c.getAusencias().forEach(au-> {
                    Element ausenciaId = document.createElement("ausenciaId");
                    ausenciaId.appendChild(document.createTextNode(String.valueOf(au.getId())));
                    ausencias.appendChild(ausenciaId);
                });
                alumno.appendChild(ausencias);

                raiz.appendChild(alumno);
            });

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            try{
                FileWriter fw = new FileWriter("/Users/miquelroca/Desktop/ADMiRoMa/AE3/guardarDatos/alumnos.xml");
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

    public void guardarProfesores(ArrayList<Profesor> profesores){
        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFact.newDocumentBuilder();
            Document document = dBuilder.newDocument();
            Element raiz = document.createElement("profesores");
            document.appendChild(raiz);
            profesores.forEach(c ->{
                Element profesor = document.createElement("profesor");

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(c.getId())));
                profesor.appendChild(id);

                Element nombre = document.createElement("nombre");
                nombre.appendChild(document.createTextNode(c.getNombre()));
                profesor.appendChild(nombre);

                Element correo = document.createElement("correo");
                correo.appendChild(document.createTextNode(c.getCorreo()));
                profesor.appendChild(correo);

                Element contrasena = document.createElement("contrasena");
                contrasena.appendChild(document.createTextNode(c.getcontrasena()));
                profesor.appendChild(contrasena);

                Element dni = document.createElement("dni");
                dni.appendChild(document.createTextNode(c.getDni()));
                profesor.appendChild(dni);

                Element telefono = document.createElement("numTelefono");
                telefono.appendChild(document.createTextNode(c.getNumTelefono()));
                profesor.appendChild(telefono);

                Element asignaturas = document.createElement("asignaturas");
                c.getAsignaturas().forEach(as-> {
                    Element asignaturaId = document.createElement("asignaturaId");
                    asignaturaId.appendChild(document.createTextNode(String.valueOf(as.getId())));
                    asignaturas.appendChild(asignaturaId);
                });
                profesor.appendChild(asignaturas);

                raiz.appendChild(profesor);
            });

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            try{
                FileWriter fw = new FileWriter("/Users/miquelroca/Desktop/ADMiRoMa/AE3/guardarDatos/profesores.xml");
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

    public void guardarAsignaturas(ArrayList<Asignatura> asignaturas){
        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFact.newDocumentBuilder();
            Document document = dBuilder.newDocument();
            Element raiz = document.createElement("asignaturas");
            document.appendChild(raiz);
            asignaturas.forEach(c ->{
                Element asignatura = document.createElement("asignatura");

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(c.getId())));
                asignatura.appendChild(id);

                Element nombre = document.createElement("nombre");
                nombre.appendChild(document.createTextNode(c.getNombre()));
                asignatura.appendChild(nombre);

                Element profesorId = document.createElement("profesorId");
                profesorId.appendChild(document.createTextNode(String.valueOf(c.getProfesor().getId())));
                asignatura.appendChild(profesorId);

                Element cantHoras = document.createElement("cantHoras");
                cantHoras.appendChild(document.createTextNode(String.valueOf(c.getCantHoras())));
                asignatura.appendChild(cantHoras);

                Element alumnos = document.createElement("alumnos");
                c.getAlumnos().forEach(as-> {
                    Element alumnoId = document.createElement("alumnoId");
                    alumnoId.appendChild(document.createTextNode(String.valueOf(as.getId())));
                    alumnos.appendChild(alumnoId);
                });
                asignatura.appendChild(alumnos);

                raiz.appendChild(asignatura);
            });

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            try{
                FileWriter fw = new FileWriter("/Users/miquelroca/Desktop/ADMiRoMa/AE3/guardarDatos/asignaturas.xml");
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

    public void guardarAusencias(ArrayList<Ausencia> ausencias){
        try{
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFact.newDocumentBuilder();
            Document document = dBuilder.newDocument();
            Element raiz = document.createElement("ausencias");
            document.appendChild(raiz);
            ausencias.forEach(c ->{
                Element ausencia = document.createElement("ausencia");

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(c.getId())));
                ausencia.appendChild(id);

                Element alumnoId = document.createElement("alumnoId");
                alumnoId.appendChild(document.createTextNode(String.valueOf(c.getAlumno().getId())));
                ausencia.appendChild(alumnoId);

                Element asignaturaId = document.createElement("asignaturaId");
                asignaturaId.appendChild(document.createTextNode(String.valueOf(c.getAsignatura().getId())));
                ausencia.appendChild(asignaturaId);

                Element horaAusencia = document.createElement("horaAusencia");
                horaAusencia.appendChild(document.createTextNode(String.valueOf(c.getHoraAusencia())));
                ausencia.appendChild(horaAusencia);

                Element justificada = document.createElement("justificada");
                justificada.appendChild(document.createTextNode(String.valueOf(c.isJustificada())));
                ausencia.appendChild(justificada);

                raiz.appendChild(ausencia);
            });

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer transformer = tranFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            try{
                FileWriter fw = new FileWriter("/Users/miquelroca/Desktop/ADMiRoMa/AE3/guardarDatos/ausencias.xml");
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
