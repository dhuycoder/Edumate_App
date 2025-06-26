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
public class SemanticSearchResponse extends AbstractResponse implements Serializable, Parcelable {

    String imageUrl;

    protected SemanticSearchResponse(Parcel in) {
        super();
        setId(in.readLong());
        imageUrl = in.readString();
    }

    public static final Creator<SemanticSearchResponse> CREATOR = new Creator<SemanticSearchResponse>() {
        @Override
        public SemanticSearchResponse createFromParcel(Parcel in) {
            return new SemanticSearchResponse(in);
        }

        @Override
        public SemanticSearchResponse[] newArray(int size) {
            return new SemanticSearchResponse[size];
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
