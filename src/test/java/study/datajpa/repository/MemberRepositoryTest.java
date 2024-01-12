package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember(){

        Member member = new Member("userB");
        Member saveMember = memberRepository.save(member);
        Optional<Member> findMember = memberRepository.findById(saveMember.getId());


        assertThat(findMember.get().getUserName()).isEqualTo(saveMember.getUserName());
        assertThat(findMember.get().getId()).isEqualTo(member.getId());
        assertThat(findMember.get()).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 검증
        Optional<Member> findMember1 = memberRepository.findById(member1.getId());
        Optional<Member> findMember2 = memberRepository.findById(member2.getId());

        assertThat(findMember1.get()).isEqualTo(member1);
        assertThat(findMember2.get()).isEqualTo(member2);

        //리스트 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 검증
        Long count = memberRepository.count();
        assertThat(count).isEqualTo(members.size());
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        //삭제 카운트 검증
        Long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }


    @Test
    public void findByUserNameAndAgeGreaterThen(){

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTop3By(){
        List<Member> members = memberRepository.findTop3By();
    }

    @Test
    public void testQuery(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }


    @Test
    public void findUserNameTest(){

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUserNameList();
        result.forEach(s -> System.out.println("s = "+ s));
    }

    @Test
    public void findMemberDtoTest(){

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member1 = new Member("AAA", 10, team1);
        Member member2 = new Member("BBB", 20, team2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> result = memberRepository.findMemberDto();
        result.forEach(m -> System.out.println("member = "+ m.toString()));
    }

    @Test
    public void findByNames(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        result.forEach(m -> System.out.println("member = "+ m.toString()));
    }

    @Test
    public void returnType(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findListMember = memberRepository.findListByUserName("AAA");
        findListMember.forEach(m -> System.out.println("member : "+m.toString()));

        Member findMember = memberRepository.findMemberByUserName("AAA");
        System.out.println("member : "+findMember.toString());

        Optional<Member> findOptionalMember = memberRepository.findOptionalByUserName("AAA");
        System.out.println("member : "+ findOptionalMember.orElseThrow());
    }


    @Test
    public void paging(){

        for(int i=1; i<=20; i++){
            Team team = teamRepository.save(new Team("team"+i));
            memberRepository.save(new Member("user"+i, 10, team));
        }

        // 조회필터에 들어갈 나이를 세팅
        int age = 10;
        // (offset)0 ~ (limit)3 까지 userName기준으로 DESC 정렬
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        // Page반환타입으로 세팅한 값들을 받음
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);

        // memberPage의 있는 컨텐츠를 member의 List로 반환
        List<Member> content = memberPage.getContent();

        // Dto로 변환하는 법!!! 필수!!
        Page<MemberDto> mDtoPage = memberPage.map(m -> new MemberDto(m));

        // 페이징에 쓰이는 전체 컨텐츠 개수를 가져옴
        Long totalCount = memberPage.getTotalElements();

        // 페이지 번호도 가져옴
        int pageNum = memberPage.getNumber();

        //전체 페이지의 개수
        int totPages = memberPage.getTotalPages();

        // 첫번째 페이지가 맞는지 확인
        Boolean firstPage = memberPage.isFirst();

        // 다음 페이지가 있는지 확인
        Boolean nextPage = memberPage.hasNext();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(20);
        assertThat(pageNum).isEqualTo(0);
        assertThat(totPages).isEqualTo(7);
        assertThat(firstPage).isTrue();
        assertThat(nextPage).isTrue();

    }

    @Test
    public void slicePaging(){

        for(int i=1; i<=20; i++){
            memberRepository.save(new Member("user"+i, 10));
        }

        // 조회필터에 들어갈 나이를 세팅
        int age = 10;
        // (offset)0 ~ (limit)3 까지 userName기준으로 DESC 정렬
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        // Page반환타입으로 세팅한 값들을 받음
        Slice<Member> memberPage = memberRepository.findSliceByAge(age, pageRequest);

        // memberPage의 있는 컨텐츠를 member의 List로 반환
        List<Member> content = memberPage.getContent();

        // 페이지 번호도 가져옴
        int pageNum = memberPage.getNumber();

        // 첫번째 페이지가 맞는지 확인
        Boolean firstPage = memberPage.isFirst();

        // 다음 페이지가 있는지 확인
        Boolean nextPage = memberPage.hasNext();

        assertThat(content.size()).isEqualTo(3);
        assertThat(pageNum).isEqualTo(0);
        assertThat(firstPage).isTrue();
        assertThat(nextPage).isTrue();
    }

    @Test
    public void bulkTest(){
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 20));
        memberRepository.save(new Member("user3", 30));
        memberRepository.save(new Member("user4", 40));
        memberRepository.save(new Member("user5", 50));

        //벌크 연산은 DB에 먼저 값을 업데이트하고 영속성 컨텍스트는 업데이트 되지 않은 상태
        int result = memberRepository.bulkAgePlus(20);

        Member member = memberRepository.findMemberByUserName("user3");
        System.out.println(member.getAge());

        AssertionsForClassTypes.assertThat(result).isEqualTo(4);
    }

    @Test
    public void findMemberLazy(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberEntityGraph();
        members.forEach(m -> {
            System.out.println(m.toString());
            System.out.println(m.getTeam().toString());
        });
    }

}