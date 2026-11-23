package negocio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import datos.ResolucionDAO;


public class ResolucionComando implements IComando {
    private final ResolucionDAO dao = new ResolucionDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            switch (comando.trim().toUpperCase()) {
                case "INSACT": // 🟢 CREATE: INSACT[num_sesion, "codigo", "descripcion", estado_acta]
                    if (parametros == null || parametros.length < 4) return "<h2>Error de Parámetros en Acta</h2>";
                    int nSes = Integer.parseInt(parametros[0]);
                    String codA = parametros[1];
                    String descA = parametros[2];
                    int estA = Integer.parseInt(parametros[3]);
                    return dao.insertarActa(nSes, codA, descA, estA)
                        ? "<h1>Acta de Sesión Generada (CU6)</h1><p>El acta " + codA + " fue guardada con éxito.</p>" 
                        : "<h2>Error al registrar Acta</h2>";

                case "INSRES": // 📜 CREATE: INSRES[id_resolucion, "contenido", "numero_resolucion", num_sesion]
                    if (parametros == null || parametros.length < 4) return "<h2>Error de Parámetros en Resolución</h2>";
                    int idRes = Integer.parseInt(parametros[0]);
                    String cont = parametros[1];
                    String numRes = parametros[2];
                    int nSesRes = Integer.parseInt(parametros[3]);
                    return dao.insertarResolucion(idRes, cont, numRes, nSesRes)
                        ? "<h1>Resolución Emitida con Éxito (CU6)</h1><p>La resolución " + numRes + " fue guardada.</p>" 
                        : "<h2>Error al registrar Resolución</h2>";
                case "AUDIT": // 🔵 READ AUDITORÍA (CU7)
                    // Formato enviado: AUDIT[num_sesion]
                    if (parametros == null || parametros.length < 1) {
                        return "<h2>Error de Parámetros</h2><p>Falta el número de sesión para procesar la auditoría legal.</p>";
                    }
                    int nSesionAudit = Integer.parseInt(parametros[0]);
                    return dao.auditarYFiscalizarSesion(nSesionAudit);


                case "VETRES": // 🛑 UPDATE: VETRES[id_resolucion]
                    if (parametros == null || parametros.length < 1) return "<h2>Error de Parámetros en Veto</h2>";
                    int idVeto = Integer.parseInt(parametros[0]);
                    return dao.aplicarVetoResolucion(idVeto)
                        ? "<div style='padding:15px; background-color:#f8d7da;'><h2>Resolución Vetada (CU6)</h2><p>La resolución con ID " + idVeto + " pasó oficialmente a estado VETADA.</p></div>"
                        : "<h2>Error al aplicar veto en PostgreSQL</h2>";
                                case "INSPUB": // 🟢 CREATE GACETA/PUBLICACIÓN (CU7)
                    // Formato enviado: INSPUB[id_publicacion, numero_edicion, "url_publicacion", num_sesion]
                    // 🚀 PARSER DINÁMICO REFORZADO: Si por alguna razón el split de tu servidor falló y metió todo en parametros[0]
                    String[] datosPub = parametros;
                    if (parametros != null && parametros.length == 1 && parametros[0].contains(",")) {
                        datosPub = parametros[0].split(",");
                    }

                    if (datosPub == null || datosPub.length < 4) {
                        return "<h2>Error de Parámetros</h2><p>Faltan datos oficiales para publicar en la Gaceta. Recibidos: " + (datosPub != null ? datosPub.length : 0) + "</p>";
                    }
                             

                    
                    // Extraemos las variables usando el arreglo reforzado
                    int idPub = Integer.parseInt(datosPub[0].trim());
                    int numEd = Integer.parseInt(datosPub[1].trim());
                    String urlP = datosPub[2].trim();
                    int nSesPub = Integer.parseInt(datosPub[3].trim());

                    boolean exitoPub = dao.insertarPublicacionGaceta(idPub, numEd, urlP, nSesPub);
                    if (exitoPub) {
                        return "<div style='padding: 20px; background-color: #d1e7dd; font-family: sans-serif; border-left: 6px solid #198754;'>" +
                               "<h1>📰 Gaceta Oficial Publicada (CU7)</h1>" +
                               "<p>Se dio de alta la <strong>Publicación ID " + idPub + "</strong> (Edición Nro " + numEd + ").</p>" +
                               "<p>Las resoluciones válidas de la sesión <strong>" + nSesPub + "</strong> han sido promulgadas en la url: <em>" + urlP + "</em>.</p>" +
                               "</div>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se pudo generar la publicación en PostgreSQL.</p>";


                default:
                    return "<h2>Comando No Soportado en CU6</h2>";
            }
        } catch (Exception e) {
            return "<h2>Error Crítico (CU6)</h2><p>" + e.getMessage() + "</p>";
        }
    }
        /**
     * 📰 PUBLICACIÓN/GACETA (CU7): Inserta la edición y le amarra las resoluciones vigentes (FUE_VETADA = FALSE)
     */
    public boolean insertarPublicacionGaceta(int idPublicacion, int numeroEdicion, String urlPublicacion, int numSesion) {
        String sqlPub = "INSERT INTO publicacion (id_publicacion, fecha_publicacion, numero_edicion, url_publicacion) VALUES (?, CURRENT_DATE, ?, ?)";
        String sqlLink = "UPDATE resolucion SET id_publicacion = ? WHERE num_sesion = ? AND fue_vetada = FALSE";
        
        try (Connection con = datos.ConexionDB.getConexion()) {
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
        
        } catch (SQLException e) {
            System.err.println("[ResolucionDAO PUBLICACION ERROR]: " + e.getMessage());
            return false;
        }
    }

}
