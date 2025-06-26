package vn.hau.edumate.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getTimeAgo(String createdAtStr) {
        try {
            // Khớp định dạng bạn đã dùng trong @JsonFormat
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy - HH:mm", Locale.getDefault());
            Date createdAtDate = sdf.parse(createdAtStr);
            Date now = new Date();

            long diffMillis = now.getTime() - createdAtDate.getTime();
            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

            if (diffMillis < 0) {
                return createdAtStr; // thời gian ở tương lai → trả về nguyên chuỗi
            }

            if (diffMinutes < 1) {
                return "Just now";
            } else if (diffMinutes < 60) {
                return diffMinutes + " minutes ago";
            } else if (diffHours < 24) {
                return diffHours + " hours ago";
            } else if (diffDays == 1) {
                return "Yesterday";
            } else if (diffDays < 7) {
                return diffDays + " days ago";
            } else {
                return createdAtStr;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return createdAtStr;
        }
    }
}
