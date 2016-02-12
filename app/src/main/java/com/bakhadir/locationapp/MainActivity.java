package com.bakhadir.locationapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bakhadir.locationapp.listeners.AccessTokenRequestListener;
import com.bakhadir.locationapp.listeners.ImageRequestListener;
import com.bakhadir.locationapp.listeners.UserInfoRequestListener;
import com.bakhadir.locationapp.models.User;

public class MainActivity extends AppCompatActivity implements AccessTokenRequestListener, ImageRequestListener {

    private LocationsAsyncTasks async;
    private ImageView userImage;
    private ViewSwitcher viewSwitcher;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

        userImage = (ImageView) findViewById(R.id.userImage);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
        userName = (TextView) findViewById(R.id.userName);
        Button locationsButton = (Button) findViewById(R.id.locationButton);

        // Set onClickListener
        locationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationsActivity.class);
                startActivity(intent);
            }
        });

        // Access request
        async = new LocationsAsyncTasks(this);
        async.requestAccess(this);
    }

    @Override
    public void onError(String errorMsg) {
        // Do something with the error message
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessGrant(String accessToken) {

        // Get user info
        async.getUserInfo(new UserInfoRequestListener() {

            @Override
            public void onError(String errorMsg) {
                // Some error getting user info
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onUserInfoFetched(User user) {
                // Check if user photo is in user obj (in cache)
                if (user.getBitmapPhoto() == null) {
                    async.getUserPhotoBitmap(MainActivity.this, user.getPhoto());
                } else { // it is indeed in cache
                    userImage.setImageBitmap(user.getBitmapPhoto());
                }
                userName.setText(getString(R.string.user_name, user.getFirstName(), user.getLastName()));
                viewSwitcher.showNext();
                Toast.makeText(MainActivity.this, "Willkommen!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onImageFetched(Bitmap bmp) {
        userImage.setImageBitmap(bmp);
    }

    public LocationsAsyncTasks getFoursquareAsync() {
        return async;
    }

    public void setFoursquareAsync(LocationsAsyncTasks locationsAsyncTasks) {
        this.async = locationsAsyncTasks;
    }
}
