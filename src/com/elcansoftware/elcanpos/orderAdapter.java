package com.elcansoftware.elcanpos;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class orderAdapter implements ListAdapter {
	ArrayList<elcanPosOrder> orders;
	Context ctx;
	int tvResource;
	public orderAdapter(Context context, int textViewResourceId,
			ArrayList<elcanPosOrder> objects) {
		//super(context, textViewResourceId, objects);
		this.orders=objects;
		this.ctx=context;
		this.tvResource=textViewResourceId;
		Log.i("ORDERADAPTER:", " instanciated");
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
        if (row == null) {
        	LayoutInflater inflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row=inflater.inflate(R.layout.orderdetalitem, null);
        	//Log.i("ORDERADAPTER","view is null");
        } 
        elcanPosOrder o = orders.get(position);
        if (o != null) {
        	   //Log.i("ORDERADAPTER","object is NOT null");
               TextView nt = (TextView) row.findViewById(R.id.tvOName);
               TextView pt = (TextView) row.findViewById(R.id.tvOPrice);
               TextView qt = (TextView) row.findViewById(R.id.tvOQty);
               TextView st = (TextView) row.findViewById(R.id.tvOSubtot);
               nt.setText(o.getName());
               pt.setText(String.format("%.2f",o.getPrice()));
               qt.setText(o.getQty().toString());
               st.setText(String.format("%.2f", o.getSubTotal()));
         }
        return row;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orders.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orders.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return ((long)Math.random()*100000);
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (orders.size()==0);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return (orders.size()>0);
	}
}
