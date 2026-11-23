package datos;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ResolucionDAO {

    public boolean insertarActa(int numSesion, String codigo, String descripcion, int estadoActa) {
        String sql = "INSERT INTO acta (num_sesion, codigo, descripcion, estado_acta) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numSesion);
            ps.setString(2, codigo);
            ps.setString(3, descripcion);
            ps.setInt(4, estadoActa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ResolucionDAO ACTA ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🟢 CREATE: Inserta una resolución mapeando las 5 columnas de tu plano oficial
     */
    public boolean insertarResolucion(int idResolucion, String contenido, String numeroResolucion, int numSesion) {
        String sql = "INSERT INTO resolucion (id_resolucion, contenido, numero_resolucion, fecha_emision, fue_vetada, num_sesion) VALUES (?, ?, ?, CURRENT_DATE, FALSE, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idResolucion);
            ps.setString(2, contenido);
            ps.setString(3, numeroResolucion);
            ps.setInt(4, numSesion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ResolucionDAO INSERT RES ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🛑 UPDATE WORKFLOW: Aplica el veto sobre el id_resolucion de tu gráfico
     */
    public boolean aplicarVetoResolucion(int idResolucion) {
        String sql = "UPDATE resolucion SET fue_vetada = TRUE WHERE id_resolucion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idResolucion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
        /**
     * 📰 PUBLICACIÓN/GACETA (CU7): Inserta la edición y le amarra las resoluciones vigentes (FUE_VETADA = FALSE)
     */
    public boolean insertarPublicacionGaceta(int idPublicacion, int numeroEdicion, String urlPublicacion, int numSesion) {
        String sqlPub = "INSERT INTO publicacion (id_publicacion, fecha_publicacion, numero_edicion, url_publicacion) VALUES (?, CURRENT_DATE, ?, ?)";
        String sqlLink = "UPDATE resolucion SET id_publicacion = ? WHERE num_sesion = ? AND fue_vetada = FALSE";
        
        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false); // Transacción segura
            
            // 1. Insertar Publicación
            try (PreparedStatement ps = con.prepareStatement(sqlPub)) {
                ps.setInt(1, idPublicacion);
                ps.setInt(2, numeroEdicion);
                ps.setString(3, urlPublicacion);
                ps.executeUpdate();
            }
            
            // 2. Asociarle las resoluciones aprobadas en limpio de esa sesión
            try (PreparedStatement psLink = con.prepareStatement(sqlLink)) {
                psLink.setInt(1, idPublicacion);
                psLink.setInt(2, numSesion);
                psLink.executeUpdate();
            }
            
            con.commit();
            return true;
        } catch (Exception e) {
            System.err.println("[ResolucionDAO PUBLICACION ERROR]: " + e.getMessage());
            return false;
        }
    }
        /**
     * 📰 AUDITORÍA Y FISCALIZACIÓN (CU7): Genera el reporte de transparencia del Consejo
     */
    public String auditarYFiscalizarSesion(int numSesion) {
        StringBuilder sb = new StringBuilder();
        String sqlActa = "SELECT * FROM acta WHERE num_sesion = ?";
        // 🚀 SEPARACIÓN DE AUDITORÍA: Listamos resoluciones aprobadas y vetadas por separado
        String sqlAprobadas = "SELECT * FROM resolucion WHERE num_sesion = ? AND fue_vetada = FALSE";
        String sqlVetadas = "SELECT * FROM resolucion WHERE num_sesion = ? AND fue_vetada = TRUE";

        sb.append("<div style='font-family: sans-serif; padding: 20px; border: 2px solid #0dcaf0; border-radius: 8px; background-color: #f8fdff;'>")
          .append("<h1 style='color: #0dcaf0; text-align: center; border-bottom: 3px solid #0dcaf0; padding-bottom: 10px; margin-top: 0;'>🔍 REPORTE DE FISCALIZACIÓN Y AUDITORÍA</h1>")
          .append("<p style='text-align: center; font-size: 13px; color: #555;'><strong>Auditoría de Transparencia Facultativa - Sesión Nro ").append(numSesion).append("</strong></p><br>");

        try (Connection con = ConexionDB.getConexion()) {
            // 1. Fiscalizar Acta
            try (PreparedStatement ps = con.prepareStatement(sqlActa)) {
                ps.setInt(1, numSesion);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sb.append("<div style='background-color: #ffffff; padding: 12px; margin-bottom: 15px; border: 1px solid #ccc; border-left: 5px solid #0dcaf0;'>")
                          .append("<h4 style='margin-top:0; color:#0dcaf0;'>📝 Acta de la Plenaria</h4>")
                          .append("<p style='margin:5px 0;'><strong>Código:</strong> ").append(rs.getString("codigo")).append("</p>")
                          .append("<p style='margin:5px 0;'><strong>Descripción:</strong> ").append(rs.getString("descripcion")).append("</p>")
                          .append("</div>");
                    }
                }
            }

            // 2. Fiscalizar Resoluciones Promulgadas (Vigentes)
            try (PreparedStatement ps = con.prepareStatement(sqlAprobadas)) {
                ps.setInt(1, numSesion);
                try (ResultSet rs = ps.executeQuery()) {
                    sb.append("<h4 style='color: #198754; margin-bottom: 5px;'>🟢 Resoluciones Promulgadas (Vigentes)</h4>");
                    int cont = 0;
                    while (rs.next()) {
                        cont++;
                        sb.append("<p style='margin: 4px 0; padding-left: 10px;'>• <strong>").append(rs.getString("numero_resolucion")).append("</strong>: ").append(rs.getString("contenido")).append("</p>");
                    }
                    if (cont == 0) sb.append("<p style='color:#6c757d; font-size:13px; padding-left:10px;'><em>Ninguna resolución vigente.</em></p>");
                }
            }

            sb.append("<br>");

            // 3. Fiscalizar Resoluciones Vetadas (Anuladas)
            try (PreparedStatement ps = con.prepareStatement(sqlVetadas)) {
                ps.setInt(1, numSesion);
                try (ResultSet rs = ps.executeQuery()) {
                    sb.append("<h4 style='color: #dc3545; margin-bottom: 5px;'>🔴 Resoluciones Vetadas (Sin Valor Legal)</h4>");
                    int cont = 0;
                    while (rs.next()) {
                        cont++;
                        sb.append("<p style='margin: 4px 0; padding-left: 10px; text-decoration: line-through; color: #777;'>• <strong>").append(rs.getString("numero_resolucion")).append("</strong>: ").append(rs.getString("contenido")).append("</p>");
                    }
                    if (cont == 0) sb.append("<p style='color:#6c757d; font-size:13px; padding-left:10px;'><em>Ninguna resolución vetada en esta plenaria.</em></p>");
                }
            }

        } catch (Exception e) {
            return "<p>Error en la auditoría de PostgreSQL: " + e.getMessage() + "</p>";
        }

        sb.append("<hr style='border: 0; border-top: 1px solid #0dcaf0; margin-top: 20px;'>")
          .append("<p style='font-size: 11px; color: #555; text-align: center;'>Gaceta de Transparencia y Fiscalización - Grupo #07 FICCT</p></div>");
        return sb.toString();
    }


}
