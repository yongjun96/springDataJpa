package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {
    //@Value("#{traget.userName + ' ' + target.age}")
    String getUserName();
}
