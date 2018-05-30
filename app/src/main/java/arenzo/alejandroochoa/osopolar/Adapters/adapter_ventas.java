package arenzo.alejandroochoa.osopolar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.R;

public class adapter_ventas  extends BaseAdapter {
    //cambio

    private Context context;
    private int layout;
    private List<oVenta> aventas;

    public adapter_ventas(Context context,int layout,List<oVenta>aventas){
        this.context=context;
        this.aventas=aventas;
        this.layout=layout;
    }

    @Override
    public int getCount() {
        return aventas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View holder pattern
        viewHolder holder;
        if (convertView == null){
            //Objeto que infla la vista
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(this.layout, null);
            holder = new viewHolder();
            holder.txtNombre = (TextView)convertView.findViewById(R.id.textcliente);
            holder.txttotal = (TextView)convertView.findViewById(R.id.texttotal);
            holder.txtfecha = (TextView)convertView.findViewById(R.id.txtfecha);
            holder.txtCreditos = (TextView)convertView.findViewById(R.id.txt_creditos);
            convertView.setTag(holder);
        }else{
            //Con esto se obtiene lo que se le puso de tag
            holder = (viewHolder) convertView.getTag();
        }
        //Se rellenan los datos
        String currentName = aventas.get(position).getTotal().toString();

        if (aventas.get(position).getIdCliente()==0){
            holder.txtNombre.setText("Cliente Nuevo");
        }
        else{
            holder.txtNombre.setText(aventas.get(position).getCliente());
        }




        holder.txtfecha.setText(aventas.get(position).getFecha());
        if (aventas.get(position).getCredito()>=1){
            holder.txtCreditos.setText("Crédito");
        }
        else {
            holder.txtCreditos.setText("Contado");
        }
       // holder.txtNombre.setText(aventas.get(position).getIdCliente().toString());
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        holder.txttotal.setText("Total"+" "+"$"+formatea.format(aventas.get(position).getTotal()));
        return convertView;
    }

    static class viewHolder{
        private TextView txtNombre;
        private TextView txttotal;
        private TextView txtfecha;
        private TextView txtCreditos;
    }

    public void refreshEvents(List<oVenta> aventas) {
        this.aventas.clear();
        this.aventas.addAll(aventas);
        notifyDataSetChanged();
    }

}
