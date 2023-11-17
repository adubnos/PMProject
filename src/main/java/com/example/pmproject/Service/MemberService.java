package com.example.pmproject.Service;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.MemberUpdateDTO;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<MemberDTO> memberDTOS(Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=10;

        Page<Member> paging=memberRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC,"memberId")));

        return paging.map(member -> MemberDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .regDate(member.getRegDate())
                .modDate(member.getModDate())
                .build());
    }

    public MemberDTO listOne(String email) {
        Member member=memberRepository.findByEmail(email).orElseThrow();

        return MemberDTO.builder()
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .tel(member.getTel())
                .role(member.getRole())
                .regDate(member.getRegDate())
                .build();
    }

    public String update(MemberUpdateDTO memberUpdateDTO, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();

        if(!passwordEncoder.matches(memberUpdateDTO.getRecentPassword(), member.getPassword())) {
            return null;
        }
        memberUpdateDTO.setNewPassword(passwordEncoder.encode(memberUpdateDTO.getNewPassword()));
        modelMapper.map(memberUpdateDTO.getNewPassword(), member.getPassword());
        Member modify = Member.builder()
                .memberId(member.getMemberId())
                .email(memberUpdateDTO.getEmail())
                .password(memberUpdateDTO.getNewPassword())
                .name(memberUpdateDTO.getName())
                .address(memberUpdateDTO.getAddress())
                .tel(memberUpdateDTO.getTel())
                .findPwdHint(member.getFindPwdHint())
                .findPwdAnswer(member.getFindPwdAnswer())
                .role(member.getRole())
                .build();
        if(!member.getName().equals(memberUpdateDTO.getName())){
            validateDuplicateMember(modify);
        }
        memberRepository.save(modify);

        return modify.getEmail();
    }


    private void validateDuplicateMember(Member modify) {

        memberRepository.findByName(modify.getName()).ifPresent(existingMember -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });
    }

    public boolean withdrawal(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow();

        if(passwordEncoder.matches(password, member.getPassword())) {
            memberRepository.delete(member);
            return true;
        }else {
            return false;
        }
    }

    public void adminWithdrawal(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        memberRepository.delete(member);
    }


}
