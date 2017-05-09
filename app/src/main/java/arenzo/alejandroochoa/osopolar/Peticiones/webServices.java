package arenzo.alejandroochoa.osopolar.Peticiones;

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
import java.util.Map;

import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
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

    private final String URLENVIARVENTAS = "";
    private final String URLENVIARVENTADETALLE = "";
    private final String URLOBTENERPRODUCTO = "";
    private final String URLOBTENERLISTAPRECIOS = "";
    private final String URLPRODUCTOSLISTAS = "";
    private final String URLOBTENERCLIENTES = "";

    public webServices(Context context) {
        this.context=context;
        bd = new baseDatos(context);
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        requestQueue= SingletonVolley.getInstance(context);
        requestQueue.getRequestQueue();
    }
    //TODO CREAR INSERCIONES A LA BASE DE DATOS
    public void obtenerProductos(final String idEquipo){
        StringRequest request = new StringRequest(Request.Method.POST, URLOBTENERPRODUCTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jaProductos = new JSONArray(response);
                            ArrayList<producto> aProductos = new parsearWebService().parsearProductos(jaProductos);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error en la conversión: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        };
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerListasPrecios(final String idEquipo){
        StringRequest request = new StringRequest(Request.Method.POST,
                URLOBTENERLISTAPRECIOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        }){
          @Override
            protected  Map<String, String> getParams(){
              Map<String, String> params = new HashMap<>();
              params.put("idEquipo", idEquipo);
              return params;
          }
        };
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerProductosListas(final String idEquipo){
        StringRequest request = new StringRequest(Request.Method.POST,
                URLPRODUCTOSLISTAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected  Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        };
        requestQueue.addToRequestQueue(request);
    }

    public void obtenerClientes(final String idEquipo){
        StringRequest request = new StringRequest(Request.Method.POST,
                URLOBTENERCLIENTES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected  Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("idEquipo", idEquipo);
                return params;
            }
        };
        requestQueue.addToRequestQueue(request);
    }

    public void enviarVentas(final ArrayList<oVenta> aVentas){
        JSONArray jaVentas = new parsearWebService().parsearVentas(aVentas);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST,
                URLENVIARVENTAS, jaVentas,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.addToRequestQueue(request);
    }

    public void enviarVentaDetalles(final ArrayList<ventaDetalle> aVentaDetalles){
        JSONArray jaVentaDetalle = new parsearWebService().parsearVentaDetalles(aVentaDetalles);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST,
                URLENVIARVENTADETALLE,
                jaVentaDetalle,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la peticion: "+error, Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.addToRequestQueue(request);
    }



}
