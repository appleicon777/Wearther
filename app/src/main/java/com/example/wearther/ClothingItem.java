package com.example.wearther;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ClothingItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String category;
    public int warmthLevel;
    public boolean isLayerable;
    public boolean isWaterproof;
    public String colorTag;
    public boolean isJean;
    public boolean isThick;
    public String imageUri;
    public String material;
    
    // Firestore 문서 id 저장 (삭제 처리 시 필요)
    public String documentId;
    
    // 사용자 구분을 위한 필드 (추가)
    public String userId; // 예: Firebase UID

    // 기본 생성자
    public ClothingItem() {}

    // Parcelable 생성자
    protected ClothingItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        warmthLevel = in.readInt();
        isLayerable = in.readByte() != 0;
        isWaterproof = in.readByte() != 0;
        colorTag = in.readString();
        isJean = in.readByte() != 0;
        isThick = in.readByte() != 0;
        imageUri = in.readString();
        material = in.readString();
        documentId = in.readString();
        userId = in.readString();
    }

    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>() {
        @Override
        public ClothingItem createFromParcel(Parcel in) {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size) {
            return new ClothingItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeInt(warmthLevel);
        dest.writeByte((byte) (isLayerable ? 1 : 0));
        dest.writeByte((byte) (isWaterproof ? 1 : 0));
        dest.writeString(colorTag);
        dest.writeByte((byte) (isJean ? 1 : 0));
        dest.writeByte((byte) (isThick ? 1 : 0));
        dest.writeString(imageUri);
        dest.writeString(material);
        dest.writeString(documentId);
        dest.writeString(userId);
    }
}
