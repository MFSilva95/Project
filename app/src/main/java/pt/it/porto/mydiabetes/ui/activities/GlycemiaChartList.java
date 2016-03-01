package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.charts.data.Logbook;

public class GlycemiaChartList extends LogbookChartList {

	int hipoGlicemia;
	int hiperGlicemia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			Logbook data = new Logbook(this);
			data.toggleFilter(0);
			data.toggleFilter(2);
			extras.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, data);
			getIntent().putExtras(extras);
		}
		super.onCreate(savedInstanceState);
		DB_Read dbRead = new DB_Read(this);
		Object[] myData = dbRead.MyData_Read();
		dbRead.close();
		hipoGlicemia = ((int) (double) myData[5]);
		hiperGlicemia = (int) ((double) myData[6]);
	}

	@Override
	public List<Line> getChartLines() {
		List<Line> lines = super.getChartLines();
		if (lines == null) {
			return null;
		}
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

		// add line with hiperGlicemia
		List<PointValue> highLineValues = new ArrayList<>();
		highLineValues.add(new PointValue(firstDate, (float) hiperGlicemia));
		highLineValues.add(new PointValue(lastDate, (float) hiperGlicemia));
		Line highLine = new Line(highLineValues);
		highLine.setHasPoints(false);
		highLine.setStrokeWidth(1);
		highLine.setColor(ChartUtils.COLOR_ORANGE);

		lines.add(highLine);

		return lines;
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_glycemia);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.glycemia, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_Glycemia) {
			Intent intent = new Intent(this, GlycemiaDetail.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
