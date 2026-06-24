package com.example.busanbank_loan.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로컬 테스트용 화면(Thymeleaf). 인증 API(1.1~1.5) 동작 확인 페이지를 제공한다.
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
