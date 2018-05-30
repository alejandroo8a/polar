package arenzo.alejandroochoa.osopolar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.osopolar.Adapters.adapter_producto_detalle;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_ventas;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class ProductoDetalleActivity extends AppCompatActivity {
    private baseDatos bd;
    private SharedPreferences pref;
    ListView listProductos;
    TextView txtmensaje;
    int id_venta;
    private ArrayList<producto> aproductos;
    private adapter_producto_detalle adapter_producto_detalle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);
        centrarTituloActionBar();

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.show();

        cargarvista();
        setearProductos();
        set_productos();
    }

    public void cargarvista(){
        listProductos=(ListView)findViewById(R.id.listproducto);
        txtmensaje=(TextView)findViewById(R.id.txtSinProductos);
        bd = new baseDatos(getApplicationContext());
       pref=getSharedPreferences("venta", Context.MODE_PRIVATE);//accedo al shardpreference

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


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {//cambio
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent favoritos=new Intent(ProductoDetalleActivity.this,HistorialActivity.class);
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


    public void setearProductos(){
        id_venta = getIntent().getExtras().getInt("id_venta");
        aproductos=bd.obtenerProductosVenta(id_venta);
        //oVenta venta = new oVenta();

        Log.d("lista",aproductos.toString());

        if(aproductos.size() > 0) {
//            Toast.makeText(ProductoDetalleActivity.this,aproductos.get(1).getNombre(),Toast.LENGTH_SHORT).show();
            adapter_producto_detalle=new adapter_producto_detalle(ProductoDetalleActivity.this,R.layout.item_detalle_producto,aproductos);
            listProductos.setAdapter(adapter_producto_detalle);

        }
        else {
            txtmensaje.setVisibility(View.VISIBLE);
            listProductos.setVisibility(View.GONE);
            Toast.makeText(ProductoDetalleActivity.this,"Vacio",Toast.LENGTH_SHORT).show();
        }
    }


    public void set_productos(){

        listProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(ProductoDetalleActivity.this,aproductos.get(position).getIdProducto().toString(),Toast.LENGTH_SHORT).show();

                //mostrarVenta(cliente);

                toDetalle(aproductos.get(position).getIdProducto());


            }
        });
    }//cambio metodo


    public void toDetalle(int id_producto){
        // ProjectsActivity is my 'home' activity
        set_venta_id();
        Intent favoritos=new Intent(ProductoDetalleActivity.this,DetallesVentasActivity.class);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        favoritos.putExtra("id_producto", id_producto);
        favoritos.putExtra("id_venta", id_venta);
        startActivity(favoritos);
        finish();


    }

    public  void set_venta_id(){
        SharedPreferences.Editor editor=pref.edit();
        editor.putInt("venta",id_venta);

        editor.commit();//para que el pref se guade
        editor.apply();//sigue el codigo
    }


}
