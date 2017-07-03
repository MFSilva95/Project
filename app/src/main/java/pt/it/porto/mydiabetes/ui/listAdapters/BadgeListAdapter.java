package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.LinkedList;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;


public class BadgeListAdapter extends BaseAdapter {

    Context _c;
    private LinkedList<BadgeRec> _data;

    public BadgeListAdapter(LinkedList<BadgeRec> data, Context c) {
        _data = data;
        _c = c;
    }


    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_badge_row, parent, false);
        }

        TextView date = (TextView) v.findViewById(R.id.date);
        TextView type = (TextView) v.findViewById(R.id.type);
        ImageView image = (ImageView) v.findViewById(R.id.image);

        BadgeRec badge = _data.get(position);
        date.setText(badge.getFormattedDate());


        if(badge.getType().equals("daily")){
            type.setText(R.string.daily);
            if(badge.getMedal().equals("bronze")){
                image.setImageResource(R.drawable.medal_bronze_daily);
            }
            if(badge.getMedal().equals("silver")){
                image.setImageResource(R.drawable.medal_silver_daily);
            }
            if(badge.getMedal().equals("gold")){
                image.setImageResource(R.drawable.medal_gold_daily);
            }
        }

        if(badge.getType().equals("beginner")){
            type.setText(R.string.beginner);
            if(badge.getName().equals("photo")){
                image.setImageResource(R.drawable.medal_gold_profile);
            }
            if(badge.getName().equals("export")){
                image.setImageResource(R.drawable.medal_gold_save);
            }
            if(badge.getName().equals("log")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_log);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_log);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_log);
            }

            if(badge.getName().equals("exercise")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_exercise);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_exercise);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_exercise);
            }

            if(badge.getName().equals("disease")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_disease);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_disease);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_disease);
            }

            if(badge.getName().equals("weight")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_weight);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_weight);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_weight);
            }

            if(badge.getName().equals("bp")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_bp);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_bp);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_bp);
            }

            if(badge.getName().equals("cholesterol")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_cholesterol);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_cholesterol);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_cholesterol);
            }

            if(badge.getName().equals("hba1c")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_hba1c);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_hba1c);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_hba1c);
            }
        }

        if(badge.getType().equals("medium")){
            type.setText(R.string.medium);
            if(badge.getName().equals("log")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_log_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_log_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_log_m);
            }

            if(badge.getName().equals("exercise")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_exercise_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_exercise_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_exercise_m);
            }

            if(badge.getName().equals("disease")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_disease_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_disease_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_disease_m);
            }

            if(badge.getName().equals("weight")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_weight_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_weight_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_weight_m);
            }

            if(badge.getName().equals("bp")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_bp_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_bp_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_bp_m);
            }

            if(badge.getName().equals("cholesterol")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_cholesterol_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_cholesterol_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_cholesterol_m);
            }

            if(badge.getName().equals("hba1c")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_hba1c_m);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_hba1c_m);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_hba1c_m);
            }
        }

        if(badge.getType().equals("advanced")){
            type.setText(R.string.advanced);
            if(badge.getName().equals("log")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_log_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_log_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_log_a);
            }

            if(badge.getName().equals("exercise")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_exercise_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_exercise_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_exercise_a);
            }

            if(badge.getName().equals("disease")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_disease_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_disease_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_disease_a);
            }

            if(badge.getName().equals("weight")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_weight_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_weight_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_weight_a);
            }

            if(badge.getName().equals("bp")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_bp_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_bp_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_bp_a);
            }

            if(badge.getName().equals("cholesterol")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_cholesterol_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_cholesterol_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_cholesterol_a);
            }

            if(badge.getName().equals("hba1c")){
                if(badge.getMedal().equals("bronze"))
                    image.setImageResource(R.drawable.medal_bronze_hba1c_a);
                if(badge.getMedal().equals("silver"))
                    image.setImageResource(R.drawable.medal_silver_hba1c_a);
                if(badge.getMedal().equals("gold"))
                    image.setImageResource(R.drawable.medal_gold_hba1c_a);
            }
        }

        return v;
    }

}
