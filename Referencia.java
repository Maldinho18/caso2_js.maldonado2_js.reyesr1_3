public class Referencia {

    private String descriptor;
    private String accion;
    private int pagina;
    private int offset;

    public Referencia (String descriptor, String accion, int pagina, int offset) {
        this.descriptor = descriptor;
        this.accion = accion;
        this.pagina = pagina;
        this.offset = offset;
    }

    public int getPagina() {
        return pagina;
    }
    public int getOffset() {
        return offset;
    }
    public String getDescriptor() {
        return descriptor;
    }
    public String getAccion() {
        return accion;
    }

}