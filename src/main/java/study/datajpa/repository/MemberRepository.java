package study.datajpa.repository;

import jakarta.persistence.Entity;
import org.hibernate.annotations.Fetch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    ex). findByUserNameAndAgeGreaterThan("AAA", 15)
        - UserNameAndAge : [userName and age] -> 쿼리에서 and 조건으로 묶임
        - GreaterThan : 파마미터(15) 조건보다 age가 크면!!
     */
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);   //쿼리 메소드 기능

    List<Member> findTop3By();

    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


    @Query("select  m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUserName(String userName); //컬렉션

    Member findMemberByUserName(String userName); //단건

    Optional<Member> findOptionalByUserName(String userName); //단건 Optional

    //페이징을 구현해주는 객체
    //카운트 쿼리와 분리해서 사용할 수 있음
    @Query(value = "select  m from Member m left join m.team t", countQuery = "select count(m.userName) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //더보기 또는 무한스크롤 페이징 (전체 페이지와 페이지번호가 없음)
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUserName(@Param("userNAme") String userName);

}
