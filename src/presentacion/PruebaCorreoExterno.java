package presentacion;
public class PruebaCorreoExterno {

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("[SIMULACIÓN] Correo Externo Entrante - GRUPO #07");
        System.out.println("====================================================");

        // Instanciamos el controlador central del sistema
        MainController controller = new MainController();

        // 1. Simulamos los datos de un email enviado desde el exterior
        String correoRemitente = "secretaria.decanto@gmail.com"; 
        
        // El Subject con el protocolo oficial: INSUSR["nombre_usuario", "clave", "rol", "ci"]
        String asuntoCorreo = "INSUSR[\"keny_parcial_ok\",\"cf2026\",\"Delegado\",\"4715292\"]";


        System.out.println("[INFO] Remitente: " + correoRemitente);
        System.out.println("[INFO] Asunto detectado: " + asuntoCorreo);
        System.out.println("[INFO] Procesando a través de la arquitectura en capas...\n");

        // 2. Pasamos el correo al controlador (Inicia el flujo: Parser -> Comando -> DAO -> PostgreSQL)
        String respuestaHtml = controller.procesarSolicitudEmail(asuntoCorreo, correoRemitente);

        // 3. Imprimimos el resultado que el sistema le enviaría de vuelta por SMTP al correo externo
        System.out.println("====================================================");
        System.out.println("[RESPUESTA DEL SISTEMA PARA EL CORREO EXTERNO]:");
        System.out.println(respuestaHtml);
        System.out.println("====================================================");
    }
}
