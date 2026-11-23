package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AsistenciaExcusaDAO {

    public boolean registrarAsistenciaExcusa(String marca, boolean presentoExcusa, String motivo, boolean suplenteHabilitado, int idMiembro, int numSesion) {
        String sql = "INSERT INTO asistencia_excusa (id_asistencia, marca_biometrica, presento_excusa, motivo_excusa, suplente_habilitado, id_miembro, num_sesion) " +
                     "VALUES ((SELECT COALESCE(MAX(id_asistencia), 0) + 1 FROM asistencia_excusa), ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, marca);
            ps.setBoolean(2, presentoExcusa);
            ps.setString(3, motivo);
            ps.setBoolean(4, suplenteHabilitado);
            ps.setInt(5, idMiembro);
            ps.setInt(6, numSesion);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AsistenciaExcusaDAO INSERT ERROR]: " + e.getMessage());
            return false;
        }
    }
}
