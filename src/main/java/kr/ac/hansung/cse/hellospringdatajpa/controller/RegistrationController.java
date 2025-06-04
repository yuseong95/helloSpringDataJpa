package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.MyRole;
import kr.ac.hansung.cse.hellospringdatajpa.entity.MyUser;
import kr.ac.hansung.cse.hellospringdatajpa.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        MyUser user = new MyUser();
        model.addAttribute("user", user);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(@ModelAttribute("user") MyUser user, Model model) {

        // 1. 이메일 중복 확인
        if (registrationService.checkEmailExists(user.getEmail())) {
            model.addAttribute("emailExists", true);
            return "signup";
        } else {
            // 2. 기본 역할 부여
            List<MyRole> userRoles = new ArrayList<>();

            MyRole role = registrationService.findByRolename("ROLE_USER");
            userRoles.add(role);

            // 3. 특정 이메일인 경우 관리자 권한 추가 부여
            if ("admin@hansung.ac.kr".equals(user.getEmail())) {
                MyRole roleAdmin = registrationService.findByRolename("ROLE_ADMIN");
                userRoles.add(roleAdmin);
            }

            // 4. 사용자 생성 후 로그인 페이지로 리다이렉트
            registrationService.createUser(user, userRoles);

            return "redirect:/login?signup"; // 성공 메시지와 함께 로그인 페이지로
        }
    }
}