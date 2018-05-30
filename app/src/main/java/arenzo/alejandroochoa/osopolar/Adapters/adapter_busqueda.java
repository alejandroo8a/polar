package arenzo.alejandroochoa.osopolar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import arenzo.alejandroochoa.osopolar.R;

/**
 * Created by Jose Antonio on 15/02/2018.
 */
//adaptador agregado
public class adapter_busqueda extends  BaseAdapter {
    private Context context;
    private int layout;
    private List<String>nombres;

    public adapter_busqueda(Context context, int layout, List<String>nombres){//contructor para invocar la clase en otro lado
        this.context=context;
        this.layout=layout;
        this.nombres=nombres;
    }


    @Override
    public int getCount() {//cuantas veces interas de una
        return nombres.size();
    }

    @Override
    public Object getItem(int position) {//optienes un item de la la colecion
        return this.nombres.get(position);
    }

    @Override
    public long getItemId(int id) {//optienes el indice de la coleccion
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//aqui donde se
        ViewHolder viewHolder;

        if (convertView==null) {
            //inflamos la vista

            LayoutInflater layoutInflater=LayoutInflater.from(this.context);//creas para
            convertView=layoutInflater.inflate(this.layout,null);

            viewHolder=new ViewHolder();
            viewHolder.textView=convertView.findViewById(R.id.txtNombre); //casteo para darle diferencia del layout a java
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }



        String Currentname=nombres.get(position);//cada ves que valla inflando toma el valor de la lista

        viewHolder.textView.setText(Currentname);

        return convertView;


    }
    static class ViewHolder{
        private TextView textView;
    }

    public void refreshEvents(List<String> aClientes) {
        this.nombres.clear();
        this.nombres.addAll(aClientes);
        notifyDataSetChanged();
    }
}
