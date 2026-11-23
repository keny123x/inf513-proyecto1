package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VotacionDAO {

    /**
     * 🟢 CREATE: Inserta un voto individual mapeando las 5 columnas de tu diagrama
     */
    public boolean insertarVotacion(int idVotacion, int idDocumento, int idMiembro, String horaVoto, String tipoVoto) {
        String sql = "INSERT INTO votacion (id_votacion, id_documento, id_miembro, hora_voto, tipo_voto) VALUES (?, ?, ?, CAST(? AS TIME), ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idVotacion);
            ps.setInt(2, idDocumento);
            ps.setInt(3, idMiembro);
            ps.setString(4, horaVoto); // Formato "HH:MM:SS"
            ps.setString(5, tipoVoto.trim().toUpperCase());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VotacionDAO INSERT ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔵 READ GLOBAL: Extrae el repositorio de votos emitidos por el Consejo
     */
    public String obtenerListaVotaciones() {
        String sql = "SELECT * FROM votacion ORDER BY id_votacion ASC";
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>")
          .append("<tr style='background-color: #d1ecf1;'><th>ID Votación</th><th>ID Documento</th><th>ID Miembro</th><th>Hora Voto</th><th>Intención de Voto</th></tr>");
          
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                String voto = rs.getString("tipo_voto");
                String colorCelda = voto.equals("A FAVOR") ? "#d1e7dd" : (voto.equals("EN CONTRA") ? "#f8d7da" : "#fff3cd");
                
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("id_votacion")).append("</td>")
                  .append("<td>").append(rs.getInt("id_documento")).append("</td>")
                  .append("<td>").append(rs.getInt("id_miembro")).append("</td>")
                  .append("<td>").append(rs.getTime("hora_voto")).append("</td>")
                  .append("<td style='background-color: ").append(colorCelda).append("; font-weight: bold;'>").append(voto).append("</td>")
                  .append("</tr>");
            }
            if (count == 0) return "<p>No existen registros de votaciones en la base de datos de la facultad.</p>";
        } catch (SQLException e) {
            return "<p>Error al extraer los datos de PostgreSQL.</p>";
        }
        sb.append("</table>");
        return sb.toString();
    }

    public String verificarQuorumSesionCU5(int numSesion) {
        // Contamos docentes y estudiantes que marcaron asistencia biométrica y NO tienen excusa activa
        String sqlDocentes = "SELECT COUNT(*) AS total FROM asistencia_excusa ae " +
                             "INNER JOIN miembro_consejo mc ON ae.id_miembro = mc.id_miembro " +
                             "WHERE ae.num_sesion = ? AND ae.presento_excusa = FALSE AND mc.tipo_estamento = 'DOCENTE'";
                             
        String sqlEstudiantes = "SELECT COUNT(*) AS total FROM asistencia_excusa ae " +
                                "INNER JOIN miembro_consejo mc ON ae.id_miembro = mc.id_miembro " +
                                "WHERE ae.num_sesion = ? AND ae.presento_excusa = FALSE AND mc.tipo_estamento = 'ESTUDIANTE'";

        int docentes = 0;
        int estudiantes = 0;

        try (Connection con = ConexionDB.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlDocentes)) {
                ps.setInt(1, numSesion);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) docentes = rs.getInt("total"); }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlEstudiantes)) {
                ps.setInt(1, numSesion);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) estudiantes = rs.getInt("total"); }
            }
        } catch (Exception e) {
            return "<p>Error de auditoría en PostgreSQL: " + e.getMessage() + "</p>";
        }

        // Regla Estatutaria UAGRM: Se requiere presencia paritaria mínima de ambos estamentos para instalar la sesión
                // 🚀 CORRECCIÓN DE ENCODING: Cambiamos los emojis por indicadores de texto limpio
        boolean quorumValido = (docentes > 0 && estudiantes > 0);
        String estadoFinal = quorumValido ? "HABILITADO" : "INSUFICIENTE";
        String colorLetrero = quorumValido ? "#d1e7dd" : "#f8d7da";
        
        // Usamos indicadores limpios en texto para evitar el choque de caracteres en tu consola
        String textoVeredicto = quorumValido ? "[OK] QUORUM HABILITADO - SE INICIA LA SESION" : "[ALERTA] QUORUM DESHABILITADO - PLENARIA SUSPENDIDA";

        return "<div style='padding: 20px; background-color: " + colorLetrero + "; border-radius: 5px; font-family: sans-serif;'>" +
               "<h2>Control Automatico de Quorum Paritario (CU5)</h2>" +
               "<h3 style='margin: 10px 0;'>" + textoVeredicto + "</h3><hr style='border:0; border-top:1px solid #ccc;'>" +
               "<p><strong>Docentes Presentes:</strong> " + docentes + "</p>" +
               "<p><strong>Estudiantes Presentes:</strong> " + estudiantes + "</p>" +
               "<p><strong>Estado en Convocatoria:</strong> <span style='font-weight:bold;'>" + estadoFinal + "</span></p>" +
               "</div>";

    }
}
