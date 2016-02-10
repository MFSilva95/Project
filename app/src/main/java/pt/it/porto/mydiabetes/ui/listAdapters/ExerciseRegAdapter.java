package pt.it.porto.mydiabetes.ui.listAdapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.R;


public class ExerciseRegAdapter extends BaseAdapter {

	private ArrayList<ExerciseRegDataBinding> _data;
    Context _c;
    
    public ExerciseRegAdapter (ArrayList<ExerciseRegDataBinding> data, Context c){
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
         if (v == null)
         {
            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_exercisereg_row, parent, false);
         }
 
           TextView data = (TextView)v.findViewById(R.id.tv_list_exercisereg_data);
           TextView hora = (TextView)v.findViewById(R.id.tv_list_exercisereg_hora);
           TextView exercise = (TextView)v.findViewById(R.id.tv_list_exercisereg_exercise);
           TextView duration = (TextView)v.findViewById(R.id.tv_list_exercisereg_duration);
           TextView effort = (TextView)v.findViewById(R.id.tv_list_exercisereg_effort);
           

           ExerciseRegDataBinding exercise_datab = _data.get(position);
           String _id = ""+exercise_datab.getId();
           Log.d("id do exercicio", _id);
           data.setText(exercise_datab.getDate());
           hora.setText(exercise_datab.getTime());
           exercise.setText(exercise_datab.getExercise());
           duration.setText(String.valueOf(exercise_datab.getDuration()));
           effort.setText(exercise_datab.getEffort());
           v.setTag(_id);
           
           
           v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ExerciseDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag()); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        	   
           });
                                     
                        
        return v;
	}
	
}
