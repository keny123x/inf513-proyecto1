package presentacion;

import mail.EmailServerService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("[🚀] ARRANQUE SEGURO - SISTEMA CONSEJO FACULTATIVO");
        System.out.println("=======================================================");

        // 🚀 TRUCO MAESTRO: Lanzamos el servicio de correo en un hilo de red independiente
        Thread hiloCorreo = new Thread(new Runnable() {
            @Override
            public void run() {
                EmailServerService emailService = new EmailServerService();
                System.out.println("[🛰️ HILO CORREO] Demonio de escucha POP3 iniciado de forma asíncrona.");
                
                while (true) {
                    try {
                        emailService.revisarBandejaEntrada();
                        Thread.sleep(15000); // Revisa cada 15 segundos de forma aislada
                    } catch (Exception e) {
                        System.out.println("[⚠️ HILO CORREO] Error en el ciclo de escucha.");
                    }
                }
            }
        });

        // Encendemos el hilo del correo para que tenga su propia vida e inmunidad
        hiloCorreo.setName("Hilo-POP3-UAGRM");
        hiloCorreo.start();

        System.out.println("[OK] Sistema base en marcha. Esperando respuesta de sockets...");
    }
}
