package kr.ac.hansung.cse.hellospringdatajpa.service;

import kr.ac.hansung.cse.hellospringdatajpa.entity.MyUser;
import kr.ac.hansung.cse.hellospringdatajpa.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

// 인증 관리자는 UserDetailsService를 통해 UserDetails 객체를 획득하고
// 이 UserDetails 객체에서 인증 및 인가에 필요한 정보를 추출하여 사용한다.
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // 1. 이메일로 사용자 조회
        MyUser myUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Email: " + userName + " not found"));

        // 2. Spring Security의 User 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(myUser.getEmail(),
                myUser.getPassword(), getAuthorities(myUser));
    }

    // 3. 사용자의 역할을 Spring Security 권한으로 변환
    private static Collection<? extends GrantedAuthority> getAuthorities(MyUser user) {
        String[] userRoles = user.getRoles()
                .stream()
                .map((role) -> role.getRolename())
                .toArray(String[]::new);

        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }
}