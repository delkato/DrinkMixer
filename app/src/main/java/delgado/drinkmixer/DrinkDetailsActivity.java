package delgado.drinkmixer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DrinkDetailsActivity extends AppCompatActivity {
    ImageView drink_image;
    TextView ingredient1;
    TextView ingredient2;
    TextView ingredient3;
    private final String DEVICE_ADDRESS="00:06:66:DC:84:5E";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket socket;
    OutputStream outputStream;
    private InputStream inputStream;
    private boolean found;
    private boolean connected = false;
    private String measure1;
    private String measure2;
    private String measure3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_details);

        Drink drink = (Drink) getIntent().getSerializableExtra("drink");
        getSupportActionBar().setTitle(drink.getName());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        drink_image = findViewById(R.id.drink_image);
        ingredient1 = findViewById(R.id.ingredient1);
        ingredient2 = findViewById(R.id.ingredient2);
        ingredient3 = findViewById(R.id.ingredient3);

        Picasso.with(this).load(drink.getThumbnail()).into(drink_image);
        ingredient1.setText(drink.getMeasure1() + " " + drink.getIngredient1());
        ingredient2.setText(drink.getMeasure2() + " " + drink.getIngredient2());
        ingredient3.setText(drink.getMeasure3() + " " + drink.getIngredient3());

        measure1 = convertMeasureToMilliliters(drink.getMeasure1());
        measure2 = convertMeasureToMilliliters(drink.getMeasure2());
        measure3 = convertMeasureToMilliliters(drink.getMeasure3());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bluetooth) {
            toggleBluetooth();
            return true;
        }
        return false;
    }

    private void toggleBluetooth() {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice device = null;

        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                if (bt.getAddress().equals(DEVICE_ADDRESS)) {
                    device = bt;
                    found = true;
                    break;
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_SHORT).show();
            found = false;
        }
        if (found && !connected) {
            connected = true;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
                socket.connect();
                Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                connected=false;
            }
            if (connected) {
                try {
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (found) {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
                Toast.makeText(getApplicationContext(), "Disconnected!", Toast.LENGTH_LONG).show();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertMeasureToMilliliters(String measure) {
        measure = measure.trim();

        double decimal;
        int mLs = 0;
        // check if we have one of the measures we're converting
        if (measure.contains("oz") || measure.contains("tsp") || measure.contains("tblsp") || measure.contains("cl") || measure.contains("cup")) {
            // check if we have a fraction in there
            if (measure.indexOf("/") > -1) {
                int counter = 0;
                for(int i = 0; i < measure.length(); i++) {
                    if( measure.charAt(i) == ' ') {
                        counter++;
                    }
                }
                // we have 2 spaces, so it's a mixed number
                if (counter > 1) {
                    int wholeNum = Integer.parseInt(measure.substring(0, measure.indexOf(" ")));
                    int numerator = Integer.parseInt(measure.substring(measure.indexOf(" ") + 1, measure.indexOf("/")));
                    int denominator = Integer.parseInt(measure.substring(measure.indexOf("/") + 1, measure.lastIndexOf(" ")));
                    double fraction = (double) numerator / denominator;
                    decimal = wholeNum + fraction;
                // we have 1 space, so it's just a fraction
                } else {
                    int numerator = Integer.parseInt(measure.substring(0, measure.indexOf("/")));
                    int denominator = Integer.parseInt(measure.substring(measure.indexOf("/") + 1, measure.lastIndexOf(" ")));
                    decimal = (double) numerator / denominator;
                }
            // get whole number since we just have a whole number measure
            } else {
                decimal = Integer.parseInt(measure.substring(0, measure.indexOf(" ")));
            }
            if (measure.contains("oz")) {
                mLs = (int) Math.round(decimal * 29.5735);
            } else if (measure.contains("tsp")) {
                mLs = (int) Math.round(decimal * 4.92892);
            } else if (measure.contains("tblsp")) {
                mLs = (int) Math.round(decimal * 14.7868);
            } else if (measure.contains("cl")) {
                mLs = (int) Math.round(decimal * 10);
            } else if (measure.contains("cup")) {
                mLs = (int) Math.round(decimal * 236.588);
            }
        }
        Log.i("mL conversion", Integer.toString(mLs));
        return Integer.toString(mLs);
    }

    public void sendDataToArduino(View view) {
        if (outputStream != null) {
            try {
                Log.i("OUTPUT STREAMIN", "hh");
                outputStream.write(("D" + measure1).getBytes());
                outputStream.write(("D" + measure2).getBytes());
                outputStream.write(("D" + measure3 + "E").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please connect to the Arduino via Bluetooth first!", Toast.LENGTH_LONG).show();
        }
    }
}
