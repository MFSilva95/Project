package com.jadg.mydiabetes.ui.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jadg.mydiabetes.ui.activities.DiseasesDetail;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.ui.listAdapters.DiseaseAdapter;
import com.jadg.mydiabetes.ui.listAdapters.DiseaseDataBinding;

import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * @link Diseases.OnFragmentInteractionListener interface to handle
 * interaction events. Use the @link Diseases#newInstance factory method to
 * create an instance of this fragment.
 */


public class Diseases extends Fragment {

    ListView diseaseList;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater diseasesmenu = getActivity().getMenuInflater();
        diseasesmenu.inflate(R.menu.diseases_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_diseases, null);

        diseaseList = (ListView) v.findViewById(R.id.diseasesFragmentList);

        fillListView(diseaseList);

        return v;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_DiseasesFragment_Add:
                Intent intent = new Intent(this.getActivity(), DiseasesDetail.class);
                startActivity(intent);
                //showDiseaseDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        diseaseList = (ListView)super.getActivity().findViewById(R.id.diseasesFragmentList);
        fillListView(diseaseList);
    }

    public void fillListView(ListView lv) {

        DB_Read rdb = new DB_Read(getActivity());
        ArrayList<DiseaseDataBinding> allDiseases = rdb.Disease_GetAll();
        rdb.close();


        lv.setAdapter(new DiseaseAdapter(allDiseases, getActivity()));
    }

}
