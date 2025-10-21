package seccion3.Act21;

public class Cancion {
    private static int id;
    private String titulo;
    private String artista;
    private String anyo;
    private String formato;

    public Cancion(String titulo, String artista, String anyo, String formato){
        this.titulo = titulo;
        this.artista = artista;
        this.anyo = anyo;
        this.formato = formato;
        id += 1;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public String getAnyo() {
        return anyo;
    }

    public String getFormato() {
        return formato;
    }

    public static int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "id='" + id + '\'' +
                "artista='" + artista + '\'' +
                ", titulo='" + titulo + '\'' +
                ", anyo='" + anyo + '\'' +
                ", formato='" + formato + '\'' +
                '}';
    }
}
