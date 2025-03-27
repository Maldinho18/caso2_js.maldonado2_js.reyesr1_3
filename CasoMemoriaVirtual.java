import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class CasoMemoriaVirtual {

    static final long TiempoHits = 50;
    static final long TiempoFallo = 10;
    static List<Referencia> referenciasSimuladas;
    static MemoriaVirtual memoriaVirtual;
    static AtomicBoolean simulacionTerminada = new AtomicBoolean(false);

    public static void main(String[] args) {
        Scanner scanner = new Scanner (System.in);
        System.out.println("Menu del caso 2 ");
        System.out.println("1. Generar archivo de referencias ");
        System.out.println("2. Simular administracion de memoria y calcular resultados ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        if (opcion == 1) {
            System.out.println("Ingrese el tamaño de la pagina (bytes): ");
            int tamanoPagina = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Ingrese el nombre del archivo imagen: ");
            String nombreArchivo = scanner.nextLine();
            generarArchivoReferencias(nombreArchivo, tamanoPagina);
        } else if (opcion == 2) {
            System.out.println("Ingrese el número de marcos de página: ");
            int numeroMarcos = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Ingrese el nombre del archivo de referencias: ");
            String nombreArchivo = scanner.nextLine();
            simularPagina(nombreArchivo, numeroMarcos);
        } else {
            System.out.println("Opción no válida");
        }
    }

    public static void generarArchivoReferencias(String nombreArchivo, int tamanoPagina) {
        try {
            Imagen img = new Imagen(nombreArchivo);
            int NF = img.alto;
            int NC = img.ancho;
            int tamanoImagen = NF * NC * 3;
            int tamanoRTA = NF * NC * 3;
            int tamanoFiltro = 3 * 3 * 4;
            int tamanoFiltroX = tamanoFiltro;
            int tamanoFiltroY = tamanoFiltro;
            int totalBytes = tamanoImagen + tamanoFiltroX + tamanoFiltroY + tamanoRTA;
            int NP = (int) Math.ceil((double) totalBytes / tamanoPagina);
            List<Referencia> listaRef = new ArrayList<>();

            for (int i = 1; i < NF - 1; i++){
                for (int j = 1; j < NC - 1; j++){
                    for (int di = -1; di <= 1; di++){
                        for (int dj = -1; dj <= 1; dj++){
                            int fila = i +di;
                            int columna = j + dj;

                            listaRef.add(crearReferencia("Imagen", fila, columna, "r", 0, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY));
                            listaRef.add(crearReferencia("Imagen", fila, columna, "g", 0, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY));
                            listaRef.add(crearReferencia("Imagen", fila, columna, "b", 0, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY));

                            int fx = di + 1;
                            int fy = dj + 1;
                            for (int k = 0; k < 3; k++){
                                listaRef.add(crearReferencia("SOBEL_X", fx, fy, "", tamanoImagen, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY));
                            }
                            for (int k = 0; k < 3; k++){
                                listaRef.add(crearReferencia("SOBEL_Y", fx, fy, "", tamanoImagen + tamanoFiltroX, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY));
                            }
                        }
                    }

                    int filaRTA = i;
                    int colRTA = j;
                    listaRef.add(crearReferencia("RTA", filaRTA, colRTA, "r",  tamanoImagen + tamanoFiltroX + tamanoFiltroY, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY, true));
                    listaRef.add(crearReferencia("RTA", filaRTA, colRTA, "g",  tamanoImagen + tamanoFiltroX + tamanoFiltroY, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY, true));
                    listaRef.add(crearReferencia("RTA", filaRTA, colRTA, "b",  tamanoImagen + tamanoFiltroX + tamanoFiltroY, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY, true));
                }
            }

            int NR = listaRef.size();

            String outputFile = "referencias.txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))){
                bw.write("TP=" + tamanoPagina);
                bw.newLine();
                bw.write("NF=" + NF);
                bw.newLine();
                bw.write("NC=" + NC);
                bw.newLine();
                bw.write("NP=" + NP);
                bw.newLine();
                bw.write("NR=" + NR);
                bw.newLine();
                for (Referencia ref : listaRef){
                    bw.write(ref.toString());
                    bw.newLine();
                }
            }
            System.out.println("Archivo de referencias generado: " + outputFile);
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static Referencia crearReferencia(String nombreMatriz, int fila, int col, String componente, int baseOffset, int tamanoPagina, int NF, int NC, int tamanoImagen, int tamanoFiltroX, int tamanoFiltroY){
        return crearReferencia(nombreMatriz, fila, col, componente, baseOffset, tamanoPagina, NF, NC, tamanoImagen, tamanoFiltroX, tamanoFiltroY, false);
    }

    public static Referencia crearReferencia(String nombreMatriz, int fila, int col, String componente, int baseOffset, int tamanoPagina, int NF, int NC, int tamanoImagen, int tamanoFiltroX, int tamanoFiltroY, boolean esE) {
        int tamanoElemento = (nombreMatriz.equals("Imagen") || nombreMatriz.equals("Rta")) ? 1 : 4;
        int indexLocal = 0;
        if (nombreMatriz.equals("Imagen") || nombreMatriz.equals("Rta")) {
            int canal = 0;
            if (componente.equalsIgnoreCase("r")) canal = 0;
            else if (componente.equalsIgnoreCase("g")) canal = 1;
            else if (componente.equalsIgnoreCase("b")) canal = 2;
            indexLocal = (fila * NC + col) * 3 + canal;
        } else if (nombreMatriz.equals("SOBEL_X") || nombreMatriz.equals("SOBEL_Y")){
            indexLocal = fila * 3 + col;
        }

        int globalDireccion = baseOffset + indexLocal * tamanoElemento;
        int pagina = globalDireccion / tamanoPagina;
        int offset = globalDireccion % tamanoPagina;
        String accion = esE ? "W" : "R";
        String descriptor = (componente.isEmpty()) ? nombreMatriz + "[" + fila + "][" + col + "]" : nombreMatriz + "[" + fila + "][" + col + "]." + componente;

        return new Referencia(descriptor, pagina, offset, accion);
    }

    public static void simularPagina(String nombreArchivo, int numeroMarcos){
        referenciasSimuladas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))){
            String linea;
            int tamanoPagina = 0;
            while ((linea = br.readLine()) != null){
                if (linea.startsWith("TP=")){
                    tamanoPagina = Integer.parseInt(linea.split("=")[1]);
                    continue;
                }
                if (linea.startsWith("NF=") || linea.startsWith("NC=") || linea.startsWith("NP=") || linea.startsWith("NR=")){
                    continue;
                }
                String[] partes = linea.split(",");
                if (partes.length == 4){
                    String descriptor = partes[0];
                    int pagina = Integer.parseInt(partes[1]);
                    int offset = Integer.parseInt(partes[2]);
                    String accion = partes[3];
                    referenciasSimuladas.add(new Referencia(descriptor, pagina, offset, accion));
                }
            }
            memoriaVirtual = new MemoriaVirtual(tamanoPagina, numeroMarcos);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        Thread lector = new Thread(new LectorReferencia(memoriaVirtual, referenciasSimuladas));
        Thread actualizador = new Thread(new ActualizadorEstado(memoriaVirtual, simulacionTerminada));
        lector.start();
        actualizador.start();

        try {
            lector.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulacionTerminada.set(true);
        try {
            actualizador.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        memoriaVirtual.imprimirResultados();
        long tiempoTotal = memoriaVirtual.getHits() * TiempoHits + memoriaVirtual.getMisses() * (TiempoFallo * 1_000_000);
        System.out.println("Tiempo total: " + tiempoTotal + " ns");
    }

    public static class LectorReferencia implements Runnable {

        private MemoriaVirtual memoria;
        private List<Referencia> referenciasSimuladas;

        public LectorReferencia(MemoriaVirtual memoria, List<Referencia> referenciasSimuladas) {
            this.memoria = memoria;
            this.referenciasSimuladas = referenciasSimuladas;
        }

        @Override
        public void run() {
            int contador = 0;

            for (Referencia referencia : referenciasSimuladas) {
                memoria.accederPagina(referencia.getPagina()); 
                contador++;

                if (contador % 10000 == 0) {
                    try {
                        Thread.sleep(1); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class ActualizadorEstado implements Runnable{
        private MemoriaVirtual memoriaVirtual;
        private AtomicBoolean simulacionTerminada;

        public ActualizadorEstado(MemoriaVirtual memoriaVirtual, AtomicBoolean simulacionTerminada){
            this.memoriaVirtual = memoriaVirtual;
            this.simulacionTerminada = simulacionTerminada;
        }

        public void run(){
            while(!simulacionTerminada.get()){
                memoriaVirtual.reiniciarBits();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}