package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PersonaDAO {

    public boolean registrarPersona(String ci, String nombre, String celular, String email) {
        String sql = "INSERT INTO persona (ci, nombre, celular, email) VALUES (?, ?, CAST(? AS INT), ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            ps.setString(2, nombre);
            ps.setString(3, celular);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[PersonaDAO INSERT ERROR]: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarPersona(String ci, String nombre, String celular, String email) {
        String sql = "UPDATE persona SET nombre = ?, celular = CAST(? AS INT), email = ? WHERE ci = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, celular);
            ps.setString(3, email);
            ps.setString(4, ci);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[PersonaDAO UPDATE ERROR]: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPersona(String ci) {
        String sql = "DELETE FROM persona WHERE ci = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[PersonaDAO DELETE ERROR]: " + e.getMessage());
            return false;
        }
    }

    public String consultarPersona(String ci) {
        String sql = "SELECT ci, nombre, celular, email FROM persona WHERE ci = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>" +
                           "<tr style='background-color: #f2f2f2;'><th>C.I.</th><th>Nombre</th><th>Celular</th><th>Email</th></tr>" +
                           "<tr><td>" + rs.getString("ci") + "</td>" +
                           "<td>" + rs.getString("nombre") + "</td>" +
                           "<td>" + rs.getInt("celular") + "</td>" +
                           "<td>" + rs.getString("email") + "</td></tr></table>";
                }
            }
            return "<p>No se encontró ninguna persona registrada con el C.I. " + ci + ".</p>";
        } catch (Exception e) {
            return "<p>Error al consultar PostgreSQL: " + e.getMessage() + "</p>";
        }
    }

    public String obtenerListaPersonas() {
        String sql = "SELECT ci, nombre, celular, email FROM persona ORDER BY ci ASC";
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>")
          .append("<tr style='background-color: #cfe2ff;'><th>C.I.</th><th>Nombre</th><th>Celular</th><th>Email</th></tr>");
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sb.append("<tr>")
                  .append("<td>").append(rs.getString("ci")).append("</td>")
                  .append("<td>").append(rs.getString("nombre")).append("</td>")
                  .append("<td>").append(rs.getInt("celular")).append("</td>")
                  .append("<td>").append(rs.getString("email")).append("</td>")
                  .append("</tr>");
            }
        } catch (Exception e) {
            return "<p>Error al extraer la lista: " + e.getMessage() + "</p>";
        }
        sb.append("</table>");
        return sb.toString();
    }
}
