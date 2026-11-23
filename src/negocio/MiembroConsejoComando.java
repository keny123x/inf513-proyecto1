package negocio;

import datos.MiembroConsejoDAO;

public class MiembroConsejoComando implements IComando {
    private final MiembroConsejoDAO dao = new MiembroConsejoDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            switch (comando.trim().toUpperCase()) {
                case "LISMC": // 🔵 READ GLOBAL (Acepta LISMC["*"])
                    return "<h1>Reporte Oficial de Miembros del Consejo (Cogobierno Paritario)</h1>" + dao.obtenerListaMiembros();
                    // Coloca este caso dentro del switch de tu archivo MiembroConsejoComando.java:
                case "ESTEST": // 📊 REPORTE ESTADÍSTICO DE ESTAMENTOS (CU8)
                    return dao.obtenerEstadisticasEstamento();

                    case "INSMC": 
                    // Formato en Subject: INSMC[15,"TITULAR","ESTUDIANTE","ACTIVO","FRENTE-U-FICCT","2026-10-15",0,"VIGENTE","4715500"]
                    if (parametros == null || parametros.length < 9) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren los 8 atributos más el C.I. de la persona relacionada.</p>";
                    }
                    
                    int idMiembro = Integer.parseInt(parametros[0]);
                    String condicion = parametros[1];
                    String estamento = parametros[2];
                    String estado = parametros[3];
                    String frente = parametros[4];
                    String fechaAcreditacion = parametros[5];
                    int contadorFaltas = Integer.parseInt(parametros[6]);
                    String estadoMandato = parametros[7];
                    String ci = parametros[8]; // 🚀 Captura el carnet de enlace

                    boolean ok = dao.registrarMiembro(idMiembro, condicion, estamento, estado, frente, fechaAcreditacion, contadorFaltas, estadoMandato, ci);
                    if (ok) {
                        return "<h1>Miembro Acreditado Relacional (CU2)</h1><p>El consejero ID " + idMiembro + " fue enlazado con éxito a la persona con C.I. " + ci + ".</p>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se pudo almacenar el registro. Verifique que el ID no esté duplicado y que el C.I. exista en la universidad.</p>";
                                case "UPDQUO": // 🟡 CONTROL DE QUÓRUM PARITARIO EN VIVO
                    // Formato en Subject: UPDQUO[1040,"HABILITADO"] o UPDQUO[1040,"DESHABILITADO"]
                    if (parametros == null || parametros.length < 2) {
                        return "<h2>Error de Parámetros</h2><p>Faltan datos de sesión y estado para procesar el quórum.</p>";
                    }
                    int numSesion = Integer.parseInt(parametros[0]);
                    String nuevoEstado = parametros[1].trim().toUpperCase(); // 'HABILITADO' o 'DESHABILITADO'

                    boolean modificado = dao.habilitarQuorumSesion(numSesion, nuevoEstado);
                    if (modificado) {
                        // 🚀 CAMBIO DE LETREROS MAESTROS SEGÚN TU SUGERENCIA DE INGENIERÍA
                        String tituloCabecera;
                        String colorAlerta;
                        
                        if (nuevoEstado.equals("HABILITADO")) {
                            tituloCabecera = "🟢 QUÓRUM HABILITADO - SE AUTORIZA LA SESIÓN";
                            colorAlerta = "#d1e7dd"; // Verde institucional de éxito
                        } else {
                            tituloCabecera = "🔴 QUÓRUM DESHABILITADO - PLENARIA SUSPENDIDA";
                            colorAlerta = "#f8d7da"; // Rojo controlado de alerta
                        }

                        return "<div style='padding: 20px; background-color: " + colorAlerta + "; border-radius: 5px; font-family: sans-serif;'>" +
                               "<h1>" + tituloCabecera + "</h1>" +
                               "<p>La sesión paritaria Nro <strong>" + numSesion + "</strong> fue procesada con éxito.</p>" +
                               "<p>El atributo estricto <strong>estado_quorum</strong> de la Convocatoria ha sido actualizado a: <strong>" + nuevoEstado + "</strong> en PostgreSQL.</p>" +
                               "<hr style='border: 0; border-top: 1px solid #ccc;'>" +
                               "<p style='font-size: 11px; color: #555;'>Sistema de Cogobierno Paritario - Grupo #07 UAGRM</p>" +
                               "</div>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se encontró la sesión " + numSesion + " registrada en la base de datos remota.</p>";
                    case "INSSES": // 🟢 CREATE: Generar Convocatoria y Sesión desde el Celular
                    // Formato: INSSES[num_sesion, id_convocatoria, "codigo_sesion", "orden_del_dia", "tipo_sesion", id_presidente]
                    if (parametros == null || parametros.length < 6) {
                        return "<h2>Error de Parámetros</h2><p>Faltan datos para generar la sesión y convocatoria.</p>";
                    }
                    int nSesion = Integer.parseInt(parametros[0]);
                    int idConv = Integer.parseInt(parametros[1]);
                    String codSesion = parametros[2];
                    String orden = parametros[3];
                    String tipoS = parametros[4];
                    int idPresi = Integer.parseInt(parametros[5]);

                    boolean creadoExito = dao.crearConvocatoriaYSesion(nSesion, idConv, codSesion, orden, tipoS, idPresi);
                    if (creadoExito) {
                        return "<h1>Planificación de Sesión Creada (CU2)</h1>" +
                               "<p>Se generó con éxito la Convocatoria ID " + idConv + " y la Sesión Nro <strong>" + nSesion + "</strong> en PostgreSQL.</p>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se pudo crear el circuito relacional. Verifique duplicados de llaves primarias.</p>";


                default:
                    return "<h2>Comando No Soportado en Miembros</h2>";
            }
        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>Problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }
}
