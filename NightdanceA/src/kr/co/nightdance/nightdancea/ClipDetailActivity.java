package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;
import kr.co.nightdance.nightdancea.views.ClipDetailAdapter;
import kr.co.nightdance.nightdancea.views.WriteReviewDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class ClipDetailActivity extends FragmentActivity implements ISCManagerResponse, DialogInterface.OnClickListener {
	public static final String TAG = "ClipDetailActivity";

	private int mClipId;
    private JSONObject mClip;
    private JSONArray mComments;
    private JSONArray mRelateClips;
    private boolean isScraped;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_detail_activity);
        
        Intent intent = getIntent(); // 값을 받아온다.
        mClipId = intent.getIntExtra("clip_id", 0);
        
        setTitle("강좌");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);	// backbutton enabled

//      // Get Clips
        SCManager scManager = new SCManager("get_clip.php", "id="+mClipId, this, this);
        scManager.execute();
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed(); 
        finish();
    }

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		// TODO Auto-generated method stub
		Log.i(TAG, tag);
		
		if(tag=="get_clip.php") {	// 카테고리 별 리스트를 얻는다.
			mClip = asyncresult.getJSONObject("clip");

			if(UserManager.getInstance().isLogin()) {
	        	isScraped	= mClip.getBoolean("is_scraped");
	        } else {
	        	isScraped	= UserManager.getInstance().isScrapedClip(mClipId);
	        }

	        MenuItem addWishItem	= (MenuItem)mMenu.findItem(R.id.action_add_wish);
	    	if(isScraped) {
				addWishItem.setChecked(false);
				addWishItem.setIcon(R.drawable.ic_action_important);
	    	} else {
				addWishItem.setChecked(true);
				addWishItem.setIcon(R.drawable.ic_action_not_important);
	    	}
			
	        SCManager scManager = new SCManager("get_comments.php", "id="+mClipId, this, this);
	        scManager.execute();
		}
		
		if(tag=="get_comments.php") {
			mComments = asyncresult.getJSONArray("comments");
	        SCManager scManager = new SCManager("get_relate_clips.php", "clip_id="+mClipId, this, this);
	        scManager.execute();
		}
		
		if(tag=="get_relate_clips.php") {
			mRelateClips	= asyncresult.getJSONArray("clips");
			
	        ClipDetailAdapter adapter = new ClipDetailAdapter(this, mClip, mComments, mRelateClips);
	        ListView listView = (ListView) findViewById(R.id.clip_detail);
	        listView.setAdapter(adapter);
		}
		
		if(tag=="add_wish_clip.php") {
			boolean isSuccess	= asyncresult.getBoolean("isSuccess");
			if(isSuccess == false) {
				MenuItem addWishItem	= (MenuItem)findViewById(R.id.action_add_wish);
				addWishItem.setChecked(true);
				addWishItem.setIcon(R.drawable.ic_action_not_important);
			}
		}
		
		if(tag=="remove_wish_clip.php") {
			MenuItem addWishItem	= (MenuItem)findViewById(R.id.action_add_wish);
			addWishItem.setChecked(false);
			addWishItem.setIcon(R.drawable.ic_action_important);
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        {
        	onBackPressed();
        }
            return true;
        case R.id.action_add_wish:
        {
        	if (item.isChecked()) {
        		item.setChecked(false);
        		item.setIcon(R.drawable.ic_action_important);
        	}
            else {
            	item.setChecked(true);
        		item.setIcon(R.drawable.ic_action_not_important);
            }

        	if (UserManager.getInstance().isLogin()) {
            	if (item.isChecked()) {
                    SCManager scManager = new SCManager("remove_wish_clip.php", "clip_id="+mClipId, this, this);
                    scManager.execute();
            	}
                else {
                    SCManager scManager = new SCManager("add_wish_clip.php", "clip_id="+mClipId, this, this);
                    scManager.execute();
                }
        	} else {
            	if (item.isChecked()) {
            		UserManager.getInstance().removeScrapedClip(mClipId);
            	}
                else {
            		UserManager.getInstance().addScrapedClipId(mClipId);
                }
        	}
        	
        	UserManager.getInstance().setViewedScrapingClip(true);
        	return true;
        }
        case R.id.action_write:
        	if(UserManager.getInstance().isLogin()) {
            	WriteReviewDialogFragment dialog = new WriteReviewDialogFragment();
            	dialog.listener = this;
            	dialog.show(getSupportFragmentManager(), "write_review");
        	} else {
		        AlertDialog.Builder bld = new AlertDialog.Builder(this);
		        bld.setTitle("");
		        bld.setNeutralButton("OK", null);
		        bld.create().show();
        	}
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clip_detail_activity, menu);
        
        mMenu	= menu;
        
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// 완료 = -1, 취소 = -2
		if(which == -1) {
			
		}
		Log.i(TAG, "onClick int = " + which);
	}
}
