package pt.it.porto.mydiabetes.ui.fragments.badges;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;

/**
 * Created by parra on 21/02/2017.
 */

public class badgesGrid extends Fragment  {
    private View layout;
    private ImageView photoBadge;
    private ImageView exportBadge;
    private ImageView bronzeLogBadge;
    private ImageView silverLogBadge;
    private ImageView goldLogBadge;
    private ImageView bronzeExerciseBadge;
    private ImageView silverExerciseBadge;
    private ImageView goldExerciseBadge;
    private ImageView bronzeDiseaseBadge;
    private ImageView silverDiseaseBadge;
    private ImageView goldDiseaseBadge;
    private ImageView bronzeWeightBadge;
    private ImageView silverWeightBadge;
    private ImageView goldWeightBadge;
    private ImageView bronzeBpBadge;
    private ImageView silverBpBadge;
    private ImageView goldBpBadge;
    private ImageView bronzeCholesterolBadge;
    private ImageView silverCholesterolBadge;
    private ImageView goldCholesterolBadge;
    private ImageView bronzeHba1cBadge;
    private ImageView silverHba1cBadge;
    private ImageView goldHba1cBadge;

    public static pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid fragment = new pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid();
        return fragment;
    }

    public badgesGrid() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_badges_grid, container, false);

        photoBadge = (ImageView) layout.findViewById(R.id.photoBadge);
        exportBadge = (ImageView) layout.findViewById(R.id.exportBadge);
        bronzeLogBadge = (ImageView) layout.findViewById(R.id.bronzeLogBadge);
        silverLogBadge = (ImageView) layout.findViewById(R.id.silverLogBadge);
        goldLogBadge = (ImageView) layout.findViewById(R.id.goldLogBadge);
        bronzeExerciseBadge = (ImageView) layout.findViewById(R.id.bronzeExerciseBadge);
        silverExerciseBadge = (ImageView) layout.findViewById(R.id.silverExerciseBadge);
        goldExerciseBadge = (ImageView) layout.findViewById(R.id.goldExerciseBadge);
        bronzeDiseaseBadge = (ImageView) layout.findViewById(R.id.bronzeDiseaseBadge);
        silverDiseaseBadge = (ImageView) layout.findViewById(R.id.silverDiseaseBadge);
        goldDiseaseBadge = (ImageView) layout.findViewById(R.id.goldDiseaseBadge);
        bronzeWeightBadge = (ImageView) layout.findViewById(R.id.bronzeWeightBadge);
        silverWeightBadge = (ImageView) layout.findViewById(R.id.silverWeightBadge);
        goldWeightBadge = (ImageView) layout.findViewById(R.id.goldWeightBadge);
        bronzeBpBadge = (ImageView) layout.findViewById(R.id.bronzeBpBadge);
        silverBpBadge = (ImageView) layout.findViewById(R.id.silverBpBadge);
        goldBpBadge = (ImageView) layout.findViewById(R.id.goldBpBadge);
        bronzeCholesterolBadge = (ImageView) layout.findViewById(R.id.bronzeCholesterolBadge);
        silverCholesterolBadge = (ImageView) layout.findViewById(R.id.silverCholesterolBadge);
        goldCholesterolBadge = (ImageView) layout.findViewById(R.id.goldCholesterolBadge);
        bronzeHba1cBadge = (ImageView) layout.findViewById(R.id.bronzeHba1cBadge);
        silverHba1cBadge = (ImageView) layout.findViewById(R.id.silverHba1cBadge);
        goldHba1cBadge = (ImageView) layout.findViewById(R.id.goldHba1cBadge);

        photoBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        exportBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeLogBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverLogBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldLogBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeExerciseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverExerciseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldExerciseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeDiseaseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverDiseaseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldDiseaseBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeWeightBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverWeightBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldWeightBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeBpBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverBpBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldBpBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeCholesterolBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverCholesterolBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldCholesterolBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeHba1cBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverHba1cBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldHba1cBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));


        checkBadges();

        return layout;
    }

    public void checkBadges(){
        DB_Read db = new DB_Read(getContext());
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        db.close();
        for (BadgeRec rec: list) {
            if(rec.getName().equals("photo")){
                photoBadge.clearColorFilter();
            }

            if(rec.getName().equals("export")){
                exportBadge.clearColorFilter();
            }

            if(rec.getName().equals("log")){
                if(rec.getMedal().equals("bronze"))
                    bronzeLogBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverLogBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldLogBadge.clearColorFilter();
            }

            if(rec.getName().equals("exercise")){
                if(rec.getMedal().equals("bronze"))
                    bronzeExerciseBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverExerciseBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldExerciseBadge.clearColorFilter();
            }

            if(rec.getName().equals("disease")){
                if(rec.getMedal().equals("bronze"))
                    bronzeDiseaseBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverDiseaseBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldDiseaseBadge.clearColorFilter();
            }

            if(rec.getName().equals("weight")){
                if(rec.getMedal().equals("bronze"))
                    bronzeWeightBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverWeightBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldWeightBadge.clearColorFilter();
            }

            if(rec.getName().equals("bp")){
                if(rec.getMedal().equals("bronze"))
                    bronzeBpBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverBpBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldBpBadge.clearColorFilter();
            }

            if(rec.getName().equals("cholesterol")){
                if(rec.getMedal().equals("bronze"))
                    bronzeCholesterolBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverCholesterolBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldCholesterolBadge.clearColorFilter();
            }

            if(rec.getName().equals("hba1c")){
                if(rec.getMedal().equals("bronze"))
                    bronzeHba1cBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverHba1cBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldHba1cBadge.clearColorFilter();
            }

        }


    }
}

