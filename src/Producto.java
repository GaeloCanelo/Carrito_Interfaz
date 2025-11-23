/* 
===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    Clase: "Producto.java" Asignación de atributos | Constructores | Getters y Setters
    6CM4
=================================================================================== 
*/
import java.io.Serializable;

// "implements Serializable" es el permiso para viajar por la red
public class Producto implements Serializable {
    
    // Identificador único (necesario para la serialización correcta entre versiones)
    private static final long serialVersionUID = 1L;

    private int id;             // Tu ID único (1, 2, 3...)
    private String nombre;      // Ej: "Orden de Tacos al Pastor"
    private String descripcion; // Ej: "5 tacos con piña, cilantro y cebolla"
    private double precio;      // Ej: 65.00
    private int stock;          // Ej: 20 (Existencias disponibles) [cite: 14]
    private String nombreImagen;// Ej: "tacos_pastor.jpg" (Solo el nombre, no la imagen real)

    // Constructor: Nos ayuda a crear productos rápido
    public Producto(int id, String nombre, String descripcion, double precio, int stock, String nombreImagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.nombreImagen = nombreImagen;
    }

    // Getters y Setters (necesarios para acceder a los datos privada)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getNombreImagen() { return nombreImagen; }
    public void setNombreImagen(String nombreImagen) { this.nombreImagen = nombreImagen; }
    
    // Método toString para imprimir fácil en consola (útil para depurar)
    @Override
    public String toString() {
        return "ID: " + id + " | " + nombre + " | $" + precio + " | Disp: " + stock;
    }
}
