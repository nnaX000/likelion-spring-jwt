package likelion.practice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String password;
    private String name;
    private String profileImage; // 프로필 이미지 (옵션)

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
