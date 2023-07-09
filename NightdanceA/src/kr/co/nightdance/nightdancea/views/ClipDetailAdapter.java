package kr.co.nightdance.nightdancea.views;

import java.text.DecimalFormat;

import kr.co.nightdance.nightdancea.ClipDetailActivity;
import kr.co.nightdance.nightdancea.NcashBuyingActivity;
import kr.co.nightdance.nightdancea.R;
import kr.co.nightdance.nightdancea.VodPlayerActivity;
import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.ImageLoader;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ClipDetailAdapter extends BaseAdapter implements OnClickListener, ISCManagerResponse, DialogInterface.OnClickListener {
	public static final String TAG = "ClipDetailAdapter";
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private JSONObject mClip;
    private JSONArray mComments;
    private JSONArray mRelateClips;
    private int mClipRowCount;
    private int mCommentSectionCount	= 0;
    private int mCommentRowCount;
    private int mRelateClipSectionCount	= 0;
    private int mRelateClipRowCount;
    private Activity mActivity;
    
    public ClipDetailAdapter(Activity activity, JSONObject clipObject, JSONArray commentArray, JSONArray relateClipArray) {
    	mActivity	= activity;
    	mClip	= clipObject;
    	mComments	= commentArray;
    	mRelateClips	= relateClipArray;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		mClipRowCount	= 3;
		mCommentRowCount	= mComments.length() == 0 ? 0 : mComments.length();	// 코멘트 없을 경우 코멘트 없음이 나옴.
		if(mCommentRowCount != 0)
			mCommentSectionCount	= 1;
		mRelateClipRowCount	= mRelateClips.length() == 0 ? 0 : mRelateClips.length();
		if(mRelateClipRowCount != 0)
			mRelateClipSectionCount	= 1;
		return mClipRowCount + mCommentRowCount + mRelateClipRowCount + mCommentSectionCount + mRelateClipSectionCount;	// 2는 섹션 타이틀
	}

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return (long)position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        if(position == 0) {	// 헤더
	        vi = inflater.inflate(R.layout.clip_detail_header, null);

	        TextView title = (TextView)vi.findViewById(R.id.clip_title); // title
		    TextView clipCredit = (TextView)vi.findViewById(R.id.clip_credit); // title
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
	        Button previewButton=(Button)vi.findViewById(R.id.preview_button);
	        Button viewButton=(Button)vi.findViewById(R.id.view_button);

	        try {
		        if(UserManager.getInstance().daysRemainingOnSubscription() != 0.f || mClip.getBoolean("is_free")) {
			        viewButton.setOnClickListener(this);
		        } else {
		        	viewButton.setVisibility(View.INVISIBLE);
		        }

		        clipCredit.setText(mClip.getString("clip_credit"));
				title.setText(mClip.getString("clip_title"));
		        imageLoader.DisplayImage(mClip.getString("clip_thumbnail"), thumb_image);
				String clipSampleUrl = mClip.getString("clip_sample_url");

		        if(clipSampleUrl.isEmpty()) {
		        	previewButton.setVisibility(View.GONE);
		        }
		        previewButton.setOnClickListener(this);
	        } catch (JSONException e) {
				e.printStackTrace();
			}

	        return vi;
        }
        
        if(position == 1) {	// 정보
	        vi = inflater.inflate(R.layout.clip_detail_info, null);

	        TextView musicTitle = (TextView)vi.findViewById(R.id.music_title); // title
	        TextView musicSinger	= (TextView)vi.findViewById(R.id.music_singer);
	        TextView genreName	= (TextView)vi.findViewById(R.id.genre_name);
	        TextView clipLevel	= (TextView)vi.findViewById(R.id.clip_level);
	        TextView clipSex	= (TextView)vi.findViewById(R.id.clip_sex);
	        RatingBar ratingBar1	= (RatingBar)vi.findViewById(R.id.rating1);
	        RatingBar ratingBar2	= (RatingBar)vi.findViewById(R.id.rating2);
	        TextView clipRate1	= (TextView)vi.findViewById(R.id.clip_rate1);
	        TextView clipRate2	= (TextView)vi.findViewById(R.id.clip_rate2);
	        TextView clipTerm	= (TextView)vi.findViewById(R.id.clip_term);
	        TextView clipPayment	= (TextView)vi.findViewById(R.id.clip_payment);

	        try {
	        	float rating1	= (float)mClip.getInt("clip_rate1") * 5 / 10;
	        	float rating2	= (float)mClip.getInt("clip_rate2") * 5 / 10;

	        	if(rating1 <= 0.f) {
	        		ratingBar1.setVisibility(View.GONE);
	        		clipRate1.setVisibility(View.VISIBLE);
	        	} else {
	        		ratingBar1.setRating(rating1);
	        		ratingBar1.setVisibility(View.VISIBLE);
	        		clipRate1.setVisibility(View.GONE);
		        }
	        	if(rating2 <= 0.f) {
	        		ratingBar2.setVisibility(View.GONE);
	        		clipRate2.setVisibility(View.VISIBLE);
	        	} else {
	        		ratingBar2.setRating(rating2);
	        		ratingBar2.setVisibility(View.VISIBLE);
	        		clipRate2.setVisibility(View.GONE);
		        }
	        	int intClipTerm	= mClip.getInt("clip_term") * 24;
	        	int intClipSex	= mClip.getInt("clip_sex");
	        	String clipSexString;
	        	if(intClipSex==0)
	        		clipSexString	= "남성";
	        	else if(intClipSex==1)
	        		clipSexString	= "여성";
	        	else
	        		clipSexString	= "남성+여성";
	        	
	        	DecimalFormat formatter = new DecimalFormat("#,###,###");
	        	int intClipPayment	= mClip.getInt("clip_payment");
	        	
				musicTitle.setText(mClip.getString("music_title"));
				musicSinger.setText(mClip.getString("music_singer"));
				genreName.setText(mClip.getString("genre_name"));
				clipLevel.setText(mClip.getString("clip_level"));
				clipSex.setText(clipSexString);
				clipTerm.setText(intClipTerm+" 시간");
				clipPayment.setText(formatter.format(intClipPayment)+" 코인");
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return vi;
        }
        
        if(position==2) {	// 설명
//	        if(convertView==null)
	            vi = inflater.inflate(R.layout.clip_detail_desc, null);
	        TextView clipText = (TextView)vi.findViewById(R.id.clip_text); // title
	        try {
				clipText.setText(mClip.getString("clip_text"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return vi;
        }
        
        if(mCommentRowCount != 0 && position==3) {	// 코멘트 섹션 타이틀
//	        if(convertView==null)
	            vi = inflater.inflate(R.layout.clip_detail_section_title, null);
	        TextView sectionTitle = (TextView)vi.findViewById(R.id.section_title); // title
	        sectionTitle.setText("고객 리뷰");
	        
	        return vi;
        }
        
        if(mCommentRowCount != 0 && position > 3 && position <= mCommentRowCount + 3) {	// 고객 리뷰
//	        if(convertView==null)
	            vi = inflater.inflate(R.layout.clip_detail_comment, null);

	        try {
				JSONObject commentObject = mComments.getJSONObject(position-4);
		        TextView commentText = (TextView)vi.findViewById(R.id.comment_text); // title
		        TextView nickname = (TextView)vi.findViewById(R.id.nickname); // title
		        commentText.setText(commentObject.getString("comment_text"));
		        nickname.setText(commentObject.getString("nickname"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return vi;
        }

        if(mRelateClipRowCount != 0 && position == 3 + mCommentRowCount + mCommentSectionCount) {	// 0-2(헤더) + 1(코멘트 타이틀) + mCommentRowCount
//	        if(convertView==null)
	            vi = inflater.inflate(R.layout.clip_detail_section_title, null);
	        TextView sectionTitle = (TextView)vi.findViewById(R.id.section_title); // title
	        sectionTitle.setText("연관 강좌");
	        
	        return vi;
        }

        if(mRelateClipRowCount != 0 && position > 3 + mCommentRowCount + mCommentSectionCount) {
//	        if(convertView==null)
	            vi = inflater.inflate(R.layout.layout_clip_cell, null);
	        
	            TextView title = (TextView)vi.findViewById(R.id.clip_title); // title
	            ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
	            TextView musicTitle	= (TextView)vi.findViewById(R.id.music_title);
	            RatingBar ratingBar	= (RatingBar)vi.findViewById(R.id.rating);
	            TextView ratingMessage	= (TextView)vi.findViewById(R.id.rating_message);
	            TextView commentCount	= (TextView)vi.findViewById(R.id.comment_count);
	            
	    		try {
					final JSONObject clip = mRelateClips.getJSONObject(position - 4 - mCommentRowCount - mCommentSectionCount);

	    	        title.setText(clip.getString("clip_title"));
	    	        musicTitle.setText(clip.getString("music_title"));
	    	        int clipRate1	= clip.getInt("clip_rate1");
	    	        if(clipRate1 == 0) {
	    	        	ratingBar.setVisibility(View.GONE);
	    	        	ratingMessage.setVisibility(View.VISIBLE);
	    	        } else {
	    		        ratingBar.setRating(clipRate1/2);
	    	        }
	    	        commentCount.setText("리뷰 "+clip.getInt("comment_count")+"개");
	    	        
	    	        imageLoader.DisplayImage(clip.getString("clip_thumbnail"), thumb_image);
	    	        
					vi.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Intent intent = new Intent(mActivity.getApplicationContext(), ClipDetailActivity.class);
							intent.putExtra("clip_id", clip.getInt("clip_id"));
							mActivity.startActivity(intent); // 두번째 액티비티를 실행합니다.
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	            
			return vi;
        }

        vi = inflater.inflate(R.layout.clip_detail_section_title, null);
        TextView sectionTitle = (TextView)vi.findViewById(R.id.section_title); // title
        sectionTitle.setText("에러");
        
    	return vi;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.view_button:
		{
			try {
				boolean isWatchable = mClip.getBoolean("is_watchable");
				if(isWatchable == true) {
					String clipUrl = mClip.getString("clip_url");
					Intent intent = new Intent(mActivity.getApplicationContext(), VodPlayerActivity.class);
					intent.putExtra("clip_url", clipUrl);
					mActivity.startActivity(intent);
				} else if(UserManager.getInstance().daysRemainingOnSubscription() == 0.f ) {
					new AlertDialog.Builder(mActivity)
				    .setTitle("이 강좌는 자유이용권 구매 후 시청하실 수 있습니다.")
				    .setPositiveButton(android.R.string.yes, null)
				    .show();
				} else {
					try {
						String clipUrl = mClip.getString("clip_url");
						Intent intent = new Intent(mActivity.getApplicationContext(), VodPlayerActivity.class);
						intent.putExtra("clip_url", clipUrl);
						mActivity.startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
//			if(UserManager.getInstance(mActivity).isLogin() == false) {
//				new AlertDialog.Builder(mActivity)
//			    .setTitle("로그인 필요")
//			    .setMessage("시청을 하려면 로그인 해 주세요.")
//			    .setPositiveButton(android.R.string.yes, null)
//			    .show();
//				return;
//			}
//			
//			try {
//				SCManager scManager = new SCManager("is_watchable_clip.php", "id="+mClip.getInt("clip_id"), this, mActivity);
//		        scManager.execute();
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
			break;
		case R.id.preview_button:
		{
			Log.i(TAG, "Preview Button Pushed");
			// 샘플 비디오 플래이
			try {
				String clipSampleUrl = mClip.getString("clip_sample_url");
				Intent intent = new Intent(mActivity.getApplicationContext(), VodPlayerActivity.class);
				intent.putExtra("clip_url", clipSampleUrl);
				mActivity.startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			break;
		}
	}

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="submit_stat.php") {
			boolean isBuying	= asyncresult.getBoolean("is_buying");
			
			if(isBuying == false) {
				new AlertDialog.Builder(mActivity)
			    .setTitle("오류")
			    .setMessage("알 수 없는 오류로 강좌를 재생할 수 없습니다. 엔캐시는 차감되지 않습니다.")
			    .setPositiveButton(android.R.string.cancel, null)
			    .show();
				return;
			}
			
			// 엔캐시 차감
			int userNcash	= asyncresult.getJSONObject("user").getInt("user_mobile_ncash");
			UserManager.getInstance(mActivity).setUserNcash(userNcash);
			
			// 동영상 플래이
			try {
				String clipUrl = mClip.getString("clip_url");
				Intent intent = new Intent(mActivity.getApplicationContext(), VodPlayerActivity.class);
				intent.putExtra("clip_url", clipUrl);
				mActivity.startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(tag=="is_watchable_clip.php") {	// 카테고리 별 리스트를 얻는다.
			boolean isWatchable	= asyncresult.getBoolean("is_watchable");
			if(isWatchable == true) {
				String clipUrl	= mClip.getString("clip_url");
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setDataAndType(Uri.parse(clipUrl), "video/mp4");
//				mActivity.startActivity(intent);

				Intent intent = new Intent(mActivity.getApplicationContext(), VodPlayerActivity.class);
				intent.putExtra("clip_url", clipUrl);
				mActivity.startActivity(intent);
			} else {
				int price	= mClip.getInt("clip_payment");
	        	DecimalFormat formatter = new DecimalFormat("#,###,###");
				String commaPrice	=  formatter.format(price);

				if(UserManager.getInstance(mActivity).getUserNcash() >= price) {
					String messageString	= (price == 0)? "무료 강좌이므로, 앤캐쉬가 차감되지 않습니다." : commaPrice + " 캐쉬가 차감됩니다.";
					new AlertDialog.Builder(mActivity)
				    .setTitle("이 강좌를 시청하시겠습니까?")
				    .setMessage(messageString)
				    .setPositiveButton(android.R.string.yes, this)
				    .setNegativeButton(android.R.string.no, null)
				    .show();
				} else {
					new AlertDialog.Builder(mActivity)
				    .setTitle("앤캐시 부족")
				    .setMessage("엔캐시가 부족합니다. 충전하시겠습니까?")
				    .setPositiveButton("충전", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(mActivity.getApplicationContext(), NcashBuyingActivity.class);
							mActivity.startActivity(intent); // 두번째 액티비티를 실행합니다.
						}
					})
				    .setNegativeButton(android.R.string.no, null)
				    .show();
				}
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		try {
			String params	= "number="+mClip.getInt("clip_id") +"&type=c";
			SCManager scManager = new SCManager("submit_stat.php", params, this, mActivity);
	        scManager.execute();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
