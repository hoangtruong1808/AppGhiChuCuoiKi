package com.example.appghichucuoiki;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.bigexcersice.fragments.DanhSachDanhMucFragment;
import com.example.bigexcersice.fragments.DanhSachGhiAmFragment;
import com.example.bigexcersice.fragments.DanhSachGhiChuFragment;
import com.example.bigexcersice.fragments.GhiAmFragment;
import com.example.bigexcersice.fragments.ThemDanhMucFragment;
import com.example.bigexcersice.fragments.ThemGhiChuFragment;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;

import org.json.JSONObject;

public class MainApp extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static final String CHECK_LOGIN = "check_login";
    public static final String GOOGLE_ACCOUNT = "google_account";
    TextView username, gmail;
    ImageView avt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        //anhxa();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DanhSachGhiChuFragment()).commit();
            navigationView.setCheckedItem(R.id.id_nav_dsGhiChu);
        }
        View headerLayout = navigationView.getHeaderView(0); // 0-index header
        /*View headerLayout =
                navigationView.inflateHeaderView(R.layout.nav_header);*/
        username = headerLayout.findViewById(R.id.id_username_txt);
        avt = headerLayout.findViewById(R.id.id_avt_imageView);
        gmail = headerLayout.findViewById(R.id.id_gmail_txt);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_nav_dsGhiChu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DanhSachGhiChuFragment()).commit();
                break;
            case R.id.id_nav_dsDanhMuc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DanhSachDanhMucFragment()).commit();
                break;
            case R.id.id_nav_dsGhiAm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DanhSachGhiAmFragment()).commit();
                break;
            case R.id.id_nav_themGhiChu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ThemGhiChuFragment()).commit();
                break;
            case R.id.id_nav_themDanhMuc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ThemDanhMucFragment()).commit();
                break;
            case R.id.id_nav_themGhiAm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new GhiAmFragment()).commit();
                break;
            case R.id.id_nav_share:
                Uri u = Uri.parse("https://install.appcenter.ms/users/hoangtruong1808-gmail.com/apps/smart-note/distribution_groups/test");
                ShareLinkContent content = new ShareLinkContent.Builder().setQuote("Ứng dụng ghi chú nhanh chóng - V1")
                        .setContentUrl(u)
                        .setShareHashtag(new ShareHashtag.Builder().setHashtag("#sotaydientu").build())
                        .build();
                ShareDialog.show(this, content);

                break;
            case R.id.id_nav_logout:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog.setTitle("Bạn muốn thoát ứng dụng?");
                dialog.setMessage("Xác nhận bên dưới!");
                dialog.setIcon(R.drawable.ico_info_disabled);
                dialog.setCancelable(false);
                dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //log_out_zalo
                        ZaloSDK.Instance.unauthenticate();
                        LoginManager.getInstance().logOut();
                        //log_out_gmail
                        SharedPreferences session = getSharedPreferences("SESSION_GMAIL_LOGIN", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = session.edit();
                        editor.putString("LOGIN", "logoutgmailok");
                        editor.commit();
                        String name = session.getString("LOGIN", "");
                        Intent intent = new Intent(MainApp.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        //Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void anhxa() {
        //  username = findViewById(R.id.id_username_txt);
        //    avt = findViewById(R.id.id_avt_imageView);
    }

    private void getProfileFacebook(AccessToken accessToken) {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String url = profile.getProfilePictureUri(80, 80).toString();
            Picasso.get().load(url).centerInside().fit().into(avt);
            gmail.setText(profile.getId());
            username.setText(profile.getName());
        }
    }

    private void getProfileGmail() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        // System.out.println("thông tin gmail "+googleSignInAccount.getDisplayName() + " " +googleSignInAccount.getEmail());
        if (googleSignInAccount.getPhotoUrl() != null)
            Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(avt);
        //  result.setText(googleSignInAccount.getId());
        username.setText(googleSignInAccount.getDisplayName());
        gmail.setText(googleSignInAccount.getEmail());
    }

    private void getProfileZalo() {
        ZaloSDK.Instance.getProfile(MainApp.this, new ZaloOpenAPICallback() {
            @Override
            public void onResult(JSONObject data) {
                gmail.setText(data.optString("id"));
                username.setText(data.optString("name"));
                JSONObject pic = data.optJSONObject("picture");
                JSONObject picData = pic.optJSONObject("data");
                String url = picData.optString("url");
                Picasso.get().load(url).centerInside().fit().into(avt);
            }
        }, new String[]{"id", "name", "picture"});
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getProfileGmail();
        Intent intent = getIntent();
        String check_login = intent.getStringExtra(CHECK_LOGIN);
        if (check_login.equals("facebook")) {
            getProfileFacebook(AccessToken.getCurrentAccessToken());
        } else if (check_login.equals("zalo")) {
            getProfileZalo();
        } else if (check_login.equals("gmail")) {
            getProfileGmail();
        } else if (check_login.equals("phone")) {
            String sdt = intent.getStringExtra("sdt");
            username.setText(sdt);
        } else if (check_login == null) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_ghichu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.id_addMenu) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ThemGhiChuFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
