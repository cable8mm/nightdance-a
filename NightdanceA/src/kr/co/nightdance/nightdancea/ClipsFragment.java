package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.views.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ClipsFragment extends Fragment implements ISCManagerResponse, OnNavigationListener, OnScrollListener {
	public static final String TAG = "ClipsFragment";
    private JSONArray mCategories	= null;
    private int mCategoryId	= 0;
    private int mPage	= 0;
    private LazyAdapter adapter;
    private ListView listView;
    private boolean isLoading	= false;	// 네트워크 로딩 중
    private boolean isDoneLoading	= false;	// 모든 로딩이 끝났는지 여부
    private int mLimit	= 10;

	private static ClipsFragment clipsFragment;
	public static synchronized ClipsFragment getInstance() {
		if(clipsFragment == null) {
			clipsFragment	= new ClipsFragment();
		}
		return clipsFragment;
	}

    public ClipsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "function onCreate");
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);

    	adapter	= new LazyAdapter(this);
    	
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
        SCManager scManager = new SCManager("get_categories.php", null, this, getActivity());
        scManager.execute();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_clips, container, false);
		
		listView = (ListView) rootView.findViewById(R.id.clip_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        listView.setOnScrollListener(this);
        TextView empty=(TextView)rootView.findViewById(R.id.empty);
        listView.setEmptyView(empty);
        setTitle("전체 메뉴");
        isDoneLoading	= false;
        
        return rootView;
    }

    public void setTitle(CharSequence title) {
        getActivity().getActionBar().setTitle(title);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    	inflater.inflate(R.menu.clips_fragment, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//        case R.id.action_userNcash_buying:
//        {
//			Intent intent = new Intent(getActivity().getApplicationContext(), NcashBuyingActivity.class);
//			startActivity(intent); // 두번째 액티비티를 실행합니다.
//        	return true;
//        }
        case R.id.clips_search:
        {
        	Intent intent	= new Intent(getActivity().getApplicationContext(), SearchClipsActivity.class);
        	startActivity(intent);
        	return true;
        }
        default:
            break;
        }

        return false;
    }
    
	@Override
	public void scPostResult(String tag, JSONObject asyncresult) throws JSONException {
		if(tag=="search_clips.php") {	// 카테고리 별 리스트를 얻는다.
			JSONArray clips	= asyncresult.getJSONArray("clips");
			adapter.concatClips(clips);
    		adapter.notifyDataSetChanged();
    		isLoading	= false;
    		isDoneLoading	= clips.length() < mLimit? true : false;
		}

		if(tag=="get_clips.php") {	// 카테고리 별 리스트를 얻는다.
			JSONArray clips	= asyncresult.getJSONArray("clips");
			adapter.concatClips(clips);
    		adapter.notifyDataSetChanged();
    		isLoading	= false;
    		isDoneLoading	= clips.length() < mLimit? true : false;
		}

		if(tag=="get_categories.php") {	// 카테고리 받고, 타이틀로 적용한다.
			Activity mainActivity	= getActivity();
			mCategories = asyncresult.getJSONArray("categories");
			Log.i(TAG, "mCategories = "+mCategories.toString());
			String[] categories	= new String[mCategories.length()+1];
			categories[0]	= "전체 강좌";
			for (int i = 0; i < mCategories.length(); ++i) {
			    JSONObject item = (JSONObject) mCategories.getJSONObject(i);
			    categories[i+1]	= item.getString("name");
			}

			mainActivity.getActionBar().setDisplayShowTitleEnabled(false);
			mainActivity.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<String>(mainActivity,
	              android.R.layout.simple_spinner_dropdown_item, categories);

	        mainActivity.getActionBar().setListNavigationCallbacks(mSpinnerAdapter , this);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		try {
			if(itemPosition == 0)
				mCategoryId	= 99;
			else {
				JSONObject category	= mCategories.getJSONObject(itemPosition-1);
				Log.i(TAG, "category = " + category.toString());
				mCategoryId	= category.getInt("id");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "onNavigationItemSelected itemPosition = "+itemPosition+", arg1 = "+itemId + ", mCategoryId = "+mCategoryId);
		// 카테고리 별 동영상 리스트를 읽어서 보여 줌.
		mPage	= 0;
        isDoneLoading	= false;
		adapter.reset();
		adapter.notifyDataSetChanged();
        isLoading	= true;
		return false;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		boolean loadMore = firstVisible + visibleCount >= totalCount;
		Log.i(TAG, "firstVisible = " + firstVisible + ", visibleCount = " + visibleCount + ", totalCount = " + totalCount);
        if(loadMore && isLoading == false && isDoneLoading == false) {
        	isLoading	= true;
        	mPage++;
        	SCManager scManager = new SCManager("get_clips.php", "category_id="+mCategoryId+"&page="+mPage, this, getActivity());
            scManager.execute();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {}
}
