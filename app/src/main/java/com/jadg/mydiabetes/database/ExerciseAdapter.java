package com.jadg.mydiabetes.database;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jadg.mydiabetes.ExercisesDetail;
import com.jadg.mydiabetes.R;

import java.util.ArrayList;


public class ExerciseAdapter extends BaseAdapter {

    private ArrayList<ExerciseDataBinding> _data;
    Context _c;

    public ExerciseAdapter(ArrayList<ExerciseDataBinding> data, Context c) {
        _data = data;
        _c = c;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_exercise_row, null);
        }

        LinearLayout lLayout = (LinearLayout) v.findViewById(R.id.ExercisesRow);

        TextView exerciseName = (TextView) v.findViewById(R.id.list_exerciseName);

        final ExerciseDataBinding exercise = _data.get(position);
        String _id = "" + exercise.getId();
        exerciseName.setTag(_id);
        exerciseName.setText(exercise.getName());

        lLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(v.getContext(), ExercisesDetail.class);
                Bundle args = new Bundle();
                args.putString("Id", String.valueOf(exercise.getId()));
                args.putString("Name", String.valueOf(exercise.getName()));

                intent.putExtras(args);
                v.getContext().startActivity(intent);
            }
        });

        return v;
    }

}
