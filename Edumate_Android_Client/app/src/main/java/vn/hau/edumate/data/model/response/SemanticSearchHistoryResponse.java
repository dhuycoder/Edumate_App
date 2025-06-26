package vn.hau.edumate.data.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SemanticSearchHistoryResponse extends AbstractResponse implements Serializable, Parcelable {
    String imageUrl;

    protected SemanticSearchHistoryResponse(Parcel in) {
        super();
        setId(in.readLong());
        imageUrl = in.readString();
    }

    public static final Creator<SemanticSearchHistoryResponse> CREATOR = new Creator<SemanticSearchHistoryResponse>() {
        @Override
        public SemanticSearchHistoryResponse createFromParcel(Parcel in) {
            return new SemanticSearchHistoryResponse(in);
        }

        @Override
        public SemanticSearchHistoryResponse[] newArray(int size) {
            return new SemanticSearchHistoryResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(imageUrl);
    }
}
