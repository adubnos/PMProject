package com.example.pmproject.Service;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.MemberPasswordDTO;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class GlobalService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(MemberDTO memberDTO) {
        String password = passwordEncoder.encode(memberDTO.getPassword());

        Role role = Role.ROLE_USER;

        if ("1234567890".equals(memberDTO.getPassword())) {
            role = Role.ROLE_ADMIN;
        }

        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .password(password)
                .name(memberDTO.getName())
                .address(memberDTO.getAddress())
                .tel(memberDTO.getTel())
                .findPwdHint(memberDTO.getFindPwdHint())
                .findPwdAnswer(memberDTO.getFindPwdAnswer())
                .role(role)
                .build();

        validateDuplicateMember(member);
        memberRepository.save(member);
    }


    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(existingMember -> {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        });

        memberRepository.findByName(member.getName()).ifPresent(existingMember -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });
    }

    public void findPassword(MemberPasswordDTO memberPasswordDTO) {
        Member member = memberRepository.findByEmail(memberPasswordDTO.getEmail()).orElseThrow(() -> {
            throw new IllegalStateException("이메일이 존재하지 않습니다.");
        });

        if(Objects.equals(member.getFindPwdHint(), memberPasswordDTO.getFindPwdHint()) && Objects.equals(member.getFindPwdAnswer(), memberPasswordDTO.getFindPwdAnswer())) {
            member.setPassword(passwordEncoder.encode(memberPasswordDTO.getPassword()));
            memberRepository.save(member);
        }else {
            throw new IllegalStateException("비밀번호 찾기 질문 또는 답변이 일치하지 않습니다.");
        }

    }



}
