package arenzo.alejandroochoa.osopolar.ClasesBase;

/**
 * Created by AlejandroMissael on 27/04/2017.
 */

public class ventaDetalle {

    private Integer idVenta;
    private Integer idProducto;
    private Float cantidad;
    private Double pUnitario;
    private Double subtotal;
    private  Integer credito;
    public String Producto;

    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    public Integer getCredito() {
        return credito;
    }

    public void setCredito(Integer credito) {
        this.credito = credito;
    }
    //private String precio_venta;

    private boolean sincronizado;
    //private Double precio_vendido;

    public ventaDetalle() {
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Double getpUnitario() {
        return pUnitario;
    }

    public void setpUnitario(Double pUnitario) {
        this.pUnitario = pUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}
