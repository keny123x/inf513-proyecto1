package negocio;

import datos.ReporteDAO;

public class ReporteComando implements IComando {
    private final ReporteDAO dao = new ReporteDAO();

    @Override
    public String ejecutar(String comando, String[] parametros) {
        try {
            if (comando.equals("GETREP")) {
                // Formato: GETREP[num_sesion]
                if (parametros == null || parametros.length < 1) {
                    return "<h2>Error de Parámetros</h2><p>Especifique el número de sesión para el reporte.</p>";
                }
                int numSesion = Integer.parseInt(parametros[0]);
                String estadisticasHtml = dao.obtenerResumenAsistencias(numSesion);
                
                return "<h1>Reporte Estadístico de Asistencias (CU8)</h1>" + 
                       "<p><strong>Resumen consolidado para la Sesión Nro: " + numSesion + "</strong></p>" +
                       estadisticasHtml + 
                       "<br><p>Auditoría de quórum paritario generada automáticamente.</p>";
            }
            return "<h2>Comando No Soportado</h2><p>Operación no válida en el módulo de Reportes.</p>";
        } catch (Exception e) {
            return "<h2>Error Crítico</h2><p>Problema en la capa de negocio: " + e.getMessage() + "</p>";
        }
    }
}
