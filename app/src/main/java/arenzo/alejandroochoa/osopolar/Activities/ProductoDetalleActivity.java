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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class ProductoDetalleActivity extends AppCompatActivity {
    private baseDatos bd;

    ListView listProductos;
    private ArrayList<producto> aproductos;
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
    }

    public void cargarvista(){
        listProductos=(ListView)findViewById(R.id.listproducto);

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
        //aproductos=bd.obtenerVentasHistorial();
        //oVenta venta = new oVenta();

        //Log.d("lista",aVentas.toString());
    }
}
