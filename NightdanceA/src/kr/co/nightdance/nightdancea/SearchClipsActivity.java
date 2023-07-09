package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.views.LazyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class SearchClipsActivity extends Activity  implements ISCManagerResponse, OnScrollListener, OnQueryTextListener, OnCloseListener{
	public static final String TAG = "SearchClipsActivity";
    private LazyAdapter adapter;
    private ListView listView;
    private boolean isLoading	= false;
    private SearchView mSearchView;
    public String mQuery;
    private int mPage	= 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "function onCreate");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_clips);

    	adapter	= new LazyAdapter(this);
    	
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);	// backbutton enabled

        listView = (ListView) findViewById(R.id.clip_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        listView.setOnScrollListener(this);
        TextView empty=(TextView)findViewById(R.id.empty);
        empty.setText("동영상 검색");
        listView.setEmptyView(empty);
    }

    public void doQuery(String query) {
    	if(adapter != null)
    		adapter.reset();
    	
    	mQuery	= query;
    	mPage	= 1;
    	
    	SCManager scManager = new SCManager("search_clips.php", "word="+query+"&page="+mPage, this, this);
        scManager.execute();
		isLoading	= true;
    }
    
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }
    
	@Override
	public void scPostResult(String tag, JSONObject asyncresult) throws JSONException {
		if(tag=="search_clips.php") {	// 카테고리 별 리스트를 얻는다.
			JSONArray clips	= asyncresult.getJSONArray("clips");
			if(clips.length() != 0) {
				adapter.concatClips(clips);
	    		adapter.notifyDataSetChanged();
	    		isLoading	= false;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		boolean loadMore = firstVisible + visibleCount >= totalCount;
        if(loadMore && isLoading == false) {
        	isLoading	= true;
        	mPage++;
        	SCManager scManager = new SCManager("search_clips.php", "word="+mQuery+"&page="+mPage, this, this);
            scManager.execute();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {}

	@Override
	public boolean onQueryTextChange(String query) {
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
//		Log.i(TAG, "onQueryTextSubmit = "+query);
//		Toast.makeText(this, "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
		mSearchView.clearFocus();
		
		doQuery(query);
		return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu( menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_clips_activity, menu);
    	mSearchView = (SearchView)menu.findItem(R.id.clips_search).getActionView();
    	mSearchView.setQueryHint("동영상 검색");
    	mSearchView.setOnQueryTextListener(this);
    	mSearchView.setOnCloseListener(this);
    	MenuItem searchMenuItem = menu.findItem( R.id.clips_search ); // get my MenuItem with placeholder submenu
        searchMenuItem.expandActionView(); // Expand the search menu item in order to show by default the query

        return true;
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
	public boolean onClose() {
		Log.i(TAG, "SearchView Close");
		return false;
	}
}
