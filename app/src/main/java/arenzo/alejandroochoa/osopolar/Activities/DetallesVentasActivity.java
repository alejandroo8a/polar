package arenzo.alejandroochoa.osopolar.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class DetallesVentasActivity extends AppCompatActivity {
    TextView textproducto, textcantidad, textprecio, textsub, texttotal;
    private baseDatos bd;
    Button cancelar;
    private ArrayList<ventaDetalle> aVentas;
    int id_producto;
    int id_venta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ventas);
        centrarTituloActionBar();
        cargar_Vista();
        obtenerdetalle();

    }

    private void cargar_Vista() {
        textproducto = (TextView) findViewById(R.id.textproducto);
        textcantidad = (TextView) findViewById(R.id.textcantidad);
        textprecio = (TextView) findViewById(R.id.textprecio);
        textsub = (TextView) findViewById(R.id.textsub);
        texttotal = (TextView) findViewById(R.id.texttotal);
        cancelar = (Button) findViewById(R.id.btnCancelarVenta);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarVenta();
            }
        });

        bd = new baseDatos(getApplicationContext());


        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {//cambio
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent favoritos = new Intent(DetallesVentasActivity.this, HistorialActivity.class);
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

    public void obtenerdetalle() {
         id_producto = getIntent().getExtras().getInt("id_producto");
        aVentas=  bd.obtenerVentaDetallebyId(id_producto);
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        int suma = 0;

        for (int i = 0; i < aVentas.size(); i++) {
            if (aVentas.size() > 0) {
                suma += aVentas.get(i).getSubtotal();
                double precios=aVentas.get(i).getSubtotal()/aVentas.get(i).getCantidad();

                textproducto.setText(aVentas.get(i).getProducto());
                textcantidad.setText(aVentas.get(i).getCantidad().toString());
                textprecio.setText("$"+formatea.format(precios));
                textsub.setText("$"+formatea.format(aVentas.get(i).getSubtotal()));
                texttotal.setText("$"+formatea.format(aVentas.get(i).getSubtotal()));
            }

        }
    }

    public void  borrarVenta(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atención")
                .setMessage("¿Está seguro de Eliminar la venta?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finalizarVenta(false);
                        id_venta = getIntent().getExtras().getInt("id_venta");
                        bd.borrarventa(id_venta);

                        toHistorial();
                        Toast.makeText(DetallesVentasActivity.this,"Venta Eliminada",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
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

    public  void toHistorial(){
        // ProjectsActivity is my 'home' activity
        Intent favoritos=new Intent(DetallesVentasActivity.this,MainActivity.class);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(favoritos);
        finish();
    }






}

