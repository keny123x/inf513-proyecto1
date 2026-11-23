package negocio;

import datos.VotacionDAO;

public class VotacionComando implements IComando {
    private final VotacionDAO dao = new VotacionDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            switch (comando.trim().toUpperCase()) {
                case "INSVOT": // 🟢 CREATE: INSVOT[id_votacion, id_documento, id_miembro, "hora_voto", "tipo_voto"]
                    if (parametros == null || parametros.length < 5) {
                        return "<h2>Error de Parámetros</h2><p>Se requieren los 5 atributos oficiales de tu diagrama para registrar la votación.</p>";
                    }
                    
                    int idVotacion = Integer.parseInt(parametros[0]);
                    int idDocumento = Integer.parseInt(parametros[1]);
                    int idMiembro = Integer.parseInt(parametros[2]);
                    String horaVoto = parametros[3]; // "14:45:00"
                    String tipoVoto = parametros[4]; // "A FAVOR", "EN CONTRA", "ABSTENCION"

                    boolean ok = dao.insertarVotacion(idVotacion, idDocumento, idMiembro, horaVoto, tipoVoto);
                    if (ok) {
                        return "<h1>Voto Emitido de Forma Oficial (CU5)</h1><p>La intención del consejero paritario con ID " + idMiembro + " fue guardada con éxito en PostgreSQL.</p>";
                    }
                    return "<h2>Error de Persistencia</h2><p>No se pudo almacenar la votación. Verifique duplicidad de ID o que existan el Miembro y Documento.</p>";

                case "LISVOT": // 🔵 READ GLOBAL (Formato asterisco: LISVOT["*"])
                    return "<h1>Registro Oficial de Escrutinios Emitidos por el Consejo (CU5)</h1>" + dao.obtenerListaVotaciones();

                                // Coloca este caso dentro del switch de tu archivo VotacionComando.java:
                case "GETQUO": // ⚖️ AUDITORÍA DE QUÓRUM PARITARIO (CU5)
                    // Formato enviado: GETQUO[1040]
                    if (parametros == null || parametros.length < 1) {
                        return "<h2>Error de Parámetros</h2><p>Falta el número de sesión para validar el quórum.</p>";
                    }
                    int nSesionQuorum = Integer.parseInt(parametros[0]);
                    return dao.verificarQuorumSesionCU5(nSesionQuorum);


                default:
                    return "<h2>Comando No Soportado en Votaciones</h2>";
            }
        } catch (Exception e) {
            return "<h2>Error Crítico (CU5)</h2><p>Problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }

    
}
