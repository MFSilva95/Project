package pt.it.porto.mydiabetes.ui.fragments.badges;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.BadgeListAdapter_old;

/**
 * Created by parra on 21/02/2017.
 */

public class badgesList extends Fragment {
    private View layout;
    private ListView list;

    public static pt.it.porto.mydiabetes.ui.fragments.badges.badgesList newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.badges.badgesList fragment = new pt.it.porto.mydiabetes.ui.fragments.badges.badgesList();
        return fragment;
    }

    public badgesList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_badges_list, container, false);
        list = (ListView) layout.findViewById(R.id.list);
        DB_Read db = new DB_Read(getContext());
        LinkedList<BadgeRec> badgeList = db.Badges_GetAll_NONDAILY();
        db.close();

        list.setAdapter(new BadgeListAdapter_old(badgeList, getContext()));
        list.setEmptyView(layout.findViewById(R.id.list_empty));

        return layout;
    }
}
