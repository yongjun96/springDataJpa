package study.datajpa.repository;

import lombok.Getter;


public class UserNameOnlyDto {

    private String userName;

    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
