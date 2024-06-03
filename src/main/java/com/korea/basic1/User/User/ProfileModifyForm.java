package com.korea.basic1.User.User;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileModifyForm {
    private String userNickname;

    private String email;

    private MultipartFile profileImage;
}
