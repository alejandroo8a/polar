package arenzo.alejandroochoa.osopolar.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.osopolar.Activities.venta;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_busqueda;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_clientes;
import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

/**
 * Created by AlejandroMissael on 28/04/2017.
 */

public class busqueda extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "busqueda";
    private final String RESULTADO = "RESULTADO";
    List<String> names;
    private ListView lvClientesCercas;
    private TextView txtSinClientes;
    ActionBar actionBar;

    private GoogleApiClient mgoogleApiClient;
    private Location mLastLocation;
    private baseDatos bd;
    private adapter_clientes adapter,adaptador;
    private adapter_busqueda adapter_busqueda;
    private ArrayList<cliente> aClientes,aBusqueda;

    public busqueda() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Obtener instancia de la action bar
        actionBar = ((AppCompatActivity) getActivity())
                .getSupportActionBar();

        if (actionBar != null) {
            // Habilitar el Up Button
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Cambiar icono del Up Button
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_busqueda, container, false);
        lvClientesCercas = (ListView) view.findViewById(R.id.lvClientesCercas);
        txtSinClientes = (TextView) view.findViewById(R.id.txtSinClientes);
        seleccionarCliente();
        configuracionClienteGoogle();
        bd = new baseDatos(getContext());
        return view;
    }

    private void configuracionClienteGoogle(){
        mgoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();
    }

    private void seleccionarCliente(){
        lvClientesCercas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarVenta(aClientes.get(position));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_full_screen_dialog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                actionBar.setDisplayHomeAsUpEnabled(false);
                this.dismiss();
                break;
            case R.id.ic_search:
                buscarCliente();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mgoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mgoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mgoogleApiClient.stopAutoManage(getActivity());
        mgoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
            Toast.makeText(getContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
        }else{
            setearClientes();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setearClientes(){
        aBusqueda = bd.obtenerClientesCercanos(mLastLocation);
        //Log.d("loca", String.valueOf(mLastLocation.getLatitude()));
        if(aBusqueda.size() > 0) {
            names=new ArrayList<>();//lista a mostra
            for (int i=0;i<aBusqueda.size();i++){
                //Toast.makeText(getContext(),aBusqueda.get(i).getNombre(), Toast.LENGTH_LONG).show();

                //Toast.makeText(getContext(), "Se encontro", Toast.LENGTH_LONG).show();


                names.add(aBusqueda.get(i).getNombre());
                // Log.d("local_cliet",aBusqueda.get(i).getNombre());

                //Log.d("clientes", String.valueOf(names));
                txtSinClientes.setVisibility(View.INVISIBLE);
                lvClientesCercas.setVisibility(View.VISIBLE);
                // ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,names);
                adapter_busqueda=new adapter_busqueda(getContext(),R.layout.item_cliente,names);
                lvClientesCercas.setAdapter(adapter_busqueda);
                set_cliente_venta();
                // adapter_busqueda.refreshEvents(names);
            }
        }else{
            txtSinClientes.setVisibility(View.VISIBLE);
            lvClientesCercas.setVisibility(View.GONE);
        }
    }//cambio metodo

    private void buscarCliente(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                (getContext().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.item_buscar_cliente, null);
        final EditText edtNombreCliente = view.findViewById(R.id.edtNombreCliente);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(edtNombreCliente.getText().length() > 0){

                            aBusqueda = bd.obtenerClientesPorNombre(edtNombreCliente.getText().toString());
                            if(aBusqueda.size() > 0){
                                names=new ArrayList<>();//lista a mostra
                                for (int i=0;i<aBusqueda.size();i++){
                                    //Toast.makeText(getContext(),aBusqueda.get(i).getNombre(), Toast.LENGTH_LONG).show();

                                    //Toast.makeText(getContext(), "Se encontro", Toast.LENGTH_LONG).show();




                                    names.add(aBusqueda.get(i).getNombre());

                                    Log.d("adaptador", String.valueOf(aBusqueda));
                                    txtSinClientes.setVisibility(View.INVISIBLE);
                                    lvClientesCercas.setVisibility(View.VISIBLE);
                                    // ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,names);
                                    adapter_busqueda=new adapter_busqueda(getContext(),R.layout.item_cliente,names);
                                    lvClientesCercas.setAdapter(adapter_busqueda);
                                    set_cliente_venta();
                                }

                            }

                            else{
                                Toast.makeText(getContext(), "Busqueda sin resultados", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }//cambio metodo

    public void set_cliente_venta(){
        lvClientesCercas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cliente cliente=bd.obtenerClientebyname(names.get(position));
                //Toast.makeText(getContext(),names.get(position),Toast.LENGTH_SHORT).show();

                mostrarVenta(cliente);


            }
        });
    }//cambio metodo

    private void mostrarVenta(final cliente cliente){
        Intent intent = new Intent(getContext(), venta.class);
        intent.putExtra(RESULTADO, cliente);
        startActivity(intent);
        this.onDestroy();
    }
}
