package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

//진짜 상속관계는 아니고 속성만 내려서 같이 쓸수 있게 해줌
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    //생성시간은 수정되면 안되기 때문에 false
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;


    //저장에 성공했을 때 실행
    @PrePersist
    public void perPersist(){
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    //플러쉬되는 시점에 실행
    @PreUpdate
    public void preUpdate(){
        this.updatedDate = LocalDateTime.now();
    }
}
