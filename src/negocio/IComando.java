package negocio;

public interface IComando {
    /**
     * Método único que implementarán todas las clases de tus Casos de Uso.
     */
    public String ejecutar(String comando, String[] parametros);
    
}
