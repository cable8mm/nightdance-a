package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment implements TextWatcher, ISCManagerResponse, OnClickListener {
	public static final String TAG = "LoginFragment";
	private EditText mUsername	= null;
	private EditText mPassword	= null;
	private Button mSubmit	= null;

	private static LoginFragment loginFragment;
	public static synchronized LoginFragment getInstance() {
		if(loginFragment == null) {
			loginFragment	= new LoginFragment();
		}
		return loginFragment;
	}

    public LoginFragment() {
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
    	setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.e(TAG, "function onCreateView");

    	// Draw RootView
    	View rootView	= null;
    	
		rootView = inflater.inflate(R.layout.fragment_userinfo_log, container, false);
        getActivity().setTitle("로그인");
        
        mUsername	= (EditText)rootView.findViewById(R.id.username);
        mUsername.addTextChangedListener(this);
        mPassword	= (EditText)rootView.findViewById(R.id.password);
        mPassword.addTextChangedListener(this);
        mSubmit	= (Button)rootView.findViewById(R.id.submit);
        mSubmit.setEnabled(false);
        mSubmit.setOnClickListener(this);
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		mSubmit.setEnabled(false);
		String username	= mUsername.getText().toString();
		String password	= mPassword.getText().toString();
        SCManager scManager = new SCManager("login_as_nightdance.php", "username="+username+"&password="+password, this, getActivity());
        scManager.execute();
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    	inflater.inflate(R.menu.login_fragment, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_signin:
        {
        	Intent intent	= new Intent(getActivity().getApplicationContext(), SigninActivity.class);
			startActivity(intent); // 회원 가입 액티비티를 실행합니다.
        	return true;
        }
        default:
            break;
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
//				UserManager.getInstance(getActivity()).login(userId,userNcash,nickname);
				
				// close virtual keyboard
				InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			    mgr.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
			} else {
				mSubmit.setEnabled(true);
				Toast.makeText(getActivity(), "아이디나 비밀번호가 틀립니다.\n다시 입력해 주세요.", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(mUsername.getText().length() != 0 && mPassword.getText().length() != 0) {
            mSubmit.setEnabled(true);
		}
	}
}
