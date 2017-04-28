package arenzo.alejandroochoa.osopolar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_producto;
import arenzo.alejandroochoa.osopolar.Localizacion.localizar;

public class venta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "venta";
    private final String RESULTADO = "RESULTADO";
    private double total = 0.0;
    private GoogleApiClient mgoogleApiClient;
    private Location mLastLocation;
    private LocationManager locationManager;

    private TextView txtNombreCliente, txtSubtotal,txtTotal;
    private EditText edtPrecioUnitario,edtCantidad;
    private Button btnFinalizarVenta, btnCancelarVenta;
    private ImageButton btnAgregarProducto;
    private GridView grdProductos;
    private Spinner spProductos;

    private adapter_producto adapter_producto;
    private ArrayList<producto> aProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        cargarElementosVista();
        centrarTituloActionBar();
        cargarNombreCliente();
        eventosVista();
    }

    private void cargarElementosVista(){
        txtNombreCliente = (TextView)findViewById(R.id.txtNombreCliente);
        txtSubtotal = (TextView)findViewById(R.id.txtSubtotal);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        edtPrecioUnitario = (EditText)findViewById(R.id.edtPrecioUnitario);
        edtCantidad = (EditText)findViewById(R.id.edtCantidad);
        btnFinalizarVenta = (Button)findViewById(R.id.btnFinalizarVenta);
        btnCancelarVenta = (Button)findViewById(R.id.btnCancelarVenta);
        btnAgregarProducto = (ImageButton)findViewById(R.id.btnAgregarProducto);
        grdProductos = (GridView)findViewById(R.id.grdProductos);
        spProductos = (Spinner)findViewById(R.id.spProductos);
        aProducto = new ArrayList<>();
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
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
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProducto();
            }
        });

        edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSubtotal.setText("El subtotal es: "+obtenerSubtotal());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPrecioUnitario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSubtotal.setText("El subtotal es: "+obtenerSubtotal());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCancelarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarVenta();
            }
        });

        btnFinalizarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerLocalizacion();
            }
        });
    }

    private void cargarNombreCliente(){
        txtNombreCliente.setText(getIntent().getStringExtra(RESULTADO));
    }

    private void agregarProducto(){
        if (edtCantidad.length() != 0 && edtPrecioUnitario.length() != 0) {
            producto oProducto = new producto();
            oProducto.setNombre(spProductos.getSelectedItem().toString());
            oProducto.setCantidad(Integer.parseInt(edtCantidad.getText().toString()));
            oProducto.setPrecio(obtenerSubtotal());
            aProducto.add(oProducto);
            adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
            grdProductos.setAdapter(adapter_producto);
            agregarTotal();
            limpiarVista();
        }else
            Toast.makeText(this, "Agrega precio y cantidad al producto.", Toast.LENGTH_SHORT).show();
    }

    private double obtenerSubtotal(){
        if (edtCantidad.length() != 0 && edtPrecioUnitario.length() != 0)
            return Double.parseDouble(edtPrecioUnitario.getText().toString()) * Double.parseDouble(edtCantidad.getText().toString());
        return 0.0;
    }

    private void agregarTotal(){
        total += obtenerSubtotal();
        txtTotal.setText("Total venta: "+total);
    }

    private void limpiarVista(){
        edtCantidad.setText("");
        edtPrecioUnitario.setText("");
    }

    private void cancelarVenta(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atención")
                .setMessage("¿Está seguro de cancelar la venta?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void obtenerLocalizacion(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            activarGps();
        else
            Toast.makeText(this, "Tu lat y lon es: "+mLastLocation.getLatitude() +"-"+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();

    }

    private void activarGps(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atención")
                .setMessage("El GPS no esta activado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLastLocation == null){
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
         }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mgoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mgoogleApiClient.disconnect();
    }
}
