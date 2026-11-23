package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteDAO {
    public String obtenerResumenAsistencias(int numSesion) {
        String sql = "SELECT presento_excusa, COUNT(*) AS total FROM asistencia_excusa WHERE num_sesion = ? GROUP BY presento_excusa";
        int presentes = 0;
        int ausentesConExcusa = 0;
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numSesion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!rs.getBoolean("presento_excusa")) {
                        presentes = rs.getInt("total");
                    } else {
                        ausentesConExcusa = rs.getInt("total");
                    }
                }
            }
            return "<p><strong>Presentes en Sala:</strong> " + presentes + " | <strong>Ausentes con Excusa:</strong> " + ausentesConExcusa + "</p>";
        } catch (Exception e) {
            return "<p>Error al extraer estadísticas de asistencia: " + e.getMessage() + "</p>";
        }
    }
}
