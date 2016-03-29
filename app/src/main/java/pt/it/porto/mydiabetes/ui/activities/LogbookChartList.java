package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public class LogbookChartList extends MultiDataChartActivity {

	public static final int DETAILS = 1;
	protected float firstDate;
	protected float lastDate;
	int hipoGlicemia;
	int hiperGlicemia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			if (!extras.containsKey(MultiDataChartActivity.EXTRAS_CHART_DATA)) {
				extras.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, new pt.it.porto.mydiabetes.ui.charts.data.Logbook(this));
			}
			if (!extras.containsKey(MultiDataChartActivity.EXTRAS_TIME_END)) {
				Calendar calendar = Calendar.getInstance();
				extras.putSerializable(MultiDataChartActivity.EXTRAS_TIME_END, calendar);
			}
			if (!extras.containsKey(MultiDataChartActivity.EXTRAS_TIME_START)) {
				Calendar calendar2 = Calendar.getInstance();
				calendar2.add(Calendar.DAY_OF_YEAR, -8);
				extras.putSerializable(MultiDataChartActivity.EXTRAS_TIME_START, calendar2);
			}
			getIntent().putExtras(extras);

		}

		DB_Read dbRead = new DB_Read(this);
		Object[] myData = dbRead.MyData_Read();
		dbRead.close();

		hipoGlicemia = ((int) (double) myData[5]);
		hiperGlicemia = (int) ((double) myData[6]);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==LogbookChartList.DETAILS && resultCode == DetailLogbookActivity.RESULT_SAVED_CHANGES) {
			updateTimeRange();
			setupContent();
		}
	}

	@Override
	public List<Line> getChartLines() {
		Cursor cursor = getCursor();
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToLast();

		int numberOfElementsInGraph = cursor.getCount();

		@SuppressWarnings("unchecked") List<PointValue>[] xss = new List[3];
		for (int i = 0; i < xss.length; i++) {
			xss[i] = new ArrayList<>(numberOfElementsInGraph * 3 / 4);
		}

		@SuppressWarnings("unchecked") ArrayList<Integer>[] positionInList = new ArrayList[xss.length];
		for (int i = 0; i < positionInList.length; i++) {
			positionInList[i] = new ArrayList<>(numberOfElementsInGraph);
		}


		ChartData chartData = getChartData();
		String date;
		int carbsVal;
		double insulinVal;
		int glycemiaVal;
		int i = numberOfElementsInGraph - 1;
		firstDate = Float.MAX_VALUE;
		lastDate = Float.MIN_VALUE;
		while (i >= 0) {
			date = cursor.getString(0);
			float dateTimeStamp = 0;
			try {
				dateTimeStamp = DateUtils.parseDateTime(date).getTime().getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (firstDate > dateTimeStamp) {
				firstDate = dateTimeStamp;
			}
			if (lastDate < dateTimeStamp) {
				lastDate = dateTimeStamp;
			}
			if (chartData.isFilterActive(0)) {
				carbsVal = cursor.getInt(1);
				if (carbsVal >= 0) {
					xss[0].add(new PointValue(dateTimeStamp, (float) carbsVal));
					positionInList[0].add(i);
				}
			}

			if (chartData.isFilterActive(1)) {
				glycemiaVal = cursor.getInt(4);
				if (glycemiaVal >= 0) {
					xss[2].add(new PointValue(dateTimeStamp, (float) glycemiaVal));
					positionInList[2].add(i);
				}
			}

			if (chartData.isFilterActive(2)) {
				insulinVal = cursor.getDouble(2);
				if (insulinVal >= 0) {
					xss[1].add(new PointValue(dateTimeStamp, (float) insulinVal));
					positionInList[1].add(i);
				}
			}

			i--;
			cursor.moveToPrevious();
		}
		List<Line> lines = new ArrayList<>(xss.length);
		for (i = 0; i < xss.length; i++) {
			Line line = getLine();
			line.setValues(xss[i]);
			line.setColor(CHART_LINE_COLORS[i]);
			line.setHasLabels(chartData.isExtraActive(0));
			lines.add(line);
		}

		if (chartData.isExtraActive(2)) {
			// add line with hipoGlicemia
			List<PointValue> lowLineValues = new ArrayList<>();
			lowLineValues.add(new PointValue(firstDate, (float) hipoGlicemia));
			lowLineValues.add(new PointValue(lastDate, (float) hipoGlicemia));
			Line lowLine = new Line(lowLineValues);
			lowLine.setHasPoints(false);
			lowLine.setAreaTransparency(50);
			lowLine.setColor(Color.parseColor("#C30909"));
			lowLine.setStrokeWidth(1);
			lowLine.setFilled(true);

			lines.add(lowLine);
		}

		if (chartData.isExtraActive(1)) {
			// add line with hiperGlicemia
			List<PointValue> highLineValues = new ArrayList<>();
			highLineValues.add(new PointValue(firstDate, (float) hiperGlicemia));
			highLineValues.add(new PointValue(lastDate, (float) hiperGlicemia));
			Line highLine = new Line(highLineValues);
			highLine.setHasPoints(false);
			highLine.setStrokeWidth(1);
			highLine.setColor(ChartUtils.COLOR_ORANGE);

			lines.add(highLine);
		}

		selectItemToListCalculator = new MyChartToListCalculator(positionInList);
		return lines;
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_logbook);
	}

	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		return new ListAdapter(getCursor());
	}

	class ListAdapter extends RecyclerView.Adapter<LogbookChartList.ListAdapter.Holder> {

		private Cursor cursor;

		public ListAdapter(Cursor cursor) {
			this.cursor = cursor;
		}


		@Override
		public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listchart_logbook_row, parent, false));
		}

		@Override
		public void onBindViewHolder(Holder holder, int position) {
			cursor.moveToPosition(position);
			cursor.getInt(0);

			String date = cursor.getString(0);
			Calendar dateTimeStamp = null;
			try {
				dateTimeStamp = DateUtils.parseDateTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.date.setText(DateUtils.getFormattedDate(dateTimeStamp));
			holder.time.setText(DateUtils.getFormattedTime(dateTimeStamp));

			int carbsVal = cursor.getInt(1);
			if (carbsVal >= 0) {
				holder.carbsVal.setText(String.valueOf(carbsVal));
				holder.carbs.setVisibility(View.VISIBLE);
			} else {
				holder.carbs.setVisibility(View.INVISIBLE);
			}

			double insulinVal = cursor.getDouble(2);
			if (insulinVal >= 0) {
				holder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", insulinVal));
				holder.insulinName.setText(cursor.getString(3));
				holder.insulin.setVisibility(View.VISIBLE);
			} else {
				holder.insulin.setVisibility(View.INVISIBLE);
			}

			int glycemiaVal = cursor.getInt(4);
			if (glycemiaVal >= 0) {
				holder.glycemiaVal.setText(String.valueOf(glycemiaVal));
				holder.glycemia.setVisibility(View.VISIBLE);
			} else {
				holder.glycemia.setVisibility(View.INVISIBLE);
			}

			holder.carbsId = cursor.getInt(5);
			holder.insulinId = cursor.getInt(6);
			holder.glycemiaId = cursor.getInt(7);

			holder.onClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
					Bundle args = new Bundle();
					Holder holder = (Holder) v.getTag();
					if (holder.glycemiaId != -1) {
						GlycemiaRec glycemiaRec = new GlycemiaRec();
						glycemiaRec.setId(holder.glycemiaId);
						args.putString("bg", String.valueOf(glycemiaRec.getId())); //bg id
						args.putParcelable(DetailLogbookActivity.ARG_BLOOD_GLUCOSE, glycemiaRec);
					}
					if (holder.carbsId != -1) {
						CarbsRec carbs = new CarbsRec();
						carbs.setId(holder.carbsId);
						args.putString("ch", String.valueOf(carbs.getId())); //ch id
						args.putParcelable(DetailLogbookActivity.ARG_CARBS, carbs);
					}
					if (holder.insulinId != -1) {
						InsulinRec insulin = new InsulinRec();
						insulin.setId(holder.insulinId);
						args.putString("ins", String.valueOf(insulin.getId())); //ins id
						args.putParcelable(DetailLogbookActivity.ARG_INSULIN, insulin);
					}
					//					args.putInt("Id", ((Holder) v.getTag()).id);
					intent.putExtras(args);
					startActivityForResult(intent, LogbookChartList.DETAILS);
				}
			});
		}

		@Override
		public int getItemCount() {
			return cursor == null ? 0 : cursor.getCount();
		}

		public class Holder extends RecyclerView.ViewHolder {
			TextView date;
			TextView time;
			TextView insulinValue;
			TextView insulinName;
			TextView glycemiaVal;
			TextView carbsVal;
			LinearLayout insulin;
			LinearLayout carbs;
			LinearLayout glycemia;

			View itemView;
			int carbsId;
			int glycemiaId;
			int insulinId;

			public Holder(View view) {
				super(view);
				this.itemView = view;
				date = (TextView) view.findViewById(R.id.date);
				time = (TextView) view.findViewById(R.id.time);
				insulinValue = (TextView) view.findViewById(R.id.insulinVal);
				insulinName = (TextView) view.findViewById(R.id.insulinName);
				glycemiaVal = (TextView) view.findViewById(R.id.glycemiaVal);
				carbsVal = (TextView) view.findViewById(R.id.carbsVal);
				carbs = (LinearLayout) view.findViewById(R.id.carbs);
				insulin = (LinearLayout) view.findViewById(R.id.insulin);
				glycemia = (LinearLayout) view.findViewById(R.id.glycemia);
			}

			public void onClickListener(View.OnClickListener listener) {
				itemView.setOnClickListener(listener);
				itemView.setTag(this);
			}
		}
	}
}
