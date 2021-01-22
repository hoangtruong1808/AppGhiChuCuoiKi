package com.example.appghichucuoiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigexcersice.database.Database;
import com.example.bigexcersice.fragments.DanhSachGhiChuFragment;
import com.example.bigexcersice.model.Account;
import com.example.bigexcersice.model.CongViec;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnOkRegis, btnCancelRegis, btnOkOTP, btnCancelOTP;
    TextView txtRegister;
    EditText edtPhone, edtPhoneRegis, edtOTP;
    //login by phone (OTP)
    String sdt, verificationId;
    FirebaseAuth mAuth;
    Dialog dialogOTP;

    //login by gmail
    SignInButton googleSignInButton;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 101;

    //login by zalo
    Button btnLoginZalo;

    //login by facebook
    LoginButton btnLoginFacebook;
    CallbackManager callbackManager;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCenter.start(getApplication(), "18011060-503a-451c-ba36-2234bf402a63",
                Analytics.class, Crashes.class);

        database = new Database(MainActivity.this, "ghichu.sqlite", null, 1);
       // database.QueryData("DROP TABLE Account");
        database.QueryData("CREATE TABLE IF NOT EXISTS Account(Id INTEGER PRIMARY KEY AUTOINCREMENT,TaiKhoan NVARCHAR(200), NgayTao TEXT)");

        anhXa();
        EventLogin();
        EventRegister();
        //login bằng facebook
        btnLoginFacebook = (LoginButton) findViewById(R.id.id_loginFacebook_btn);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(MainActivity.this, MainApp.class);
                intent.putExtra(MainApp.CHECK_LOGIN, "facebook");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Đăng Nhập Facebook Thành Công", Toast.LENGTH_LONG).show();//+ loginResult.getAccessToken().getUserId());
                loginByThirdParty(Profile.getCurrentProfile().getId());
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_LONG).show();
            }
        });
        //login bằng zalo
        if (ZaloSDK.Instance.isAuthenticate(null)) {
            onLoginSuccess();
        }
        btnLoginZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginZalo();
            }
        });

        //login bằng gmail
        googleSignInButton = findViewById(R.id.id_loginGmail_btn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void anhXa() {
        btnLogin = findViewById(R.id.id_login_btn);
        edtPhone = findViewById(R.id.id_phone_edt);
        txtRegister = findViewById(R.id.id_register_edt);
        btnLoginZalo = findViewById(R.id.id_loginZalo_btn);
    }

    private void EventLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPhone.getText().toString().equals("") || edtPhone.getText().toString() == null || edtPhone.getText().length() < 10) {
                    edtPhone.setError("Số điện thoại không Hợp Lệ");
                    edtPhone.requestFocus();
                    return;
                }
                boolean flag = false;
                Cursor dataAccount = database.GetData("SELECT * FROM Account WHERE TaiKhoan = '"+edtPhone.getText().toString().trim()+"'");
                while (dataAccount.moveToNext()) {
                    int idAc = dataAccount.getInt(0);
                    String taiKhoanAc = dataAccount.getString(1);
                    String dateAc = dataAccount.getString(2);
                    Account account = new Account(idAc, taiKhoanAc, dateAc);
                    System.out.println("aaaa: "+account);
                    if(taiKhoanAc.equals(edtPhone.getText().toString().trim())){
                        loginByThirdParty(taiKhoanAc);
                        flag = true;
                        Intent intent = new Intent(MainActivity.this, MainApp.class);
                        intent.putExtra(MainApp.CHECK_LOGIN, "phone");
                        intent.putExtra("sdt",taiKhoanAc);
                        startActivity(intent);
                        finish();
                    }
                }
                if(flag == false){
                    Toast.makeText(MainActivity.this,"Số điện thoại chưa đăng ký!", Toast.LENGTH_LONG).show();
                }
                //ktr dtb
            }
        });
    }

    public void EventRegister() {
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Đăng Ký Tài Khoản");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.register_custom_dialog);
                //Regis
                edtPhoneRegis = dialog.findViewById(R.id.id_phoneRegis_edt);
                btnOkRegis = (Button) dialog.findViewById(R.id.id_okRegis_btn);
                btnCancelRegis = (Button) dialog.findViewById(R.id.btn_cancelRegis_btn);

                btnCancelRegis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btnOkRegis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sdt = edtPhoneRegis.getText().toString();
                        if (sdt.equals("") || sdt.length() < 10) {
                            edtPhoneRegis.setError("SĐT Không Hợp Lệ");
                            edtPhoneRegis.requestFocus();
                            return;
                        }
                        boolean flag = false;
                        Cursor dataAccount = database.GetData("SELECT * FROM Account WHERE TaiKhoan = '"+sdt+"'");
                        while (dataAccount.moveToNext()) {
                            int idAc = dataAccount.getInt(0);
                            String taiKhoanAc = dataAccount.getString(1);
                            String dateAc = dataAccount.getString(2);
                            Account account = new Account(idAc, taiKhoanAc, dateAc);
                            if(taiKhoanAc.equals(sdt)){
                                flag = true;
                            }
                        }
                        if(flag == true){
                            Toast.makeText(MainActivity.this,"Số điện thoại đã đăng ký!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //Nhắn OTP
                        //Gửi OTP cho sdtư
                        mAuth = FirebaseAuth.getInstance();
                        sendVerificationCode(sdt);
                        //tắt dialog regis
                        dialog.cancel();
                        dialogOTP = new Dialog(MainActivity.this);
                        dialogOTP.setTitle("Nhập OTP");
                        dialogOTP.setCancelable(false);
                        dialogOTP.setContentView(R.layout.otp_custom_dialog);
                        //Regis
                        edtOTP = dialogOTP.findViewById(R.id.id_otp_edt);
                        btnOkOTP = (Button) dialogOTP.findViewById(R.id.id_okOTP_btn);
                        btnCancelOTP = (Button) dialogOTP.findViewById(R.id.btn_cancelOTP_btn);
                        //=======================
                        btnOkOTP.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String code = edtOTP.getText().toString().trim();
                                if (code.isEmpty() || code.length() < 6) {
                                    edtOTP.setError("Nhập Mã OTP");
                                    //return;
                                } else {
                                    verifyCode(code);
                                }
                            }
                        });
                        btnCancelOTP.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogOTP.cancel();
                            }
                        });
                        dialogOTP.show();
                    }
                });
                dialog.show();
            }
        });
    }

    //==================================================START - OTP==================================================
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //chuyển thông tin qua bên đăng nhập sau khi đăng kí thành công
                            //lưu dtb
                            System.out.println("ok rồi! " + sdt);
                            edtPhone.setText(sdt);
                            loginByThirdParty(sdt);
                            dialogOTP.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "OTP không hợp lệ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        //fun: lấy cái mã trong tn điền tự động vào input
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                //editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(MainActivity.this, "Yêu Cầu Không Hợp Lệ", Toast.LENGTH_LONG).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(MainActivity.this, "Quá Nhiều Yêu Cầu", Toast.LENGTH_LONG).show();
            }
        }
    };

    //==================================================END - OTP==================================================
    // BEGIN _ GMAIL
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //của zalo
        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //gmail
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                        Log.d("EMAIL ", googleSignInAccount.getEmail());
                        onLoggedIn(googleSignInAccount);
                        loginByThirdParty(googleSignInAccount.getId());
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w("Main_Activity", "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
        }

    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        SharedPreferences session = getSharedPreferences("SESSION_GMAIL_LOGIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.putString("LOGIN", "logingmailok");
        editor.commit();
        Intent intent = new Intent(this, MainApp.class);
        intent.putExtra(MainApp.GOOGLE_ACCOUNT, googleSignInAccount);
        intent.putExtra(MainApp.CHECK_LOGIN, "gmail");
        startActivity(intent);
        System.out.println("rồi vô đây" + googleSignInAccount);
        finish();
    }

    // nếu đã đăng nhập mail r
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences session = getSharedPreferences("SESSION_GMAIL_LOGIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        String name = session.getString("LOGIN", "");
        Log.d("login", name);
        if (name.equals("logingmailok")) {
            GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (alreadyloggedAccount != null) {
                Toast.makeText(this, "Đăng Nhập Gmail Thành Công", Toast.LENGTH_LONG).show();
                //   signOut();
                onLoggedIn(alreadyloggedAccount);
            } else {
                System.out.println("not logged in");
            }
        } else if (name.equals("logoutgmailok")) {
            signOut();
            revokeAccess();
        } else {
            Log.d("gmail login: ", "fail");
        }

    }

    public void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(MainActivity.this, "Google Sign Out done.", Toast.LENGTH_SHORT).show();
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //  Toast.makeText(MainActivity.this, "Google access revoked.", Toast.LENGTH_SHORT).show();
                        //getProfileInformation(null);
                    }
                });
    }

    //END _ GMAIL
    public static String getSHA(Context ctx) throws Exception {
        PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
        for (Signature signature : info.signatures) {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            String sig = Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
            if (sig.trim().length() > 0) {
                return sig;
            }
        }
        return null;
    }

    //BEGIN _ ZALO
    private void loginZalo() {
        ZaloSDK.Instance.authenticate(this, LoginVia.APP, listener);
    }

    OAuthCompleteListener listener = new OAuthCompleteListener() {
        @Override
        public void onGetOAuthComplete(OauthResponse response) {
            if (TextUtils.isEmpty(response.getOauthCode())) {
                onLoginError(response.getErrorCode(), response.getErrorMessage());
            } else {
                ZaloSDK.Instance.getProfile(MainActivity.this, new ZaloOpenAPICallback() {
                    @Override
                    public void onResult(JSONObject data) {
                        String id = data.optString("id");
                        loginByThirdParty(id);
                        onLoginSuccess();
                        Toast.makeText(MainActivity.this, "Đăng Nhập Zalo Thành Công", Toast.LENGTH_LONG).show();
                    }
                }, new String[]{"id"});

            }
        }

        @Override
        public void onAuthenError(int errorCode, String message) {
            if (message == null) message = "Unknown error";
            onLoginError(errorCode, message);
        }
    };

    private void onLoginSuccess() {
        Intent intent = new Intent(this, MainApp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MainApp.CHECK_LOGIN, "zalo");
        startActivity(intent);
        finish();
    }

    private void onLoginError(int code, String message) {
        Toast.makeText(this, "Code: " + code + " Message " + message, Toast.LENGTH_LONG).show();
    }

    private void loginByThirdParty(String id) {
        //lưu xuống csdl
        System.out.println("ĐĂNG NHẬP: "+id);
        Cursor dataAccount = database.GetData("SELECT * FROM Account WHERE TaiKhoan = '"+id+"'");
        boolean flag = false;
        while (dataAccount.moveToNext()) {
            int idAc = dataAccount.getInt(0);
            String taiKhoanAc = dataAccount.getString(1);
            String dateAc = dataAccount.getString(2);
            //Account account = new Account(idAc, taiKhoanAc, dateAc);
            if(taiKhoanAc.equals(id)){
               flag = true;
            }
        }
        if(flag == false) {
            java.util.Date utilDate = new java.util.Date();
            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            database.QueryData("INSERT INTO Account VALUES(null, '" + id + "','" + sf.format(utilDate) + "')");
        }
        saveIdUser(id);

    }

    private void saveIdUser(String id) {
        SharedPreferences pre = getSharedPreferences("ID_USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString("id", id);
        edit.commit();

        System.out.println("ĐĂNG NHẬP 2: "+getProfileLogin());
    }

    private String getProfileLogin() {
        SharedPreferences pre = getSharedPreferences("ID_USER", MODE_PRIVATE);
        String id = pre.getString("id", "");
        return id;
    }
}
