package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {


    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){

        Member member = new Member("userA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);   // 같은 인스턴스 보장 (1차 캐시)
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 검증
        Optional<Member> findMember1 = memberJpaRepository.findById(member1.getId());
        Optional<Member> findMember2 = memberJpaRepository.findById(member2.getId());

        assertThat(findMember1.get()).isEqualTo(member1);
        assertThat(findMember2.get()).isEqualTo(member2);

        //리스트 검증
        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 검증
        Long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(members.size());
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        //삭제 카운트 검증
        Long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }


    @Test
    public void findByUserNameAndAgeGreaterThen(){

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void paging(){

        for(int i=1; i<=20; i++){
            memberJpaRepository.save(new Member("user"+i, 10));
        }

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> memberPage = memberJpaRepository.findBpPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(memberPage.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(20);
    }

    @Test
    public void bulkTest(){
        memberJpaRepository.save(new Member("user1", 10));
        memberJpaRepository.save(new Member("user2", 20));
        memberJpaRepository.save(new Member("user3", 30));
        memberJpaRepository.save(new Member("user4", 40));
        memberJpaRepository.save(new Member("user5", 50));

        int result = memberJpaRepository.bulkAgePlus(20);

        assertThat(result).isEqualTo(4);
    }

}