package com.androititlan.gdg.androiddesignlibrary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.androititlan.gdg.androiddesignlibrary.R;
import com.androititlan.gdg.androiddesignlibrary.api.MyTwitterApiClient;
import com.androititlan.gdg.androiddesignlibrary.util.ConfigPreferences;
import com.androititlan.gdg.androiddesignlibrary.util.Configure;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "rGx9VWUiQkJMVfXk56BoTh0Nk";
    private static final String TWITTER_SECRET = "uJWsxx4TJeVdvizCiBhRr053t1FmRHouAcaH1kjSrFdmaNpxeZ";


  @Bind(R.id.button_twitter) TwitterLoginButton twitterLoginButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
    Fabric.with(this, new Twitter(authConfig));
    Configure.getTwitterAuthConfig(this);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @Override protected void onResume() {
    super.onResume();
    twitterLogin();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    twitterLoginButton.onActivityResult(requestCode, resultCode, data);
  }

  private void twitterLogin() {

    twitterLoginButton.setCallback(new Callback<TwitterSession>() {
      @Override public void success(Result<TwitterSession> result) {

        TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
        getTwitterProfile(twitterSession, result.data.getUserId(), result.data.getUserName());
        ConfigPreferences.setId(LoginActivity.this, result.data.getUserId());
      }

      @Override public void failure(TwitterException exception) {
        exception.printStackTrace();
        Toast.makeText(LoginActivity.this,
            "Parece que hay un problema con tu conexión intentalo mas tarde.", Toast.LENGTH_SHORT)
            .show();
      }
    });
  }

  private void getTwitterProfile(TwitterSession twitterSession, Long userId,
      final String userName) {

    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
    myTwitterApiClient.getTwitterService().show(userId, null, true, new Callback<User>() {
      @Override public void success(Result<User> result) {
        Toast.makeText(LoginActivity.this, result.data.profileImageUrl, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

        Bundle bundle =
            Configure.getProfile(result.data.name, userName, result.data.profileImageUrl,
                result.data.profileBannerUrl);
        intent.putExtra(Configure.AVATAR_PROFILE, bundle);
        startActivity(intent);
      }

      @Override public void failure(TwitterException e) {
        e.printStackTrace();
      }
    });
  }

  private String getUrlPhotoProfile(String cadena) {
    return cadena.substring(0, cadena.indexOf(Configure.SUFIXE)) + Configure.JPEG_EXTENSION;
  }
}
