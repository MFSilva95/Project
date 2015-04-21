
package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import repack.org.bouncycastle.asn1.x509.Holder;

import com.jadg.mydiabetes.LogbookDetail;
import com.jadg.mydiabetes.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class LogbookAdapter extends BaseAdapter{

	private ArrayList<LogbookDataBinding> _data;
    Context _c;

    public LogbookAdapter (ArrayList<LogbookDataBinding> data, Context c){
        _data = data;
        _c = c;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Holder holder = null;
		
        if (v == null)
        {
           LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           v = vi.inflate(R.layout.list_logbook_row, null);
        }
 
        TextView data = (TextView)v.findViewById(R.id.tv_list_logbookreg_data);
        TextView hora = (TextView)v.findViewById(R.id.tv_list_logbookreg_hora);
        TextView ivalue = (TextView)v.findViewById(R.id.tv_list_logbookreg_insulin_value);
        TextView itag = (TextView)v.findViewById(R.id.tv_list_logbookreg_insulin);
        TextView gvalue = (TextView)v.findViewById(R.id.tv_list_logbookreg_glycemia_value);
        TextView gtag = (TextView)v.findViewById(R.id.tv_list_logbookreg_glycemia);
        TextView cvalue = (TextView)v.findViewById(R.id.tv_list_logbookreg_carbs_value);
        TextView ctag = (TextView)v.findViewById(R.id.tv_list_logbookreg_carbs_title);
        TextView tag = (TextView)v.findViewById(R.id.tv_list_logbookreg_tag);
        TextView insulin = (TextView)v.findViewById(R.id.tv_list_logbookreg_insulin);
	   
        final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_logbookreg_detail);
	   
        final LogbookDataBinding logbook_datab = _data.get(position);
        final CarbsDataBinding ch = logbook_datab.get_ch();
        final InsulinRegDataBinding ins = logbook_datab.get_ins();
        final GlycemiaDataBinding bg = logbook_datab.get_bg();
        DB_Read rdb = new DB_Read(_c);
        
        
        if (ch!=null && ins!= null && bg!=null){//refeicao completa
        	data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText(bg.getValue().toString());
            cvalue.setText(ch.getCarbsValue().toString());
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = ch.getId()+"_"+ins.getId()+"_"+bg.getId();
            viewdetail.setTag(_id);
            
            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        }else if(ch!=null && ins== null && bg==null){//so hidratos carbono
        	data.setText(ch.getDate());
            hora.setText(ch.getTime());
            ivalue.setText("");
            gvalue.setText("");
            cvalue.setText(ch.getCarbsValue().toString());
            TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
            tag.setText(t.getName());
            final String _id = ch.getId()+"_"+0+"_"+0;
            viewdetail.setTag(_id);
            
            ivalue.setVisibility(View.GONE);
            itag.setVisibility(View.GONE);
            gvalue.setVisibility(View.GONE);
            gtag.setVisibility(View.GONE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        }else if(ch==null && ins!= null && bg!=null){//insulina com parametro da glicemia
        	data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText(bg.getValue().toString());
            cvalue.setText("");
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = 0+"_"+ins.getId()+"_"+bg.getId();
            viewdetail.setTag(_id);
            
            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);
        }else if(ch==null && ins== null && bg!=null){//so glicemia
        	data.setText(bg.getDate());
            hora.setText(bg.getTime());
            ivalue.setTag("");;
            gvalue.setText(bg.getValue().toString());
            cvalue.setText("");;
            TagDataBinding t = rdb.Tag_GetById(bg.getIdTag());
            tag.setText(t.getName());
            final String _id = 0+"_"+0+"_"+bg.getId();
            viewdetail.setTag(_id);
            
            ivalue.setVisibility(View.GONE);
            itag.setVisibility(View.GONE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);

        }else if(ch==null && ins!= null && bg==null){//so insulina
        	data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText("");;
            cvalue.setText("");
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = 0+"_"+ins.getId()+"_"+0;
            viewdetail.setTag(_id);
            
            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.GONE);
            gtag.setVisibility(View.GONE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);
        }
        
        final String id_ch;
        final String id_ins;
        final String id_bg;

        if(ch!=null){
        	id_ch = ""+ch.getId();
        	//viewdetail.setTag(id_ch);
        }else{
        	id_ch = "";
        }
        
        if(ins!=null){
        	id_ins=""+ins.getId();
        	//viewdetail.setTag(id_ins);
        }else{
        	id_ins = "";
        }
        
        if(bg!=null){
        	id_bg=""+bg.getId();
        	//viewdetail.setTag(id_bg);
        }else{
        	id_bg="";
        }
        
        
       
        
        
        
        
        viewdetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), LogbookDetail.class);
				Bundle args = new Bundle();
				args.putString("ch", id_ch); //ch id
				args.putString("ins", id_ins); //ins id
				args.putString("bg", id_bg); //bg id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        });
        
        rdb.close();
	    return v;
	}

}

/*


package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import repack.org.bouncycastle.asn1.x509.Holder;

import com.jadg.mydiabetes.LogbookDetail;
import com.jadg.mydiabetes.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class LogbookAdapter extends BaseAdapter{

	private ArrayList<LogbookDataBinding> _data;
    Context _c;

    public LogbookAdapter (ArrayList<LogbookDataBinding> data, Context c){
        _data = data;
        _c = c;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView==null){
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_logbook_row, null);
			holder = new ViewHolder();
			
			holder.data = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_data);
			holder.hora = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_hora);
			holder.ivalue = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_insulin_value);
			holder.itag = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_insulin);
			holder.gvalue = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_glycemia_value);
			holder.gtag = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_glycemia);
			holder.cvalue = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_carbs_value);
			holder.ctag = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_carbs_title);
			holder.tag = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_tag);
			holder.insulin = (TextView)convertView.findViewById(R.id.tv_list_logbookreg_insulin);
			holder.viewdetail = (ImageButton)convertView.findViewById(R.id.ib_list_logbookreg_detail);
			
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
	
 
        
	   
        final LogbookDataBinding logbook_datab = _data.get(position);
        final CarbsDataBinding ch = logbook_datab.get_ch();
        final InsulinRegDataBinding ins = logbook_datab.get_ins();
        final GlycemiaDataBinding bg = logbook_datab.get_bg();
        DB_Read rdb = new DB_Read(_c);
        
        
        if (ch!=null && ins!= null && bg!=null){//refeicao completa
        	holder.data.setText(ins.getDate());
            holder.hora.setText(ins.getTime());
            holder.ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            holder.insulin.setText(insulina.getName());
            holder.gvalue.setText(bg.getValue().toString());
            holder.cvalue.setText(ch.getCarbsValue().toString());
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            holder.tag.setText(t.getName());
            
            holder.ivalue.setVisibility(View.VISIBLE);
            holder.itag.setVisibility(View.VISIBLE);
            holder.gvalue.setVisibility(View.VISIBLE);
            holder.gtag.setVisibility(View.VISIBLE);
            holder.cvalue.setVisibility(View.VISIBLE);
            holder.ctag.setVisibility(View.VISIBLE);
            
            
            //final String _id = ch.getId()+"_"+ins.getId()+"_"+bg.getId();
            //viewdetail.setTag(_id);
        }else if(ch!=null && ins== null && bg==null){//so hidratos carbono
        	holder.data.setText(ch.getDate());
            holder.hora.setText(ch.getTime());
            holder.ivalue.setVisibility(View.GONE);
            holder.itag.setVisibility(View.GONE);
            holder.gvalue.setVisibility(View.GONE);
            holder.gtag.setVisibility(View.GONE);
            holder.cvalue.setText(ch.getCarbsValue().toString());
            holder.cvalue.setVisibility(View.VISIBLE);
            holder.ctag.setVisibility(View.VISIBLE);
            TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
            holder.tag.setText(t.getName());
            //final String _id = ch.getId()+"_"+0+"_"+0;
            //viewdetail.setTag(_id);
        }else if(ch==null && ins!= null && bg!=null){//insulina com parametro da glicemia
        	holder.data.setText(ins.getDate());
            holder.hora.setText(ins.getTime());
            holder.ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            holder.insulin.setText(insulina.getName());
            holder.gvalue.setText(bg.getValue().toString());
            holder.ivalue.setVisibility(View.VISIBLE);
            holder.itag.setVisibility(View.VISIBLE);
            holder.gvalue.setVisibility(View.VISIBLE);
            holder.gtag.setVisibility(View.VISIBLE);
            holder.cvalue.setVisibility(View.GONE);
            holder.ctag.setVisibility(View.GONE);
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            holder.tag.setText(t.getName());
            //final String _id = 0+"_"+ins.getId()+"_"+bg.getId();
            //viewdetail.setTag(_id);
        }else if(ch==null && ins== null && bg!=null){//so glicemia
        	holder.data.setText(bg.getDate());
            holder.hora.setText(bg.getTime());
            holder.ivalue.setVisibility(View.GONE);
            holder.itag.setVisibility(View.GONE);
            holder.gvalue.setText(bg.getValue().toString());
            holder.gvalue.setVisibility(View.VISIBLE);
            holder.gtag.setVisibility(View.VISIBLE);
            holder.cvalue.setVisibility(View.GONE);
            holder.ctag.setVisibility(View.GONE);
            TagDataBinding t = rdb.Tag_GetById(bg.getIdTag());
            holder.tag.setText(t.getName());
            //final String _id = 0+"_"+0+"_"+bg.getId();
            //viewdetail.setTag(_id);

        }else if(ch==null && ins!= null && bg==null){//so insulina
        	holder.data.setText(ins.getDate());
            holder.hora.setText(ins.getTime());
            holder.ivalue.setText(ins.getInsulinUnits().toString());
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            holder.insulin.setText(insulina.getName());
            holder.ivalue.setVisibility(View.VISIBLE);
            holder.itag.setVisibility(View.VISIBLE);
            holder.gvalue.setVisibility(View.GONE);
            holder.gtag.setVisibility(View.GONE);
            holder.cvalue.setVisibility(View.GONE);
            holder.ctag.setVisibility(View.GONE);
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            holder.tag.setText(t.getName());
            //final String _id = 0+"_"+ins.getId()+"_"+0;
            //viewdetail.setTag(_id);
        }
        
        final String id_ch;
        final String id_ins;
        final String id_bg;

        if(ch!=null){
        	id_ch = ""+ch.getId();
        }else{
        	id_ch = "";
        }
        
        if(ins!=null){
        	id_ins=""+ins.getId();
        }else{
        	id_ins = "";
        }
        
        if(bg!=null){
        	id_bg=""+bg.getId();
        }else{
        	id_bg="";
        }
        
        
       
        
        
        
        
        holder.viewdetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), LogbookDetail.class);
				Bundle args = new Bundle();
				args.putString("ch", id_ch); //ch id
				args.putString("ins", id_ins); //ins id
				args.putString("bg", id_bg); //bg id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        });
        
        rdb.close();
	    return convertView;
	}
	
	public static class ViewHolder{
		public TextView data;
	    public TextView hora;
	    public TextView ivalue;
	    public TextView itag;
	    public TextView gvalue;
	    public TextView gtag;
	    public TextView cvalue;
	    public TextView ctag;
	    public TextView tag;
	    public TextView insulin;
	    public ImageButton viewdetail;
	}

}
*/
