/* 
===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    Clase "GeneradorCatalogo.java" para crear la Base de Datos de Productos
    6CM4
=================================================================================== 
*/
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GeneradorCatalogo {
    
    public static void main(String[] args) {
        // 1. Creamos una lista vacía para meter los productos
        ArrayList<Producto> catalogo = new ArrayList<>();

        // 2. Llenamos la lista con productos (ID, Nombre, Descrip, Precio, Stock, Imagen)
        
        catalogo.add(new Producto(1, "Churrumais", "Frituras de maíz con chile y limón", 15.00, 50, "churrumais.png"));
        catalogo.add(new Producto(2, "Chachitos", "Cereal de trigo inflado sabor vainilla", 22.50, 30, "chachitos.png"));
        catalogo.add(new Producto(3, "Charritos", "Frituras de harina de trigo", 12.00, 40, "charritos.png"));
        catalogo.add(new Producto(4, "Papas Sabritas", "Papas fritas sabor adobadas", 18.00, 25, "sabritas_adobadas.png"));
        catalogo.add(new Producto(5, "Totis Donitas", "Donitas de harina con sal y limón", 10.00, 60, "totis.png"));

        // 3. Guardamos esa lista en un archivo físico
        try {
            // El archivo se llamará "productos.dat"
            FileOutputStream archivoSalida = new FileOutputStream("productos.dat");
            ObjectOutputStream objetoSalida = new ObjectOutputStream(archivoSalida);
            
            // Se escribe la lista completa al archivo
            objetoSalida.writeObject(catalogo);
            
            objetoSalida.close();
            archivoSalida.close();
            
            System.out.println("¡Éxito! El archivo 'productos.dat' se creó correctamente.");
            System.out.println("Se guardaron " + catalogo.size() + " productos.");
            
        } catch (Exception e) {
            System.out.println("Error al guardar el catálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}