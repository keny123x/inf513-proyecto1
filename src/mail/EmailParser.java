package mail;

import jakarta.mail.internet.MimeUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailParser {

    public static String decodificarAsunto(String asuntoCrudo) {
        if (asuntoCrudo == null) {
            return "";
        }
        try {
            return MimeUtility.decodeText(asuntoCrudo).trim();
        } catch (Exception e) {
            return asuntoCrudo.trim();
        }
    }

    public static String[] extraerParametros(String asunto) {
        try {
            if (asunto == null || !asunto.contains("[") || !asunto.contains("]")) {
                return new String[0];
            }

            int inicio = asunto.indexOf("[");
            int fin = asunto.lastIndexOf("]");
            if (fin <= inicio) {
                return new String[0];
            }

            String interior = asunto.substring(inicio + 1, fin).trim();
            if (interior.isEmpty()) {
                return new String[0];
            }

            List<String> lista = new ArrayList<>();
            // Separa cada argumento por coma ignorando comas dentro de comillas
            Pattern regex = Pattern.compile("(?:[^,\"']+|\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*')+");
            Matcher matcher = regex.matcher(interior);

            while (matcher.find()) {
                String token = matcher.group().trim();
                // Quita comillas dobles o simples que envuelven el valor
                if ((token.startsWith("\"") && token.endsWith("\"")) || (token.startsWith("'") && token.endsWith("'"))) {
                    token = token.substring(1, token.length() - 1);
                }
                // Desescapa comillas internas
                token = token.replace("\\\"", "\"").replace("\\'", "'").trim();
                lista.add(token);
            }

            String[] resultado = lista.toArray(new String[0]);
            System.out.println("[PARSER DEBUG] Datos procesados: " + java.util.Arrays.toString(resultado));
            return resultado;

        } catch (Exception e) {
            util.Logger.registrarError("EmailParser", "Fallo al parsear asunto: " + asunto + " -> " + e.getMessage());
            return new String[0];
        }
    }
}
