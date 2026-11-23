package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Habilitacion_SuplenteDAO {

    /**
     * 🟢 TRANSACTION: Asienta la excusa del titular e inyecta la habilitación del suplente de un solo golpe
     */
    public boolean registrarExcusaYSuplente(int idAsistencia, int idSuplente, int idTitular, int idSuplenteMiembro, int numSesion, String motivo, String nombreSuplente) {
        String sqlExcusa = "INSERT INTO asistencia_excusa (id_asistencia, marca_biometrica, presento_excusa, motivo_excusa, suplente_habilitado, id_miembro, num_sesion) VALUES (?, CAST(CURRENT_TIMESTAMP AS VARCHAR), TRUE, ?, TRUE, ?, ?)";
        String sqlSuplente = "INSERT INTO habilitacion_suplente (id_suplente, id_suplenete_mimebro, id_titular, num_sesion, nombre) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false); // Iniciamos transacción segura para no dejar cables sueltos
            
            // 1. Insertar en AsistenciaExcusa
            try (PreparedStatement psExc = con.prepareStatement(sqlExcusa)) {
                psExc.setInt(1, idAsistencia);
                psExc.setString(2, motivo);
                psExc.setInt(3, idTitular);
                psExc.setInt(4, numSesion);
                psExc.executeUpdate();
            }
            
            // 2. Insertar en Habilitacion_suplente
            try (PreparedStatement psSup = con.prepareStatement(sqlSuplente)) {
                psSup.setInt(1, idSuplente);
                psSup.setInt(2, idSuplenteMiembro);
                psSup.setInt(3, idTitular);
                psSup.setInt(4, numSesion);
                psSup.setString(5, nombreSuplente);
                psSup.executeUpdate();
            }
            
            con.commit(); // Consolidamos los datos en PostgreSQL de la facultad
            return true;
        } catch (Exception e) {
            System.err.println("[SuplenteDAO TRANSACTION ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔵 READ GLOBAL: Devuelve el listado HTML de suplentes activos
     */
    public String obtenerListaSuplentes() {
        String sql = "SELECT * FROM habilitacion_suplente ORDER BY id_suplente ASC";
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>")
          .append("<tr style='background-color: #ffe5d9;'><th>ID Suplente</th><th>ID Consejero Suplente</th><th>ID Titular Afectado</th><th>Nro Sesión</th><th>Nombre Completo</th></tr>");
          
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("id_suplente")).append("</td>")
                  .append("<td>").append(rs.getInt("id_suplenete_mimebro")).append("</td>")
                  .append("<td>").append(rs.getInt("id_titular")).append("</td>")
                  .append("<td>").append(rs.getInt("num_sesion")).append("</td>")
                  .append("<td>").append(rs.getString("nombre")).append("</td>")
                  .append("</tr>");
            }
            if (count == 0) return "<p>No hay ninguna habilitación de suplentes registrada para las plenarias actuales.</p>";
        } catch (Exception e) {
            return "<p>Error al extraer los suplentes de PostgreSQL.</p>";
        }
        sb.append("</table>");
        return sb.toString();
    }
}
