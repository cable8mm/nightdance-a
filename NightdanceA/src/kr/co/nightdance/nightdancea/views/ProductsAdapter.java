package kr.co.nightdance.nightdancea.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kr.co.nightdance.nightdancea.R;
import kr.co.nightdance.nightdancea.utils.IPurchaseRequest;
import kr.co.nightdance.nightdancea.utils.iap.SkuDetails;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsAdapter extends BaseAdapter {
	public static final String TAG = "ProductsAdapter";
//	public Map<String,SkuDetails> mProducts;
	public ArrayList<SkuDetails> mProducts;
    private static LayoutInflater inflater=null;
    private Activity mActivity;
	public IPurchaseRequest _delegate	= null;

    public ProductsAdapter(Activity activity, IPurchaseRequest delegate) {
    	mActivity	= activity;
    	mProducts	= new ArrayList<SkuDetails>();
    	_delegate	= delegate;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    		Log.i(TAG, "sku price = "+sku.getIntPrice());
    	}
    }
    
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
    
	@Override
	public int getCount() {
		return mProducts == null? 0 : mProducts.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
        if(convertView==null)
        	convertView = inflater.inflate(R.layout.layout_product_cell, null);

    	TextView textLabel	= (TextView)convertView.findViewById(R.id.text_label);
    	TextView detailTextLabel	= (TextView)convertView.findViewById(R.id.detail_text_label);
    	TextView priceLabel	= (TextView)convertView.findViewById(R.id.price_label);
    	
		SkuDetails	sku	= mProducts.get(position);
		textLabel.setText(sku.getTitle());
		detailTextLabel.setText(sku.getDescription());
		priceLabel.setText(sku.getPrice());
		final String price	= sku.getPrice();
		final String productId	= sku.getSku();
		priceLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				_delegate.requestPurchase(productId);
				Toast.makeText(mActivity, "결제 : " + price, Toast.LENGTH_SHORT).show();
			}
			
		});
        
		return convertView;
	}
}
