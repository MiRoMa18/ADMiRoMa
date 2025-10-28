import java.util.ArrayList;

public class Asignatura {
    private Profesor profesor;
    private ArrayList<Alumno> alumnos;
    private int id;
    private String nombre;
    private int cantHoras;

    public Asignatura(String nombre, int id, int cantHoras) {
        this.nombre = nombre;
        this.id = id;
        this.cantHoras = cantHoras;
        this.alumnos = new ArrayList<>();
        this.profesor = null;
    }

    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cantHoras=" + cantHoras +
                ", profesor=" + (profesor != null ? profesor.getNombre() : "null") +
                ", cantAlumnos=" + alumnos.size() +
                '}';
    }

    public void eliminarAlumnos(Alumno alumno){
        this.alumnos.remove(alumno);
    }

    public void agregarAlumnos(Alumno alumno){
        this.alumnos.add(alumno);
    }

    public void agregarProfesor(Profesor profesor){
        this.profesor = profesor;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantHoras() {
        return cantHoras;
    }

    public void setCantHoras(int cantHoras) {
        this.cantHoras = cantHoras;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ArrayList<Alumno> alumnos) {
        this.alumnos = alumnos;
    }
}
