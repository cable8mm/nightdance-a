package kr.co.nightdance.nightdancea.views;

import kr.co.nightdance.nightdancea.ClipDetailActivity;
import kr.co.nightdance.nightdancea.R;
import kr.co.nightdance.nightdancea.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter implements OnItemClickListener {
	public static final String TAG = "LazyAdapter";

	Activity activity;
    public JSONArray mClips;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Fragment f) {
    	mClips= new JSONArray();
        activity	= f.getActivity();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public LazyAdapter(Activity f) {
    	mClips= new JSONArray();
        activity	= f;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    
    public void reset() {
    	mClips	= new JSONArray();
    }
    
    public void concatClips(JSONArray d) {
        try {
            for (int i = 0; i < d.length(); i++) {
    				mClips.put(d.get(i));
            }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public int getCount() {
        return mClips.length();
    }

    public Object getItem(int position) {
    	try {
			JSONObject clip	= mClips.getJSONObject(position);
	        return clip;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.layout_clip_cell, null);

        TextView title = (TextView)vi.findViewById(R.id.clip_title); // title
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        TextView musicTitle	= (TextView)vi.findViewById(R.id.music_title);
        RatingBar ratingBar	= (RatingBar)vi.findViewById(R.id.rating);
        TextView ratingMessage	= (TextView)vi.findViewById(R.id.rating_message);
        TextView commentCount	= (TextView)vi.findViewById(R.id.comment_count);
        
		try {
			JSONObject clip	= mClips.getJSONObject(position);
	        title.setText(Integer.toString(position+1) + ". " + clip.getString("clip_title"));
	        musicTitle.setText(clip.getString("music_title"));
	        int clipRate1	= clip.getInt("clip_rate1");
	        if(clipRate1 <= 0) {
	        	ratingBar.setVisibility(View.GONE);
	        	ratingMessage.setVisibility(View.VISIBLE);
	        } else {
		        ratingBar.setRating(clipRate1/2);
	        	ratingBar.setVisibility(View.VISIBLE);
	        	ratingMessage.setVisibility(View.GONE);
	        }
	        commentCount.setText("리뷰 "+clip.getInt("comment_count")+"개");
	        
	        imageLoader.DisplayImage(clip.getString("clip_thumbnail"), thumb_image);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return vi;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			JSONObject clipObject	= (JSONObject) parent.getAdapter().getItem(position);
			Intent intent = new Intent(activity.getApplicationContext(), ClipDetailActivity.class);
			intent.putExtra("clip_id", clipObject.getInt("clip_id"));
			activity.startActivity(intent); // 두번째 액티비티를 실행합니다.
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}