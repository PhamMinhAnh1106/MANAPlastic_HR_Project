package com.manaplastic.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
//    @GetMapping({"/", "/{path:[^\\.]*}", "/{path:.*}/{path2:.*}"} // test FE
@GetMapping({"/", "/{path:^(?!error|swagger-ui|v3|webjars|api).*}/**"}) // swagger
    public String forwardFrontEndPaths() {
//        return "forward:/";
    return "redirect:/index.html"; // Thay forward bằng redirect
    }
}
