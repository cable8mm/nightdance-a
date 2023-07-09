package kr.co.nightdance.nightdancea;

import java.util.ArrayList;
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
import kr.co.nightdance.nightdancea.views.ProductsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

public class NcashBuyingActivity extends Activity implements IPurchaseRequest, ISCManagerResponse, OnIabPurchaseFinishedListener, QueryInventoryFinishedListener, OnIabSetupFinishedListener {
	public static final String TAG = "NcashBuyingActivity";
	public Activity mActivity;
	
	private IabHelper mHelper;
	private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkfOKePWAeEe3hrqKFv1ZOXp4zOW51dQy3o5mJGISiDQjER667fs8Txic5Unen4oowCgOZ91uRKcmZIchTHx0rhNuhld6vUh4dPw2gK05SQCx6l0aPb290EQh5KdLYDfk+i6EIL0eavio2aqqtAOXucyQ+LCETnfuhHFyVUN5pbxEL9U0Goz941NpgpDWaThTqtCmVjwMVIp3ZL3gSWTVGoAL6nteqwwzVCROzeXUaZTlRBT2g9qDjTti9YKikdadfCMIbxDC59ivSIrO7DKk/JyGp1FagF/SCqQ+H7mX8h7x1072nXl9rlzVbOQ8NZkvzFxmWc7vXEya9iPCnDcvxQIDAQAB";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

	private ListView listView;
    private ProductsAdapter adapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ncash_buying_activity);

        this.setTitle("모바일 엔캐시 구입");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);	// backbutton enabled
        
        mActivity	= this;
        
        // bind
    	adapter	= new ProductsAdapter(this, this);
        listView = (ListView) findViewById(R.id.iap_items);
        listView.setAdapter(adapter);

        mHelper = new IabHelper(this);
//        mHelper = IabHelper.getInstance(this);
        mHelper.enableDebugLogging(true);
        
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(this);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
    @Override
    public void onBackPressed()
    {
        super.onBackPressed(); 
        finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
        	
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;
    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
            }
            else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    // updates UI to reflect model
    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
//        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
//        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String title, String message) {
        Log.e(TAG, "**** NcashBuyingActivity Error: " + message);
//        alert("Error: " + message);
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle(title);
        bld.setMessage(message);
        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
//				finish();
			}
		});
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
    
    void complain(String message) {
        Log.e(TAG, "**** NcashBuyingActivity Error: " + message);
//        alert("Error: " + message);
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
//				finish();
			}
		});
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

	@Override
	public void requestPurchase(String productId) {
        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        setWaitScreen(true);
        Log.d(TAG, "Launching purchase flow for infinite gas subscription.");
//        mHelper.launchPurchaseFlow(this,
//        		productId, IabHelper.ITEM_TYPE_INAPP,
//                RC_REQUEST, mPurchaseFinishedListener, payload);
        mHelper.launchPurchaseFlow(this,
        		productId, IabHelper.ITEM_TYPE_INAPP,
                RC_REQUEST, this, payload);

	}

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="submit_payment.php") {	// 카테고리 별 리스트를 얻는다.
			boolean isPurchased	= asyncresult.getBoolean("isPurchased");
			if(isPurchased) {
				JSONObject user	= asyncresult.getJSONObject("user");
				int userMobileNcash	= user.getInt("user_mobile_ncash");
				UserManager.getInstance(this).setUserNcash(userMobileNcash);
				alert("충전이 완료되었습니다. Do dance by yourself.");
			}
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

        // if we were disposed of in the meantime, quit.
        if (mHelper == null) return;

        if (result.isFailure()) {
//            complain("Error purchasing: " + result);
            complain("NCahBuyingActivity 구입하지 못했습니다.\n에러 메시지 : " + result);
            setWaitScreen(false);
            return;
        }
        
        if (!verifyDeveloperPayload(purchase)) {
            complain("Error purchasing. Authenticity verification failed.");
            setWaitScreen(false);
            return;
        }

        Log.d(TAG, "Purchase successful.");
        String params	= "product_id="+purchase.getSku()+"&transaction_id="+purchase.getOrderId();
        SCManager scManager = new SCManager("submit_payment.php", params, this, this);
        scManager.execute();		
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        Log.d(TAG, "Query inventory finished.");

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

//        // Do we have the premium upgrade?
//        List<String> getAllOwnedSkus()
        Purchase premiumPurchase = inventory.getPurchase("ncash_1000");
        boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
        Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//
//        // Do we have the infinite gas plan?
//        Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
//        mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
//                verifyDeveloperPayload(infiniteGasPurchase));
//        Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                    + " infinite gas subscription.");
//        if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
//
//        // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//        Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
//        if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//            Log.d(TAG, "We have gas. Consuming it.");
//            mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
//            return;
//        }

        updateUi();
        setWaitScreen(false);
        Log.d(TAG, "Initial inventory query finished; enabling main UI.");
    }

	@Override
	public void onIabSetupFinished(IabResult result) {
      Log.d(TAG, "Setup finished.");

      if (!result.isSuccess()) {
          // Oh noes, there was a problem.
//          complain("Problem setting up in-app billing: " + result);
        complain("결제 불능", "앱내 결제를 위한 세팅에 문제가 있습니다.\n안드로이드 계정이 설정되어 있는지 확인 해 주세요.");
          return;
      }

      // Have we been disposed of in the meantime? If so, quit.
      if (mHelper == null) return;

      // IAB is fully set up. Now, let's get an inventory of stuff we own.
      Log.d(TAG, "Setup successful. Querying inventory.");
      
      final ArrayList<String> skuList = new ArrayList<String> ();
      skuList.add("ncash_1000");
      skuList.add("ncash_2000");
      skuList.add("ncash_3000");
      skuList.add("ncash_4000");
      skuList.add("ncash_5000");
      skuList.add("ncash_10000");
      skuList.add("ncash_20000");
      skuList.add("ncash_30000");
      skuList.add("ncash_50000");
      
      mHelper.queryInventoryAsync(true, skuList, this);
	}
}
