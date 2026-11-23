package negocio;

import datos.CorrespondeciaDAO; 


public class CorrespondenciaComando implements IComando {
    private final CorrespondeciaDAO dao = new CorrespondeciaDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            // 🚀 MEJORA DE SEGURIDAD: Limpiamos espacios y forzamos mayúsculas para que tus 'case' entren siempre libres de errores
            String operacion = comando.trim().toUpperCase();

            switch (operacion) {
                case "INSDOC": // 🟢 CREATE: Cargar documento al Repositorio
                    // Formato enviado: INSDOC[705,"Convalidacion de Materias Keny","Estudiante Yurgel","/docs/solicitud705.pdf","2026-10-12",false]
                    if (parametros == null || parametros.length < 6) {
                        return "<h2>Error de Parámetros</h2><p>Faltan datos para ingresar el documento al repositorio.</p>";
                    }
                    int idDocumento = Integer.parseInt(parametros[0]);
                    String titulo = parametros[1];       
                    String tipo = parametros[2];         
                    String urlArchivo = parametros[3];   
                    String fechaIngreso = parametros[4]; 
                    boolean esUrgente = Boolean.parseBoolean(parametros[5]); 

                    // Llama a tu método nativo de base de datos
                    boolean insertado = dao.insertarDocumento(idDocumento, titulo, tipo, urlArchivo, fechaIngreso, esUrgente);
                    
                    if (insertado) {
                        return "<h2>Éxito de Workflow (CU4)</h2><p>El documento '" + titulo + "' ha sido ingresado al repositorio en estado [Pendiente].</p>";
                    } else {
                        return "<h2>Error de Persistencia</h2><p>No se pudo almacenar la correspondencia en PostgreSQL.</p>";
                    }

                case "LISPEN": // 🔵 READ: Consultar la cola de trámites pendientes en el Workflow
                    String tablaHtml = dao.obtenerColaPendientes();
                    return "<h2>Cola de Correspondencia Pendiente (CU4)</h2>" + tablaHtml;

                 case "AGEDOC": // 🟡 UPDATE: Agendar documento vinculándolo a una Convocatoria
                    // Formato en Subject: AGEDOC[715,1040]
                    if (parametros == null || parametros.length < 2) {
                        return "<h2>Error de Parámetros</h2><p>Se requiere el ID del documento y el ID de la convocatoria.</p>";
                    }
                    
                    int idDoc = Integer.parseInt(parametros[0]);
                    int idConv = Integer.parseInt(parametros[1]);

                    boolean agendado = dao.agendarDocumento(idDoc, idConv);
                    
                    if (agendado) {
                        return "<h1>Workflow Actualizado (CU4)</h1><p>El documento ID " + idDoc + " pasó a estado [En Debate] y fue agendado con éxito en la convocatoria " + idConv + ".</p>";
                    } else {
                        return "<h2>Error de Workflow</h2><p>No se pudo agendar el documento. Verifique los identificadores en PostgreSQL.</p>";
                    }
                
                default:
                    return "<h2>Comando No Soportado</h2><p>El comando " + comando + " no pertenece al módulo de Correspondencia.</p>";
            }
        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>Problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }
}
