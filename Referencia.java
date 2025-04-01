public class Referencia {

    private String descriptor;
    private String accion;
    private int pagina;
    private int offset;

    public Referencia (String descriptor, int pagina, int offset, String accion) {
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

    @Override
    public String toString() {
        return descriptor + "," + pagina + "," + offset + "," + accion;
    }

}

