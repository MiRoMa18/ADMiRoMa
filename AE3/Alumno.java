import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Alumno {
    private int id;
    private String nombre;
    private String curso;
    private String dni;
    private String fecha_nacimiento;
    private ArrayList<String> nombresProgenitores;
    private ArrayList<String> correoProgenitores;
    private ArrayList<Asignatura> asignaturas;
    private ArrayList<Ausencia> ausencias;


    public Alumno(ArrayList<String> correoProgenitores, ArrayList<String> nombresProgenitores, String fecha_nacimiento, String dni, String curso, String nombre, int id) {
        this.ausencias = new ArrayList<>();
        this.asignaturas = new ArrayList<>();
        this.correoProgenitores = correoProgenitores;
        this.nombresProgenitores = nombresProgenitores;
        this.fecha_nacimiento = fecha_nacimiento;
        this.dni = dni;
        this.curso = curso;
        this.nombre = nombre;
        this.id = id;
    }

    public void checkAvisoAusencias(){
        for (Asignatura asignatura : this.asignaturas) {
            int cantAusenciasAsignatura=0;
            for (Ausencia ausencia : this.ausencias) {
                if(ausencia.getAsignatura().getId() == asignatura.getId()){
                    cantAusenciasAsignatura++;
                }
            }
            double limite = (double) (asignatura.getCantHoras() * 10) / 100;
            if(cantAusenciasAsignatura>=limite){
                enviarCorreo(asignatura, cantAusenciasAsignatura);
            }
        }
    }

    public void enviarCorreo(Asignatura asignatura, int cantAusencias){
        // Hay que activar verificacion de 2 pasos en la cuenta de correo y generar una contraseña de aplicación, que es la que irá en miContraseña
        // Verificacion: https://myaccount.google.com/security
        // Contra Aplicación:https://myaccount.google.com/apppasswords
        String miCorreo = "laleza@florida-uni.es";
        String miContraseña = "";

        String host = "smtp.gmail.com";
        String puerto = "587";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(miCorreo, miContraseña);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(miCorreo));
            mensaje.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(miCorreo));
            mensaje.setSubject("Aviso: Exceso de ausencias - " + this.nombre);

            String textoCorreo = "CORREOS DESTINATARIOS ORIGINALES:\n";
            for (String correo : this.correoProgenitores) {
                textoCorreo += "- " + correo + "\n";
            }

            textoCorreo += "Alumno: " + this.nombre + "\n";
            textoCorreo += "Ha superado el 10% de ausencias. Lleva " + cantAusencias +"/"+ asignatura.getCantHoras() +" clases asistidas";

            mensaje.setText(textoCorreo);
            Transport.send(mensaje);
        } catch (MessagingException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarAsignatura(Asignatura asignatura){
        this.asignaturas.remove(asignatura);
    }

    public void eliminarAusencia(Ausencia ausencia){
        this.ausencias.remove(ausencia);
    }

    public void agregarAusencia(Ausencia ausencia){
        this.ausencias.add(ausencia);
    }

    public void agregarAsignatura(Asignatura asignatura){
        this.asignaturas.add(asignatura);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public ArrayList<Asignatura> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(ArrayList<Asignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public ArrayList<String> getCorreoProgenitores() {
        return correoProgenitores;
    }

    public void setCorreoProgenitores(ArrayList<String> correoProgenitores) {
        this.correoProgenitores = correoProgenitores;
    }

    public ArrayList<String> getNombresProgenitores() {
        return nombresProgenitores;
    }

    public void setNombresProgenitores(ArrayList<String> nombresProgenitores) {
        this.nombresProgenitores = nombresProgenitores;
    }

    public ArrayList<Ausencia> getAusencias() {
        return ausencias;
    }

    public void setAusencias(ArrayList<Ausencia> ausencias) {
        this.ausencias = ausencias;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", curso='" + curso + '\'' +
                ", dni='" + dni + '\'' +
                ", fecha_nacimiento='" + fecha_nacimiento + '\'' +
                ", nombresProgenitores=" + nombresProgenitores +
                ", correoProgenitores=" + correoProgenitores +
                ", cantAsignaturas=" + asignaturas.size() +
                ", cantAusencias=" + ausencias.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Alumno alumno = (Alumno) o;
        return id == alumno.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
