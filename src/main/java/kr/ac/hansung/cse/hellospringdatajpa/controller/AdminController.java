package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.MyUser;
import kr.ac.hansung.cse.hellospringdatajpa.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String adminHome() {
        return "adminhome";
    }

    @GetMapping("/users")
    @Transactional(readOnly = true)  // 트랜잭션 추가로 Lazy Loading 문제 해결
    public String listUsers(Model model) {
        logger.info("=== AdminController.listUsers() 호출됨 ===");

        try {
            List<MyUser> users = userRepository.findAll();
            logger.info("조회된 사용자 수: {}", users.size());

            for (MyUser user : users) {
                // 여기서 roles에 접근해서 Lazy Loading 강제 실행
                if (user.getRoles() != null) {
                    user.getRoles().size(); // Lazy Loading 강제 실행
                }

                logger.info("사용자 - ID: {}, Email: {}, 역할 수: {}",
                        user.getId(), user.getEmail(),
                        user.getRoles() != null ? user.getRoles().size() : 0);

                if (user.getRoles() != null) {
                    user.getRoles().forEach(role ->
                            logger.info("  -> 역할: {}", role.getRolename()));
                }
            }

            model.addAttribute("users", users);
            logger.info("Model에 users 추가 완료");

        } catch (Exception e) {
            logger.error("사용자 목록 조회 중 오류 발생: ", e);
        }

        return "admin/users";
    }
}