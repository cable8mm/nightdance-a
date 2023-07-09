package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;
import kr.co.nightdance.nightdancea.views.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class WishClipsFragment extends Fragment implements ISCManagerResponse {
	public static final String TAG = "UserClipsFragment";
    private LazyAdapter adapter;
    private ListView listView;
    private JSONArray mClips;
    public static String title	= "즐겨찾기";
//    private boolean isRead	= false;

    public WishClipsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "function onCreate");
    	super.onCreate(savedInstanceState);

    	adapter	= new LazyAdapter(this);

    	getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
		View rootView = inflater.inflate(R.layout.fragment_clips, container, false);
		
		listView = (ListView) rootView.findViewById(R.id.clip_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);

        TextView empty=(TextView)rootView.findViewById(R.id.empty);
        listView.setEmptyView(empty);
        
        setTitle(WishClipsFragment.title);
        
        reload();
        
        return rootView;
    }

    private void reload() {
		if(UserManager.getInstance(getActivity()).isLogin() == true) {
	        SCManager scManager = new SCManager("get_wish_clips.php", null, this, getActivity());
	        scManager.execute();
		}

		if(UserManager.getInstance(getActivity()).isLogin() == false) {
			TextView emptyTextView	= (TextView)listView.getEmptyView();
			emptyTextView.setText("즐겨찾기 한 강좌가 없습니다.");
			int[] scrapedClipArray	= UserManager.getInstance().getScrapedClipIds();
			if(scrapedClipArray != null) {
		        SCManager scManager = new SCManager("get_clips_from_ids.php", "clip_ids="+UserManager.getInstance().getScrapedClipIdsString(), this, getActivity());
		        scManager.execute();
			}
		}
		
    	UserManager.getInstance().setViewedScrapingClip(false);
    }
    
    @Override
    public void onResume() {
    	Log.i(TAG, "onResume()");
    	if(UserManager.getInstance().isViewedScrapingClip()) {
    		reload();
    	}
    	
    	super.onResume();
    }
    
    public void setTitle(CharSequence title) {
        getActivity().getActionBar().setTitle(title);
    }

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="get_wish_clips.php") {	// 카테고리 별 리스트를 얻는다.
			mClips	= asyncresult.getJSONArray("clips");
			adapter.mClips	= mClips;
//			adapter.concatClips(mClips);
    		adapter.notifyDataSetChanged();
//    		isRead	= true;
		}
		
		if(tag=="get_clips_from_ids.php") {
			mClips	= asyncresult.getJSONArray("clips");
			Log.i(TAG, "mClips = "+mClips.toString());
//			adapter.concatClips(mClips);
			adapter.mClips	= mClips;
    		adapter.notifyDataSetChanged();
//    		isRead	= true;
		}
	}
	
	public void emptyClips() {
//		isRead	= false;
		adapter.reset();
	}
}
