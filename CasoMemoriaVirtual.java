import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

public class CasoMemoriaVirtual {
    static final long TiempoHits = 50;
    static final long TimepoFallo = 10;
    static List<Referencia> referenciasSimuladas;
    static MemoriaVirtual momoriaVirtual;
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
            BufferedImage img = ImageIO.read(new File(nombreArchivo));
            if (img == null) {
                System.out.println("No se pudo leer la imagen");
                return;
            }
            int NF = img.getHeight();
            int NC = img.getWidth();

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

    public static Referencia crearReferencia(String nombreMatriz, int fila, int col, String componente, int baseOffset, int tamanoPagina, int NF, int NC, int tamanoImagen, int tamanoFiltroX, int tamanoFiltroY, boolean esRTA){
        //CODIGO y arreglar return 
        return new Referencia(nombreMatriz, componente, tamanoPagina, baseOffset);
    }

    public static void simularPagina(String nombreArchivo, int numeroMarcos){
        //CODIGO
    }
}