package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.views.ChargeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class NcashesActivity extends Activity implements ISCManagerResponse {
	private ChargeAdapter adapter;
	private ListView listView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ncashes_activity);

        this.setTitle("엔캐시 상세 내역");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);	// backbutton enabled
        
        // bind
        listView	= (ListView)findViewById(R.id.charge_list);
        
        SCManager scManager = new SCManager("get_charge_list.php", null, this, this);
        scManager.execute();
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
    protected void onSaveInstanceState(Bundle outState) {
    }

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="get_charge_list.php") {
			JSONArray chargeList	= asyncresult.getJSONArray("chargeList");
	        adapter	= new ChargeAdapter(this, chargeList);
	        listView.setAdapter(adapter);
    		adapter.notifyDataSetChanged();
		}
	}
}
