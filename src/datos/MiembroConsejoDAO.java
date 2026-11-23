package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MiembroConsejoDAO {

  
       /**
     * 🟢 CREATE RELACIONAL: Inserta un consejero paritario amarrado a una Persona por su CI
     */
        /**
     * 🟢 CREATE RELACIONAL: Versión purificada de 9 parámetros sin variables sueltas
     */
     public boolean registrarMiembro(int idMiembro, String condicion, String estamento, String estado, String frente, String fecha, int faltas, String mandato, String ci) {
        // 🚀 CORRECCIÓN DE MÁSCARAS: El CAST de la fecha va estrictamente en el sexto '?' de la consulta
        String sql = "INSERT INTO miembro_consejo (id_miembro, condicion, tipo_estamento, estado, frente_electoral, fecha_acreditacion, contador_faltas, estado_mandato, ci) VALUES (?, ?, ?, ?, ?, CAST(? AS DATE), ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
             ps.setInt(1, idMiembro);
            ps.setString(2, condicion.trim().toUpperCase());
            ps.setString(3, estamento.trim().toUpperCase());
            ps.setString(4, estado.trim().toUpperCase());
            ps.setString(5, frente.trim());
            ps.setString(6, fecha.trim());
            ps.setInt(7, faltas);
            ps.setString(8, mandato.trim().toUpperCase());
            ps.setString(9, ci.trim());            
            
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("[MiembroConsejoDAO INSERT RELACIONAL ERROR]: " + e.getMessage());
            return false;
        }
    }


    /**
     * 🔵 READ GLOBAL: Extrae la lista masiva de consejeros paritarias
     */
    public String obtenerListaMiembros() {
        String sql = "SELECT * FROM miembro_consejo ORDER BY id_miembro ASC";
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>")
          .append("<tr style='background-color: #ffecb3;'><th>ID Miembro</th><th>Condición</th><th>Estamento</th><th>Estado</th><th>Frente</th><th>Acreditación</th><th>Faltas</th><th>Mandato</th></tr>");
          
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("id_miembro")).append("</td>")
                  .append("<td>").append(rs.getString("condicion")).append("</td>")
                  .append("<td>").append(rs.getString("tipo_estamento")).append("</td>")
                  .append("<td>").append(rs.getString("estado")).append("</td>")
                  .append("<td>").append(rs.getString("frente_electoral")).append("</td>")
                  .append("<td>").append(rs.getDate("fecha_acreditacion")).append("</td>")
                  .append("<td>").append(rs.getInt("contador_faltas")).append("</td>")
                  .append("<td>").append(rs.getString("estado_mandato")).append("</td>")
                  .append("</tr>");
            }
        } catch (SQLException e) {
            return "<p>Error al extraer los datos de PostgreSQL.</p>";
        }
        sb.append("</table>");
        return sb.toString();
    }
     public boolean habilitarQuorumSesion(int numSesion, String estadoQuorum) {
        // 🚀 USAMOS LAS COLUMNAS EXACTAS: id_convocatoria y num_sesion de la clase Sesion
        String sql = "UPDATE convocatoria SET estado_quorum = ? WHERE id_convocatoria = (SELECT id_convocatoria FROM sesion WHERE num_sesion = ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, estadoQuorum);
            ps.setInt(2, numSesion);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[QuorumDAO ERROR - Atributos Estrictos]: " + e.getMessage());
            return false;
        }
    }
        /**
     * 🟢 CREATE TRANSACCIONAL: Crea Convocatoria (8 atributos) y Sesión (5 atributos) de un solo golpe desde el correo
     */
    public boolean crearConvocatoriaYSesion(int numSesion, int idConvocatoria, String codigoSesion, String ordenDia, String tipoSesion, int idPresidente) {
        String sqlConv = "INSERT INTO convocatoria (id_convocatoria, codigo_sesion, estado_quorum, fecha, hora, orden_del_dia, tipo_sesion, aprobado_presidente) VALUES (?, ?, 'PENDIENTE', CURRENT_DATE, CURRENT_TIME, ?, ?, TRUE)";
        String sqlSes = "INSERT INTO sesion (id_presidente_miembro, id_convocatoria, num_sesion, fecha, hora) VALUES (?, ?, ?, CURRENT_DATE, CURRENT_TIME)";
        
        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false); // Transacción segura
            
            // 1. Insertar Convocatoria
            try (PreparedStatement psConv = con.prepareStatement(sqlConv)) {
                psConv.setInt(1, idConvocatoria);
                psConv.setString(2, codigoSesion);
                psConv.setString(3, ordenDia);
                psConv.setString(4, tipoSesion.trim().toUpperCase());
                psConv.executeUpdate();
            }
            
            // 2. Insertar Sesión amarrada al id_convocatoria
            try (PreparedStatement psSes = con.prepareStatement(sqlSes)) {
                psSes.setInt(1, idPresidente);
                psSes.setInt(2, idConvocatoria);
                psSes.setInt(3, numSesion);
                psSes.executeUpdate();
            }
            
            con.commit(); // Consolidar en PostgreSQL
            return true;
        } catch (Exception e) {
            System.err.println("[MiembroConsejoDAO INSSES ERROR]: " + e.getMessage());
            return false;
        }
    }
    public String obtenerEstadisticasEstamento() {
        String sql = "SELECT " +
                     "SUM(CASE WHEN tipo_estamento = 'DOCENTE' THEN 1 ELSE 0 END) AS docentes, " +
                     "SUM(CASE WHEN tipo_estamento = 'ESTUDIANTE' THEN 1 ELSE 0 END) AS estudiantes, " +
                     "COUNT(*) AS total " +
                     "FROM miembro_consejo";
        
        int docentes = 0;
        int estudiantes = 0;
        int total = 0;

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                docentes = rs.getInt("docentes");
                var estResult = rs.getInt("estudiantes");
                estudiantes = estResult;
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            return "<p>Error estadístico en PostgreSQL: " + e.getMessage() + "</p>";
        }

        if (total == 0) return "<p>No existen miembros registrados para generar estadísticas.</p>";

        // Cálculos analíticos de porcentajes
        int porcDoc = (docentes * 100) / total;
        int porcEst = (estudiantes * 100) / total;

        return "<div style='font-family: sans-serif; padding: 20px; border: 2px solid #6f42c1; border-radius: 8px; background-color: #fbf9ff; max-width: 500px;'> " +
               "<h2 style='color: #6f42c1; margin-top: 0; text-align: center;'>📊 CU8: REPORTE Y ESTADÍSTICAS</h2>" +
               "<p style='text-align: center; color: #555; font-size: 12px;'>Distribución Oficial de Consejeros Paritarios</p><hr style='border:0; border-top:1px solid #ddd;'>" +
               
               "<p><strong>Docentes Acreditados:</strong> " + docentes + " (" + porcDoc + "%)</p>" +
               "<div style='background-color: #e9ecef; border-radius: 4px; height: 20px; width: 100%; margin-bottom: 15px;'>" +
               "  <div style='background-color: #0d6efd; width: " + porcDoc + "%; height: 100%; border-radius: 4px;'></div>" +
               "</div>" +
               
               "<p><strong>Estudiantes Acreditados:</strong> " + estudiantes + " (" + porcEst + "%)</p>" +
               "<div style='background-color: #e9ecef; border-radius: 4px; height: 20px; width: 100%; margin-bottom: 15px;'>" +
               "  <div style='background-color: #ffc107; width: " + porcEst + "%; height: 100%; border-radius: 4px;'></div>" +
               "</div>" +
               
               "<hr style='border:0; border-top:1px solid #ddd;'>" +
               "<p style='margin-bottom: 0; font-size: 13px; text-align: center;'><strong>Universo Total de Miembros:</strong> " + total + " Consejeros</p>" +
               "</div>";
    }

}
