package com.pigdroid.gameboard.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.app.SettingsActivity;
import com.pigdroid.gameboard.view.detail.contact.ContactListActivity;
import com.pigdroid.gameboard.view.detail.game.GameListActivity;

import org.codehaus.jackson.JsonNode;

public class DrawerActivity extends ServicesActivity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean twoPane = false;
	
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	private View drawerContent;

    private SoundManager soundManager;

	protected void gotoChild(
			Class<?> childActivityClass, Class<?> keepActivityClass) {
        mDrawerLayout.closeDrawer(drawerContent);
		Class<?> clazz = this.getClass();
		if (!clazz.equals(DrawerActivity.class)) {
			Intent intent = new Intent(this, childActivityClass);
			DrawerActivity.this.startActivity(intent);
			if (!clazz.equals(keepActivityClass)) {
				DrawerActivity.this.finish();
			}
			overridePendingTransition(R.anim.from_right, R.anim.to_left);
		}
	}

    @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        soundManager = new SoundManager(this);
	}

	@Override
	protected void onDestroy() {
        soundManager.release();
        soundManager = null;
		super.onDestroy();
	}

    public void play(int r) {
        soundManager.play(r);
    }

	@Override
    protected void onStart() {
        super.onStart();
    }

	@Override
	protected void onKickOff() {
		super.onKickOff();

		final LinearLayout profile = (LinearLayout) findViewById(R.id.profile);
		if (profile != null) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					final JsonNode user = restService.getUserProfileJson();
					if (user != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								TextView text = (TextView) profile.findViewById(android.R.id.text1);
								JsonNode prop = user.get("firstName");
								text.setText(prop != null ? prop.getTextValue() : "No name");

								text = (TextView) profile.findViewById(android.R.id.text2);
								prop = user.get("email");
								text.setText(prop != null ? prop.getTextValue() : "No email");

								ImageView image = (ImageView) profile.findViewById(android.R.id.icon);
								prop = user.get("imageUrl");
								if (prop != null && prop.getTextValue() != null) {
									GameBoardApplication.getPicasso().load(prop.getTextValue()).into(image);
								}
							}
						});
					}
					return null;
				}
			}.execute();
		}

		initDrawerLayout();
		drawerContent = findViewById(R.id.drawer_content);
		if (drawerContent != null) {
//        	LayoutParams params = drawerContent.getLayoutParams();
//        	drawerContent.setLayoutParams(params);
//        	mDrawerLayout.setX(mDrawerLayout.getX() + 60);
//        	drawerContent.setX(drawerContent.getX() + 60);
//        	drawerContent.setVisibility(View.VISIBLE);

			findViewById(R.id.contacts).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoChild(ContactListActivity.class, MainActivity.class);
				}
			});
			findViewById(R.id.games).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoChild(GameListActivity.class, MainActivity.class);
				}
			});
            findViewById(R.id.settings).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            });

			findViewById(R.id.about).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    Intent intent = new Intent(DrawerActivity.this, AboutActivity.class);
                    startActivity(intent);
				}
			});


			mTitle = mDrawerTitle = getTitle();


			// enable ActionBar app icon to behave as action to toggle nav drawer
			android.support.v7.app.ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setHomeButtonEnabled(true);
			}
		}
		getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.gridnoise_repeating));

		final AdView adView = (AdView) findViewById(R.id.adView);
		if (adView != null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AdRequest adRequest = new AdRequest.Builder().build();
					try {
						adView.setAdSize(AdSize.SMART_BANNER);
					} catch (Exception e) {
					}
					try {
						adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
					} catch (Exception e) {
					}
					adView.loadAd(adRequest);

				}
			});
		}
	}

	private void initDrawerLayout() {
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        // ActionBarDrawerToggle ties together the the proper interactions
	        // between the sliding drawer and the action bar app icon
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
//                    R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
	            public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(mTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	
	            public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(mDrawerTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	            @Override
        		public void onDrawerSlide(View drawerView,
        				float slideOffset) {
        			super.onDrawerSlide(drawerView, slideOffset);
        		}
	            
	        };
	        if (!getClass().equals(MainActivity.class)) {
	        	mDrawerToggle.setDrawerIndicatorEnabled(false);
	        } else {
	        	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	        	
	        	//Will Show and hide this thing on first run!
	        	new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
			        	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
					        	mDrawerLayout.openDrawer(Gravity.LEFT);
							}
						});
			        	try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
					        	mDrawerLayout.closeDrawers();
							}
						});
						return null;
					}
				}.execute();
	        }
        }
	}
    
	@Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
    	switch (item.getItemId()) {
            case android.R.id.home:
                if (!getClass().equals(MainActivity.class)) {
                    goToParent();
                } else {
                    mDrawerToggle.onOptionsItemSelected(item);
                }
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 123, null);
                break;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        // Handle action buttons
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (123 == requestCode) {
            //onReloadSettings();
        }
    }

    private boolean goToParent() {
		if (!getClass().equals(MainActivity.class)) {
			finish();
			overridePendingTransition(R.anim.from_left, R.anim.to_right);
            return true;
		}
        return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
			 onBackPressed();
	    	 return true;
	     }
	     return super.onKeyDown(keyCode, event);    
	}
	
	@Override
	public void onBackPressed() {
		if (!goToParent()) {
            finish();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
//		boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerContent);
		return super.onPrepareOptionsMenu(menu);
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
        	mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	public boolean isTwoPane() {
		return twoPane;
	}

	public void setTwoPane(boolean twoPane) {
		this.twoPane = twoPane;
	}
    
}
