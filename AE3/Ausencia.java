import java.time.LocalDateTime;

public class Ausencia {
    private int id;
    private LocalDateTime horaAusencia;
    private boolean justificada;
    private Asignatura asignatura;
    private Alumno alumno;

    public Ausencia(LocalDateTime horaAusencia, boolean justificada, int id) {
        this.horaAusencia = horaAusencia;
        this.justificada = justificada;
        this.id = id;
        this.asignatura = null;
        this.alumno = null;
    }

    public void agregarAlumno(Alumno alumno){
        this.alumno= alumno;
    }

    public void agregarAsignatura(Asignatura asignatura){
        this.asignatura= asignatura;
    }

    public LocalDateTime getHoraAusencia() {
        return horaAusencia;
    }

    public void setHoraAusencia(LocalDateTime horaAusencia) {
        this.horaAusencia = horaAusencia;
    }

    public int getId() {
        return id;
    }

    public boolean isJustificada() {
        return justificada;
    }

    public void setJustificada(boolean justificada) {
        this.justificada = justificada;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    @Override
    public String toString() {
        return "Ausencia{" +
                "id=" + id +
                ", horaAusencia=" + horaAusencia +
                ", justificada=" + justificada +
                ", asignatura=" + (asignatura != null ? asignatura.getNombre() : "null") +
                ", alumno=" + (alumno != null ? alumno.getNombre() : "null") +
                '}';
    }
}
