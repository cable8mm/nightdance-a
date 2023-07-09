package kr.co.nightdance.nightdancea;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends Activity implements ISCManagerResponse, TextWatcher, OnClickListener, DialogInterface.OnClickListener {
	public static final String TAG = "SigninActivity";
	public EditText mUsername;
	public EditText mPassword;
	public EditText mPasswordConfirm;
	public EditText mNickname;
	public EditText mEmailAddress;
	public Button mSubmit;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        
        setTitle("회원 가입");
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mUsername	= (EditText)findViewById(R.id.username);
        mPassword	= (EditText)findViewById(R.id.password);
        mPasswordConfirm	= (EditText)findViewById(R.id.password_confirm);
        mNickname	= (EditText)findViewById(R.id.nickname);
        mEmailAddress	= (EditText)findViewById(R.id.email_address);
        mSubmit	= (Button)findViewById(R.id.submit);
        
        mUsername.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mPasswordConfirm.addTextChangedListener(this);
        mNickname.addTextChangedListener(this);
        mEmailAddress.addTextChangedListener(this);
        
        mSubmit.setEnabled(false);
        mSubmit.setOnClickListener(this);
    }

	@Override
	public void afterTextChanged(Editable arg0) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int before,
			int count) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.i(TAG, "onTextChanged " + mUsername.getText().length() + " " + mPassword.getText().length() + " " + mPasswordConfirm.getText().length());
		// 필수입력사항 체크
		if(mUsername.getText().length() == 0 
				|| mPassword.getText().length() == 0 
				|| mPasswordConfirm.getText().length() == 0
				|| mPassword.getText().toString().equals(mPasswordConfirm.getText().toString()) == false
				|| mNickname.getText().length() == 0
				) {
			Log.i(TAG, "필수입력사항 체크");
            mSubmit.setEnabled(false);
			return;
		}

		// 이메일을 입력했지만, 이메일 형태가 아닐 경우...
		if(mEmailAddress.getText().length() != 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddress.getText().toString()).matches() == false) {
			Log.i(TAG, "이메일을 입력했지만, 이메일 형태가 아닐 경우...");
			mSubmit.setEnabled(false);
			return;
		}
		
		mSubmit.setEnabled(true);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.submit:
		{
			String params	= "username="+mUsername.getText().toString()
					+"&password="+mPassword.getText().toString()
					+"&nickname="+mNickname.getText().toString()
					+"&email_address="+mEmailAddress.getText().toString()
					;
	        SCManager scManager = new SCManager("submit_account.php", params, this, this);
	        scManager.execute();
		}
			break;
		}
	}

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		if(tag=="submit_account.php") {
			boolean isRegistrated	= asyncresult.getBoolean("isRegistrated");
			if(isRegistrated == false) {
				String message	= asyncresult.getString("message");
		        AlertDialog.Builder bld = new AlertDialog.Builder(this);
		        bld.setMessage(message);
		        bld.setNeutralButton("OK", null);
		        Log.d(TAG, "Showing alert dialog: " + message);
		        bld.create().show();
			} else {	// 로그인 성공.
		        AlertDialog.Builder bld = new AlertDialog.Builder(this);
		        bld.setMessage("회원 가입이 완료되었습니다. 축하합니다.");
		        bld.setPositiveButton("로그인", this);
		        Log.d(TAG, "Showing alert dialog: " + "회원 가입이 완료되었습니다. 축하합니다.");
		        bld.create().show();
			}
			return;
		}
		
		if(tag=="login_as_nightdance.php") {
			boolean isLogin	= asyncresult.getBoolean("is_login");
			
			if(isLogin) {
				JSONObject user	= asyncresult.getJSONObject("user");
				int userId	= user.getInt("user_id");
				int userNcash	= user.getInt("user_mobile_ncash");
				String nickname	= user.getString("nickname");
				String ticketExpired	= user.getString("ticket_expired");
				UserManager.getInstance(this).login(userId,userNcash,nickname, ticketExpired);
				
				// close virtual keyboard
				InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    mgr.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
			    
			    finish();
			} else {
				mSubmit.setEnabled(true);
				Toast.makeText(this, "아이디나 비밀번호가 틀립니다.\n다시 입력해 주세요.", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
        SCManager scManager = new SCManager("login_as_nightdance.php", "username="+mUsername.getText().toString()+"&password="+mPassword.getText().toString(), this, this);
        scManager.execute();
	}
}
