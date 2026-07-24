import java.util.ArrayList;
import java.util.List;

public class Estudiante {
    private String nombre;
    private int edad;
    private String matricula;
    private List<Materia> materias;

    public Estudiante(String nombre, int edad, String matricula) {
        this.nombre = nombre;
        this.edad = edad;
        this.matricula = matricula;
        this.materias = new ArrayList<>();
    }

    public void agregarMateria(String nombreMateria) {
        for (Materia m : materias) {
            if (m.getNombreMateria().equalsIgnoreCase(nombreMateria)) {
                return; // Evita materias duplicadas con el mismo nombre
            }
        }
        this.materias.add(new Materia(nombreMateria));
    }

    public double calcularPromedioGeneral() {
        if (materias.isEmpty()) return 0.0;

        double sumaPromedios = 0;
        int materiasConNotas = 0;

        for (Materia m : materias) {
            if (!m.getNotas().isEmpty()) {
                sumaPromedios += m.calcularPromedioMateria();
                materiasConNotas++;
            }
        }

        if (materiasConNotas == 0) return 0.0;
        return sumaPromedios / materiasConNotas;
    }

    public String getInsigniaRango() {
        if (materias.isEmpty()) return "➖ Sin Materias";

        double promedio = calcularPromedioGeneral();
        if (promedio >= 90 && promedio <= 100) {
            return "💎 Diamante";
        } else if (promedio >= 80 && promedio < 90) {
            return "🥈 Plata";
        } else if (promedio >= 70 && promedio < 80) {
            return "🥉 Bronce";
        } else {
            return "⚠️ En Riesgo";
        }
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public String getMatricula() { return matricula; }
    public List<Materia> getMaterias() { return materias; }

    @Override
    public String toString() {
        return matricula + " - " + nombre;
    }
}