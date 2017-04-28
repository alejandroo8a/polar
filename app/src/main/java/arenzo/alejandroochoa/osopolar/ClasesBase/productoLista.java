package arenzo.alejandroochoa.osopolar.ClasesBase;

/**
 * Created by AlejandroMissael on 27/04/2017.
 */

public class productoLista {

    private Integer idProducto;
    private Integer idListaPrecio;
    private Double precio;

    public productoLista() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdListaPrecio() {
        return idListaPrecio;
    }

    public void setIdListaPrecio(Integer idListaPrecio) {
        this.idListaPrecio = idListaPrecio;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
