package pt.it.porto.mydiabetes.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsulinCalc extends Fragment {


	private static final String ARG_FACTOR_GLYCEMIA = "Args_factor_glycemia";
	private static final String ARG_FACTOR_CARBS = "Args_factor_carbs";

	private TextView correctionGlycemia;
	private TextView correctionCarbs;
	private TextView insulinOnBoard;
	private TextView resultTotal;
	private TextView resultRound;
	private LinearLayout blockIOB;

	public InsulinCalc() {
		// Required empty public constructor
	}


	public static InsulinCalc newInstance(int factorInsulin, int factorCarbs) {
		Bundle args = new Bundle();
		args.putInt(ARG_FACTOR_GLYCEMIA, factorInsulin);
		args.putInt(ARG_FACTOR_CARBS, factorCarbs);

		InsulinCalc fragment = new InsulinCalc();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_insulin_meal_calc, container, false);
		this.resultRound = (TextView) view.findViewById(R.id.result_round);
		this.resultTotal = (TextView) view.findViewById(R.id.result_total);
		this.insulinOnBoard = (TextView) view.findViewById(R.id.insulin_on_board);
		this.correctionCarbs = (TextView) view.findViewById(R.id.correction_carbs);
		this.correctionGlycemia = (TextView) view.findViewById(R.id.correction_glycemia);
		this.blockIOB = (LinearLayout) view.findViewById(R.id.block_iob);


		Bundle args = getArguments();

		if (args != null) {
			TextView txtView = (TextView) view.findViewById(R.id.txt_correction_carbs);
			txtView.setText(String.format(txtView.getText().toString(), args.getInt(ARG_FACTOR_CARBS)));
			txtView = (TextView) view.findViewById(R.id.txt_correction_glycemia);
			txtView.setText(String.format(txtView.getText().toString(), args.getInt(ARG_FACTOR_GLYCEMIA)));
		}
		return view;
	}


	public void setResult(float result) {
		this.resultTotal.setText(String.format("%.1f", result));
		this.resultRound.setText(String.format("(%d)", Math.round(result > 0 ? result : 0))); // if lower than one, the recommendation is 0
	}

	public void setInsulinOnBoard(float insulinOnBoard) {
		this.insulinOnBoard.setText(String.format("%.1f", insulinOnBoard));
		this.blockIOB.setVisibility(View.VISIBLE);
	}

	public void setCorrectionCarbs(float correctionCarbs) {
		this.correctionCarbs.setText(String.format("%.1f", correctionCarbs));
	}

	public void setCorrectionGlycemia(float correctionGlycemia) {
		this.correctionGlycemia.setText(String.format("%.1f", correctionGlycemia));
	}


}
