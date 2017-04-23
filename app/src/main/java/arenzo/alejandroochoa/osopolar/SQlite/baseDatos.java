package arenzo.alejandroochoa.osopolar.SQlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AlejandroMissael on 23/04/2017.
 */

public class baseDatos extends SQLiteOpenHelper {

    //CONFIGURACION DE LA BASE DE DATOS
    private final static String nombreDB = "osopolar.sqlite";
    private final static String TAG = "baseDatos";
    private final static int version = 1;

    public baseDatos(Context context){
        super(context,nombreDB,null,version);
    }

    //TABLAS A GENERAR
    private final static String tablaProducto = "CREATE TABLE Producto(IdProducto INTEGER PRIMARY KEY, Nombre TEXT, UnidadMedida TEXT, Activo BOOLEAN)";
    private final static String tablaProductoLista = "CREATE TABLE ProductoLista(IdProducto INTEGER, IdListaPrecio INTEGER, Precio REAL, PRIMARY KEY(IdProducto, IdListaPrecio))";
    private final static String tablaListaPrecio = "CREATE TABLE ListaPrecio(IdListaPrecio INTEGER PRIMARY KEY, Descripcion TEXT)";
    private final static String tablaVenta = "CREATE TABLE Venta(IdVenta INTEGER AUTOINCREMENT, Vendedor INTEGER, Fecha TEXT, Total REAL, Latitud TEXT, Longitud TEXT, Cancelada BOOLEAN)";
    private final static String tablaVentaDetalle = "CREATE TABLE VentaDetalle(IdVenta INTEGER, IdProducto INTEGER, Cantidad Integer, PUnitario REAL, Subtotal REAL, PRIMARY KEY(IdVenta, IdProducto))";
    private final static String tablaCliente = "CREATE TABLE Cliente(IdCliente INTEGER, IdListaPrecio INTEGER, Nombre TEXT, Calle TEXT, Numero TEXT, Latitud TEXT, Longitud TEXT, Sincronizado BOOLEAN, PRIMARY KEY(IdCliente, IdListaPrecio))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tablaProducto);
        db.execSQL(tablaProductoLista);
        db.execSQL(tablaListaPrecio);
        db.execSQL(tablaVenta);
        db.execSQL(tablaVentaDetalle);
        db.execSQL(tablaCliente);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Producto");
        db.execSQL("DROP TABLE IF EXISTS ProductoLista");
        db.execSQL("DROP TABLE IF EXISTS ListaPrecio");
        db.execSQL("DROP TABLE IF EXISTS Venta");
        db.execSQL("DROP TABLE IF EXISTS VentaDetalle");
        db.execSQL("DROP TABLE IF EXISTS Cliente");
        db.execSQL(tablaProducto);
        db.execSQL(tablaProductoLista);
        db.execSQL(tablaListaPrecio);
        db.execSQL(tablaVenta);
        db.execSQL(tablaVentaDetalle);
        db.execSQL(tablaCliente);
    }
}
