import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemoriaVirtual {
    
    private int tamanoPagina;
    private int marcosPagina;
    private Map<Integer, Boolean> bitReferencia;
    private LinkedList<Integer> listaPaginas;
    private int misses;
    private int hits;

    public MemoriaVirtual(int tamanoPagina, int marcosPagina) {
        this.tamanoPagina = tamanoPagina;
        this.marcosPagina = marcosPagina;
        this.bitReferencia = Collections.synchronizedMap(new HashMap<>());
        this.listaPaginas = new LinkedList<>();
        this.misses = 0;
        this.hits = 0;
    }

    public synchronized void accederPagina(int pagina){
        
        if(listaPaginas.contains(pagina)){
            hits++;
            bitReferencia.put(pagina, true);
        } else {
            misses++;
            if (listaPaginas.size() == marcosPagina){
                reemplazarPagina();
            }
            listaPaginas.add(pagina);
            bitReferencia.put(pagina, true);
        }
    }

    public synchronized void reemplazarPagina(){

        Integer paginaEliminar = null;

        for(Integer pagina: listaPaginas){
            if(!bitReferencia.get(pagina)){
                paginaEliminar = pagina;
                break;
            }
        }

        if(paginaEliminar == null){
            for(Integer pagina: listaPaginas){
                bitReferencia.put(pagina, false);
            }
            paginaEliminar = listaPaginas.getFirst();
        }
        listaPaginas.remove(paginaEliminar);
        bitReferencia.remove(paginaEliminar);
        /* 
        Iterator<Integer> iterador = listaPaginas.iterator();

        while (iterador.hasNext()){
            int pagina = iterador.next();
            if (!bitReferencia.get(pagina)){
                iterador.remove();
                bitReferencia.remove(pagina);
                return;
            } else {
                bitReferencia.put(pagina, false);
            }
        }
        int paginaAEliminar = listaPaginas.removeFirst();
        bitReferencia.remove(paginaAEliminar);
        */
    }
        

    public synchronized void reiniciarBits(){
        for (Integer pagina : bitReferencia.keySet()){
            bitReferencia.put(pagina, false);
        }
    }

    public int getTamanoPagina(){
        return tamanoPagina;
    }

    public synchronized int getMisses(){
        return misses;
    }

    public synchronized int getHits(){
        return hits;
    }

    public void imprimirResultados(){
        int total = misses + hits;
        if(total == 0){
            System.out.println("No se han realizado referencias a memoria");
            return;
        }
        double porcentajeMisses = (double)misses / total * 100;
        double porcentajeHits = (double)hits / total * 100;
        System.out.println("Total referencias a memoria: " + total);
        System.out.println("Misses: " + misses + " (" + String.format("%.2f", porcentajeMisses) + "%)");
        System.out.println("Hits: " + hits + " (" + String.format("%.2f", porcentajeHits) + "%)");
    }
}