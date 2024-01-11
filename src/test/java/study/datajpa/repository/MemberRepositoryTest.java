package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember(){

        Member member = new Member("userB");
        Member saveMember = memberRepository.save(member);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());


        assertThat(findMember.get().getUserName()).isEqualTo(saveMember.getUserName());
        assertThat(findMember.get().getId()).isEqualTo(member.getId());
        assertThat(findMember.get()).isEqualTo(member);
    }

}