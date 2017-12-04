package delgado.drinkmixer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    EditText nameText;
    ListView listView;
    ProgressBar progressBar;
    static final String API_KEY = "1";
    static final String API_URL = "http://www.thecocktaildb.com/api/json/v1/";
    DrinkAdapter adapter;
    List<Drink> drinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        listView = findViewById(R.id.listView);
        nameText = findViewById(R.id.nameText);
        progressBar = findViewById(R.id.progressBar);
        adapter = new DrinkAdapter(this, R.layout.activity_listview, drinks);
        listView.setAdapter(adapter);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
                drinks.clear();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, DrinkDetailsActivity.class);
                Drink drink = drinks.get(position);
                intent.putExtra("drink", drink);
                startActivity(intent);
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

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
