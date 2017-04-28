package arenzo.alejandroochoa.osopolar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.conexion;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final String EXISTEIDEQUIPO = "EXISTEIDEQUIPO";
    private final String IDEQUIPO = "IDEQUIPO";

    private SharedPreferences sharedPreferences;

    private Button btnNuevaVenta;
    private ImageButton btnSincronizar;
    private TextView txtVentaTotal,txtMontoVentas;
    private ListView lvVentas;
    // PARA AGREGAR LA FUENTE POR CODIGO tv.setTypeface(Fuentes.myFont(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        centrarTituloActionBar();
        cargarElementosVista();
        eventosVista();
        permisoLocalizacion();
        saberSiExisteIdEquipo();
    }

    private void cargarElementosVista(){
        btnNuevaVenta = (Button)findViewById(R.id.btnNuevaVenta);
        btnSincronizar = (ImageButton)findViewById(R.id.btnSincronizar);
        txtVentaTotal = (TextView)findViewById(R.id.txtVentaTotal);
        txtMontoVentas = (TextView)findViewById(R.id.txtMontoVentas);
        lvVentas = (ListView)findViewById(R.id.lvVentas);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getApplicationContext())){
                    if (conexion.isOnline()){
                        ;
                    }else
                        avisoNoConexion();
                }else
                    avisoNoRed();

            }
        });
    }

    private void mostrarEscaneo(){
        Intent intent = new Intent(this, escaneo.class);
        startActivity(intent);
    }

    private void saberSiExisteIdEquipo(){
        Boolean existe = sharedPreferences.getBoolean(EXISTEIDEQUIPO, false);
        if (!existe){
            alertaAgregarIdEquipo();
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
                final EditText edtID = (EditText)view.findViewById(R.id.edtIdEquipo);
                if (edtID.length() == 4){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(EXISTEIDEQUIPO, true);
                    editor.putInt(IDEQUIPO, Integer.parseInt(edtID.getText().toString()));
                    editor.commit();
                    dialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, "Debes de añadir un ID de cuatro digitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void avisoNoConexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AVISO")
                .setMessage("No estas en la red correcta, intentalo nuevamente.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
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

}
