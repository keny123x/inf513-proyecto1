package negocio;

import datos.AsistenciaExcusaDAO;

public class AsistenciaExcusaComando implements IComando {
    private final AsistenciaExcusaDAO dao = new AsistenciaExcusaDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            if (comando.trim().toUpperCase().equals("INSASI")) {
                // Formato en Subject: INSASI["2026-10-15 14:30:00",false,"Ninguno",false,25,1040]
                if (parametros == null || parametros.length < 6) {
                    return "<h2>Error de Parámetros</h2><p>Faltan datos oficiales para asentar el control biométrico o excusa.</p>";
                }

                // 🚀 CORRECCIÓN DE NOMBRES: Cambiamos las variables internas para que no choquen con el arreglo 'parametros'
                String marca = parametros[0];
                boolean presentoExcusa = Boolean.parseBoolean(parametros[1]);
                String motivo = parametros[2];
                boolean suplenteHabilitado = Boolean.parseBoolean(parametros[3]);
                int idMiembro = Integer.parseInt(parametros[4]);
                int numSesion = Integer.parseInt(parametros[5]);

                boolean ok = dao.registrarAsistenciaExcusa(marca, presentoExcusa, motivo, suplenteHabilitado, idMiembro, numSesion);
                if (ok) {
                    return "<h1>Control Asistencia/Excusa Registrado (CU5)</h1><p>El registro biométrico se guardó con éxito en PostgreSQL bajo el estándar de tu gráfico.</p>";
                }
                return "<h2>Error de Persistencia</h2><p>No se pudo almacenar en la universidad. Verifique que existan el Miembro y la Sesión.</p>";
            }
            return "<h2>Comando No Soportado</h2>";
        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>" + e.getMessage() + "</p>";
        }
    }
}
