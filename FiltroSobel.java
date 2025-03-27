public class FiltroSobel {
    Imagen imagenIn;
    Imagen imagenOut;
    
    // Sobel Kernels para detección de bordes
    static final int[][] SOBEL_X = { 
        {-1, 0, 1}, 
        {-2, 0, 2}, 
        {-1, 0, 1} 
    };
    
    static final int[][] SOBEL_Y = { 
        {-1, -2, -1}, 
        { 0,  0,  0}, 
        { 1,  2,  1} 
    };
    
    public FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida) {
        this.imagenIn = imagenEntrada;
        this.imagenOut = imagenSalida;
    }
    
    /**
     * Aplica el filtro de Sobel a la imagen de entrada y almacena el resultado en la imagen de salida.
     */
    public void applySobel() {
        // Recorrer la imagen (evitando los bordes)
        for (int i = 1; i < imagenIn.alto - 1; i++) {
            for (int j = 1; j < imagenIn.ancho - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;
                
                // Aplicar los kernels Sobel X y Y
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        int red = imagenIn.imagen[i+ki][j+kj][0];
                        int green = imagenIn.imagen[i+ki][j+kj][1];
                        int blue = imagenIn.imagen[i+ki][j+kj][2];
                        
                        gradXRed += red * SOBEL_X[ki+1][kj+1];
                        gradXGreen += green * SOBEL_X[ki+1][kj+1];
                        gradXBlue += blue * SOBEL_X[ki+1][kj+1];
                        
                        gradYRed += red * SOBEL_Y[ki+1][kj+1];
                        gradYGreen += green * SOBEL_Y[ki+1][kj+1];
                        gradYBlue += blue * SOBEL_Y[ki+1][kj+1];
                    }
                }
                
                // Calcular la magnitud del gradiente para cada canal y limitar a 255
                int red = Math.min(Math.max((int)Math.sqrt(gradXRed * gradXRed + gradYRed * gradYRed), 0), 255);
                int green = Math.min(Math.max((int)Math.sqrt(gradXGreen * gradXGreen + gradYGreen * gradYGreen), 0), 255);
                int blue = Math.min(Math.max((int)Math.sqrt(gradXBlue * gradXBlue + gradYBlue * gradYBlue), 0), 255);
                
                // Asignar los nuevos valores en la imagen de salida
                imagenOut.imagen[i][j][0] = (byte) red;
                imagenOut.imagen[i][j][1] = (byte) green;
                imagenOut.imagen[i][j][2] = (byte) blue;
            }
        }
    }
}

