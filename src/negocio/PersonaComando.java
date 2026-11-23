package negocio;

import datos.PersonaDAO;

public class PersonaComando implements IComando {
    private final PersonaDAO personaDAO = new PersonaDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            String operacion = comando.trim().toUpperCase();

            // 1. DELETE: DELPER["ci"]
            if (operacion.equals("DELPER")) {
                if (parametros == null || parametros.length < 1) {
                    return "<h2>Error de Parámetros</h2><p>Falta el C.I. de la persona para poder eliminarla.</p>";
                }
                String ci = parametros[0];
                boolean eliminado = personaDAO.eliminarPersona(ci);
                
                if (eliminado) {
                    return "<h1>Persona Eliminada</h1><p>La persona con C.I. " + ci + " fue borrada con éxito de PostgreSQL.</p>";
                } else {
                    return "<h1>Error</h1><p>No se encontró ninguna persona con el C.I. " + ci + " en la universidad.</p>";
                }
            }

            // 2. READ INDIVIDUAL: GETPER["ci"]
            if (operacion.equals("GETPER")) {
                if (parametros == null || parametros.length < 1) {
                    return "<h2>Error de Parámetros</h2><p>Falta el C.I. de la persona.</p>";
                }
                String ci = parametros[0];
                String tablaHtml = personaDAO.consultarPersona(ci);
                return "<h1>Ficha de Consulta Institucional (CU2)</h1>" + tablaHtml;
            }

            // 3. UPDATE: UPDPER["ci", "nombre", "celular", "email"]
            if (operacion.equals("UPDPER")) {
                if (parametros == null || parametros.length < 4) {
                    return "<h2>Error de Parámetros</h2><p>Parámetros insuficientes para la actualización. Se requieren 4 datos: ci, nombre, celular, email.</p>";
                }
                String ci = parametros[0];
                String nombre = parametros[1];
                String celular = parametros[2];
                String email = parametros[3];

                boolean ok = personaDAO.modificarPersona(ci, nombre, celular, email);
                return ok ? "<h1>Persona Actualizada</h1><p>Los datos de '" + nombre + "' se modificaron correctamente.</p>" 
                          : "<h2>Error de Actualización</h2><p>No se pudo actualizar el registro en PostgreSQL.</p>";
            }

            // 4. READ GLOBAL: LISPER["*"]
            if (operacion.equals("LISPER")) {
                String reportePersonasHtml = personaDAO.obtenerListaPersonas();
                return "<h1>Reporte General de Personas Registradas (UAGRM)</h1>" + reportePersonasHtml;
            }

            // 5. CREATE: INSPER["ci", "nombre", "celular", "email"]
            if (operacion.equals("INSPER")) {
                if (parametros == null || parametros.length < 4) {
                    return "<h2>Error de Parámetros</h2><p>Parámetros insuficientes para registrar la persona. Se requieren 4 datos: ci, nombre, celular, email.</p>";
                }

                String ci = parametros[0];
                String nombre = parametros[1];
                String celular = parametros[2];
                String email = parametros[3];

                boolean exito = personaDAO.registrarPersona(ci, nombre, celular, email);
                
                if (exito) {
                    return "<h1>Persona Registrada</h1><p>La persona '" + nombre + "' con C.I. " + ci + " ha sido guardada con éxito en la base de datos.</p>";
                } else {
                    return "<h1>Error de Base de Datos</h1><p>No se pudo guardar la persona. Verifique si el C.I. ya existe en la universidad.</p>";
                }
            }

            return "<h2>Comando Desconocido</h2><p>La operación '" + operacion + "' no pertenece al módulo de Gestión de Personas.</p>";

        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>Ocurrió un problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }
}
