package arenzo.alejandroochoa.osopolar.Peticiones;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.listaPrecio;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.productoLista;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

/**
 * Created by AlejandroMissael on 25/04/2017.
 */

public class webServices {

    private static final String TAG = "webServices";
    public Context context;
    private SingletonVolley requestQueue;
    private baseDatos bd;

    private final String URLBASEDEVELOP = "http://xtremesoftware.com.mx/portafolio/oso/public/api/";
    private final String URLENVIARVENTAS = "EnviarVentas";
    private final String URLOBTENERPRODUCTO = "ObtenerProductos";
    private final String URLOBTENERLISTAPRECIOS = "ObtenerListasPrecios";
    private final String URLPRODUCTOSLISTAS = "ObtenerProductosLista";
    private final String URLOBTENERCLIENTES = "ObtenerClientes";

    public webServices(Context context) {
        this.context=context;
        bd = new baseDatos(context);
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        requestQueue= SingletonVolley.getInstance(context);
        requestQueue.getRequestQueue();
    }

    public void obtenerProductos(final String idEquipo, final Dialog dialog){
        StringRequest request = new StringRequest(Request.Method.GET, URLBASEDEVELOP + URLOBTENERPRODUCTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject joProductos = new JSONObject(response);
                            ArrayList<producto> aProductos = new parsearWebService().parsearProductos(joProductos);
                            bd.insertarProductos(aProductos, context);
                            obtenerListasPrecios(idEquipo, dialog);
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Toast.makeText(context, "Error en la conversión: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        }) /*{
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        }*/;
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerListasPrecios(final String idEquipo, final Dialog dialog){
        StringRequest request = new StringRequest(Request.Method.GET,
                URLBASEDEVELOP + URLOBTENERLISTAPRECIOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject joListaPrecio = new JSONObject(response);
                            ArrayList<listaPrecio> aListaPrecio = new parsearWebService().parsearListaPrecio(joListaPrecio);
                            bd.insertarListaPrecio(aListaPrecio, context);
                            obtenerProductosListas(idEquipo, dialog);
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        })/*{
          @Override
            protected  Map<String, String> getParams(){
              Map<String, String> params = new HashMap<>();
              params.put("idEquipo", idEquipo);
              return params;
          }
        }*/;
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerProductosListas(final String idEquipo, final Dialog dialog){
        StringRequest request = new StringRequest(Request.Method.GET,
                URLBASEDEVELOP + URLPRODUCTOSLISTAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject joProductoLista = new JSONObject(response);
                            ArrayList<productoLista> aProductoLista = new parsearWebService().parsearProductoLista(joProductoLista);
                            bd.insertarProductoListas(aProductoLista, context);
                            obtenerClientes(idEquipo, dialog);
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        })/*{
            @Override
            protected  Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        }*/;
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerClientes(final String idEquipo, final Dialog dialog){
        StringRequest request = new StringRequest(Request.Method.GET,
                URLBASEDEVELOP + URLOBTENERCLIENTES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject joClientes = new JSONObject(response);
                            ArrayList<cliente> aClientes = new parsearWebService().parsearClientes(joClientes);
                            bd.insertarCliente(aClientes, context);
                            dialog.dismiss();
                            Toast.makeText(context, "Sincronización finalizada con éxito", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        })/*{
            @Override
            protected  Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        }*/;
        requestQueue.addToRequestQueue(request);
    }

    public boolean enviarVentas(final List<oVenta> aVentas, final List<ventaDetalle> aVentaDetalle, final String idEquipo, final Dialog anillo){
        JSONArray jaVentas = parsearWebService.parsearVentas(aVentas, aVentaDetalle);

        Log.d("ventas volley",String.valueOf(jaVentas));
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST,
                URLBASEDEVELOP + URLENVIARVENTAS,
                jaVentas,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            obtenerProductos(idEquipo, anillo);
                        Toast.makeText(context, "success ", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                anillo.dismiss();
                Toast.makeText(context, "Error en la aqui: "+error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.addToRequestQueue(request);

        return true;
    }



}
