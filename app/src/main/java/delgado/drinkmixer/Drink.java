package delgado.drinkmixer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Kath on 11/13/2017.
 */

public class Drink implements Serializable {
    private String name;
    private String thumbnail;
    private String ingredient1;
    private String ingredient2;
    private String ingredient3;
    private String measure1;
    private String measure2;
    private String measure3;
    private String instructions;

    public Drink() {}

    public Drink(JSONObject json) throws JSONException {
        this.name = json.getString("strDrink");
        this.thumbnail = json.getString("strDrinkThumb");
        this.ingredient1 = json.getString("strIngredient1");
        this.ingredient2 = json.getString("strIngredient2");
        this.ingredient3 = json.getString("strIngredient3");
        this.measure1 = json.getString("strMeasure1");
        this.measure2 = json.getString("strMeasure2");
        this.measure3 = json.getString("strMeasure3");
        this.instructions = json.getString("strInstructions");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIngredient1() {
        return ingredient1;
    }

    public void setIngredient1(String ingredient1) {
        this.ingredient1 = ingredient1;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public void setIngredient2(String ingredient2) {
        this.ingredient2 = ingredient2;
    }

    public String getIngredient3() {
        return ingredient3;
    }

    public void setIngredient3(String ingredient3) {
        this.ingredient3 = ingredient3;
    }

    public String getMeasure1() {
        return measure1;
    }

    public void setMeasure1(String measure1) {
        this.measure1 = measure1;
    }

    public String getMeasure2() {
        return measure2;
    }

    public void setMeasure2(String measure2) {
        this.measure2 = measure2;
    }

    public String getMeasure3() {
        return measure3;
    }

    public void setMeasure3(String measure3) {
        this.measure3 = measure3;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", ingredient1='" + ingredient1 + '\'' +
                ", ingredient2='" + ingredient2 + '\'' +
                ", ingredient3='" + ingredient3 + '\'' +
                ", measure1='" + measure1 + '\'' +
                ", measure2='" + measure2 + '\'' +
                ", measure3='" + measure3 + '\'' +
                ", instructions='" + instructions + '\'' +
                '}';
    }
}
