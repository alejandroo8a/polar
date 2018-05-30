package arenzo.alejandroochoa.osopolar.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.listaPrecio;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.productoLista;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;

/**
 * Created by AlejandroMissael on 23/04/2017.
 */

public class baseDatos extends SQLiteOpenHelper {

    //CONFIGURACION DE LA BASE DE DATOS
    private final static String nombreDB = "osopolar.sqlite";
    private final static String TAG = "baseDatos";
    private final static int version = 1;
    private final String VENTA = "Venta", PRODUCTO = "Producto", PRODUCTOLISTA = "ProductoLista", LISTAPRECIO = "ListaPrecio", VENTADETALLE = "VentaDetalle", CLIENTE = "Cliente",REDES="Redes";

    public baseDatos(Context context){
        super(context,nombreDB,null,version);
    }

    //TABLAS A GENERAR
    private final static String tablaProducto = "CREATE TABLE Producto(IdProducto INTEGER PRIMARY KEY, Nombre TEXT, UnidadMedida TEXT, Activo BOOLEAN)";
    private final static String tablaProductoLista = "CREATE TABLE ProductoLista(IdProducto INTEGER, IdListaPrecio INTEGER, Precio INTEGER, PRIMARY KEY(IdProducto, IdListaPrecio))";
    private final static String tablaListaPrecio = "CREATE TABLE ListaPrecio(IdListaPrecio INTEGER PRIMARY KEY, Descripcion TEXT)";
    private final static String tablaVenta = "CREATE TABLE Venta(IdVenta INTEGER PRIMARY KEY AUTOINCREMENT, IdCliente INTEGER, Vendedor INTEGER, Fecha TEXT, Total REAL, Latitud TEXT, Longitud TEXT, Cancelada BOOLEAN, Sincronizado BOOLEAN,credito INTEGER)";
    private final static String tablaVentaDetalle = "CREATE TABLE VentaDetalle(IdVenta INTEGER, IdProducto INTEGER, Cantidad Float,Precio_venta REAL, PUnitario REAL, Subtotal REAL, Sincronizado BOOLEAN,credito INTEGER)";
    private final static String tablaCliente = "CREATE TABLE Cliente(IdCliente INTEGER, IdListaPrecio INTEGER, Nombre TEXT, Calle TEXT, Numero TEXT, Latitud TEXT, Longitud TEXT,credito INTEGER, PRIMARY KEY(IdCliente, IdListaPrecio))";
    public static final String COLUMN_ID = "IdVenta";
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
        db.execSQL("DROP TABLE IF EXISTS Redes");
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
            nuevaVentaDetalle.put("PUnitario", venta.getpUnitario());
            nuevaVentaDetalle.put("Subtotal", venta.getSubtotal());
            nuevaVentaDetalle.put("Sincronizado", venta.isSincronizado());
            nuevaVentaDetalle.put("credito",venta.getCredito());
            Log.d("vista_db", venta.getpUnitario().toString());
            try {
                db.insertOrThrow(VENTADETALLE, null, nuevaVentaDetalle);
                estadoInsercion = true;
            } catch (SQLiteException ex) {
                Toast.makeText(context, "Error al insertar detalle de venta: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return estadoInsercion;
    }




    public boolean insertarVenta(oVenta venta, Context context){
        SQLiteDatabase db = getWritableDatabase();
        boolean estadoInsercion = false;
        ContentValues nuevaVenta = new ContentValues();
        nuevaVenta.put("IdCliente", venta.getIdCliente());
        nuevaVenta.put("Vendedor", venta.getVendedor());
        nuevaVenta.put("Fecha", obtenerFecha());
        nuevaVenta.put("Total", venta.getTotal());
        nuevaVenta.put("Latitud", venta.getLatitud());
        nuevaVenta.put("Longitud", venta.getLongitud());
        nuevaVenta.put("Cancelada", venta.getCancelada());
        nuevaVenta.put("Sincronizado", venta.getSincronizado());
        nuevaVenta.put("credito",venta.getCredito());
        try {
            db.insertOrThrow(VENTA, null, nuevaVenta);
            estadoInsercion = true;
        }catch (SQLiteException ex){
            Toast.makeText(context, "Error al insertar venta: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return estadoInsercion;
    }

    public void insertarProductos(ArrayList<producto> aProductos, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for(producto producto : aProductos){
            ContentValues cvProducto = new ContentValues();
            cvProducto.put("IdProducto", producto.getIdProducto());
            cvProducto.put("Nombre", producto.getNombre());
            cvProducto.put("UnidadMedida", producto.getUnidadMedida());
            cvProducto.put("Activo", producto.getActivo());
            try{
                db.insertOrThrow(PRODUCTO, null, cvProducto);

                Log.d("Productos",producto.getNombre());
                //Toast.makeText(context, "Productos Almacenados", Toast.LENGTH_SHORT).show();
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el producto: ", Toast.LENGTH_SHORT).show();
                Log.d("Productos","error");
            }
        }
    }

    public void insertarListaPrecio(ArrayList<listaPrecio> aListaPrecio, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (listaPrecio lista : aListaPrecio){
            ContentValues cvLista = new ContentValues();
            cvLista.put("IdListaPrecio", lista.getidListaPrecio());
            cvLista.put("Descripcion", lista.getDescripcion());


            try{
                db.insertOrThrow(LISTAPRECIO, null, cvLista);
                Log.d("Lista",lista.getidListaPrecio().toString());
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar ListaPrecio: ", Toast.LENGTH_SHORT).show();
                Log.d("ListaPrecio","error");
                return;
            }
        }
    }

    public void insertarProductoListas(ArrayList<productoLista> aProductoLista, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (productoLista producto : aProductoLista){
            ContentValues cvProducto = new ContentValues();
            cvProducto.put("IdProducto", producto.getIdProducto());
            cvProducto.put("IdListaPrecio", producto.getIdListaPrecio());
            cvProducto.put("Precio", producto.getPrecio());
            try{
                db.insertOrThrow(PRODUCTOLISTA, null, cvProducto);
                Log.d("ListaProducto",producto.getPrecio().toString());
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar ProductoLista: ", Toast.LENGTH_SHORT).show();
                Log.d("ProductoListas","error");
                return;
            }
        }
    }


    /*public void insertarProductoListas(ArrayList<productoLista> aProductoLista, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (productoLista producto : aProductoLista){
            ContentValues cvProducto = new ContentValues();
            cvProducto.put("IdProducto", producto.getIdProducto());
            cvProducto.put("IdListaPrecio", producto.getIdListaPrecio());
            cvProducto.put("Precio", producto.getPrecio());
           Toast.makeText(context,"IdProducot :"+ cvProducto.get("IdProducto")+" "+"Id_listaPrecio"+" "+ cvProducto.get("IdListaPrecio")+"  "+"PRECIO: "+cvProducto.get("IdProducto") ,Toast.LENGTH_SHORT).show();


            try{
                db.insert(PRODUCTOLISTA, null, cvProducto);
                Toast.makeText(context, "Se inserto productos: ",Toast.LENGTH_SHORT).show();
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el productoLista: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }*/

    public void insertarCliente(ArrayList<cliente> aClientes, Context context){//metodo que cambio
        SQLiteDatabase db = getWritableDatabase();
        for (cliente cliente : aClientes){
            ContentValues cvCliente = new ContentValues();
            cvCliente.put("IdCliente", cliente.getIdCliente());
            cvCliente.put("IdListaPrecio", cliente.getIdListaPrecios());
            cvCliente.put("Nombre", cliente.getNombre());
            cvCliente.put("Calle", cliente.getCalle());
            cvCliente.put("Numero", cliente.getNumero());
            cvCliente.put("Latitud", cliente.getLatitud());
            cvCliente.put("Longitud", cliente.getLongitud());
            cvCliente.put("credito",cliente.getCredito());

            try{
                db.insertOrThrow(CLIENTE, null, cvCliente);
                Log.d("insert datos",cvCliente.toString());
                //Log.d("credito", String.valueOf(cursor.getString(7)));
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el cliente: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    //ACTUALIZAR*****************************


    //BORRAR*****************************

    public void borrarTodosDatos(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(PRODUCTO, null, null);
        db.delete(PRODUCTOLISTA, null, null);
        db.delete(LISTAPRECIO, null, null);
        db.delete(CLIENTE, null, null);
    }

    public void borrarventas(){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(VENTA, null, null);
        db.delete(VENTADETALLE, null, null);
    }

    public void borrarventa(int id_venta){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + VENTA + " WHERE " + COLUMN_ID + " = " + id_venta + ";");
        db.execSQL("DELETE FROM " + VENTADETALLE + " WHERE " + COLUMN_ID + " = " + id_venta + ";");
        db.close();

        //borrarventaDetalle(id_venta);
    }


    public void borrarventaDetalle(int id_venta){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + VENTADETALLE + " WHERE " + COLUMN_ID + " = " + id_venta + ";");
        db.close();

        //borrarDetalle(id_venta);
    }


    //SELECCIONAR*****************************

    public int obtenerUltimoIdVenta(){
        SQLiteDatabase db = getWritableDatabase();
        int idVenta = 0;
        Cursor cursor = db.rawQuery("SELECT  MAX(IdVenta) FROM "+VENTA,null);
        if (cursor.moveToFirst())
            idVenta = cursor.getInt(0);
        return idVenta;
    }


    public int obtenerCantidadVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        int cantidadVentas = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            cantidadVentas = cursor.getInt(0);
        }
        return cantidadVentas;
    }


    public double obtenerMontoVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total) FROM "+VENTA+" WHERE Cancelada = 0 ", null);
        if (cursor.moveToFirst()){
            montoVentas = cursor.getDouble(0);
        }
        return montoVentas;
    }

    public cliente  obtenerClientePorId(int idCliente){
        SQLiteDatabase db = getWritableDatabase();
        cliente cliente = new cliente();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE+" WHERE IdCliente = "+ idCliente, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                cliente.setIdCliente(cursor.getInt(0));
                cliente.setIdListaPrecios(cursor.getInt(1));
                cliente.setNombre(cursor.getString(2));
                cliente.setCalle(cursor.getString(3));
                cliente.setNumero(cursor.getString(4));
                cliente.setLatitud(cursor.getString(5));
                cliente.setLongitud(cursor.getString(6));
                cliente.setCredito(cursor.getInt(7));
            }


            Log.d("credito", String.valueOf(cursor.getString(7)));
        }

        return cliente;

    }

    public String  obtenerClientePorIdVenta(int idCliente){
        SQLiteDatabase db = getWritableDatabase();
        //cliente cliente = new cliente();

        String cliente="";
        Cursor cursor = db.rawQuery("SELECT Nombre FROM "+CLIENTE+" WHERE IdCliente = "+ idCliente, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                cliente=cursor.getString(0);

            }


        Log.d("nombre", cliente);
        }

        return cliente;

    }


    public cliente  obtenerClientebyname(String nombre){
        SQLiteDatabase db = getWritableDatabase();
        cliente cliente = new cliente();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE+ " WHERE Nombre LIKE '" + nombre + "%'", null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                cliente.setIdCliente(cursor.getInt(0));
                cliente.setIdListaPrecios(cursor.getInt(1));
                cliente.setNombre(cursor.getString(2));
                cliente.setCalle(cursor.getString(3));
                cliente.setNumero(cursor.getString(4));
                cliente.setLatitud(cursor.getString(5));
                cliente.setLongitud(cursor.getString(6));
                cliente.setCredito(cursor.getInt(7));
            }

            Log.d("credito", String.valueOf(cursor.getString(7)));

        }

        return cliente;

    }//cambio metodo

    public ArrayList<cliente> obtenerClientesPorNombre(String nombre){
        ArrayList<cliente> aClientes = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CLIENTE + " WHERE Nombre LIKE '" + nombre + "%'", null);
        if (cursor.moveToFirst()) {
            do {
                cliente cliente = new cliente();
                cliente.setIdCliente(cursor.getInt(0));
                cliente.setIdListaPrecios(cursor.getInt(1));
                cliente.setNombre(cursor.getString(2));
                cliente.setCalle(cursor.getString(3));
                cliente.setNumero(cursor.getString(4));
                cliente.setLatitud(cursor.getString(5));
                cliente.setLongitud(cursor.getString(6));
                cliente.setCredito(cursor.getInt(7));

//                cliente.setNombre(cursor.getString(0));

                //cliente.notifyAll();
                Log.d("clinte", String.valueOf(cursor.getString(2)));
                Log.d("credito", String.valueOf(cursor.getString(7)));

                aClientes.add(cliente);
            } while (cursor.moveToNext());
        }

        return aClientes;
    }//cambio metodo

    public ArrayList<cliente> obtenerClientesCercanos(Location lastLocation){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<cliente> aClientes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE, null);
        if (cursor.moveToFirst()){
            do{
                Log.d("loca_db",cursor.getString(5));
                Log.d("locas_db",cursor.getString(6));
                Log.d("mia",lastLocation.toString());
                Log.d("distancia", String.valueOf(obtenerDistancia(cursor.getString(5), cursor.getString(6), lastLocation)));
                if (obtenerDistancia(cursor.getString(5), cursor.getString(6), lastLocation) < 3000){
                    cliente cliente = new cliente();
                    cliente.setIdCliente(cursor.getInt(0));
                    cliente.setIdListaPrecios(cursor.getInt(1));
                    cliente.setNombre(cursor.getString(2));
                    cliente.setCalle(cursor.getString(3));
                    cliente.setNumero(cursor.getString(4));
                    cliente.setLatitud(cursor.getString(5));
                    cliente.setLongitud(cursor.getString(6));
                    cliente.setCredito(cursor.getInt(7));
                    aClientes.add(cliente);
                    Log.d("clienes_cercanos", String.valueOf(cursor.getString(7)));
                }

            }while (cursor.moveToNext());
        }
        return aClientes;
    }//cambio metodo


    public ArrayList<producto> obtenerProductos(int idListaPrecio){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<producto> aProducto = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+PRODUCTO, null);
        if (cursor.moveToFirst()){
            do{
                producto producto = new producto();
                producto.setIdProducto(cursor.getInt(0));
                producto.setNombre(cursor.getString(1));
                producto.setUnidadMedida(cursor.getString(2));
                producto.setActivo(cursor.getString(3));
                aProducto.add(producto);
            }while (cursor.moveToNext());
            for(producto producto : aProducto) {
                cursor = db.rawQuery("SELECT Precio FROM " + PRODUCTOLISTA + " WHERE IdProducto = " + producto.getIdProducto() + " AND IdListaPrecio = " + idListaPrecio, null);
                if(cursor.moveToFirst()){
                    producto.setPrecio(cursor.getDouble(0));
                }
            }
        }
        return aProducto;
    }


    public ArrayList<producto> obtenerProductosVenta(int idventa){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<producto> aProducto = new ArrayList<>();


        Cursor cursor = db.rawQuery("SELECT " + PRODUCTO + ".* FROM " +VENTADETALLE+" INNER JOIN " +PRODUCTO+ " ON " + VENTADETALLE + ".IdProducto = " + PRODUCTO + ".IdProducto"
                +" WHERE IdVenta = "+ idventa
                , null);
        if (cursor.moveToFirst()){
            do{
                producto producto = new producto();
                producto.setIdProducto(cursor.getInt(0));
                producto.setNombre(cursor.getString(1));
                producto.setUnidadMedida(cursor.getString(2));
                producto.setActivo(cursor.getString(3));
                aProducto.add(producto);
            }while (cursor.moveToNext());

        }
        return aProducto;
    }

    public ArrayList<oVenta> obtenerVentas(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<oVenta> aVentas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            do{
                oVenta venta = new oVenta();
                venta.setIdVenta(cursor.getInt(0));
                venta.setIdCliente(cursor.getInt(1));
                venta.setVendedor(cursor.getString(2));
                venta.setFecha(cursor.getString(3));
                venta.setTotal(cursor.getDouble(4));
                venta.setLatitud(cursor.getString(5));
                venta.setLongitud(cursor.getString(6));
                venta.setCancelada(cursor.getInt(7) > 0);
                venta.setCredito(cursor.getInt(9));
                aVentas.add(venta);
            }while (cursor.moveToNext());
        }
        return aVentas;
    }

    public ArrayList<oVenta> obtenerVentasHistorial(){//cambio
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<oVenta> aVentas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            do {
                oVenta venta = new oVenta();
                String nombre = obtenerClientePorIdVenta(cursor.getInt(1));//cambio cambpo
                if (cursor.getDouble(4) > 0.0){
                    venta.setCliente(nombre);
                venta.setIdVenta(cursor.getInt(0));
                venta.setIdCliente(cursor.getInt(1));
                venta.setVendedor(cursor.getString(2));
                venta.setFecha(cursor.getString(3));
                venta.setTotal(cursor.getDouble(4));
                venta.setLatitud(cursor.getString(5));
                venta.setLongitud(cursor.getString(6));
                venta.setCancelada(cursor.getInt(7) > 0);
                venta.setCredito(cursor.getInt(9));
                aVentas.add(venta);
           }
                Log.d("nombresito", nombre);
            }while (cursor.moveToNext());
        }
        else{
            Log.d("VACIO","SIMON");
        }
        return aVentas;
    }

    public ArrayList<ventaDetalle> obtenerVentaDetalle(){//cambio
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<ventaDetalle> aVentaDetalle = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTADETALLE, null);
        if (cursor.moveToFirst()){
            do{
                ventaDetalle venta = new ventaDetalle();
                venta.setIdVenta(cursor.getInt(0));
                venta.setIdProducto(cursor.getInt(1));
                venta.setCantidad(cursor.getFloat(2));
                venta.setpUnitario(cursor.getDouble(3));
                venta.setSubtotal(cursor.getDouble(4));
                venta.setCredito(cursor.getInt(7));
                aVentaDetalle.add(venta);
            }while (cursor.moveToNext());
        }
        return aVentaDetalle;
    }

//
    public ArrayList<ventaDetalle> obtenerVentaDetallebyId(int id_producto){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<ventaDetalle> aVentaDetalle = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTADETALLE+" WHERE IdProducto = "+ id_producto, null);
        if (cursor.moveToFirst()){
            do{

                ventaDetalle venta = new ventaDetalle();
                String producto=obtenerProductoPorIdVenta(cursor.getInt(1));
                venta.setIdVenta(cursor.getInt(0));
                venta.setIdProducto(cursor.getInt(1));
                venta.setProducto(producto);
                venta.setCantidad(cursor.getFloat(2));
                venta.setpUnitario(cursor.getDouble(3));
                venta.setSubtotal(cursor.getDouble(4));
                venta.setCredito(cursor.getInt(7));
                aVentaDetalle.add(venta);
            }while (cursor.moveToNext());
        }
        return aVentaDetalle;
    }

    public String  obtenerProductoPorIdVenta(int IdProducto){
        SQLiteDatabase db = getWritableDatabase();
        //cliente cliente = new cliente();

        String cliente="";
        Cursor cursor = db.rawQuery("SELECT Nombre FROM "+PRODUCTO+" WHERE IdProducto = "+ IdProducto, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                cliente=cursor.getString(0);

            }


            Log.d("nombre", cliente);
        }

        return cliente;

    }

    public ArrayList<String> obtenerTopVentas(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> aNombre = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + PRODUCTO + ".Nombre, SUM(" + VENTADETALLE + ".Cantidad),IdVenta FROM " + VENTADETALLE +
                " INNER JOIN " + PRODUCTO + " ON " + VENTADETALLE + ".IdProducto = " + PRODUCTO + ".IdProducto" +
                " GROUP BY " + PRODUCTO + ".Nombre" +
                " ORDER BY SUM(" + VENTADETALLE + ".cantidad) DESC" +
                " LIMIT 10", null);
        if (cursor.moveToFirst()){


            do{

                double precio_venta=obtenerMontoVentasID(cursor.getInt(2));
                double suma=+precio_venta;
                Log.d("total", String.valueOf(precio_venta));
                //aNombre.add(cursor.getString(0) + " con " + cursor.getInt(1) + " productos vendidos"+"  "+"$"+0);
                if (precio_venta>0){
                    aNombre.add("Cantidad de producto"+" "+cursor.getString(1)+" "+"del producto"+" "+cursor.getString(0));
                }

            }while (cursor.moveToNext());
        }
        return aNombre;
    }

    public double obtenerMontoVentasID(int id_venta){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total),Cancelada FROM "+VENTA+" WHERE Cancelada = 0 ", null);
        if (cursor.moveToFirst()){

            do{
                if (cursor.getInt(1)==0){
                    montoVentas = cursor.getDouble(0);
                }
                else{
                    montoVentas=0;
                }

            }while (cursor.moveToNext());
        }
        return montoVentas;
    }


    public  double obtenerMontoVentasTotal(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total),credito FROM "+VENTA+" WHERE Cancelada = 0 ", null);
        if (cursor.moveToFirst()){

            do{
                montoVentas = cursor.getDouble(0);
                Log.d("MIAS",String.valueOf(cursor.getInt(1)));
            }while (cursor.moveToNext());
        }
        return montoVentas;
    }

    public  double obtenerMontoVentasCredito(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total),credito FROM "+VENTA+" WHERE credito>=1 AND Cancelada=0", null);
        if (cursor.moveToFirst()){

            do{

                montoVentas = cursor.getDouble(0);
                //Log.d("Credito",String.valueOf(cursor.getInt(1)));
            }while (cursor.moveToNext());
        }
        else{
            Log.d("ERROR","1");
        }
        return montoVentas;
    }

    public  double obtenerMontoVentasContado(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total),credito FROM "+VENTA+" WHERE credito<=0 AND Cancelada=0", null);
        if (cursor.moveToFirst()){

            do{
                montoVentas = cursor.getDouble(0);
                // Log.d("Contado",String.valueOf(cursor.getInt(1)));
            }while (cursor.moveToNext());
        }

        else{
            Log.d("ERROR","1");
        }
        return montoVentas;
    }

    //METODOS PROPIOS PARA LAS CONSULTAS

    public double obtenerDistancia(String latitud, String longitud, Location lastLocation){
        Location ubicacionCliente = new Location("uCliente");
        ubicacionCliente.setLatitude(Double.parseDouble(latitud));
        ubicacionCliente.setLongitude(Double.parseDouble(longitud));
        return lastLocation.distanceTo(ubicacionCliente);
    }

    private String obtenerFecha(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm ");
        return dateFormat.format(date);
    }
}
