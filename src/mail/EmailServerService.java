package mail;

import java.util.Properties;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import presentacion.MainController;

public class EmailServerService {

    private static final String EMAIL_USER = "grupo07sc";             
    private static final String EMAIL_PASS = "grup007grup007*";        
    private static final String HOST_SERVER = "mail.tecnoweb.org.bo";

    private final MainController controller = new MainController();

    public void revisarBandejaEntrada() {
        Properties props = new Properties();
        props.put("mail.pop3.host", HOST_SERVER);
        props.put("mail.pop3.port", "110");
        props.put("mail.pop3.connectiontimeout", "3000");
        props.put("mail.pop3.timeout", "3000");
        props.put("mail.pop3.rstrip", "true");
        props.put("mail.pop3.disabletop", "false");
        props.put("mail.pop3.forgettopheaders", "true");
        props.put("mail.pop3.pipelining", "true");

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("pop3");
            store.connect(HOST_SERVER, EMAIL_USER, EMAIL_PASS);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE); 

            Message[] mensajes = inbox.getMessages();
            System.out.println("[POP3 FACULTAD] Conexión exitosa. Mensajes en cola: " + mensajes.length);

            for (Message mensaje : mensajes) {
                String de = ((InternetAddress) mensaje.getFrom()[0]).getAddress();
                String asunto = EmailParser.decodificarAsunto(mensaje.getSubject());

                // Evita bucles infinitos ignorando respuestas propias o correos vacíos del sistema
                if (asunto != null && !asunto.isEmpty() && !asunto.toUpperCase().startsWith("RE:") && !asunto.toUpperCase().startsWith("UNDELIVERED")) {
                    System.out.println("[UAGRM POP3] Procesando solicitud: " + asunto);
                    
                    // El controlador valida sintaxis, comando y ejecuta o devuelve HTML de Error
                    String respuestaHtml = controller.procesarSolicitudEmail(asunto, de);
                    enviarRespuesta(de, asunto, respuestaHtml);
                }
                
                // Marcamos como leído/eliminado para no reprocesar en el siguiente ciclo
                mensaje.setFlag(jakarta.mail.Flags.Flag.DELETED, true);
            }

            inbox.close(true); 
            store.close();
        } catch (Exception e) {
            System.out.println("[INFO] Vigilando cola POP3 en mail.tecnoweb.org.bo:110...");
            util.Logger.registrarError("EmailServerService", e.getMessage());
        }
    }

     public void enviarRespuesta(String destinatario, String asunto, String cuerpoHtml) {
     System.out.println("[SMTP NATIVO] Despachando respuesta por mail.tecnoweb.org.bo...");

     Properties props = new Properties();
     props.put("mail.smtp.host", HOST_SERVER);
     props.put("mail.smtp.port", "25");
     props.put("mail.smtp.auth", "false");
     props.put("mail.smtp.connectiontimeout", "5000");
     props.put("mail.smtp.timeout", "5000");

     Session session = Session.getInstance(props, null);

     try {
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress("grupo07sc@tecnoweb.org.bo", "UAGRM - GRUPO #07"));
         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario.trim()));
         message.setSubject("RE: " + asunto);
         message.setContent(cuerpoHtml, "text/html; charset=utf-8");

         Transport.send(message);
         System.out.println("[SMTP ÉXITO] Respuesta enviada a: " + destinatario);

     } catch (Exception e) {
         System.err.println("[SMTP CRÍTICO] Error de envío: " + e.getMessage());
         util.Logger.registrarError("SMTP", "Error enviando a " + destinatario + ": " + e.getMessage());
     }
 }
}
