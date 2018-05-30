package arenzo.alejandroochoa.osopolar.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.osopolar.Adapters.adapter_ventas;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class HistorialActivity extends AppCompatActivity {


    ListView listhistorial;
    TextView txtTotal,txtContado,txtCredito,txtmensaje;
    private baseDatos bd;
    List<String> names;
    //ArrayList<oVenta> aVentas = new ArrayList<>();
    private ArrayList<oVenta> aVentas;
    private adapter_ventas adapter_busqueda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        //this.setTitle("Mis Ventas");
        centrarTituloActionBar();

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.show();
        cargarvista();
        setTotal();
        setTotalContado();
        setTotalCredito();
        setearVentas();
        set_ventas();


    }

    public void cargarvista(){
        txtTotal=(TextView)findViewById(R.id.txtTota);
        txtContado=(TextView)findViewById(R.id.txtCredito);
        txtCredito=(TextView)findViewById(R.id.txtContado);
        listhistorial=(ListView)findViewById(R.id.listhistorial);
        txtmensaje=(TextView)findViewById(R.id.txtSinClientes);
        bd = new baseDatos(getApplicationContext());
    }

    public void setearVentas(){
        aVentas=bd.obtenerVentasHistorial();
        //oVenta venta = new oVenta();

        Log.d("lista",aVentas.toString());
        if(aVentas.size() > 0) {
            //adapter_busqueda=new adapter_ventas(HistorialActivity.this,R.layout.item_cliente,aVentas);
            //listhistorial.setAdapter(adapter_busqueda);

           /* for (int i=0;i<aVentas.size();i++){

                names=new ArrayList<>();//lista a mostra

                names.add(aVentas.get(i).getIdCliente().toString());

                //venta.setCredito(0);



                //Log.d("lista",ventas.toString());
                adapter_busqueda=new adapter_ventas(HistorialActivity.this,R.layout.item_cliente,names);
                listhistorial.setAdapter(adapter_busqueda);
            }*/


            adapter_busqueda=new adapter_ventas(HistorialActivity.this,R.layout.item_historial,aVentas);
            listhistorial.setAdapter(adapter_busqueda);


        }
        else {
            txtmensaje.setVisibility(View.VISIBLE);
            listhistorial.setVisibility(View.GONE);
        }
    }

    public void set_ventas(){

        listhistorial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(HistorialActivity.this,aVentas.get(position).getIdVenta().toString(),Toast.LENGTH_SHORT).show();

                //mostrarVenta(cliente);
                goToDetalle(aVentas.get(position).getIdVenta());

            }
        });
    }//cambio metodo

    public void setTotal(){


        //Log.d("mis ventas",aVentas.toString());
        double total= bd.obtenerMontoVentasTotal();
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat formatea = new DecimalFormat("###,###.##");

        if (total<=0.0){
            txtTotal.setText("TOTAL"+" "+"$"+" "+"0.00");
        }
        else{
            txtTotal.setText("TOTAL"+" "+"$"+" "+formatea.format(total));
        }

    }

    public void setTotalCredito(){
        double totalCredito= bd.obtenerMontoVentasCredito();
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        Log.d("creditos",String.valueOf(totalCredito));
        if (totalCredito<=0.0){
            txtCredito.setText("CRÉDITO"+" "+"$"+" "+"0.00");
        }
        else{
            txtCredito.setText("CRÉDITO"+" "+"$"+" "+formatea.format(totalCredito));
        }

    }

    public void setTotalContado(){
        double totalContado= bd.obtenerMontoVentasContado();
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        Log.d("contado",String.valueOf(totalContado));
        if (totalContado<=0.0){
            txtContado.setText("CONTADO"+" "+"$"+" "+"0.00");
        }
        else{
            txtContado.setText("CONTADO"+" "+"$"+" "+formatea.format(totalContado));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {//cambio
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent favoritos=new Intent(HistorialActivity.this,MainActivity.class);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(favoritos);
                finish();

                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void goToDetalle(int id_venta){
        // ProjectsActivity is my 'home' activity
        Intent favoritos=new Intent(HistorialActivity.this,DetallesVentasActivity.class);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        favoritos.putExtra("id_venta", id_venta);
        startActivity(favoritos);
        finish();
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



}


