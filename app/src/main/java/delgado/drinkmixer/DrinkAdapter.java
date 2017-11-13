package delgado.drinkmixer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class DrinkAdapter extends ArrayAdapter {
    private final Activity activity;
    private final int resource;
    private final List<Drink> drinks;

    public DrinkAdapter(@NonNull Activity activity, int resource, @NonNull List<Drink> drinks) {
        super(activity, resource, drinks);
        this.activity = activity;
        this.resource = resource;
        this.drinks = drinks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if(rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(resource, null);

            view = new ViewHolder();
            view.drinkName= (TextView) rowView.findViewById(R.id.drink_name);
            view.drinkThumbnail= (ImageView) rowView.findViewById(R.id.drink_icon);
            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }


        String someDrinkName = drinks.get(position).getName();
        String someDrinkURL = drinks.get(position).getThumbnail();
        view.drinkName.setText(someDrinkName);
        Picasso.with(activity).load(someDrinkURL).into(view.drinkThumbnail);
        return rowView;
    }

    protected static class ViewHolder{
        protected TextView drinkName;
        protected ImageView drinkThumbnail;
    }
}