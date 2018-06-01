package arenzo.alejandroochoa.osopolar.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.conexion;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.Peticiones.webServices;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class MainActivity extends AppCompatActivity {
    //
// //
// TODO ENVIAR LOS DATOS DE SINCRONIZAR
    //
// //
// TODO ENVIAR LOS DATOS DE SINCRONIZAR
    private final static String TAG = "MainActivity";
    private final String EXISTEIDEQUIPO = "EXISTEIDEQUIPO";
    private final String EXISTERED = "EXISTERED";
    private final String EXISTESERVER = "EXISTESERVER";
    private final String SSID = "SSID";
    private final String SERVER = "SERVER";
    private final String IDEQUIPO = "IDEQUIPO";
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesRedes;
    private baseDatos bd;
   // public Server server;


    private Button btnNuevaVenta,btnHistorial;
    private FloatingActionButton fbtnSincronizar;
    private TextView txtVentaTotal,txtMontoVentas;
    private ListView lvVentas;
    ProgressDialog anillo = null;
    Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        centrarTituloActionBar();
        cargarElementosVista();
        eventosVista();
        permisoLocalizacion();
        saberSiExisteIdEquipo();
        saberSiExistered();
        saberSiExisteserver();
        obtenerCantidadVentas();



    }



    private void cargarElementosVista(){
        btnNuevaVenta = (Button)findViewById(R.id.btnNuevaVenta);
        fbtnSincronizar = (FloatingActionButton)findViewById(R.id.fbtnSincronizar);
        txtVentaTotal = (TextView)findViewById(R.id.txtVentaTotal);
        txtMontoVentas = (TextView)findViewById(R.id.txtMontoVentas);
        lvVentas = (ListView)findViewById(R.id.lvVentas);
        btnHistorial=(Button)findViewById(R.id.btnHistorial);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferencesRedes = PreferenceManager.getDefaultSharedPreferences(this);
        bd = new baseDatos(getApplicationContext());

        //String ssid=getWifiName(context);
        //camarapermiso();

        String red=sharedPreferencesRedes.getString(SSID, "");

        if (!red.isEmpty()){
            fbtnSincronizar.setVisibility(View.INVISIBLE);

            getWifiName(context);
        }
        else{
            fbtnSincronizar.setVisibility(View.VISIBLE);
        }



        /*if (ssid=="FIESTA DEL PUERQUITO 2016"){
            fbtnSincronizar.setVisibility(View.VISIBLE);
        }else{
            fbtnSincronizar.setVisibility(View.INVISIBLE);
        }*/




    }

    private void centrarTituloActionBar() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private void eventosVista(){
        btnNuevaVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEscaneo();
            }
        });

        fbtnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getApplicationContext())){
                    bd.borrarTodosDatos();
                    sincronizarTodosDatos();
                }else
                    avisoNoRed();

            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHistorial();
            }
        });
    }

    private void obtenerCantidadVentas(){
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        int totalVentas = bd.obtenerCantidadVentasHoy();
        txtVentaTotal.setText(String.valueOf(totalVentas));
        double montoVentas = bd.obtenerMontoVentasHoy();

        if (montoVentas<=0){
            txtMontoVentas.setText("$ " + df.format(montoVentas));
        }
        else{
            String sumando=formatea.format(montoVentas);
            txtMontoVentas.setText("$ " +sumando);

        }

    }

    private void mostrarEscaneo(){
        Intent intent = new Intent();
        intent.setClass(this,escaneo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        // finish();
    }

    private void saberSiExisteIdEquipo(){
        Boolean existe = sharedPreferences.getBoolean(EXISTEIDEQUIPO, false);
        if (!existe){
            alertaAgregarIdEquipo();
        }

        //Log.d("id_ex",existe.toString());
    }

    private void saberSiExistered(){
        Boolean existered = sharedPreferencesRedes.getBoolean(EXISTERED, false);

        if (!existered){
            alertaAgregarred();
        }

       // Log.d("id_exred",existered.toString());
    }


    private void saberSiExisteserver(){//cambio
        Boolean existered = sharedPreferencesRedes.getBoolean(EXISTERED, false);

        if (!existered){
            alertaAgregarserver();
        }
        //alertaAgregarserver();

//        Log.d("id_exserver",server.getURLBASEDEVELOP().toString());
    }

    private void cargarTopDeVentas(){
        if (bd.obtenerTopVentas().size()>0){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bd.obtenerTopVentas());
            lvVentas.setAdapter(adapter);
        }



    }

    private void alertaAgregarIdEquipo(){
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_inicio_id, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ID Equipo")
                .setMessage("Agrega un ID para iniciar sesión")
                .setView(view)
                .setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edtID = view.findViewById(R.id.edtIdEquipo);
                if (edtID.length() == 4){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(EXISTEIDEQUIPO, true);
                    editor.putString(IDEQUIPO, edtID.getText().toString());
                    editor.apply();
                    dialog.dismiss();
                    sincronizarTodosDatos();
                }else{
                    Toast.makeText(MainActivity.this, "Debes de añadir un ID de cuatro digitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void alertaAgregarred(){
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_red, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("WIFI ")
                .setMessage("Agregar Nombre de la red conectada")
                .setView(view)
                .setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edtID = view.findViewById(R.id.edtRed);

                if (edtID.length()>0) {

                    SharedPreferences.Editor editor = sharedPreferencesRedes.edit();
                    editor.putBoolean(EXISTERED, true);
                    editor.putString(SSID, edtID.getText().toString());
                    editor.apply();
                    dialog.dismiss();
                }


                    //sincronizarTodosDatos();

            }
        });
    }

    private void alertaAgregarserver(){//cambio

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_red, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configuración del servidor ")
                .setMessage("Agregar el url del servidor ")
                .setView(view)
                .setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edtID = view.findViewById(R.id.edtRed);

                if (edtID.length()>0) {

                    SharedPreferences.Editor editor = sharedPreferencesRedes.edit();
                    editor.putBoolean(EXISTESERVER, true);
                    editor.putString(SERVER, edtID.getText().toString());
                    editor.apply();
                    dialog.dismiss();

                   // server.setURLBASEDEVELOP(edtID.getText().toString());

                    Log.d("Insertar_serber",edtID.getText().toString());


                }




                //sincronizarTodosDatos();

            }
        });


    }

    private void sincronizarTodosDatos(){
        mostrarCargandoAnillo();
        List<oVenta> aVentas = bd.obtenerVentas();
        if(aVentas.size() > 0){

            Log.d("ventas", String.valueOf(aVentas));

            requestVentas(aVentas, bd.obtenerVentaDetalle());
        } else {
            requestObtenerProductos(sharedPreferences.getString(IDEQUIPO, "1"));
        }
    }

    private void requestVentas(List<oVenta> aVentas, List<ventaDetalle> aVentaDetalle){
        final webServices webServices = new webServices(getApplicationContext(),sharedPreferences.getString(SERVER, ""));
        if ( webServices.enviarVentas(aVentas, aVentaDetalle, sharedPreferences.getString(IDEQUIPO, "1"), anillo)){
            txtVentaTotal.setText("0");
            txtMontoVentas.setText("$0.00");
            bd.borrarventas();
            bd.borrarTodosDatos();
            sincronizarTodosDatos();
            Toast.makeText(context,"Ventas Registradas en el sistema",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context,MainActivity.class);
            startActivity(intent);


//            lvVentas.removeAllViews();


        }
    }

    public void requestObtenerProductos(String idEquipo){
        final webServices webServices = new webServices(getApplicationContext(),sharedPreferences.getString(SERVER, "1"));
        webServices.obtenerProductos(idEquipo, anillo);
       // webServices.obtenerListasPrecios(idEquipo, anillo);
    }

    private void avisoNoRed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AVISO")
                .setMessage("Encienda el Wi-Fi o los datos móviles.")
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    //1 VERIFICAR EL PERMISO
    private void permisoLocalizacion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permiso = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permiso != PackageManager.PERMISSION_GRANTED)
                solicitarPermiso();
        }
    }

    //2 SOLICITAR EL PERMISO
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermiso(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            abrirConfiguracion();
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
    }

    //3 PROCESAR LA RESPUESTA
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0)
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ;
            }
            else
                abrirConfiguracion();

    }

    private void abrirConfiguracion(){
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerCantidadVentas();
        cargarTopDeVentas();
    }

    private void mostrarCargandoAnillo(){
        this.anillo = ProgressDialog.show(this, "Sincronizando", "Obteniendo todos los datos...", false, false);
    }


    public void getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    // return wifiInfo.getSSID().toString();

                    String oso=wifiInfo.getSSID().toString();

                    // Log.d("ok",oso);
                    //String red="ARRISGW";

                    String red=sharedPreferences.getString(SSID, "");

                    if (oso.equals("\"" + red+ "\"")){
                        fbtnSincronizar.setVisibility(View.VISIBLE);
                        //Toast.makeText(context,wifiInfo.getSSID(),Toast.LENGTH_SHORT).show();
                        // Log.e("ok",oso);
                    }
                    else{
                        //Toast.makeText(context,"Dispositivo no conectado a la red matriz",Toast.LENGTH_SHORT).show();
                        //Log.e("ok",oso);

                        fbtnSincronizar.setVisibility(View.INVISIBLE);
                        //fbtnSincronizar.setVisibility(View.VISIBLE);
                    }




                }
                else {
                    Toast.makeText(context,"Dispositivo no conectado a la red matriz",Toast.LENGTH_SHORT).show();
                    fbtnSincronizar.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    private void goToHistorial(){
        // ProjectsActivity is my 'home' activity
        Intent favoritos=new Intent(MainActivity.this,HistorialActivity.class);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();
    }





}
