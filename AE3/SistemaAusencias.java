import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SistemaAusencias {
    Scanner sc = new Scanner(System.in);

    private Map<Integer, Profesor> profesores;
    private Map<Integer, Alumno> alumnos;
    private Map<Integer, Ausencia> ausencias;
    private Map<Integer, Asignatura> asignaturas;

    public void iniciar(){
        CargaDatos cg = new CargaDatos();
        cg.cargarDatos();

        this.profesores = cg.getProfesores();
        this.alumnos = cg.getAlumnos();
        this.ausencias = cg.getAusencias();
        this.asignaturas = cg.getAsignaturas();
        iniciarSesion();
        menu();

        GuardarDatos gd = new GuardarDatos();
        gd.guardarDatos(this.profesores, this.alumnos, this.ausencias, this.asignaturas);
    }

    public void menu(){
        while(true){
            System.out.println("=".repeat(28));
            System.out.println("Sistema de Ausencias Alumnos");
            System.out.println("=".repeat(28));

            System.out.println("1. Crear, editar o eliminar alumno");
            System.out.println("2. Crear, editar o eliminar asignatura");
            System.out.println("3. Crear, editar o eliminar ausencia");
            System.out.println("0. Salir");
            int opcionMenu = comprobarOpcion(3);
            alumnos.values().forEach(Alumno::checkAvisoAusencias);
            switch(opcionMenu){
                case 1-> crudAlumno();
                case 2-> crudAsignatura();
                case 3 -> crudAusencia();
                case 0 -> {
                    return;
                }
            }
        }
    }

    //=========== CRUD Ausencia ===========
    public void crudAusencia(){
        System.out.println("=".repeat(28));
        System.out.println("CRUD Ausencia");
        System.out.println("=".repeat(28));

        System.out.println("1. Crear ausencia");
        System.out.println("2. Editar ausencia");
        System.out.println("3. Eliminar ausencia");
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcion(3);

        switch(opcionMenu){
            case 1-> crearAusencia();
            case 2-> editarAusencia();
            case 3-> eliminarAusencia();
            case 0 -> {
                return;
            }
        }
    }

    public Ausencia elegirAusencia(){
        if(ausencias.isEmpty()) {
            System.out.println("No hay ausencias registradas");
            return null;
        }
        int[] ids = ausencias.values().stream().mapToInt(Ausencia::getId).toArray();
        ausencias.values().forEach(au->{
            String asignatura = (au.getAsignatura() != null) ? au.getAsignatura().getNombre() : "Sin asignatura";
            String alumno = (au.getAlumno() != null) ? au.getAlumno().getNombre() : "Sin alumno";
            System.out.println(au.getId()+". En "+ asignatura + " de " + alumno + " a las " + au.getHoraAusencia());
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcionListasMaps(ids);
        if(opcionMenu!=0){
            return ausencias.get(opcionMenu);
        }
        return null;
    }

    public void eliminarAusencia(){
        Ausencia ausencia = elegirAusencia();
        if(ausencia != null) {
            Alumno alumno = ausencia.getAlumno();
            if(alumno != null) {
                alumno.eliminarAusencia(ausencia);
            }
            ausencias.remove(ausencia.getId());
        }
    }

    public void editarAusencia(){
        Ausencia ausencia= elegirAusencia();
        if(ausencia!=null){
            String nombreAlumno = (ausencia.getAlumno() != null) ? ausencia.getAlumno().getNombre() : "Sin alumno";
            String nombreAsignatura = (ausencia.getAsignatura() != null) ? ausencia.getAsignatura().getNombre() : "Sin asignatura";
            System.out.println("=".repeat(87));
            System.out.println(" ".repeat(33)+"Edición Ausencias");
            System.out.println("Editando ausencia de " + nombreAlumno + " en " + nombreAsignatura + " a las " + ausencia.getHoraAusencia());
            System.out.println("=".repeat(87));

            System.out.println("1. Cambiar asignatura de la ausencia");
            System.out.println("2. Cambiar alumno de la ausencia");
            System.out.println("3. Cambiar estado ausencia");
            System.out.println("0. Volver al menú principal ↩️");
            int opcionMenu = comprobarOpcion(3);

            switch(opcionMenu){
                case 1 -> editarAsignaturaToAusencia(ausencia);
                case 2 -> editarAlumnoToAusencia(ausencia);
                case 3 -> cambiarEstadoAusencia(ausencia);
                case 0 -> {
                    return;
                }
            }
        }
    }

    public void cambiarEstadoAusencia(Ausencia ausencia){
        if(ausencia.isJustificada()){
            System.out.println("La ausencia esta justificada");
        }else{
            System.out.println("La ausencia no esta justificada");
        }
        System.out.println("Cambiar? (S|N)");
        String confirm = sc.nextLine().trim().toUpperCase();
        if(confirm.equals("S")){
            System.out.println("Ausencia Cambiada ✅");
            ausencia.setJustificada(!ausencia.isJustificada());
        }else{
            System.out.println("Ausencia no Cambiada ❌");
        }
    }

    public void editarAlumnoToAusencia(Ausencia ausencia){
        System.out.println("Qué alumno ha sido?");
        int opcionAlumno = comprobarOpcionListasMaps(idsAlumnos());

        if(opcionAlumno!= 0){
            Alumno alumno = alumnos.get(opcionAlumno);
            if(alumno != null) {
                Alumno alumnoBorrarAusencia = ausencia.getAlumno();
                if(alumnoBorrarAusencia != null) {
                    alumnoBorrarAusencia.eliminarAusencia(ausencia);
                }
                ausencia.setAlumno(alumno);
                alumno.agregarAusencia(ausencia);
            }
        }
    }

    public void editarAsignaturaToAusencia(Ausencia ausencia){
        System.out.println("En qué asignatura ha sido?");
        int opcionAsignatura = comprobarOpcionListasMaps(idsAsignaturas());
        if(opcionAsignatura!=0){
            Asignatura asignatura = asignaturas.get(opcionAsignatura);
            ausencia.setAsignatura(asignatura);
        }
    }

    public void crearAusencia(){
        int idMax = ausencias.values().stream().mapToInt(Ausencia::getId).max().orElse(-1);
        LocalDateTime horaAusencia = LocalDateTime.now();

        System.out.println("En qué asignatura ha sido?");
        int opcionAsignatura = comprobarOpcionListasMaps(idsAsignaturas());

        System.out.println("Qué alumno ha sido?");
        int opcionAlumno = comprobarOpcionListasMaps(idsAlumnos());

        if(opcionAlumno != 0 && opcionAsignatura != 0){
            Asignatura asignatura = asignaturas.get(opcionAsignatura);
            Alumno alumno = alumnos.get(opcionAlumno);

            if(asignatura != null && alumno != null) {
                Ausencia ausencia = new Ausencia(horaAusencia, false, (idMax)+1);
                ausencia.agregarAlumno(alumno);
                ausencia.agregarAsignatura(asignatura);
                ausencias.put((idMax+1), ausencia);
                alumnos.get(alumno.getId()).agregarAusencia(ausencia);
            }
        }
    }
    //=========== =========== =========== ===========

    //=========== CRUD Asignatura ===========
    public void crudAsignatura(){
        System.out.println("=".repeat(28));
        System.out.println("CRUD Asignatura");
        System.out.println("=".repeat(28));

        System.out.println("1. Crear asignatura");
        System.out.println("2. Editar asignatura");
        System.out.println("3. Eliminar asignatura");
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcion(3);

        switch(opcionMenu){
            case 1-> crearAsignatura();
            case 2 -> editarAsignatura();
            case 3 -> eliminarAsignatura();
            case 0 -> {
                return;
            }
        }
    }

    public Asignatura elegirAsignatura(){
        if(asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas");
            return null;
        }
        int opcionMenu = comprobarOpcionListasMaps(idsAsignaturas());
        if(opcionMenu!=0){
            return asignaturas.get(opcionMenu);
        }
        return null;
    }

    public void eliminarAsignatura(){
        Asignatura asignatura = elegirAsignatura();
        if(asignatura!=null){
            if(asignatura.getProfesor() !=null){
                asignatura.getProfesor().eliminarAsignatura(asignatura);
            }

            if(!asignatura.getAlumnos().isEmpty()){
                asignatura.getAlumnos().forEach(al -> al.eliminarAsignatura(asignatura));
            }

            List<Ausencia> ausenciasEliminar = ausencias.values().stream().filter(au-> au.getAsignatura() != null && au.getAsignatura().equals(asignatura)).toList();

            ausenciasEliminar.forEach(au-> {
                Alumno alumno = au.getAlumno();
                if(alumno!=null) alumno.eliminarAusencia(au);
                ausencias.remove(au.getId());
            });

            asignaturas.remove(asignatura.getId());
        }
    }

    public void editarAsignatura(){
        Asignatura asignatura = elegirAsignatura();
        if(asignatura!=null){
            System.out.println("=".repeat(28));
            System.out.println("Edición Asignatura");
            System.out.println("Editando: " + asignatura.getNombre());
            System.out.println("=".repeat(28));

            System.out.println("1. Agregar alumno");
            System.out.println("2. Eliminar alumno");
            System.out.println("3. Cambiar profesor");
            System.out.println("4. Editar nombre");
            System.out.println("5. Editar cantidad de horas");
            System.out.println("0. Volver al menú principal ↩️");
            int opcionMenu = comprobarOpcion(5);

            switch(opcionMenu){
                case 1 -> agregarAlumnoToAsignatura(asignatura);
                case 2 -> eliminarAlumnoToAsignatura(asignatura);
                case 3 -> cambiarProfesorToAsignatura(asignatura);
                case 4 -> editarNombreToAsignatura(asignatura);
                case 5 -> editarCantHorasToAsignatura(asignatura);
                case 0 -> {
                    return;
                }
            }
        }
    }

    public void editarCantHorasToAsignatura(Asignatura asignatura){
        System.out.println("Introduzca la nueva cantidad de horas de la asignatura");
        int cantHoras = 0;
        while(true){
            System.out.println("Introduzca la cantidad de horas que tiene");
            if(sc.hasNextInt()){
                cantHoras = sc.nextInt();
                sc.nextLine();
                if(cantHoras>0){
                    break;
                }else{
                    System.out.println("No puede ser negativo el numero");
                }
            }else{
                System.out.println("Debe ser un número entero");
                sc.next();
            }
        }
        asignatura.setCantHoras(cantHoras);
    }

    public void editarNombreToAsignatura(Asignatura asignatura){
        System.out.println("Introduzca el nuevo nombre de la asignatura");
        asignatura.setNombre(sc.nextLine());
    }

    public void cambiarProfesorToAsignatura(Asignatura asignatura){
        System.out.println("Actualmente esta para el profesor: " + (asignatura.getProfesor()==null ? "Ninguna todavía" : asignatura.getProfesor().getNombre()));
        System.out.println("A qué profesor quieres cambiar?");
        int[] ids = profesores.values().stream().mapToInt(Profesor::getId).toArray();
        profesores.values().forEach(pr->{
            if(!pr.equals(asignatura.getProfesor())){
                System.out.println(pr.getId()+". "+ pr.getNombre());
            }
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionProfesor = comprobarOpcionListasMaps(ids);
        if(opcionProfesor!=0){
            Profesor profesorNuevo = profesores.get(opcionProfesor);
            boolean encontrado = profesorNuevo.getAsignaturas().stream().anyMatch(as -> as.equals(asignatura));
            if(!encontrado){
                Profesor profesorViejo = asignatura.getProfesor();
                if(profesorViejo != null) {
                    profesorViejo.eliminarAsignatura(asignatura);
                }
                profesorNuevo.agregarAsignatura(asignatura);
                asignatura.setProfesor(profesorNuevo);
            } else {
                System.out.println("Este profesor ya tiene esta asignatura");
            }
        }
    }

    public void eliminarAlumnoToAsignatura(Asignatura asignatura){
        System.out.println("Qué alumno quieres eliminar?");
        int[] ids = asignatura.getAlumnos().stream().mapToInt(Alumno::getId).toArray();
        asignatura.getAlumnos().forEach(as->{
            System.out.println(as.getId()+". "+ as.getNombre());
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionAlumno = comprobarOpcionListasMaps(ids);
        if(opcionAlumno!=0){
            Alumno alumno = alumnos.get(opcionAlumno);
            asignatura.eliminarAlumnos(alumno);
            alumno.eliminarAsignatura(asignatura);
        }
    }

    public void agregarAlumnoToAsignatura(Asignatura asignatura){
        System.out.println("Qué alumno quieres añadir?");
        List<Alumno> alumnosDisponibles = alumnos.values().stream().filter(al -> !asignatura.getAlumnos().contains(al)).toList();
        if(alumnosDisponibles.isEmpty()) {
            System.out.println("Todos los alumnos ya están en esta asignatura");
            return;
        }
        int[] ids = alumnosDisponibles.stream().mapToInt(Alumno::getId).toArray();
        alumnosDisponibles.forEach(al->{
            System.out.println(al.getId() + ". " + al.getNombre());
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionAlumno = comprobarOpcionListasMaps(ids);
        if(opcionAlumno!=0){
            Alumno alumno = alumnos.get(opcionAlumno);
            if(alumno != null) {
                asignatura.agregarAlumnos(alumno);
                alumno.agregarAsignatura(asignatura);
            }
        }
    }

    public void crearAsignatura(){
        int idMax = asignaturas.values().stream().mapToInt(Asignatura::getId).max().orElse(-1);

        System.out.println("Introduzca el nombre de la asignatura");
        String nombre = sc.nextLine();

        int cantHoras = 0;
        while(true){
            System.out.println("Introduzca la cantidad de horas que tiene");
            if(sc.hasNextInt()){
                cantHoras = sc.nextInt();
                sc.nextLine();
                if(cantHoras>0){
                    break;
                }else{
                    System.out.println("No puede ser negativo el numero");
                }
            }else{
                System.out.println("Debe ser un número entero");
                sc.next();
            }
        }

        asignaturas.put((idMax+1), new Asignatura(nombre, (idMax+1), cantHoras));
    }
    //=========== =========== =========== ===========

    //=========== CRUD Alumno ===========
    public void crudAlumno(){
        System.out.println("=".repeat(28));
        System.out.println("CRUD Alumnos");
        System.out.println("=".repeat(28));

        System.out.println("1. Crear alumno");
        System.out.println("2. Editar alumno");
        System.out.println("3. Eliminar alumno");
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcion(3);

        switch(opcionMenu){
            case 1-> crearAlumno();
            case 2-> editarAlumno();
            case 3-> eliminarAlumno();
            case 0 -> {
                return;
            }
        }
    }

    public void eliminarAlumno(){
        Alumno alumno = elegirAlumno();
        if(alumno != null) {

            ArrayList<Ausencia> ausenciasDelAlumno = new ArrayList<>(alumno.getAusencias());
            for(Ausencia ausencia : ausenciasDelAlumno) {
                ausencias.remove(ausencia.getId());
            }

            for(Asignatura asignatura : alumno.getAsignaturas()) {
                asignatura.getAlumnos().remove(alumno);
            }

            alumnos.remove(alumno.getId());
        }
    }

    public Alumno elegirAlumno(){
        if(alumnos.isEmpty()) {
            System.out.println("No hay alumnos registrados");
            return null;
        }
        int opcionMenu = comprobarOpcionListasMaps(idsAlumnos());
        if(opcionMenu!=0){
            return alumnos.get(opcionMenu);
        }
        return null;
    }

    public void editarAlumno(){
        Alumno alumno= elegirAlumno();
        if(alumno!=null){
            System.out.println("=".repeat(28));
            System.out.println("Edición Alumnos");
            System.out.println("Editando: " + alumno.getNombre());
            System.out.println("=".repeat(28));

            System.out.println("1. Agregar asignaturas");
            System.out.println("2. Agregar ausencias");
            System.out.println("3. Eliminar asignaturas");
            System.out.println("4. Eliminar ausencias");
            System.out.println("5. Editar nombre");
            System.out.println("6. Editar DNI");
            System.out.println("7. Editar fecha nacimiento");
            System.out.println("0. Volver al menú principal ↩️");
            int opcionMenu = comprobarOpcion(7);

            switch(opcionMenu){
                case 1 -> agregarAsignaturasToAlumno(alumno);
                case 2 -> agregarAusenciaToAlumno(alumno);
                case 3 -> eliminarAsignaturasToAlumno(alumno);
                case 4 -> eliminarAusenciaToAlumno(alumno);
                case 5 -> editarNombreToAlumno(alumno);
                case 6 -> editarDniToAlumno(alumno);
                case 7 -> editarFechaNacimientoToAlumno(alumno);
                case 0 -> {
                    return;
                }
            }
        }
    }

    public void eliminarAusenciaToAlumno(Alumno alumno){
        if(alumno.getAusencias().isEmpty()) {
            System.out.println("El alumno no tiene ausencias registradas");
            return;
        }
        int[] ids = alumno.getAusencias().stream().mapToInt(Ausencia::getId).toArray();
        alumno.getAusencias().forEach(au->{
            String nombreAsignatura = (au.getAsignatura() != null) ? au.getAsignatura().getNombre() : "Sin asignatura";
            System.out.println(au.getId()+". En " + nombreAsignatura + " a las " + au.getHoraAusencia());
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcionListasMaps(ids);
        if(opcionMenu != 0) {
            Ausencia ausencia = ausencias.get(opcionMenu);
            if(ausencia != null) {
                alumno.eliminarAusencia(ausencia);
                ausencias.remove(opcionMenu);
            }
        }
    }

    public void eliminarAsignaturasToAlumno(Alumno alumno){
        int[] ids = alumno.getAsignaturas().stream().mapToInt(Asignatura::getId).toArray();
        alumno.getAsignaturas().forEach(as->{
            System.out.println(as.getId()+". " + as.getNombre());
        });
        System.out.println("0. Volver al menú principal ↩️");
        int opcionMenu = comprobarOpcionListasMaps(ids);
        if(opcionMenu != 0) {
            Asignatura asignatura = asignaturas.get(opcionMenu);
            if(asignatura != null) {
                alumno.eliminarAsignatura(asignatura);
                asignatura.getAlumnos().remove(alumno);
            }
        }
    }

    public void editarFechaNacimientoToAlumno(Alumno alumno){
        System.out.println("Introduzca la nueva fecha de nacimiento");
        alumno.setFecha_nacimiento(sc.nextLine());
    }

    public void editarDniToAlumno(Alumno alumno){
        System.out.println("Introduzca el nuevo dni");
        alumno.setDni(sc.nextLine());
    }

    public void editarNombreToAlumno(Alumno alumno){
        System.out.println("Introduzca el nuevo nombre");
        alumno.setNombre(sc.nextLine());
    }

    public void agregarAusenciaToAlumno(Alumno alumno){
        if(alumno.getAsignaturas().isEmpty()) {
            System.out.println("El alumno no tiene asignaturas. Primero debe inscribirse en una asignatura.");
            return;
        }
        int idMax = ausencias.values().stream().mapToInt(Ausencia::getId).max().orElse(-1);
        LocalDateTime horaAusencia = LocalDateTime.now();

        System.out.println("Que asignatura ha sido?:");
        int opcionMenu = comprobarOpcionListasMaps(idsAsignaturas());
        if(opcionMenu!=0) {
            Asignatura asignatura = alumno.getAsignaturas().stream()
                    .filter(as -> as.getId() == opcionMenu)
                    .findFirst()
                    .orElse(null);

            if(asignatura != null) {
                Ausencia ausencia = new Ausencia(horaAusencia, false, (idMax + 1));
                ausencia.agregarAsignatura(asignatura);
                ausencia.agregarAlumno(alumno);
                ausencias.put(ausencia.getId(), ausencia);
                alumno.agregarAusencia(ausencia);
            }else {
                System.out.println("El alumno no está inscrito en esa asignatura");
            }
        }
    }

    public void agregarAsignaturasToAlumno(Alumno alumno){
        int opcionMenu = comprobarOpcionListasMaps(idsAsignaturas());
        if(opcionMenu!=0){
            Asignatura asignatura = asignaturas.get(opcionMenu);
            boolean encontrado = alumno.getAsignaturas().stream()
                    .anyMatch(as -> as.getId() == asignatura.getId());

            if(!encontrado){
                alumno.agregarAsignatura(asignatura);
                asignatura.agregarAlumnos(alumno);
            } else {
                System.out.println("Ya tiene esa asignatura");
            }
        }
    }

    public void crearAlumno(){
        int idMax = alumnos.values().stream().mapToInt(Alumno::getId).max().orElse(-1);
        ArrayList<String> nombresProgenitores = new ArrayList<>();
        ArrayList<String> correosProgenitores = new ArrayList<>();

        System.out.println("Introduzca el nombre del alumno: ");
        String nombre = sc.nextLine();

        System.out.println("Introduzca el curso del alumno: ");
        String curso = sc.nextLine();

        System.out.println("Introduzca el dni del alumno: ");
        String dni = sc.nextLine();

        System.out.println("Introduzca la fecha de nacimiento del alumno: ");
        String fechaNacimiento = sc.nextLine();

        System.out.println("Introduzca el nombre del primer progenitor: ");
        nombresProgenitores.add(sc.nextLine());
        System.out.println("Introduzca el nombre del segundo progenitor: ");
        nombresProgenitores.add(sc.nextLine());

        System.out.println("Introduzca el correo del primer progenitor: ");
        correosProgenitores.add(sc.nextLine());
        System.out.println("Introduzca el correo del segundo progenitor: ");
        correosProgenitores.add(sc.nextLine());
        alumnos.put((idMax+1), new Alumno(correosProgenitores,nombresProgenitores, fechaNacimiento, dni,curso,nombre, (idMax+1)));
    }
    //=========== =========== =========== ===========

    public int[] idsAsignaturas(){
        //Sacar asignatura
        int[] idsAsignaturas = asignaturas.values().stream().mapToInt(Asignatura::getId).toArray();
        asignaturas.values().forEach(as->{
            System.out.println(as.getId()+". " + as.getNombre());
        });
        System.out.println("0. Volver al menú principal ↩️");
        return idsAsignaturas;
    }

    public int[] idsAlumnos(){
        int[] ids = alumnos.values().stream().mapToInt(Alumno::getId).toArray();
        alumnos.values().forEach(al->{
            System.out.println(al.getId()+". "+ al.getNombre());
        });
        System.out.println("0. Volver al menú principal ↩️");
        return ids;
    }
    public int comprobarOpcionListasMaps(int[] ids){
        int num;
        boolean encontrado = false;
        while(true){
            System.out.println("Eliga una opción: ");
            if(sc.hasNextInt()){
                num = sc.nextInt();
                sc.nextLine();
                for (int id : ids) {
                    if (num == id || num == 0){
                        encontrado = true;
                    }
                }
                if(encontrado){
                    break;
                }else{
                    System.out.println("Opción inválida");
                }
            }else{
                System.out.println("Opción inválida");
                sc.next();
            }
        }
        return num;
    }

    public int comprobarOpcion(int numOpciones){
        int num;
        while(true){
            System.out.println("Eliga una opción (0-"+numOpciones +"): " );
            if(sc.hasNextInt()){
                num = sc.nextInt();
                sc.nextLine();
                if(num>=0 && num<=numOpciones){
                    break;
                }else{
                    System.out.println("Opción inválida");
                }
            }else{
                System.out.println("Opción inválida");
                sc.next();
            }
        }
        return num;
    }

    public Profesor iniciarSesion(){
        System.out.println("=".repeat(24));
        System.out.println("Inicie sesión profesor/a");
        System.out.println("=".repeat(24));
        Profesor profesor = null;

        while(true){
            System.out.println("Introduzca su dni");
            String dni = sc.nextLine();
            System.out.println("Introduzca su contraseña");
            String contrasena = sc.nextLine();

            profesor = profesores.values().stream().filter(p-> p.getDni().equals(dni.trim()) && p.getcontrasena().equals(contrasena.trim())).findFirst().orElse(null);
            if(profesor != null){
                break;
            } else{
                System.out.println("Credenciales incorrectas, vuelve a probar");
            }
        }
        return profesor;
    }
}
