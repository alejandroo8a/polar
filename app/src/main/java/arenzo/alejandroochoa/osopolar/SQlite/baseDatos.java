package arenzo.alejandroochoa.osopolar.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;

/**
 * Created by AlejandroMissael on 23/04/2017.
 */

public class baseDatos extends SQLiteOpenHelper {

    //CONFIGURACION DE LA BASE DE DATOS
    private final static String nombreDB = "osopolar.sqlite";
    private final static String TAG = "baseDatos";
    private final static int version = 1;
    private final String VENTA = "Venta", PRODUCTO = "Producto", PRODUCTOLISTA = "ProductoLista", LISTAPRECIO = "ListaPrecio", VENTADETALLE = "VentaDetalle", CLIENTE = "Cliente";

    public baseDatos(Context context){
        super(context,nombreDB,null,version);
    }

    //TABLAS A GENERAR
    private final static String tablaProducto = "CREATE TABLE Producto(IdProducto INTEGER PRIMARY KEY, Nombre TEXT, UnidadMedida TEXT, Activo BOOLEAN)";
    private final static String tablaProductoLista = "CREATE TABLE ProductoLista(IdProducto INTEGER, IdListaPrecio INTEGER, Precio REAL, PRIMARY KEY(IdProducto, IdListaPrecio))";
    private final static String tablaListaPrecio = "CREATE TABLE ListaPrecio(IdListaPrecio INTEGER PRIMARY KEY, Descripcion TEXT)";
    private final static String tablaVenta = "CREATE TABLE Venta(IdVenta INTEGER PRIMARY KEY AUTOINCREMENT, Vendedor INTEGER, Fecha TEXT, Total REAL, Latitud TEXT, Longitud TEXT, Cancelada BOOLEAN, Sincronizado BOOLEAN)";
    private final static String tablaVentaDetalle = "CREATE TABLE VentaDetalle(IdVenta INTEGER, IdProducto INTEGER, Cantidad Integer, PUnitario REAL, Subtotal REAL, Sincronizado BOOLEAN)";
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

    //INSERTAR*****************************
    public boolean insertarVentaDetalle(ArrayList<ventaDetalle> aVentas, Context context){
        SQLiteDatabase db = getWritableDatabase();
        boolean estadoInsercion = false;
        for (ventaDetalle venta : aVentas) {
            ContentValues nuevaVentaDetalle = new ContentValues();
            nuevaVentaDetalle.put("IdVenta", venta.getIdVenta());
            nuevaVentaDetalle.put("IdProducto", venta.getIdProducto());
            nuevaVentaDetalle.put("Cantidad", venta.getCantidad());
            nuevaVentaDetalle.put("PUnitario", venta.getIdProducto());
            nuevaVentaDetalle.put("Subtotal", venta.getSubtotal());
            nuevaVentaDetalle.put("Sincronizado", venta.isSincronizado());
            try {
                db.insertOrThrow(VENTADETALLE, null, nuevaVentaDetalle);
                estadoInsercion = true;
            } catch (SQLiteException ex) {
                Toast.makeText(context, "Error al insertar detalle de venta: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return estadoInsercion;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean insertarVenta(oVenta venta, Context context){
        SQLiteDatabase db = getWritableDatabase();
        boolean estadoInsercion = false;
        ContentValues nuevaVenta = new ContentValues();
        nuevaVenta.put("Vendedor", venta.getVendedor());
        nuevaVenta.put("Fecha", obtenerFecha());
        nuevaVenta.put("Total", venta.getTotal());
        nuevaVenta.put("Latitud", venta.getLatitud());
        nuevaVenta.put("Longitud", venta.getLongitud());
        nuevaVenta.put("Cancelada", venta.getCancelada());
        nuevaVenta.put("Sincronizado", venta.getSincronizado());
        try {
            db.insertOrThrow(VENTA, null, nuevaVenta);
            estadoInsercion = true;
        }catch (SQLiteException ex){
            Toast.makeText(context, "Error al insertar venta: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return estadoInsercion;
    }


    //ACTUALIZAR*****************************


    //BORRAR*****************************


    //SELECCIONAR*****************************

    public int obtenerUltimoIdVenta(){
        SQLiteDatabase db = getWritableDatabase();
        int idVenta = 0;
        Cursor cursor = db.rawQuery("SELECT  MAX(IdVenta) FROM "+VENTA,null);
        if (cursor.moveToFirst())
            idVenta = cursor.getInt(0);
        return idVenta;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int obtenerCantidadVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        int cantidadVentas = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            cantidadVentas = cursor.getInt(0);
        }
        return cantidadVentas;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double obtenerMontoVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total) FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            montoVentas = cursor.getInt(0);
        }
        return montoVentas;
    }

    public ArrayList<cliente> obtenerClientesCercanos(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<cliente> aClientes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE, null);
        if (cursor.moveToFirst()){
            do{

            }while (cursor.moveToNext());
        }
        return aClientes;
    }

    private double obtenerDistancia(){
        double distancia = 0.0;
        return distancia;
    }

    public oVenta verTablaVentas(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTA,null);
        oVenta venta = new oVenta();
        if (cursor.moveToFirst()) {
            venta.setLatitud(cursor.getString(4));
            venta.setLongitud(cursor.getString(5));
            /*do {
                Log.d(TAG, "verTablaVentas: " + cursor.getString(6));
            } while (cursor.moveToNext());*/
        }
        return venta;
    }

    public void verTablaVentasDetalle(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTADETALLE,null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "verTablaVentasdetalle: " + cursor.getInt(0)+"-"+cursor.getInt(1));
            } while (cursor.moveToNext());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String obtenerFecha(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
