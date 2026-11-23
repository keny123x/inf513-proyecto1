package presentacion;

import java.util.Properties;
import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class PruebaPOP3 {
    public static void main(String[] args) {
        System.out.println("[🛰️] Iniciando prueba de conexion directa POP3...");
        
        String host = "mail.tecnoweb.org.bo";
        String user = "grupo07sc";
        String pass = "grup007grup007*";

        Properties props = new Properties();
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", "110");
        props.put("mail.pop3.connectiontimeout", "3000"); // 3 segundos maximo
        props.put("mail.pop3.timeout", "3000");           // 3 segundos maximo

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("pop3");
            
            System.out.println("[🔒] Conectando al socket de la UAGRM...");
            store.connect(host, user, pass);
            System.out.println("[OK] ¡Autenticacion Exitosa!");

            System.out.println("[📂] Abriendo INBOX...");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY); // Solo lectura para agilizar

            int totalMensajes = inbox.getMessageCount();
            System.out.println("\n==========================================");
            System.out.println("[📥 EXITO] Mensajes reales en el servidor: " + totalMensajes);
            System.out.println("==========================================\n");

            inbox.close(false);
            store.close();
            System.out.println("[🔌] Sesion cerrada limpiamente.");

        } catch (Exception e) {
            System.err.println("[X] Error de conexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
