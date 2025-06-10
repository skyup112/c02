package com.example.b03.util;

import com.example.b03.domain.Member;
import com.example.b03.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordMigrationRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        List<Member> members = memberRepository.findAll();
        boolean changed = false;

        for (Member member : members) {
            String pw = member.getPassword();

            // BCrypt는 $2로 시작하고 60자 이상, 아니라면 평문으로 간주
            if (pw != null && (!pw.startsWith("$2") || pw.length() < 60)) {
                String encoded = passwordEncoder.encode(pw);
                member.setPassword(encoded);
                changed = true;
                System.out.println("암호화된 회원: " + member.getLoginId());
            }
        }

        if (changed) {
            memberRepository.saveAll(members);
            System.out.println("✅ 비밀번호 마이그레이션 완료");
        } else {
            System.out.println("ℹ️ 암호화할 비밀번호 없음 (이미 완료됨)");
        }
    }
}
