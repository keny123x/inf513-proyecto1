package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CorrespondeciaDAO {
    public boolean insertarDocumento(int idDocumento, String titulo, String tipo, String urlArchivo, String fechaIngreso, boolean esUrgente) {
        String sql = "INSERT INTO correspondencia (id_documento, estado_flujo, titulo, tipo, url_archivo, fecha_ingreso, es_urgente, id_convocatoria) VALUES (?, 'Pendiente', ?, ?, ?, CAST(? AS DATE), ?, NULL)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDocumento);
            ps.setString(2, titulo);
            ps.setString(3, tipo);
            ps.setString(4, urlArchivo);
            ps.setString(5, fechaIngreso);
            ps.setBoolean(6, esUrgente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CorrespondeciaDAO INSERT ERROR]: " + e.getMessage());
            return false;
        }
    }

    public String obtenerColaPendientes() {
        String sql = "SELECT id_documento, titulo, tipo, fecha_ingreso, es_urgente FROM correspondencia WHERE id_convocatoria IS NULL AND estado_flujo = 'Pendiente' ORDER BY fecha_ingreso ASC";
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'><tr><th>ID</th><th>Título</th><th>Tipo</th><th>Fecha</th></tr>");
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int contador = 0;
            while (rs.next()) {
                contador++;
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("id_documento")).append("</td>")
                  .append("<td>").append(rs.getString("titulo")).append("</td>")
                  .append("<td>").append(rs.getString("tipo")).append("</td>")
                  .append("<td>").append(rs.getDate("fecha_ingreso")).append("</td>")
                  .append("</tr>");
            }
            if (contador == 0) return "No hay trámites pendientes.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en PostgreSQL.";
        }
        sb.append("</table>");
        return sb.toString();
    }

    public boolean agendarDocumento(int idDocumento, int idConvocatoria) {
        String sql = "UPDATE correspondencia SET id_convocatoria = ?, estado_flujo = 'En Debate' WHERE id_documento = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConvocatoria);
            ps.setInt(2, idDocumento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        /**
     * 📂 REPOSITORIO: Inserta correspondencia oficial de 7 atributos según tu diagrama
     */
    public boolean insertarCorrespondencia(int idDoc, int idConv, String titulo, String tipo, String url, boolean esUrgente) {
        // 🚀 CORRECCIÓN CLAVE: Quitamos el '?' en la posición fija de 'RECIBIDO' y dejamos los 6 comodines para las variables
        String sql = "INSERT INTO correspondencia (id_documento, id_convocatoria, estado_flujo, titulo, tipo, url_archivo, fecha_ingreso, es_urgente) VALUES (?, ?, 'RECIBIDO', ?, ?, ?, CURRENT_DATE, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idDoc);        // 1. Mapea id_documento
            ps.setInt(2, idConv);       // 2. Mapea id_convocatoria
            ps.setString(3, titulo);    // 3. Mapea titulo
            ps.setString(4, tipo);      // 4. Mapea tipo
            ps.setString(5, url);       // 5. Mapea url_archivo
            ps.setBoolean(6, esUrgente); // 6. Mapea es_urgente
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[CorrespondenciaDAO INSCOR ERROR]: " + e.getMessage());
            return false;
        }
    }
    


    /**
     * 🔄 WORKFLOW: Modifica el atributo 'estado_flujo' de tu diagrama de clases
     */
    public boolean actualizarWorkflowFlujo(int idDoc, String nuevoEstado) {
        String sql = "UPDATE correspondencia SET estado_flujo = ? WHERE id_documento = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.trim().toUpperCase());
            ps.setInt(2, idDoc);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
}
