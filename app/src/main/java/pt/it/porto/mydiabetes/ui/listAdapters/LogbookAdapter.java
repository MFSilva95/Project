package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.ui.dataBinding.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.LogbookDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.TagDataBinding;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public class LogbookAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<LogbookDataBinding> _data;

	public LogbookAdapter(ArrayList<LogbookDataBinding> data, Context c) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_logbook_row, parent, false);
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_logbookreg_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_logbookreg_hora);
		TextView ivalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin_value);
		TextView itag = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin);
		TextView gvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia_value);
		TextView gtag = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia);
		TextView cvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_value);
		TextView ctag = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_title);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_logbookreg_tag);

		LogbookDataBinding logbook_datab = _data.get(position);
		CarbsDataBinding ch = logbook_datab.get_ch();
		InsulinRegDataBinding ins = logbook_datab.get_ins();
		GlycemiaDataBinding bg = logbook_datab.get_bg();
		DB_Read rdb = new DB_Read(_c);

		v.setTag(logbook_datab);

		if (ch != null && ins != null && bg != null) {//refeicao completa
			data.setText(ins.getFormattedDate());
			hora.setText(ins.getFormattedTime());
			ivalue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", ins.getInsulinUnits()));
			InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
			itag.setText(insulina.getName());
			gvalue.setText(String.valueOf(bg.getValue()));
			cvalue.setText(String.valueOf(ch.getCarbsValue()));
			TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.VISIBLE);
			itag.setVisibility(View.VISIBLE);
			gvalue.setVisibility(View.VISIBLE);
			gtag.setVisibility(View.VISIBLE);
			cvalue.setVisibility(View.VISIBLE);
			ctag.setVisibility(View.VISIBLE);
		} else if (ch != null && ins == null && bg == null) {//so hidratos carbono
			data.setText(ch.getFormattedDate());
			hora.setText(ch.getFormattedTime());
			ivalue.setText("");
			gvalue.setText("");
			cvalue.setText(String.valueOf(ch.getCarbsValue()));
			TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.GONE);
			itag.setVisibility(View.GONE);
			gvalue.setVisibility(View.GONE);
			gtag.setVisibility(View.GONE);
			cvalue.setVisibility(View.VISIBLE);
			ctag.setVisibility(View.VISIBLE);
		} else if (ch == null && ins != null && bg != null) {//insulina com parametro da glicemia
			data.setText(ins.getFormattedDate());
			hora.setText(ins.getFormattedTime());
			ivalue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", ins.getInsulinUnits()));
			InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
			itag.setText(insulina.getName());
			gvalue.setText(String.valueOf(bg.getValue()));
			cvalue.setText("");
			TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.VISIBLE);
			itag.setVisibility(View.VISIBLE);
			gvalue.setVisibility(View.VISIBLE);
			gtag.setVisibility(View.VISIBLE);
			cvalue.setVisibility(View.GONE);
			ctag.setVisibility(View.GONE);
		} else if (ch == null && ins == null && bg != null) {//so glicemia
			data.setText(bg.getFormattedDate());
			hora.setText(bg.getFormattedTime());
			ivalue.setTag("");
			gvalue.setText(String.valueOf(bg.getValue()));
			cvalue.setText("");
			TagDataBinding t = rdb.Tag_GetById(bg.getIdTag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.GONE);
			itag.setVisibility(View.GONE);
			gvalue.setVisibility(View.VISIBLE);
			gtag.setVisibility(View.VISIBLE);
			cvalue.setVisibility(View.GONE);
			ctag.setVisibility(View.GONE);

		} else if (ch == null && ins != null && bg == null) {//so insulina
			data.setText(ins.getFormattedDate());
			hora.setText(ins.getFormattedTime());
			ivalue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", ins.getInsulinUnits()));
			InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
			itag.setText(insulina.getName());
			gvalue.setText("");
			cvalue.setText("");
			TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.VISIBLE);
			itag.setVisibility(View.VISIBLE);
			gvalue.setVisibility(View.GONE);
			gtag.setVisibility(View.GONE);
			cvalue.setVisibility(View.GONE);
			ctag.setVisibility(View.GONE);
		} else if (ch != null && ins != null && bg == null) {//hidratos e insulina
			data.setText(ins.getFormattedDate());
			hora.setText(ins.getFormattedTime());
			ivalue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", ins.getInsulinUnits()));
			InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
			itag.setText(insulina.getName());
			gvalue.setText("");
			cvalue.setText(String.valueOf(ch.getCarbsValue()));
			TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.VISIBLE);
			itag.setVisibility(View.VISIBLE);
			gvalue.setVisibility(View.GONE);
			gtag.setVisibility(View.GONE);
			cvalue.setVisibility(View.VISIBLE);
			ctag.setVisibility(View.VISIBLE);
		} else if (ch != null && ins == null && bg != null) {//hidratos e glicemia
			data.setText(ch.getFormattedDate());
			hora.setText(ch.getFormattedTime());
			ivalue.setText("");
			itag.setText("");
			gvalue.setText(String.valueOf(bg.getValue()));
			cvalue.setText(String.valueOf(ch.getCarbsValue()));
			TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
			tag.setText(t.getName());

			ivalue.setVisibility(View.GONE);
			itag.setVisibility(View.GONE);
			gvalue.setVisibility(View.VISIBLE);
			gtag.setVisibility(View.VISIBLE);
			cvalue.setVisibility(View.VISIBLE);
			ctag.setVisibility(View.VISIBLE);
		}


		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
				Bundle args = new Bundle();
				LogbookDataBinding logbookDataBinding = (LogbookDataBinding) v.getTag();
				GlycemiaDataBinding glycemiaDataBinding = logbookDataBinding.get_bg();
				if (glycemiaDataBinding != null) {
					args.putString("bg", String.valueOf(glycemiaDataBinding.getId())); //bg id
					args.putParcelable(DetailLogbookActivity.ARG_BLOOD_GLUCOSE, glycemiaDataBinding);
				}
				CarbsDataBinding carbs = logbookDataBinding.get_ch();
				if (carbs != null) {
					args.putString("ch", String.valueOf(carbs.getId())); //ch id
					args.putParcelable(DetailLogbookActivity.ARG_CARBS, carbs);
				}
				InsulinRegDataBinding insulin = logbookDataBinding.get_ins();
				if (insulin != null) {
					args.putString("ins", String.valueOf(insulin.getId())); //ins id
					args.putParcelable(DetailLogbookActivity.ARG_INSULIN, insulin);
				}
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		rdb.close();
		return v;
	}

}