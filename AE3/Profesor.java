import java.util.ArrayList;

public class Profesor {
    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String numTelefono;
    private ArrayList<Asignatura> asignaturas;
    private String dni;

    public Profesor(int id, String dni, String numTelefono, String contrasena, String correo, String nombre) {
        this.asignaturas = new ArrayList<>();
        this.dni = dni;
        this.numTelefono = numTelefono;
        this.contrasena = contrasena;
        this.correo = correo;
        this.nombre = nombre;
        this.id = id;
    }

    public void eliminarAsignatura(Asignatura asignatura){
        this.asignaturas.remove(asignatura);
    }

    public void agregarAsignatura(Asignatura asignatura){
        this.asignaturas.add(asignatura);
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public ArrayList<Asignatura> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(ArrayList<Asignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getcontrasena() {
        return contrasena;
    }

    public void setcontrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", numTelefono='" + numTelefono + '\'' +
                ", cantAsignaturas=" + asignaturas.size() +
                ", dni='" + dni + '\'' +
                '}';
    }
}
