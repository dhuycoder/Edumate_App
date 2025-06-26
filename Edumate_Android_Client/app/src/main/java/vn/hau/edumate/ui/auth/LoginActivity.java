package vn.hau.edumate.ui.auth;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.concurrent.Executors;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.TokenResponse;
import vn.hau.edumate.data.network.TokenManager;
import vn.hau.edumate.databinding.ActivityLoginBinding;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.main.MainActivity;

/**
 * Activity for handling user login with Google and Facebook
 */
public class LoginActivity extends BaseActivity {
    private static final String GOOGLE_TAG = "GoogleActivity";
    private static final String FACEBOOK_TAG = "FacebookLogin";

    private ActivityLoginBinding binding;

    private LoginViewModel viewModel;

    // Auth components
    private CallbackManager mCallbackManager;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setupViews();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Setup click listeners
        setupListeners();

        // Observe ViewModel LiveData
        setupObservers();

        // Initialize Facebook CallbackManager
        mCallbackManager = CallbackManager.Factory.create();

        // Initialize CredentialManager for Google Sign-In
        credentialManager = CredentialManager.create(getBaseContext());
    }

    private void setupViews() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initially hide progress bar
        binding.loading.setVisibility(View.INVISIBLE);
    }

    private void setupListeners() {
        binding.btnGoogleLogin.setOnClickListener(view -> launchCredentialManager());
        binding.btnFacebookLogin.setOnClickListener(view -> loginWithFacebook());
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnGoogleLogin.setEnabled(!isLoading);
            binding.btnFacebookLogin.setEnabled(!isLoading);
        });

        // Observe current user
        viewModel.getCurrentUser().observe(this, this::updateUI);

        // Observe authentication success
        viewModel.getAuthSuccess().observe(this, (tokenResponse) -> handleAuthSuccess(tokenResponse, getApplication()));

        // Observe authentication errors
        viewModel.getAuthError().observe(this, errorMessage -> {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Lỗi đăng nhập")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .show();
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in
        viewModel.checkCurrentUser();
    }

    /**
     * Shows success dialog with token information and navigates to MainActivity
     */
    private void handleAuthSuccess(TokenResponse tokenResponse, Application application) {
        /*new AlertDialog.Builder(this)
                .setTitle("Login Successful")
                .setMessage("Access Token:\n" + tokenResponse.getAccessToken()
                        + "\nRefresh Token:\n " + tokenResponse.getRefreshToken()
                        + "\nAccess Token Ex: " + tokenResponse.getAccessTokenExpiresIn()
                        + "\nRefresh ex: \n" + tokenResponse.getRefreshTokenExpiresIn())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();*/
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        Log.d("access token", tokenResponse.getAccessToken());
//        SharedPreferences prefs = application.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
//        prefs.edit().putString("ACCESS_TOKEN", tokenResponse.getAccessToken()).apply();
        TokenManager tokenManager = TokenManager.getInstance(application);
        tokenManager.saveToken(tokenResponse);
        Log.d("refresh token", tokenManager.getRefreshToken());
    }

    /**
     * Initiates Facebook login flow
     */
    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FACEBOOK_TAG, "facebook:onSuccess:" + loginResult);
                viewModel.signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(FACEBOOK_TAG, "facebook:onCancel");
                /*Toast.makeText(LoginActivity.this, "Đăng nhập Facebook đã bị hủy",
                        Toast.LENGTH_LONG).show();*/
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FACEBOOK_TAG, "facebook:onError", error);
                /*Toast.makeText(LoginActivity.this, "Lỗi đăng nhập Facebook: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show()*/
                ;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Launches Google credential manager for sign-in
     */
    private void launchCredentialManager() {
        // Create Google ID option
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.web_client))
                .build();

        // Create the Credential Manager request
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        try {
            credentialManager.getCredentialAsync(
                    this,
                    request,
                    new CancellationSignal(),
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleGoogleCredential(result.getCredential());
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            Log.e(GOOGLE_TAG, "Couldn't retrieve user's credentials: " + e.getLocalizedMessage());
                            /*runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this,
                                        "Lỗi đăng nhập: " + e.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });*/
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(GOOGLE_TAG, "Error launching credential manager: " + e.getLocalizedMessage());
            /*Toast.makeText(this, "Lỗi khi khởi chạy đăng nhập: " + e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();*/
        }
    }

    /**
     * Handles the Google credential response
     */
    private void handleGoogleCredential(Credential credential) {
        if (credential instanceof CustomCredential customCredential
                && customCredential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            viewModel.signInWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Log.w(GOOGLE_TAG, "Credential is not of type Google ID!");
            /*runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this,
                        "Loại xác thực không được hỗ trợ",
                        Toast.LENGTH_SHORT).show();
            });*/
        }
    }

    /**
     * Updates UI based on authentication state
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(GOOGLE_TAG, "User logged in: " + user.getDisplayName());
            // You can update UI for logged in state
            // For example, show profile info or navigate to another screen
        } else {
            Log.d(GOOGLE_TAG, "User not logged in");
            // Update UI for logged out state
        }
    }
}