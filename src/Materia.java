import java.util.ArrayList;
import java.util.List;

public class Materia {
    private String nombreMateria;
    private List<Double> notas;

    public Materia(String nombreMateria) {
        this.nombreMateria = nombreMateria;
        this.notas = new ArrayList<>();
    }

    public void agregarNota(double nota) {
        if (nota >= 0 && nota <= 100) {
            this.notas.add(nota);
        }
    }

    public double calcularPromedioMateria() {
        if (notas.isEmpty()) return 0.0;

        double suma = 0;
        for (double n : notas) {
            suma += n;
        }
        return suma / notas.size();
    }

    public String getNombreMateria() { return nombreMateria; }
    public List<Double> getNotas() { return notas; }

    public String getNotasFormateadas() {
        if (notas.isEmpty()) return "Sin notas";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notas.size(); i++) {
            sb.append(String.format("%.1f", notas.get(i)));
            if (i < notas.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return nombreMateria + " (Prom: " + String.format("%.1f", calcularPromedioMateria()) + ")";
    }
}