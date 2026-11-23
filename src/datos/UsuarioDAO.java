package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * 🟢 CREATE: Inserta un usuario usando el 'ci' como String (VARCHAR en BD)
     */
    public boolean crearUsuario(String username, String contrasena, String rol, String ci) {
        // Normalizamos el rol: "delegado" o "DELEGADO" -> "Delegado"
        String rolNormalizado = rol.trim().substring(0, 1).toUpperCase() + rol.trim().substring(1).toLowerCase();

        if (!rolNormalizado.equals("Presidente") && !rolNormalizado.equals("Secretario") && !rolNormalizado.equals("Delegado")) {
            System.err.println("[CU1 ERROR] El rol " + rol + " no pertenece al Consejo Facultativo.");
            return false;
        }

        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, rol, ci) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username.trim());
            ps.setString(2, contrasena.trim());
            ps.setString(3, rolNormalizado);
            ps.setString(4, ci.trim());
            
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
    // 🚀 ESTO ES CLAVE: Te dirá la razón exacta en la consola de tu servidor/IDE
    System.err.println("[CRÍTICO] Error al insertar usuario.");
    System.err.println("Mensaje de Postgres: " + e.getMessage());
    System.err.println("Código de Estado SQL: " + e.getSQLState());
    
    return false;
}
    }

    /**
     * 🔴 DELETE: Elimina físicamente un usuario por su id_usuario
     */
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("[UsuarioDAO DELETE ERROR] " + e.getMessage());
            return false;
        }
    }

    /**
     * 🟡 UPDATE: Modifica los datos incluyendo el 'ci' como String
     */
    public boolean modificarUsuario(int id, String user, String pass, String rol, String ci) {
        String sql = "UPDATE usuario SET nombre_usuario=?, contrasena=?, rol=?, ci=? WHERE id_usuario=?";
        try (Connection con = ConexionDB.getConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user); 
            ps.setString(2, pass); 
            ps.setString(3, rol); 
            ps.setString(4, ci); // 🚀 CORREGIDO: Se envía como String
            ps.setInt(5, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            System.err.println("[UsuarioDAO UPDATE ERROR] " + e.getMessage());
            return false; 
        }
    }

     /**
     * 🔵 READ INDIVIDUAL: Consulta un usuario obteniendo el 'ci' como String
     */
    public String consultarUsuario(int id) {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.rol, u.ci, p.nombre " +
                     "FROM usuario u " +
                     "INNER JOIN persona p ON u.ci = p.ci " +
                     "WHERE u.id_usuario = ?";
        try (Connection con = ConexionDB.getConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String titular = rs.getString("nombre");
                    return "<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>" +
                           "<tr style='background-color: #f2f2f2;'><th>ID</th><th>Usuario</th><th>Rol</th><th>CI</th><th>Titular</th></tr>" +
                           "<tr><td>" + rs.getInt("id_usuario") + "</td>" +
                           "<td>" + rs.getString("nombre_usuario") + "</td>" +
                           "<td>" + rs.getString("rol") + "</td>" +
                           "<td>" + rs.getString("ci") + "</td>" +
                           "<td>" + titular + "</td></tr></table>";
                }
            }
        } catch (Exception e) { 
            return "Error al consultar: " + e.getMessage(); 
        }
        return "No encontrado";
    }

    /**
     * 🔵 READ GLOBAL: Lista todos los accesos con el 'ci' tratado como String
     */
    public String obtenerListaUsuarios() {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.rol, u.ci, p.nombre " +
                     "FROM usuario u " +
                     "INNER JOIN persona p ON u.ci = p.ci " +
                     "ORDER BY u.id_usuario ASC";
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>")
          .append("<tr style='background-color: #d1e7dd;'><th>ID Usuario</th><th>Nombre Usuario</th><th>Rol</th><th>CI</th><th>Titular</th></tr>");
          
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int contador = 0;
            while (rs.next()) {
                contador++;
                String titular = rs.getString("nombre");
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("id_usuario")).append("</td>")
                  .append("<td>").append(rs.getString("nombre_usuario")).append("</td>")
                  .append("<td>").append(rs.getString("rol")).append("</td>")
                  .append("<td>").append(rs.getString("ci")).append("</td>")
                  .append("<td>").append(titular).append("</td>")
                  .append("</tr>");
            }
            if (contador == 0) return "<p>No hay ningún usuario registrado actualmente.</p>";
        } catch (SQLException e) {
            return "<p>Error al extraer la lista de usuarios: " + e.getMessage() + "</p>";
        }
        sb.append("</table>");
        return sb.toString();
    }
}
