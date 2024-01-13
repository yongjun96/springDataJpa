package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Optional<Member> findMember = memberRepository.findById(id);
        return findMember.get().getUserName();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUserName();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "userName") Pageable pageable){
        PageRequest pageRequest = PageRequest.of(1, 2);

        Page<Member> page = memberRepository.findAll(pageRequest);
        return page.map(m -> new MemberDto(m));
    }

    //테스트를 위해 생성
    @PostConstruct
    public void init(){
        for(int i=0; i<=100; i++){
            memberRepository.save(new Member("user"+i, i));
        }
    }

}
