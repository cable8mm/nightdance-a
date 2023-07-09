package kr.co.nightdance.nightdancea;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.ObservingService;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserInfoFragment extends PreferenceFragment implements OnPreferenceChangeListener, Observer, TextWatcher, ISCManagerResponse, DialogInterface.OnClickListener, OnPreferenceClickListener {
	public static final String TAG = "UserInfoFragment";
	private EditText mUsername	= null;
	private EditText mPassword	= null;
	private AlertDialog loginDialog;
	private Preference mLoginoutPreference;
	private EditTextPreference mNicknamePreference;
	private String mNewNickname;
    public static String title	= "내 정보";
	
	private static UserInfoFragment userInfoFragment;

	public static synchronized UserInfoFragment getInstance() {
		if(userInfoFragment == null) {
			userInfoFragment	= new UserInfoFragment();
		}
		return userInfoFragment;
	}
	
    public UserInfoFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(savedInstanceState != null) {
            for(String key : savedInstanceState.keySet()) {
                Log.i(TAG, "bundle key : " + key);
            }
    	}
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    	setHasOptionsMenu(true);

		LayoutInflater inflater	= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout	= inflater.inflate(R.layout.layout_dialog_login, null);
		mUsername	= (EditText)layout.findViewById(R.id.username);
		mPassword	= (EditText)layout.findViewById(R.id.password);
        mUsername.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);

		loginDialog	= new AlertDialog.Builder(getActivity())
		.setView(layout)
		.setTitle("로그인")
		.setPositiveButton(android.R.string.yes, this)
		.setNegativeButton(android.R.string.no, null)
		.create();
		
		loginDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button yesButton	= (Button)((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				yesButton.setEnabled(false);
			}
			
		});

    	ObservingService.getInstance().addObserver("LOG_UPDATED", this);

    	mLoginoutPreference	= (Preference) findPreference("loginout_preference");
    	mNicknamePreference	= (EditTextPreference) findPreference("nickname_preference");

    	if(UserManager.getInstance().isLogin()) {
    		Log.i(TAG, "LOGIN");
    		login();
    	} else {
    		Log.i(TAG, "LOGOUT");
    		logout();
    	}
    	
    	mLoginoutPreference.setOnPreferenceClickListener(this);
    	mNicknamePreference.setOnPreferenceClickListener(this);
    	mNicknamePreference.setOnPreferenceChangeListener(this);
    }
    
    private void login() {
		mLoginoutPreference.setTitle("로그아웃");
		String nickname	= UserManager.getInstance().getNickname();
		mNicknamePreference.setTitle(nickname);
    	mNicknamePreference.setText(nickname);
    	mNicknamePreference.setSummary("닉네임, 수정할 수 있습니다.");
    	mNicknamePreference.setSelectable(true);
    	mNicknamePreference.setOnPreferenceClickListener(this);
    	getActivity().invalidateOptionsMenu();
    }
    
    private void logout() {
		mLoginoutPreference.setTitle("로그인");
		mNicknamePreference.setTitle("닉네임");
		mNicknamePreference.setText("Guest");
    	mNicknamePreference.setSummary("로그인 후 닉네임을 변경할 수 있습니다.");
    	mNicknamePreference.setSelectable(false);
    	mNicknamePreference.setOnPreferenceClickListener(null);
    	getActivity().invalidateOptionsMenu();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
        getActivity().setTitle("내 정보");
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_registration);
        if(UserManager.getInstance().isLogin())
        	item.setVisible(false);
        else
        	item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    	inflater.inflate(R.menu.user_info_fragment, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_registration:
			Intent intent = new Intent(getActivity().getApplicationContext(), SigninActivity.class);
			startActivity(intent); // 두번째 액티비티를 실행합니다.
        	return true;
        default:
            break;
        }

        return false;
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Log.i(TAG, "username and password = " + mUsername.getText() + " , " + mPassword.getText());
		if(mUsername.getText().toString().trim().equals("")) {
			Toast.makeText(getActivity(), "아이디를 넣어주세요.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mPassword.getText().toString().trim().equals("")) {
			Toast.makeText(getActivity(), "비밀번호를 넣어주세요.", Toast.LENGTH_LONG).show();
			return;
		}
		
        SCManager scManager = new SCManager("login_as_nightdance.php", "username="+mUsername.getText()+"&password="+mPassword.getText(), this, getActivity());
        scManager.execute();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference == mLoginoutPreference) {
	    	if(UserManager.getInstance().isLogin()) {
				new AlertDialog.Builder(getActivity())
			    .setTitle("로그아웃")
			    .setMessage("이 단말기에 있는 정보는 삭제됩니다. 로그아웃 하시겠습니까?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
						UserManager.getInstance().logout();
			        }
			     })
			    .setNegativeButton(android.R.string.no, null)
			    .show();
	    	} else {
	    		loginDialog.show();
	    	}
		}
		
		if(preference == mNicknamePreference) {
			Log.i(TAG, "onPreferenceClick, preference == mNicknamePreference");
//			new AlertDialog.Builder(getActivity())
//		    .setTitle("닉네임 수정")
//		    .setMessage("새로운 닉네임을 입력하고 확인을 누르세요.")
//		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//		        public void onClick(DialogInterface dialog, int which) { 
//					UserManager.getInstance(getActivity()).logout();
//		        }
//		     })
//		    .setNegativeButton(android.R.string.no, null)
//		    .show();
		}
		
		return false;
	}

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="login_as_nightdance.php") {
			boolean isLogin	= asyncresult.getBoolean("is_login");
			
			if(isLogin) {
				JSONObject user	= asyncresult.getJSONObject("user");
				int userId	= user.getInt("user_id");
				int userNcash	= user.getInt("user_mobile_ncash");
				String nickname	= user.getString("nickname");
				String ticketExpired	= user.getString("ticket_expired");
				UserManager.getInstance(getActivity()).login(userId,userNcash,nickname, ticketExpired);
				
				// close virtual keyboard
//				InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			    mgr.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
			} else {
				Toast.makeText(getActivity(), "아이디나 비밀번호가 틀립니다.\n다시 입력해 주세요.", Toast.LENGTH_LONG).show();
			}
		}
		
		if(tag=="change_nickname.php") {
			boolean isChanged	= asyncresult.getBoolean("isChanged");
			
			if(isChanged) {
				UserManager.getInstance().setNickname(mNewNickname);
				mNicknamePreference.setTitle(mNewNickname);
				mNicknamePreference.setText(mNewNickname);
				Log.i(TAG, "New Nickname = " + mNewNickname);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(mUsername.getText().length() != 0 && mPassword.getText().length() != 0) {
			Button yesButton	= (Button)loginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
			yesButton.setEnabled(true);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> dic	= (Hashtable<String, Object>) data;
		String k	= (String) dic.get("key");
		
		if(k=="LOG_UPDATED") {
			Log.i(TAG, "LOG_UPDATED");
			boolean isLogin	= (Boolean) dic.get("userInfo");
			if(isLogin) {
				login();
			} else {
				logout();
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference == mNicknamePreference) {
			mNewNickname	= ((String)newValue).trim();
			if(mNewNickname.toString().equals("")) {
				
				return true;
			}
			
	        SCManager scManager = new SCManager("change_nickname.php", "nickname="+mNewNickname, this, getActivity());
	        scManager.execute();
		}
		return false;
	}
}
