package negocio;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AyudaComando implements IComando {

    @Override
    public String ejecutar(String comando, String[] parametros) {
        // Sufijo único basado en hora-minuto-segundo (ej. 114235) -> Nunca se repite
        int r = Integer.parseInt(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));

        String ci = "8" + r;
        String nombre = "Ing. Evans Balcazar " + r;
        String email = "ebalcazar" + r + "@uagrm.edu.bo";
        String user = "ebalcazar" + r;

        int idMiembro = r;
        int idMiembroSup = r + 1;
        int numSesion = r;
        int idConv = r;
        int idConvExtra = r + 1;
        int idDoc = r;
        int idAsis = r;
        int idSup = r;
        int idVot = r;
        int idRes = r;
        int idPub = r;

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; padding: 15px; border: 2px solid #0d6efd; border-radius: 8px; background-color: #f8f9fa;'>");
        sb.append("<h2 style='color: #0d6efd; text-align: center; margin-top: 0;'>CATÁLOGO DE COMANDOS (INF513 - GRUPO 07SC)</h2>");


        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; font-size: 12px; background-color: #ffffff;'>");
        sb.append("<tr style='background-color: #0d6efd; color: #ffffff;'><th>Módulo</th><th>Comando</th><th>Parámetros Esperados</th><th>Ejemplo Nuevo Listo para Copiar</th></tr>");

        // CU1.A: Personas
        sb.append("<tr><td rowspan='5'><b>CU1.A: Personas</b></td><td>INSPER</td><td>Registrar: <i>[ci, nombre, celular, email]</i></td><td>INSPER[\"" + ci + "\",\"" + nombre + "\",\"76543210\",\"" + email + "\"]</td></tr>");
        sb.append("<tr><td>UPDPER</td><td>Modificar: <i>[ci, nombre, celular, email]</i></td><td>UPDPER[\"" + ci + "\",\"" + nombre + " Titular\",\"76543211\",\"" + email + "\"]</td></tr>");
        sb.append("<tr><td>DELPER</td><td>Eliminar: <i>[ci]</i></td><td>DELPER[\"" + ci + "\"]</td></tr>");
        sb.append("<tr><td>GETPER</td><td>Consultar: <i>[ci]</i></td><td>GETPER[\"" + ci + "\"]</td></tr>");
        sb.append("<tr><td>LISPER</td><td>Listar todas</td><td>LISPER[\"*\"]</td></tr>");

        // CU1.B: Usuarios
        sb.append("<tr style='background-color: #f2f6fc;'><td rowspan='5'><b>CU1.B: Usuarios</b></td><td>INSUSR</td><td>Crear: <i>[usuario, clave, rol, ci]</i></td><td>INSUSR[\"" + user + "\",\"ficct2026\",\"Delegado\",\"" + ci + "\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>UPDUSR</td><td>Modificar: <i>[id, usuario, clave, rol, ci]</i></td><td>UPDUSR[1,\"" + user + "\",\"nueva2026\",\"Secretario\",\"" + ci + "\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>DELUSR</td><td>Eliminar: <i>[id_usuario]</i></td><td>DELUSR[1]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>GETUSR</td><td>Consultar: <i>[id_usuario]</i></td><td>GETUSR[1]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>LISUSR</td><td>Listar usuarios</td><td>LISUSR[\"*\"]</td></tr>");

        // CU2: Miembros y Sesión
        sb.append("<tr><td rowspan='4'><b>CU2: Miembros</b></td><td>INSMC</td><td>Acreditar: <i>[id, cond, estam, estado, frente, fecha, faltas, mandato, ci]</i></td><td>INSMC[" + idMiembro + ",\"TITULAR\",\"DOCENTE\",\"ACTIVO\",\"Frente FICCT\",\"2026-11-23\",0,\"VIGENTE\",\"" + ci + "\"]</td></tr>");
        sb.append("<tr><td>LISMC</td><td>Listar consejeros</td><td>LISMC[\"*\"]</td></tr>");
        sb.append("<tr><td>INSSES</td><td>Crear sesión: <i>[num_ses, id_conv, cod, orden, tipo, id_presi]</i></td><td>INSSES[" + numSesion + "," + idConv + ",\"SES-ORD-" + r + "\",\"1. Quorum. 2. Tramites\",\"ORDINARIA\"," + idMiembro + "]</td></tr>");
        sb.append("<tr><td>UPDQUO</td><td>Quórum: <i>[num_sesion, estado]</i></td><td>UPDQUO[" + numSesion + ",\"HABILITADO\"]</td></tr>");

        // CU3: Convocatorias y Suplentes
        sb.append("<tr style='background-color: #f2f6fc;'><td rowspan='6'><b>CU3: Convocatorias</b></td><td>INSCON</td><td>Crear: <i>[id, codigo, fecha, hora, orden, tipo]</i></td><td>INSCON[" + idConvExtra + ",\"SES-EXT-" + r + "\",\"2026-11-23\",\"15:00:00\",\"Punto unico\",\"EXTRAORDINARIA\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>GETCON</td><td>Consultar: <i>[id_convocatoria]</i></td><td>GETCON[" + idConvExtra + "]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>UPDCON</td><td>Modificar: <i>[id, cod, fecha, hora, orden, tipo, quorum]</i></td><td>UPDCON[" + idConvExtra + ",\"SES-EXT-" + r + "B\",\"2026-11-24\",\"16:00:00\",\"Nuevo orden\",\"EXTRAORDINARIA\",\"HABILITADO\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>DELCON</td><td>Eliminar: <i>[id_convocatoria]</i></td><td>DELCON[" + idConvExtra + "]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>INSEXC</td><td>Suplente: <i>[id_asi, id_sup, id_tit, id_sup_m, sesion, motivo, nom]</i></td><td>INSEXC[" + idAsis + "," + idSup + "," + idMiembro + "," + idMiembroSup + "," + numSesion + ",\"Comision\",\"Carlos Suplente " + r + "\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>LISSUP</td><td>Listar suplentes</td><td>LISSUP[\"*\"]</td></tr>");

        // CU4: Correspondencia
        sb.append("<tr><td rowspan='3'><b>CU4: Correspondencia</b></td><td>INSDOC</td><td>Ingresar: <i>[id_doc, titulo, tipo, url, fecha, urgente]</i></td><td>INSDOC[" + idDoc + ",\"Titulacion Directa " + r + "\",\"TRAMITE\",\"/docs/doc" + idDoc + ".pdf\",\"2026-11-23\",true]</td></tr>");
        sb.append("<tr><td>LISPEN</td><td>Listar pendientes</td><td>LISPEN[\"*\"]</td></tr>");
        sb.append("<tr><td>AGEDOC</td><td>Agendar: <i>[id_documento, id_convocatoria]</i></td><td>AGEDOC[" + idDoc + "," + idConv + "]</td></tr>");

        // CU5: Quórum y Votaciones
        sb.append("<tr style='background-color: #f2f6fc;'><td rowspan='4'><b>CU5: Votaciones</b></td><td>INSASI</td><td>Asistencia: <i>[fecha_hora, excusa, motivo, sup_hab, id_miembro, sesion]</i></td><td>INSASI[\"2026-11-23 14:05:00\",false,\"Sin novedad\",false," + idMiembro + "," + numSesion + "]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>GETQUO</td><td>Auditar quórum: <i>[num_sesion]</i></td><td>GETQUO[" + numSesion + "]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>INSVOT</td><td>Votar: <i>[id_vot, id_doc, id_miembro, hora, tipo_voto]</i></td><td>INSVOT[" + idVot + "," + idDoc + "," + idMiembro + ",\"14:20:00\",\"A FAVOR\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>LISVOT</td><td>Listar escrutinio</td><td>LISVOT[\"*\"]</td></tr>");

        // CU6 y CU7: Actas, Resoluciones y Gaceta
        sb.append("<tr><td rowspan='5'><b>CU6-7: Resoluciones</b></td><td>INSACT</td><td>Acta: <i>[num_sesion, codigo, descripcion, estado]</i></td><td>INSACT[" + numSesion + ",\"ACT-" + r + "\",\"Sesion ordinaria con quorum\",1]</td></tr>");
        sb.append("<tr><td>INSRES</td><td>Resolución: <i>[id_res, contenido, num_res, num_sesion]</i></td><td>INSRES[" + idRes + ",\"Se aprueba solicitud por unanimidad\",\"RES-" + r + "/2026\"," + numSesion + "]</td></tr>");
        sb.append("<tr><td>VETRES</td><td>Vetar: <i>[id_resolucion]</i></td><td>VETRES[" + idRes + "]</td></tr>");
        sb.append("<tr><td>INSPUB</td><td>Gaceta: <i>[id_pub, num_edicion, url, num_sesion]</i></td><td>INSPUB[" + idPub + "," + r + ",\"https://uagrm.edu.bo\"," + numSesion + "]</td></tr>");
        sb.append("<tr><td>AUDIT</td><td>Auditoría legal: <i>[num_sesion]</i></td><td>AUDIT[" + numSesion + "]</td></tr>");

        // CU8 y Ayuda
        sb.append("<tr style='background-color: #f2f6fc;'><td rowspan='3'><b>CU8: Reportes</b></td><td>ESTEST</td><td>Estadísticas paridad</td><td>ESTEST[\"*\"]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>GETREP</td><td>Resumen asistencias: <i>[num_sesion]</i></td><td>GETREP[" + numSesion + "]</td></tr>");
        sb.append("<tr style='background-color: #f2f6fc;'><td>HELP</td><td>Muestra este manual</td><td>HELP[\"*\"]</td></tr>");

        sb.append("</table></div>");
        return sb.toString();
    }
}
