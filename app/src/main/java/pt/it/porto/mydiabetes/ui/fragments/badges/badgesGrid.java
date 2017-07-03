package pt.it.porto.mydiabetes.ui.fragments.badges;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.Calendar;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

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
    private ImageView bronzeDailyBadge;
    private ImageView silverDailyBadge;
    private ImageView goldDailyBadge;

    private ImageView bronzeLogMediumBadge;
    private ImageView silverLogMediumBadge;
    private ImageView goldLogMediumBadge;
    private ImageView bronzeExerciseMediumBadge;
    private ImageView silverExerciseMediumBadge;
    private ImageView goldExerciseMediumBadge;
    private ImageView bronzeDiseaseMediumBadge;
    private ImageView silverDiseaseMediumBadge;
    private ImageView goldDiseaseMediumBadge;
    private ImageView bronzeWeightMediumBadge;
    private ImageView silverWeightMediumBadge;
    private ImageView goldWeightMediumBadge;
    private ImageView bronzeBpMediumBadge;
    private ImageView silverBpMediumBadge;
    private ImageView goldBpMediumBadge;
    private ImageView bronzeCholesterolMediumBadge;
    private ImageView silverCholesterolMediumBadge;
    private ImageView goldCholesterolMediumBadge;
    private ImageView bronzeHba1cMediumBadge;
    private ImageView silverHba1cMediumBadge;
    private ImageView goldHba1cMediumBadge;

    private ImageView bronzeLogAdvancedBadge;
    private ImageView silverLogAdvancedBadge;
    private ImageView goldLogAdvancedBadge;
    private ImageView bronzeExerciseAdvancedBadge;
    private ImageView silverExerciseAdvancedBadge;
    private ImageView goldExerciseAdvancedBadge;
    private ImageView bronzeDiseaseAdvancedBadge;
    private ImageView silverDiseaseAdvancedBadge;
    private ImageView goldDiseaseAdvancedBadge;
    private ImageView bronzeWeightAdvancedBadge;
    private ImageView silverWeightAdvancedBadge;
    private ImageView goldWeightAdvancedBadge;
    private ImageView bronzeBpAdvancedBadge;
    private ImageView silverBpAdvancedBadge;
    private ImageView goldBpAdvancedBadge;
    private ImageView bronzeCholesterolAdvancedBadge;
    private ImageView silverCholesterolAdvancedBadge;
    private ImageView goldCholesterolAdvancedBadge;
    private ImageView bronzeHba1cAdvancedBadge;
    private ImageView silverHba1cAdvancedBadge;
    private ImageView goldHba1cAdvancedBadge;

    private TextView bronzeDailyCount;
    private TextView silverDailyCount;
    private TextView goldDailyCount;

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
        DB_Read read = new DB_Read(getContext());
        int lvl = LevelsPointsUtils.getLevel(getContext(), read);
        read.close();

        RelativeLayout beginnerBadges = (RelativeLayout) layout.findViewById(R.id.beginnerBadgesText);
        final ExpandableRelativeLayout expandableBegginerBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableBegginerBadges);
        expandableBegginerBadges.collapse();
        beginnerBadges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableBegginerBadges.toggle();
            }
        });

        TextView textMedium = (TextView) layout.findViewById(R.id.textMedium);
        RelativeLayout mediumBadges = (RelativeLayout) layout.findViewById(R.id.mediumBadges);
        final ExpandableRelativeLayout expandableMediumBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableMediumBadges);
        expandableMediumBadges.collapse();
        if(lvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL ) {
            int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
            TypedArray ta = getContext().obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            mediumBadges.setBackground(drawableFromTheme);
            textMedium.setText(getString(R.string.medium));
            mediumBadges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandableMediumBadges.toggle();
                }
            });
        }
        else{
            mediumBadges.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divider));
            textMedium.setText(getString(R.string.medium_unlock_lvl10));
        }


        TextView textAdvanced = (TextView) layout.findViewById(R.id.textAdvanced);
        RelativeLayout advancedBadges = (RelativeLayout) layout.findViewById(R.id.advancedBadges);
        final ExpandableRelativeLayout expandableAdvancedBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableAdvancedBadges);
        expandableAdvancedBadges.collapse();
        if(lvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL ) {
            int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
            TypedArray ta = getContext().obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            advancedBadges.setBackground(drawableFromTheme);
            textAdvanced.setText(getString(R.string.advanced));
            advancedBadges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandableAdvancedBadges.toggle();
                }
            });
        }
        else{
            advancedBadges.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divider));
            textAdvanced.setText(getString(R.string.advanced_unlock_lvl30));
        }

        RelativeLayout dailyBadges = (RelativeLayout) layout.findViewById(R.id.dailyBadges);
        final ExpandableRelativeLayout expandableDailyBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableDailyBadges);
        expandableDailyBadges.collapse();
        dailyBadges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableDailyBadges.toggle();
            }
        });


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

        bronzeLogMediumBadge = (ImageView) layout.findViewById(R.id.bronzeLogMediumBadge);
        silverLogMediumBadge = (ImageView) layout.findViewById(R.id.silverLogMediumBadge);
        goldLogMediumBadge = (ImageView) layout.findViewById(R.id.goldLogMediumBadge);
        bronzeExerciseMediumBadge = (ImageView) layout.findViewById(R.id.bronzeExerciseMediumBadge);
        silverExerciseMediumBadge = (ImageView) layout.findViewById(R.id.silverExerciseMediumBadge);
        goldExerciseMediumBadge = (ImageView) layout.findViewById(R.id.goldExerciseMediumBadge);
        bronzeDiseaseMediumBadge = (ImageView) layout.findViewById(R.id.bronzeDiseaseMediumBadge);
        silverDiseaseMediumBadge = (ImageView) layout.findViewById(R.id.silverDiseaseMediumBadge);
        goldDiseaseMediumBadge = (ImageView) layout.findViewById(R.id.goldDiseaseMediumBadge);
        bronzeWeightMediumBadge = (ImageView) layout.findViewById(R.id.bronzeWeightMediumBadge);
        silverWeightMediumBadge = (ImageView) layout.findViewById(R.id.silverWeightMediumBadge);
        goldWeightMediumBadge = (ImageView) layout.findViewById(R.id.goldWeightMediumBadge);
        bronzeBpMediumBadge = (ImageView) layout.findViewById(R.id.bronzeBpMediumBadge);
        silverBpMediumBadge = (ImageView) layout.findViewById(R.id.silverBpMediumBadge);
        goldBpMediumBadge = (ImageView) layout.findViewById(R.id.goldBpMediumBadge);
        bronzeCholesterolMediumBadge = (ImageView) layout.findViewById(R.id.bronzeCholesterolMediumBadge);
        silverCholesterolMediumBadge = (ImageView) layout.findViewById(R.id.silverCholesterolMediumBadge);
        goldCholesterolMediumBadge = (ImageView) layout.findViewById(R.id.goldCholesterolMediumBadge);
        bronzeHba1cMediumBadge = (ImageView) layout.findViewById(R.id.bronzeHba1cMediumBadge);
        silverHba1cMediumBadge = (ImageView) layout.findViewById(R.id.silverHba1cMediumBadge);
        goldHba1cMediumBadge = (ImageView) layout.findViewById(R.id.goldHba1cMediumBadge);

        bronzeLogAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeLogAdvancedBadge);
        silverLogAdvancedBadge = (ImageView) layout.findViewById(R.id.silverLogAdvancedBadge);
        goldLogAdvancedBadge = (ImageView) layout.findViewById(R.id.goldLogAdvancedBadge);
        bronzeExerciseAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeExerciseAdvancedBadge);
        silverExerciseAdvancedBadge = (ImageView) layout.findViewById(R.id.silverExerciseAdvancedBadge);
        goldExerciseAdvancedBadge = (ImageView) layout.findViewById(R.id.goldExerciseAdvancedBadge);
        bronzeDiseaseAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeDiseaseAdvancedBadge);
        silverDiseaseAdvancedBadge = (ImageView) layout.findViewById(R.id.silverDiseaseAdvancedBadge);
        goldDiseaseAdvancedBadge = (ImageView) layout.findViewById(R.id.goldDiseaseAdvancedBadge);
        bronzeWeightAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeWeightAdvancedBadge);
        silverWeightAdvancedBadge = (ImageView) layout.findViewById(R.id.silverWeightAdvancedBadge);
        goldWeightAdvancedBadge = (ImageView) layout.findViewById(R.id.goldWeightAdvancedBadge);
        bronzeBpAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeBpAdvancedBadge);
        silverBpAdvancedBadge = (ImageView) layout.findViewById(R.id.silverBpAdvancedBadge);
        goldBpAdvancedBadge = (ImageView) layout.findViewById(R.id.goldBpAdvancedBadge);
        bronzeCholesterolAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeCholesterolAdvancedBadge);
        silverCholesterolAdvancedBadge = (ImageView) layout.findViewById(R.id.silverCholesterolAdvancedBadge);
        goldCholesterolAdvancedBadge = (ImageView) layout.findViewById(R.id.goldCholesterolAdvancedBadge);
        bronzeHba1cAdvancedBadge = (ImageView) layout.findViewById(R.id.bronzeHba1cAdvancedBadge);
        silverHba1cAdvancedBadge = (ImageView) layout.findViewById(R.id.silverHba1cAdvancedBadge);
        goldHba1cAdvancedBadge = (ImageView) layout.findViewById(R.id.goldHba1cAdvancedBadge);

        bronzeDailyBadge = (ImageView) layout.findViewById(R.id.bronzeDailyBadge);
        silverDailyBadge = (ImageView) layout.findViewById(R.id.silverDailyBadge);
        goldDailyBadge = (ImageView) layout.findViewById(R.id.goldDailyBadge);

        bronzeDailyCount = (TextView) layout.findViewById(R.id.bronzeDailyCount);
        silverDailyCount = (TextView) layout.findViewById(R.id.silverDailyCount);
        goldDailyCount = (TextView) layout.findViewById(R.id.goldDailyCount);

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
        bronzeDailyBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverDailyBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldDailyBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));

        bronzeLogMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverLogMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldLogMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeExerciseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverExerciseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldExerciseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeDiseaseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverDiseaseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldDiseaseMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeWeightMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverWeightMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldWeightMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeBpMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverBpMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldBpMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeCholesterolMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverCholesterolMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldCholesterolMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeHba1cMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverHba1cMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldHba1cMediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));

        bronzeLogAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverLogAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldLogAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeExerciseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverExerciseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldExerciseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeDiseaseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverDiseaseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldDiseaseAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeWeightAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverWeightAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldWeightAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeBpAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverBpAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldBpAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeCholesterolAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverCholesterolAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldCholesterolAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        bronzeHba1cAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        silverHba1cAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        goldHba1cAdvancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));

        checkBadges();

        return layout;
    }


    public void checkBadges(){
        DB_Read db = new DB_Read(getContext());
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        db.close();
        int bronzeDailyFlag = 0;
        int silverDailyFlag = 0;
        int goldDailyFlag = 0;

        for (BadgeRec rec: list) {
            //Beginner
            if(rec.getName().equals("photo")){
                photoBadge.clearColorFilter();
            }

            if(rec.getName().equals("export")){
                exportBadge.clearColorFilter();
            }

            if(rec.getName().equals("log") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeLogBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverLogBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldLogBadge.clearColorFilter();
            }

            if(rec.getName().equals("exercise") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeExerciseBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverExerciseBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldExerciseBadge.clearColorFilter();
            }

            if(rec.getName().equals("disease") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeDiseaseBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverDiseaseBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldDiseaseBadge.clearColorFilter();
            }

            if(rec.getName().equals("weight") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeWeightBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverWeightBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldWeightBadge.clearColorFilter();
            }

            if(rec.getName().equals("bp") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeBpBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverBpBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldBpBadge.clearColorFilter();
            }

            if(rec.getName().equals("cholesterol") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeCholesterolBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverCholesterolBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldCholesterolBadge.clearColorFilter();
            }

            if(rec.getName().equals("hba1c") && rec.getType().equals("beginner")){
                if(rec.getMedal().equals("bronze"))
                    bronzeHba1cBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverHba1cBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldHba1cBadge.clearColorFilter();
            }

            //Medium
            if(rec.getName().equals("log") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeLogMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverLogMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldLogMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("exercise") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeExerciseMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverExerciseMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldExerciseMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("disease") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeDiseaseMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverDiseaseMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldDiseaseMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("weight") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeWeightMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverWeightMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldWeightMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("bp") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeBpMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverBpMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldBpMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("cholesterol") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeCholesterolMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverCholesterolMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldCholesterolMediumBadge.clearColorFilter();
            }

            if(rec.getName().equals("hba1c") && rec.getType().equals("medium")){
                if(rec.getMedal().equals("bronze"))
                    bronzeHba1cMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverHba1cMediumBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldHba1cMediumBadge.clearColorFilter();
            }

            //Advanced
            if(rec.getName().equals("log") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeLogAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverLogAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldLogAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("exercise") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeExerciseAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverExerciseAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldExerciseAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("disease") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeDiseaseAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverDiseaseAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldDiseaseAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("weight") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeWeightAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverWeightAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldWeightAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("bp") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeBpAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverBpAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldBpAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("cholesterol") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeCholesterolAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverCholesterolAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldCholesterolAdvancedBadge.clearColorFilter();
            }

            if(rec.getName().equals("hba1c") && rec.getType().equals("advanced")){
                if(rec.getMedal().equals("bronze"))
                    bronzeHba1cAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("silver"))
                    silverHba1cAdvancedBadge.clearColorFilter();
                if(rec.getMedal().equals("gold"))
                    goldHba1cAdvancedBadge.clearColorFilter();
            }

            //daily
            if(rec.getType().equals("daily") && !rec.getFormattedDate().equals(DateUtils.getFormattedDate(Calendar.getInstance()))){
                if(rec.getMedal().equals("bronze")) {
                    bronzeDailyBadge.clearColorFilter();
                    bronzeDailyFlag++;
                }
                if(rec.getMedal().equals("silver")) {
                    silverDailyBadge.clearColorFilter();
                    silverDailyFlag++;
                }
                if(rec.getMedal().equals("gold")) {
                    goldDailyBadge.clearColorFilter();
                    goldDailyFlag++;
                }
            }

        }
        bronzeDailyCount.setText("("+bronzeDailyFlag+" "+getString(R.string.badges)+")");
        silverDailyCount.setText("("+silverDailyFlag+" "+getString(R.string.badges)+")");
        goldDailyCount.setText("("+goldDailyFlag+" "+getString(R.string.badges)+")");

    }
}

