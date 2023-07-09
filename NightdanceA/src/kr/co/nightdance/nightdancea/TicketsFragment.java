package kr.co.nightdance.nightdancea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kr.co.nightdance.nightdancea.utils.IPurchaseRequest;
import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;
import kr.co.nightdance.nightdancea.utils.iap.IabHelper;
import kr.co.nightdance.nightdancea.utils.iap.IabHelper.OnIabPurchaseFinishedListener;
import kr.co.nightdance.nightdancea.utils.iap.IabHelper.OnIabSetupFinishedListener;
import kr.co.nightdance.nightdancea.utils.iap.IabHelper.QueryInventoryFinishedListener;
import kr.co.nightdance.nightdancea.utils.iap.IabResult;
import kr.co.nightdance.nightdancea.utils.iap.Inventory;
import kr.co.nightdance.nightdancea.utils.iap.Purchase;
import kr.co.nightdance.nightdancea.utils.iap.SkuDetails;
import kr.co.nightdance.nightdancea.views.TicketAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TicketsFragment extends Fragment implements IPurchaseRequest, ISCManagerResponse, OnIabPurchaseFinishedListener, OnIabSetupFinishedListener, QueryInventoryFinishedListener {
	public static final String TAG = "TicketsFragment";
	private ListView listView;
	private TicketAdapter adapter;
	private IabHelper mHelper;
	public ArrayList<SkuDetails> mProducts;
	public String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkfOKePWAeEe3hrqKFv1ZOXp4zOW51dQy3o5mJGISiDQjER667fs8Txic5Unen4oowCgOZ91uRKcmZIchTHx0rhNuhld6vUh4dPw2gK05SQCx6l0aPb290EQh5KdLYDfk+i6EIL0eavio2aqqtAOXucyQ+LCETnfuhHFyVUN5pbxEL9U0Goz941NpgpDWaThTqtCmVjwMVIp3ZL3gSWTVGoAL6nteqwwzVCROzeXUaZTlRBT2g9qDjTti9YKikdadfCMIbxDC59ivSIrO7DKk/JyGp1FagF/SCqQ+H7mX8h7x1072nXl9rlzVbOQ8NZkvzFxmWc7vXEya9iPCnDcvxQIDAQAB";
    static final int RC_REQUEST = 10002;
    public static String title	= "자유이용권 구입";

	   public TicketsFragment() {
	        // Empty constructor required for fragment subclasses
	   }
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	Log.i(TAG, "method name = onCreate");
	    	super.onCreate(savedInstanceState);

	    	mProducts	= new ArrayList<SkuDetails>();

	    	adapter	= new TicketAdapter(this, this);
	    	
//	        mHelper = IabHelper.getInstance(getActivity());
	        mHelper = ((MainActivity)getActivity()).getIabHelper();
//	        mHelper.enableDebugLogging(true);
//	        
//	        Log.d(TAG, "Starting setup.");
	        mHelper.startSetup(this);
	    	
	    }
	    
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	Log.i(TAG, "function onCreateView");
	    	
	    	View rootView = inflater.inflate(R.layout.fragment_tickets, container, false);
	        getActivity().setTitle(TicketsFragment.title);
	        
	        SCManager scManager = new SCManager("get_tickets.php", null, this, getActivity());
	        scManager.execute();
	        
			listView = (ListView) rootView.findViewById(R.id.ticket_list);
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(adapter);
	        
	        return rootView;
	    }
	    
	    @Override
	    public void onDestroyView() {
	    	super.onDestroyView();
	    }

		@Override
		public void scPostResult(String tag, JSONObject asyncresult)
				throws JSONException {
			if(tag=="get_tickets.php") {
				JSONArray ticketList	= asyncresult.getJSONArray("tickets");
		        adapter.mTickets	=  ticketList;
	    		adapter.notifyDataSetChanged();
	    		
		    	TextView ticketHeader	= (TextView)getView().findViewById(R.id.ticket_header);
	    		Date expiryDate	= UserManager.getInstance().getExpiryDate();
	    		Date current	= new Date();
	    		if(expiryDate == null) {
			    	ticketHeader.setText("자유이용권으로 580여개의 동영상을 즐기세요.");
			    	ticketHeader.setHeight(0);
	    		} else if(expiryDate.compareTo(current) > 0) {
	    			String expiryDateString	= UserManager.getInstance().getExpiryDateString();
	    			ticketHeader.setText(expiryDateString);
			    	ticketHeader.setHeight(40);
	    		} else {
	    			ticketHeader.setText("자유이용권으로 580여개의 동영상을 즐기세요.");
			    	ticketHeader.setHeight(0);
	    		}
			}
		}

	    @Override
	    public void onStart() {
	    	super.onStart();
	        getActivity().setTitle(TicketsFragment.title);
	    }
		
	    // Enables or disables the "please wait" screen.
	    void setWaitScreen(boolean set) {
//	        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
//	        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
	    }

		@Override
		public void requestPurchase(String productId) {
	        /* TODO: for security, generate your payload here for verification. See the comments on
	         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
	         *        an empty string, but on a production app you should carefully generate this. */
	        String payload = "98as9dfiakjsfh9aw8ehf";

	        setWaitScreen(true);
	        Log.d(TAG, "requestPurchase = " + productId);
//	        mHelper.launchPurchaseFlow(this,
//	        		productId, IabHelper.ITEM_TYPE_INAPP,
//	                RC_REQUEST, mPurchaseFinishedListener, payload);
	        mHelper.launchPurchaseFlow(getActivity(),
	        		productId, IabHelper.ITEM_TYPE_INAPP,
	                RC_REQUEST, this, payload);
		}

		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
	        Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

	        // if we were disposed of in the meantime, quit.
	        if (mHelper == null) return;

	        if (result.isFailure()) {
//	            complain("Error purchasing: " + result);
	            complain("취소 되었습니다.");
	            setWaitScreen(false);
	            return;
	        }
	        
	        if (!verifyDeveloperPayload(purchase)) {
	            complain("인증 확인 실패로 취소되었습니다.");
	            setWaitScreen(false);
	            return;
	        }

	        Log.d(TAG, "Purchase successful.");
//	        String params	= "product_id="+purchase.getSku()+"&transaction_id="+purchase.getOrderId();
//	        SCManager scManager = new SCManager("submit_payment.php", params, this, this);
//	        scManager.execute();				
	    }

	    /** Verifies the developer payload of a purchase. */
	    boolean verifyDeveloperPayload(Purchase p) {
//	        String payload = p.getDeveloperPayload();

	        return true;
	    }
		
	    void complain(String message) {
	        Log.e(TAG, "**** NcashBuyingActivity Error: " + message);
//	        alert("Error: " + message);
	        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
	        bld.setMessage(message);
	        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					getActivity().finish();
				}
			});
	        Log.d(TAG, "Showing alert dialog: " + message);
	        bld.create().show();
	    }
		
	    void complain(String title, String message) {
	        Log.e(TAG, "**** NcashBuyingActivity Error: " + message);
//	        alert("Error: " + message);
	        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
	        bld.setTitle(title);
	        bld.setMessage(message);
	        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					getActivity().finish();
				}
			});
	        Log.d(TAG, "Showing alert dialog: " + message);
	        bld.create().show();
	    }

		@Override
		public void onIabSetupFinished(IabResult result) {
		      Log.d(TAG, "Setup finished.");

		      if (!result.isSuccess()) {
		          // Oh noes, there was a problem.
//		          complain("Problem setting up in-app billing: " + result);
		        complain("결제 불능", "앱내 결제를 위한 세팅에 문제가 있습니다.\n안드로이드 계정이 설정되어 있는지 확인 해 주세요.");
		          return;
		      }

		      // Have we been disposed of in the meantime? If so, quit.
		      if (mHelper == null) return;

		      // IAB is fully set up. Now, let's get an inventory of stuff we own.
		      Log.d(TAG, "Setup successful. Querying inventory.");
		      
		      final ArrayList<String> skuList = new ArrayList<String> ();
		      skuList.add("kr.co.nightdance.nightdancea.ticket.7");
		      skuList.add("kr.co.nightdance.nightdancea.ticket.30");
		      
		      mHelper.queryInventoryAsync(true, skuList, this);			
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
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
	        // Have we been disposed of in the meantime? If so, quit.
	        if (mHelper == null) return;

	        // Is it a failure?
	        if (result.isFailure()) {
	            complain("Failed to query inventory: " + result);
	            return;
	        }

	        Map<String,SkuDetails> products	= inventory.getAllProducts();
	        for( Map.Entry<String, SkuDetails> v : products.entrySet() ) {
	        	   if( v != null ) {
	        	       Log.e(TAG, "products = " + v.getKey());
	        	   }
	        	}
	        
	        adapter.setProducts(inventory.getAllProducts());
			adapter.notifyDataSetChanged();

	        Log.e(TAG, "Query inventory was successful.");

	        /*
	         * Check for items we own. Notice that for each purchase, we check
	         * the developer payload to see if it's correct! See
	         * verifyDeveloperPayload().
	         */

//	        // Do we have the premium upgrade?
//	        List<String> getAllOwnedSkus()

//	        updateUi();
	        setWaitScreen(false);
	        Log.d(TAG, "Initial inventory query finished; enabling main UI.");		}
}
