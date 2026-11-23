package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvocatoriaDAO {

    /**
     * 🟢 CREATE: INSCON
     */
    public boolean insertarConvocatoria(int id, String codigo, String fecha, String hora, String orden, String tipo) {
        String sql = "INSERT INTO convocatoria (id_convocatoria, codigo_sesion, estado_quorum, fecha, hora, orden_del_dia, tipo_sesion, aprobado_presidente) VALUES (?, ?, 'PENDIENTE', CAST(? AS DATE), CAST(? AS TIME), ?, ?, TRUE)";
        try (Connection con = datos.ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.setString(2, codigo);
            ps.setString(3, fecha);
            ps.setString(4, hora);
            ps.setString(5, orden);
            ps.setString(6, tipo.trim().toUpperCase());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ConvocatoriaDAO INSERT ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔵 READ INDIVIDUAL: GETCON
     */
    public String consultarConvocatoria(int id) {
        String sql = "SELECT * FROM convocatoria WHERE id_convocatoria = ?";
        try (Connection con = datos.ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "<div style='font-family: sans-serif; padding: 15px; border-left: 5px solid #0d6efd; background-color: #f8f9fa;'>" +
                           "<h2>📅 Consulta de Convocatoria (CU3)</h2>" +
                           "<p><strong>ID Convocatoria:</strong> " + rs.getInt("id_convocatoria") + "</p>" +
                           "<p><strong>Código Oficial:</strong> " + rs.getString("codigo_sesion") + "</p>" +
                           "<p><strong>Fecha Programada:</strong> " + rs.getDate("fecha") + "</p>" +
                           "<p><strong>Hora Estipulada:</strong> " + rs.getTime("hora") + "</p>" +
                           "<p><strong>Orden del Día:</strong> " + rs.getString("orden_del_dia") + "</p>" +
                           "<p><strong>Tipo de Sesión:</strong> [" + rs.getString("tipo_sesion") + "]</p>" +
                           "<p><strong>Estado Quórum:</strong> <span style='font-weight:bold;'>" + rs.getString("estado_quorum") + "</span></p>" +
                           "</div>";
                }
            }
        } catch (SQLException e) {
            return "<p>Error al consultar la convocatoria en PostgreSQL: " + e.getMessage() + "</p>";
        }
        return "<p>La convocatoria con ID " + id + " no fue encontrada en el sistema.</p>";
    }

    /**
     * 🔴 DELETE: DELCON
     */
    public boolean eliminarConvocatoria(int id) {
        String sql = "DELETE FROM convocatoria WHERE id_convocatoria = ?";
        try (Connection con = datos.ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ConvocatoriaDAO DELETE ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🟡 UPDATE: UPDCON
     */
    public boolean modificarConvocatoria(int id, String codigo, String fecha, String hora, String orden, String tipo, String aprobado) {
        String sql = "UPDATE convocatoria SET codigo_sesion = ?, fecha = CAST(? AS DATE), hora = CAST(? AS TIME), orden_del_dia = ?, tipo_sesion = ?, estado_quorum = ? WHERE id_convocatoria = ?";
        try (Connection con = datos.ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigo);
            ps.setString(2, fecha);
            ps.setString(3, hora);
            ps.setString(4, orden);
            ps.setString(5, tipo.trim().toUpperCase());
            ps.setString(6, aprobado.trim().toUpperCase());
            ps.setInt(7, id);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ConvocatoriaDAO UPDATE ERROR]: " + e.getMessage());
            return false;
        }
    }
}
