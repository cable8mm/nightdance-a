package kr.co.nightdance.nightdancea.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kr.co.nightdance.nightdancea.R;
import kr.co.nightdance.nightdancea.utils.IPurchaseRequest;
import kr.co.nightdance.nightdancea.utils.ImageLoader;
import kr.co.nightdance.nightdancea.utils.iap.SkuDetails;

import org.json.JSONArray;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TicketAdapter extends BaseAdapter implements OnItemClickListener{
	public static final String TAG = "TicketAdapter";
	public JSONArray mTickets;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
	public ArrayList<SkuDetails> mProducts;
	public IPurchaseRequest _delegate	= null;
	public Fragment mFragment;
    
    public TicketAdapter(Fragment f, IPurchaseRequest delegate) {
    	mFragment	= f;
    	mProducts	= new ArrayList<SkuDetails>();
    	_delegate	= delegate;
        inflater = (LayoutInflater)f.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(f.getActivity().getApplicationContext());
    }
    
    public void setProducts(Map<String, SkuDetails> products) {
    	// http://stackoverflow.com/questions/780541/how-to-sort-hash-map
    	List<SkuDetails> skusByPrice = new ArrayList<SkuDetails>(products.values());
    	Collections.sort(skusByPrice, new Comparator<SkuDetails>() {
    		@Override
            public int compare(SkuDetails o1, SkuDetails o2) {
                return (o1.getIntPrice()-o2.getIntPrice() > 0)? 1 : -1;
            }
        });
    	
    	for(SkuDetails sku : skusByPrice) {
    		mProducts.add(sku);
    		Log.i(TAG, "mProduct add = "+sku.toString());
    	}
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    
	@Override
	public int getCount() {
		return mProducts == null? 0 : mProducts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
//		try {
//			return mTickets.getJSONObject(position);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
        	convertView = inflater.inflate(R.layout.layout_ticket_cell, null);

      TextView ticketName	= (TextView)convertView.findViewById(R.id.ticket_name);
      ImageView ticketThumbnail	= (ImageView)convertView.findViewById(R.id.ticket_thumbnail);
      TextView ticketPrice	= (TextView)convertView.findViewById(R.id.ticket_price);
      TextView ticketTerm	= (TextView)convertView.findViewById(R.id.ticket_term);

		SkuDetails	sku	= mProducts.get(position);
		ticketName.setText(sku.getTitle());
		ticketPrice.setText(sku.getDescription());
		ticketPrice.setText("가격 : "+sku.getPrice());
		imageLoader.DisplayImage(sku.getThumbnail(), ticketThumbnail);
		ticketTerm.setText("이용 기간 : "+sku.getTerm()+" 일");
		Log.i(TAG, "SkyDetails = "+sku.toString());

		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SkuDetails	sku	= mProducts.get(position);
		final String productId	= sku.getSku();
//		_delegate.requestPurchase(productId);

		String productTestId	= "android.test.purchased";
		_delegate.requestPurchase(productTestId);
		
//		final String price	= sku.getPrice();
//		Toast.makeText(mFragment.getActivity(), "결제 : " + price, Toast.LENGTH_SHORT).show();
	}
}
