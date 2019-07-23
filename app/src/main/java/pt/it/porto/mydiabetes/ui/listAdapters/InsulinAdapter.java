package pt.it.porto.mydiabetes.ui.listAdapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.ui.activities.InsulinsDetail;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Insulin;


public class InsulinAdapter extends BaseAdapter {
    private ArrayList<Insulin> _data;
    Context _c;

    public InsulinAdapter(ArrayList<Insulin> data, Context c) {
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
            v = vi.inflate(R.layout.list_insulin_row, parent, false);
        }


        LinearLayout rLayout = (LinearLayout) v.findViewById(R.id.InsulinsRow);


        TextView iname = (TextView) v.findViewById(R.id.list_insulinName);
        TextView itype = (TextView) v.findViewById(R.id.list_insulinType);
        TextView iaction = (TextView) v.findViewById(R.id.list_insulinAction);
        //TextView iduration = (TextView)v.findViewById(R.id.list_insulinDuration);


        final Insulin insulin = _data.get(position);
        final String _id = "" + insulin.getId();
        iname.setText(insulin.getName());
        iname.setTag(_id);


        int indexAction = 0; //note that below can give and error and we'll get position 0 (-1) could throw error
        try {
			indexAction = Integer.parseInt(insulin.getAction());
		} catch (NumberFormatException nfe) {
			// index will get 0
			Log.e ("getView", "Read a text that was not a number from action"+ nfe);
		}
        Resources res = v.getContext().getResources();
        String[] actions = res.getStringArray(R.array.insulin_action);

        iaction.setText(actions[indexAction]);
        //iduration.setText(insulin.getDuration().toString());

        int indexType = 0; //note that below can give and error and we'll get position 0 (-1) could throw error
        try {
            indexType = Integer.parseInt(insulin.getType());
        } catch (NumberFormatException nfe) {
            // index will get 0
            Log.e ("getView", "Read a text that was not a number from action"+ nfe);
        }
        String[] types = res.getStringArray(R.array.insulin_type);
        itype.setText(types[indexType]);

        rLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(v.getContext(), InsulinsDetail.class);
                Bundle args = new Bundle();
                args.putString("Id", String.valueOf(insulin.getId()));

                intent.putExtras(args);
                v.getContext().startActivity(intent);
            }
        });

        return v;
    }

}
