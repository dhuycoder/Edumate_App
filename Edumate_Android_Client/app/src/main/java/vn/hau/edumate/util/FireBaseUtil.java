package vn.hau.edumate.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseUtil {
    // Lấy ra FirebaseUser hiện tại
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Lấy UID của người dùng hiện tại
    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Lấy email người dùng hiện tại
    public static String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    // Kiểm tra đã đăng nhập chưa
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
}
