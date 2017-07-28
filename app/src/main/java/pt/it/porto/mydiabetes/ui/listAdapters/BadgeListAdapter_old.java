package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.LinkedList;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;


public class BadgeListAdapter_old extends BaseAdapter {

    Context _c;
    private LinkedList<BadgeRec> _data;

    public BadgeListAdapter_old(LinkedList<BadgeRec> data, Context c) {
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
        ImageView imageBadge = (ImageView) v.findViewById(R.id.badgeType);

        BadgeRec badge = _data.get(position);
        date.setText(badge.getFormattedDate());


        // Type -> daily, [beginner, ...]
        // Medal -> bronze, silver, gold
        // ID -> randomID
        // Name -> photo, BP, etc

        String medalType = "medal_"+badge.getMedal()+"_"+badge.getType();
        String badgeType = badge.getName();

        Log.i("cenas", "MEDAL getType: "+badge.getType()+" getMedal:"+badge.getMedal()+" getID:"+badge.getId());
        Log.i("cenas", "MEDAL getName: "+badge.getName());
        try{
            image.setImageResource(_c.getResources().getIdentifier(medalType,"drawable",_c.getPackageName()));
            imageBadge.setImageResource(_c.getResources().getIdentifier(badgeType,"drawable",_c.getPackageName()));
        }catch (Exception e){
            Log.i("cemas", "MISSING:"+badgeType);
            e.printStackTrace();
        }
        return v;
    }

}
