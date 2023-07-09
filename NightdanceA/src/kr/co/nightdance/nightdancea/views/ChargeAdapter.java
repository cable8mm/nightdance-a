package kr.co.nightdance.nightdancea.views;

import kr.co.nightdance.nightdancea.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChargeAdapter extends BaseAdapter {
	public static final String TAG = "ChargeAdapter";
	private JSONArray mCharges;
    private static LayoutInflater inflater=null;

    public ChargeAdapter(Activity activity,JSONArray chargeArray) {
    	mCharges	= chargeArray;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCharges.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
        if(convertView==null)
        	convertView = inflater.inflate(R.layout.layout_charge_cell, null);

        try {
        	TextView textLabel	= (TextView)convertView.findViewById(R.id.text_label);
        	TextView detailTextLabel	= (TextView)convertView.findViewById(R.id.detail_text_label);
        	
			JSONObject	charge	= mCharges.getJSONObject(position);
        	int logPayment	= charge.getInt("log_payment");
        	if(logPayment < 0) {
        		textLabel.setTextColor(Color.RED);
        	} else {
        		textLabel.setTextColor(Color.BLUE);
        	}
        	
        	String detailText	= charge.getString("desc_name") 
        			+ " (" + charge.getString("log_method") 
        			+ charge.getString("log_method_detail")
        			+ ") / "
        			+ Integer.toString(logPayment)
        			+ " 코인";
			
			textLabel.setText(charge.getString("log_time"));
			detailTextLabel.setText(detailText);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return convertView;
	}

}
