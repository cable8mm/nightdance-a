/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.nightdance.nightdancea;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import kr.co.nightdance.nightdancea.utils.ISCManagerResponse;
import kr.co.nightdance.nightdancea.utils.Installation;
import kr.co.nightdance.nightdancea.utils.ObservingService;
import kr.co.nightdance.nightdancea.utils.SCManager;
import kr.co.nightdance.nightdancea.utils.UserManager;
import kr.co.nightdance.nightdancea.utils.iap.IabHelper;
import kr.co.nightdance.nightdancea.utils.iap.SkuDetails;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author leesamgu
 * MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkfOKePWAeEe3hrqKFv1ZOXp4zOW51dQy3o5mJGISiDQjER667fs8Txic5Unen4oowCgOZ91uRKcmZIchTHx0rhNuhld6vUh4dPw2gK05SQCx6l0aPb290EQh5KdLYDfk+i6EIL0eavio2aqqtAOXucyQ+LCETnfuhHFyVUN5pbxEL9U0Goz941NpgpDWaThTqtCmVjwMVIp3ZL3gSWTVGoAL6nteqwwzVCROzeXUaZTlRBT2g9qDjTti9YKikdadfCMIbxDC59ivSIrO7DKk/JyGp1FagF/SCqQ+H7mX8h7x1072nXl9rlzVbOQ8NZkvzFxmWc7vXEya9iPCnDcvxQIDAQAB
 */

public class MainActivity extends FragmentActivity implements ISCManagerResponse, Observer {
	public static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int currentFragmentPosition	= 0;
    private TicketsFragment mTicketsFragment	= null;
    private WishClipsFragment mWishClipsFragment	= null;
//    private BoughtClipsFragment mBoughtClipsFragment	= null;
    private ClipsFragment mClipsFragment	= null;
//    private LoginFragment mLoginFragment	= null;
    private UserInfoFragment mUserInfoFragment	= null;
    private Fragment _fragment;
	private IabHelper mHelper;
	public ArrayList<SkuDetails> mProducts;
	public String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkfOKePWAeEe3hrqKFv1ZOXp4zOW51dQy3o5mJGISiDQjER667fs8Txic5Unen4oowCgOZ91uRKcmZIchTHx0rhNuhld6vUh4dPw2gK05SQCx6l0aPb290EQh5KdLYDfk+i6EIL0eavio2aqqtAOXucyQ+LCETnfuhHFyVUN5pbxEL9U0Goz941NpgpDWaThTqtCmVjwMVIp3ZL3gSWTVGoAL6nteqwwzVCROzeXUaZTlRBT2g9qDjTti9YKikdadfCMIbxDC59ivSIrO7DKk/JyGp1FagF/SCqQ+H7mX8h7x1072nXl9rlzVbOQ8NZkvzFxmWc7vXEya9iPCnDcvxQIDAQAB";
    static final int RC_REQUEST = 10002;


    public IabHelper getIabHelper() {
    	return mHelper;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // License
//        mLicenseCheckerCallback	= new MyLicenseCheckerCallback();
//        mHandler = new Handler();
        
        // Visual Setting
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        mHelper = IabHelper.getInstance(this);
        mHelper	= new IabHelper(this);

     // create shortcut if requested
//        addShortcut(this);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
        	public void onDrawerStateChanged(int newState) {
        		if(newState == DrawerLayout.STATE_DRAGGING) {
        			setTitle("");
        		}
        	}
        	
            public void onDrawerClosed(View view) {
            	if(currentFragmentPosition == 0) {
                	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                    getActionBar().setDisplayShowTitleEnabled(false);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	} else {
                	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                   getActionBar().setDisplayShowTitleEnabled(true);
                   setFragmentTitle();
                   
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	}
            }

            public void onDrawerOpened(View drawerView) {
            	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                getActionBar().setDisplayShowTitleEnabled(true);
                setTitle("메인 메뉴");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
		String[] mMenuTitles	= getResources().getStringArray(R.array.main_activity_drawer_items_array);
		
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        String appKey	= Installation.id(this);

        SCManager scManager = new SCManager("get_access_token.php", "consumer_key=android&signature=dksemfhdlem239&app_key="+appKey, this, this);
        scManager.execute();
        
    	ObservingService.getInstance().addObserver("LOG_UPDATED", this);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            Log.i(TAG, "DrawerItemClickListener position = " + position);
        }
    }

    // http://stackoverflow.com/questions/16745001/navigation-drawer-actionbar-button-not-working
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    
    private void selectItem(int position) {	// 동영상 강좌, 찜한 강좌, 구입한 강좌, 다운로드, 내 정보
    	Log.i(TAG, "function selectItem. position="+position);
    	currentFragmentPosition	= position;

    	if(position == 3 || position == 2)	// 내 정보
    	{
    		if(mUserInfoFragment == null) {
    			mUserInfoFragment	= new UserInfoFragment();
    		}
    			_fragment	= mUserInfoFragment;
//    	} else if(position == 2) {
//    		if(mTicketsFragment == null) {
//    			mTicketsFragment	= new TicketsFragment();
//    		}
//    		_fragment	= mTicketsFragment;
    	} else if(position == 1) {
    		if(mWishClipsFragment == null) {
    			mWishClipsFragment	= new WishClipsFragment();
    		}
    		_fragment	= mWishClipsFragment;
    	} else	{// 0 = 동영상 강좌
    		if(mClipsFragment == null) {
    			mClipsFragment	= new ClipsFragment();
    		}
    		_fragment	= mClipsFragment;
    	}

    	FragmentTransaction ft = getFragmentManager().beginTransaction();
    	ft.replace(R.id.content_frame, _fragment);
    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    	ft.addToBackStack(null);
    	ft.commit();
    	
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
    	super.setTitle(title);
        getActionBar().setTitle(title);
    }

    public void setFragmentTitle() {
    	if(_fragment instanceof WishClipsFragment) {
    		setTitle(WishClipsFragment.title);
    		return;
    	}

    	if(_fragment instanceof UserInfoFragment) {
    		setTitle(UserInfoFragment.title);
    		return;
    	}

    	if(_fragment instanceof TicketsFragment) {
    		setTitle(TicketsFragment.title);
    		return;
    	}
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public void scPostResult(String tag, JSONObject asyncresult)
			throws JSONException {
		// TODO Auto-generated method stub
		if(tag=="get_access_token.php") {
			// SCManager set accessToken
			String accessToken	= asyncresult.getString("token");
			SCManager.setAccessToken(accessToken, this);
			
	        SCManager scManager = new SCManager("get_userinfo.php", null, this, this);
	        scManager.execute();
		}
		
		if(tag=="get_userinfo.php") {
			boolean isUser	= asyncresult.getBoolean("is_user");
			if(isUser) {
				JSONObject user	= asyncresult.getJSONObject("user");
				UserManager.getInstance(this).login(user.getInt("user_id"), user.getInt("user_mobile_ncash"), user.getString("nickname"), user.getString("ticket_expired"));
			} else {
				UserManager.getInstance(this).logout();
			}
	        selectItem(0);	// 개시
		}
	}

	@Override
	public void update(Observable observable, Object data) {
//		@SuppressWarnings("unchecked")
//		Hashtable<String,Object> dic	= (Hashtable<String, Object>) data;
//		String k	= (String) dic.get("key");
		
//		if(k=="LOG_UPDATED") {
//			Log.i(TAG, "LOG_UPDATED");
//			boolean isLogin	= (Boolean) dic.get("userInfo");
//			if(isLogin) {
//				if(mUserInfoFragment == null) {
//					mUserInfoFragment	= new UserInfoFragment();
//				}
//
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		        transaction.replace(R.id.content_frame, mUserInfoFragment);
//		        transaction.commit();
//			} else {
//				if(mLoginFragment == null) {
//					mLoginFragment	= new LoginFragment();
//				}
//
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		        transaction.replace(R.id.content_frame, mLoginFragment);
//		        transaction.commit();
//		        
//		        if(mBoughtClipsFragment != null) {
//		        	mBoughtClipsFragment.emptyClips();
//		        }
//		        if(mWishClipsFragment != null) {
//		        	mWishClipsFragment.emptyClips();
//		        }
//			}
//		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
        	
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}