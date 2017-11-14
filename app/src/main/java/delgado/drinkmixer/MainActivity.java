package delgado.drinkmixer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    private final String DEVICE_ADDRESS="00:06:66:DC:84:5E";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    EditText nameText;
    ListView listView;
    ProgressBar progressBar;
    static final String API_KEY = "1";
    static final String API_URL = "http://www.thecocktaildb.com/api/json/v1/";
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean found;
    private boolean connected = false;
    DrinkAdapter adapter;
    List<Drink> drinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        listView = (ListView) findViewById(R.id.listView);
        nameText = (EditText) findViewById(R.id.nameText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        adapter = new DrinkAdapter(this, R.layout.activity_listview, drinks);
        listView.setAdapter(adapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
                drinks.clear();
            }
        });
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
        ArrayList list = new ArrayList();
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
                socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
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

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            String name = nameText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL(API_URL + API_KEY + "/search.php?s=" + name);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "There was a problem retrieving data from the database.";
            }
            progressBar.setVisibility(View.GONE);
            //Log.i("INFO", response);
            //responseView.setText(response);
            try {
                JSONArray drinksJSONArray = ((JSONObject) new JSONTokener(response).nextValue()).getJSONArray("drinks");
                for (int i = 0; i < drinksJSONArray.length(); i++) {
                    Drink drink = new Drink(drinksJSONArray.getJSONObject(i));
                    drinks.add(drink);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
