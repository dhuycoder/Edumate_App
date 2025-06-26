package vn.hau.edumate.data.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AISearchHistoryResponse extends AbstractResponse implements Serializable, Parcelable {
    String uid;
    String imageUrl;
    String answer;

    protected AISearchHistoryResponse(Parcel in) {
        super();
        imageUrl = in.readString();
        answer = in.readString();
    }

    public static final Creator<AISearchHistoryResponse> CREATOR = new Creator<AISearchHistoryResponse>() {
        @Override
        public AISearchHistoryResponse createFromParcel(Parcel in) {
            return new AISearchHistoryResponse(in);
        }

        @Override
        public AISearchHistoryResponse[] newArray(int size) {
            return new AISearchHistoryResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(answer);
    }
}
