package negocio;

import datos.ConvocatoriaDAO;
import datos.Habilitacion_SuplenteDAO;

public class ConvocatoriaComando implements IComando {
    private final ConvocatoriaDAO dao = new ConvocatoriaDAO();
    private final datos.Habilitacion_SuplenteDAO  Habilitacion_SuplenteDAO= new datos.Habilitacion_SuplenteDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            switch (comando.trim().toUpperCase()) {
                case "INSCON": // 🟢 CREATE: INSCON[id, "codigo", "fecha", "hora", "orden", "tipo"]
                    if (parametros == null || parametros.length < 6) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren exactamente 6 datos para la convocatoria.</p>";
                    }
                    // 🚀 MACO DE ÍNDICES: Mapeamos cada posición del arreglo por separado
                    int idIns = Integer.parseInt(parametros[0]);
                    String codigoIns = parametros[1];
                    String fechaIns = parametros[2];
                    String horaIns = parametros[3];
                    String ordenIns = parametros[4];
                    String tipoIns = parametros[5];

                    return dao.insertarConvocatoria(idIns, codigoIns, fechaIns, horaIns, ordenIns, tipoIns)
                        ? "<h1>Convocatoria Registrada (CU3)</h1><p>La planificación con ID " + idIns + " fue guardada con éxito.</p>" 
                        : "<h2>Error de Guardado</h2><p>No se pudo almacenar el registro en PostgreSQL.</p>";

                case "GETCON": // 🔵 READ INDIVIDUAL: GETCON[id]
                    if (parametros == null || parametros.length < 1) {
                        return "<h2>Error de Parámetros</h2><p>Falta el ID de la convocatoria.</p>";
                    }
                    int idGet = Integer.parseInt(parametros[0]);
                    return "<h1>Consulta de Convocatoria (CU3)</h1>" + dao.consultarConvocatoria(idGet);

                case "DELCON": // 🔴 DELETE: DELCON[id]
                    if (parametros == null || parametros.length < 1) {
                        return "<h2>Error de Parámetros</h2><p>Falta el ID de la convocatoria para eliminar.</p>";
                    }
                    int idDel = Integer.parseInt(parametros[0]);
                    return dao.eliminarConvocatoria(idDel)
                        ? "<h1>Convocatoria Eliminada (CU3)</h1><p>El registro ID " + idDel + " fue borrado con éxito de la UAGRM.</p>" 
                        : "<h2>Error al Eliminar</h2><p>No se encontró la convocatoria en la base de datos.</p>";

                case "UPDCON": // 🟡 UPDATE: UPDCON[id, "codigo", "fecha", "hora", "orden", "tipo", aprobado]
                    if (parametros == null || parametros.length < 7) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren exactamente 7 datos para modificar.</p>";
                    }
                    int idUpd = Integer.parseInt(parametros[0]);
                    String codigoUpd = parametros[1];
                    String fechaUpd = parametros[2];
                    String horaUpd = parametros[3];
                    String ordenUpd = parametros[4];
                    String tipoUpd = parametros[5];
                    String aprobadoUpd = parametros[6];

                    return dao.modificarConvocatoria(idUpd, codigoUpd, fechaUpd, horaUpd, ordenUpd, tipoUpd, aprobadoUpd)
                        ? "<h1>Convocatoria Modificada (CU3)</h1><p>Los datos de la convocatoria ID " + idUpd + " se actualizaron correctamente.</p>" 
                        : "<h2>Error al Modificar</h2><p>No se pudo actualizar el registro en la universidad.</p>";


                case "INSEXC": // 🟢 CREATE: INSEXC[id_asistencia, id_suplente, id_titular, id_suplente_miembro, num_sesion, "motivo", "nombre"]
                    if (parametros == null || parametros.length < 7) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren exactamente 7 datos para justificar y habilitar al suplente.</p>";
                    }
                    int idAsis = Integer.parseInt(parametros[0]);
                    int idSupl = Integer.parseInt(parametros[1]);
                    int idTitu = Integer.parseInt(parametros[2]);
                    int idSupM = Integer.parseInt(parametros[3]);
                    int nSesi = Integer.parseInt(parametros[4]);
                    String motEx = parametros[5];
                    String nomSu = parametros[6];

                    boolean exitoTrans = Habilitacion_SuplenteDAO.registrarExcusaYSuplente(idAsis, idSupl, idTitu, idSupM, nSesi, motEx, nomSu);
                    if (exitoTrans) {
                        return "<h1>Excusa Aceptada y Suplente Habilitado (CU3)</h1>" +
                               "<p>El titular ID " + idTitu + " fue justificado. El suplente <strong>" + nomSu + "</strong> quedó habilitado para la sesión " + nSesi + ".</p>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se pudo almacenar la transacción. Asegúrese de que el ID de asistencia o suplente no estén duplicados.</p>";

                case "LISSUP": // 🔵 READ GLOBAL (Formato de asterisco oficial del ingeniero)
                    return "<h1>Reporte Oficial de Suplentes Habilitados (CU3)</h1>" + Habilitacion_SuplenteDAO.obtenerListaSuplentes();


                default:
                    return "<h2>Comando No Soportado en CU3</h2>";
            }
        } catch (NumberFormatException nfe) {
            return "<h2>Error de Formato</h2><p>El ID de la convocatoria debe ser un número entero puro sin comillas.</p>";
        } catch (Exception e) {
            return "<h2>Error Crítico (CU3)</h2><p>Ocurrió un problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }
}
