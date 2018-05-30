package arenzo.alejandroochoa.osopolar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by manuel on 03/02/2018.
 */

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private final String RESULTADO = "RESULTADO";
    private final static String TAG = "Scaner";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("contenido", rawResult.getContents()); // Prints scan results
        String validador =rawResult.getContents().toString();

        Log.v("nombre", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        cliente cliente;
        if (rawResult!=null){
            if (rawResult.getContents()!=null){

                if (!rawResult.equals("")){


                baseDatos bd = new baseDatos(getApplicationContext());
                    if (validador.matches("\\d*")){
                        cliente = bd.obtenerClientePorId(Integer.parseInt(rawResult.getContents()));;//crear un cliente nuevo

                        if (cliente.getIdCliente()!=null){
                            Intent intent = new Intent(SimpleScannerActivity.this, venta.class);
                            intent.putExtra(RESULTADO,cliente);
                            startActivity(intent);
                        }

                        else{
                            Toast.makeText(SimpleScannerActivity.this,"Cliente no encontrado",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SimpleScannerActivity.this, escaneo.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            //intent.putExtra(RESULTADO, cliente);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(SimpleScannerActivity.this,"Cliente no encontrado",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SimpleScannerActivity.this, escaneo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        //intent.putExtra(RESULTADO, cliente);
                        startActivity(intent);
                        finish();
                    }




                }
            }
        }
        else{
            Toast.makeText(SimpleScannerActivity.this,"Codigo Vacio o incorrecto",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SimpleScannerActivity.this, escaneo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

            startActivity(intent);
            finish();
        }




        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}
