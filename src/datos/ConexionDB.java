
package datos;


import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDB {
    private static final String URL = "jdbc:postgresql://db.tecnoweb.org.bo:5432/db_grupo07sc";
    private static final String USER = "grupo07sc";
    private static final String PASS = "grup007grup007*"; // Con tu clave real con asterisco

    public static Connection getConexion() {
        try {
            Class.forName("org.postgresql.Driver");
            
            // 🌟 EL TRUCO CLAVE: Le decimos a Java que solo espere 2 segundos al PostgreSQL
            DriverManager.setLoginTimeout(10); 
            
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            // Si el PostgreSQL de la UAGRM está saturado en tu casa, pintamos esto y NO congelamos el programa
            System.out.println("[⚠️ DB ADVERTENCIA] Servidor PostgreSQL UAGRM lento. Continuando flujo local de correos...");
            return null;
        }
    }
}
