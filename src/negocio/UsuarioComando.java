package negocio;

import datos.UsuarioDAO;

public class UsuarioComando implements IComando {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

     private final datos.UsuarioDAO dao = new datos.UsuarioDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            switch (comando.trim().toUpperCase()) {
                case "DELUSR":
                    if (parametros == null || parametros.length < 1) return "<h2>Error</h2>";
                    return usuarioDAO.eliminarUsuario(Integer.parseInt(parametros[0])) 
                        ? "<h1>Usuario Eliminado (CU1)</h1>" : "<h2>Error</h2>";

                case "GETUSR":
                    if (parametros == null || parametros.length < 1) return "<h2>Error</h2>";
                    return "<h1>Consulta de Usuario</h1>" + usuarioDAO.consultarUsuario(Integer.parseInt(parametros[0]));

                case "LISUSR":
                    return "<h1>Reporte General de Usuarios (CU1)</h1>" + usuarioDAO.obtenerListaUsuarios();

                case "UPDUSR": // UPDATE USUARIO
    if (parametros == null || parametros.length < 5) { // 🚀 Validamos que vengan los 5 datos
        return "<h2>Error de Parámetros</h2><p>Se requieren 5 datos: id, usuario, clave, rol y ci.</p>";
    }
    
    int idUpd = Integer.parseInt(parametros[0]);
    String userUpd = parametros[1];
    String passUpd = parametros[2];
    String rolUpd = parametros[3];
    String ciUpd = parametros[4]; // 🚀 Capturamos el carnet enviado para la actualización
    
    // 🚀 Pasamos los 5 argumentos correspondientes al método
    return usuarioDAO.modificarUsuario(idUpd, userUpd, passUpd, rolUpd, ciUpd)
        ? "<h1>Usuario Actualizado (CU1)</h1>" 
        : "<h2>Error de Actualización</h2>";

                    case "INSUSR": // 🔐 CREATE USUARIO (CU1)
                    if (parametros == null || parametros.length < 4) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren 4 datos: usuario, clave, rol y ci.</p>";
                    }
                    String user = parametros[0];
                    String pass = parametros[1];
                    String rol = parametros[2];
                    String ciAsociado = parametros[3]; // 🚀 Capturamos el carnet del miembro

                    boolean exito = dao.crearUsuario(user, pass, rol, ciAsociado);
                    if (exito) {
                        return "<h1>Usuario Creado</h1><p>El perfil '" + user + "' fue enlazado al miembro con C.I. " + ciAsociado + ".</p>";
                    }
                    return "<h2>Error de Persistencia</h2>";


                default:
                    return "<h2>Comando No Soportado</h2>";
            }
        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>" + e.getMessage() + "</p>";
        }
    }
}
