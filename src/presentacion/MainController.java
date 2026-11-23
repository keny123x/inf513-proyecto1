package presentacion;

import java.util.HashMap;
import java.util.Map;
import negocio.IComando;
import negocio.MiembroConsejoComando;
import negocio.ConvocatoriaComando;
import negocio.CorrespondenciaComando;
import mail.EmailParser;
import negocio.UsuarioComando; 
import negocio.PersonaComando;




public class MainController {

    private final Map<String, IComando> comandos;


    public MainController() {
        this.comandos = new HashMap<>();


               // CU1: Gestión de Usuarios
                // // CU1: Gestión de Usuarios
        this.comandos.put("INSUSR", new negocio.UsuarioComando());
        this.comandos.put("DELUSR", new negocio.UsuarioComando()); // 🚀 Amar
        this.comandos.put("LISUSR", new negocio.UsuarioComando());
        this.comandos.put("UPDUSR", new negocio.UsuarioComando());
        this.comandos.put("GETUSR", new negocio.UsuarioComando());

        
      // PERSONA
        this.comandos.put("INSPER", new negocio.PersonaComando());
        this.comandos.put("DELPER", new negocio.PersonaComando());
        this.comandos.put("UPDPER", new negocio.PersonaComando());
        this.comandos.put("GETPER", new negocio.PersonaComando());
        this.comandos.put("LISPER", new negocio.PersonaComando());

        
// 🚀 Amarrado al mismo ejecutor

        
        // CU2: Miembros del consejo
         this.comandos.put("INSMC", new negocio.MiembroConsejoComando());
        this.comandos.put("LISMC", new negocio.MiembroConsejoComando());;
        this.comandos.put("UPDQUO", new negocio.MiembroConsejoComando());
        
        // CU3: Convocatorias , Excusas, Habilitacion de suplentes
        this.comandos.put("GETCON", new ConvocatoriaComando());
        this.comandos.put("DELCON", new negocio.ConvocatoriaComando());
        this.comandos.put("UPDCON", new negocio.ConvocatoriaComando());
        this.comandos.put("INSCON", new negocio.ConvocatoriaComando());

        this.comandos.put("INSEXC", new negocio.ConvocatoriaComando());
        this.comandos.put("LISSUP", new negocio.ConvocatoriaComando());
   
        // CU4: Repositorio Documental y Workflow (Correspondencia)
       this.comandos.put("INSDOC", new negocio.CorrespondenciaComando());
        this.comandos.put("LISPEN", new negocio.CorrespondenciaComando());
        this.comandos.put("AGEDOC", new negocio.CorrespondenciaComando());

        // // CU5: Gestión de Asistencia Biométrica, Quórum y Ejecución de Votaciones
         this.comandos.put("INSASI", new negocio.AsistenciaExcusaComando());
           // // CU6: Gestión de Ciclo de Vida de Resoluciones, Veto y Generación de Actas
        this.comandos.put("INSRES", new negocio.ResolucionComando());
        this.comandos.put("INSACT", new negocio.ResolucionComando());
        this.comandos.put("GETQUO", new negocio.VotacionComando());
        this.comandos.put("VETRES", new negocio.ResolucionComando());

          // // CU7: Gestión de Fiscalizar, Auditar y Publicar Información (Gaceta)
        this.comandos.put("INSPUB", new negocio.ResolucionComando());

        this.comandos.put("INSVOT", new negocio.VotacionComando());
        this.comandos.put("LISVOT", new negocio.VotacionComando());
        this.comandos.put("AUDIT", new negocio.ResolucionComando());

        // // CU8: Reportes y Estadísticas
        this.comandos.put("GETREP", new negocio.ReporteComando());
        this.comandos.put("INSSES", new negocio.MiembroConsejoComando());
        this.comandos.put("ESTEST", new negocio.MiembroConsejoComando());

        // Ayuda / Catálogo General de Comandos
        this.comandos.put("HELP", new negocio.AyudaComando());
        this.comandos.put("AYUDA", new negocio.AyudaComando());



    }

    /**
     * Recibe la solicitud del email descodificada de la red
     */
        public String procesarSolicitudEmail(String asunto, String remitente) {
        // 1. Validación perimetral del protocolo de la materia
        if (asunto == null || !asunto.contains("[") || !asunto.contains("]")) {
            util.Logger.registrarError("MainController", "Formato inválido de " + remitente + ": " + asunto);
            return "<h2>Error de Formato</h2><p>El asunto no cumple con el protocolo del Consejo.</p>";
        }

        // Extraemos el comando (lo que está antes del corchete)
        String comandoStr = asunto.substring(0, asunto.indexOf("[")).trim().toUpperCase();
        
        // Usamos el parser corregido
        String[] parametros = mail.EmailParser.extraerParametros(asunto);

        // 2. Enrutamos al caso de uso correspondiente del patrón Command
        IComando comandoEjecutable = comandos.get(comandoStr);

        if (comandoEjecutable == null) {
            util.Logger.registrarError("MainController", "Comando no reconocido de " + remitente + ": " + comandoStr);
            return "<h2>Comando Inválido</h2><p>El comando '" + comandoStr + "' no está mapeado en el mapa.</p>";
        }

        // 3. Ejecutamos la lógica de negocio y registramos en logs
        String respuesta = comandoEjecutable.ejecutar(comandoStr, parametros);
        if (respuesta.contains("Error")) {
            util.Logger.registrarError(remitente, "Fallo en " + asunto + " -> " + respuesta.replaceAll("<[^>]*>", " ").trim());
        } else {
            util.Logger.registrarAcceso(remitente, asunto, "EXITOSO");
        }
        return respuesta;
    }
}
