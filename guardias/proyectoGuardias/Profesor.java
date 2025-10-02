package proyectoGuardias;

import java.util.Map;

public class Profesor {
    private static int contadorIds = 0;
    private String nombre;
    private int id;
    private Map<String, Map<String, String>> horario;
    private int numSustituciones;

    public Profesor(String nombre, Map<String, Map<String, String>> horario) {
        this.nombre = nombre;
        this.horario = horario;
        this.id = contadorIds++;
        this.numSustituciones = 0;
    }

    public String getNombre() {
        return nombre;
    }
    public int getId() {
        return id;
    }
    public Map<String, Map<String, String>> getHorario() {
        return horario;
    }
    public int getNumSustituciones() {
        return numSustituciones;
    }
    public void setNumSustituciones(int numSustituciones) {
        this.numSustituciones = numSustituciones;
    }
    public int incrementarSustituciones() {
        return ++numSustituciones;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "nombre='" + nombre + '\'' +
                ", id=" + id +
                ", numSustituciones=" + numSustituciones +
                '}';
    }





}
