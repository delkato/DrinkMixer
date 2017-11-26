package delgado.drinkmixer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DrinkDetailsActivity extends AppCompatActivity {
    ImageView drink_image;
    TextView ingredient1;
    TextView ingredient2;
    TextView ingredient3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_details);

        Drink drink = (Drink) getIntent().getSerializableExtra("drink");
        getSupportActionBar().setTitle(drink.getName());

        drink_image = findViewById(R.id.drink_image);
        ingredient1 = findViewById(R.id.ingredient1);
        ingredient2 = findViewById(R.id.ingredient2);
        ingredient3 = findViewById(R.id.ingredient3);

        Picasso.with(this).load(drink.getThumbnail()).into(drink_image);
        ingredient1.setText(drink.getMeasure1() + " " + drink.getIngredient1());
        ingredient2.setText(drink.getMeasure2() + " " + drink.getIngredient2());
        ingredient3.setText(drink.getMeasure3() + " " + drink.getIngredient3());
    }
}
