package vn.hau.edumate.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import vn.hau.edumate.data.model.response.TokenResponse;

public class TokenManager {
    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ACCESS_TOKEN_EXPIRES_IN = "access_token_expires_in";
    private static final String KEY_REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";

    private final SharedPreferences sharedPreferences;
    private static volatile TokenManager instance;

    private TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize encrypted shared preferences", e);
        }
    }

    public static TokenManager getInstance(Context context) {
        if(instance == null) {
            synchronized (TokenManager.class) {
                if(instance == null) {
                    instance = new TokenManager(context);
                }
            }

        }
        return instance;
    }

        public void saveToken(TokenResponse token) {
            sharedPreferences.edit()
                    .putString(KEY_ACCESS_TOKEN, token.getAccessToken())
                    .putString(KEY_REFRESH_TOKEN, token.getRefreshToken())
                    .putLong(KEY_ACCESS_TOKEN_EXPIRES_IN,  token.getAccessTokenExpiresIn())
                    .putLong(KEY_REFRESH_TOKEN_EXPIRES_IN,  token.getRefreshTokenExpiresIn())
                    .apply();
        }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public boolean isAccessTokenExpired() {
        long expiresAt1 = System.currentTimeMillis();
        long expiresAt = sharedPreferences.getLong(KEY_ACCESS_TOKEN_EXPIRES_IN, 0);
        return System.currentTimeMillis() >= expiresAt;
    }

    public boolean isRefreshTokenExpired() {
        long expiresAt = sharedPreferences.getLong(KEY_REFRESH_TOKEN_EXPIRES_IN, 0);
        return System.currentTimeMillis() >= expiresAt;
    }

    public void clearTokens() {
        sharedPreferences.edit().clear().apply();
    }
}
