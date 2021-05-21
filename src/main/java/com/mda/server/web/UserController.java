package com.mda.server.web;

import com.mda.server.domain.user.User;
import com.mda.server.service.user.UserService;
import com.mda.server.web.dto.UserResponseDto;
import com.mda.server.web.dto.UserSaveRequestDto;
import com.mda.server.web.dto.userEnter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserService userService;
    UserSaveRequestDto user = new UserSaveRequestDto();

    @PostMapping("/test1")
    public User getUser(HttpServletRequest request){

        user.setUserId(request.getParameter("id"));
        user.setUserPwd(request.getParameter("pwd"));
        return userService.save(user);
    }

    @GetMapping("/test2")
    public UserSaveRequestDto test(){
        return user;
    }

    @GetMapping("/test5/{id}")
    public UserResponseDto findById (@PathVariable String id){
        return userService.findById(id);
    }

    //테스트용으로 만들어봤습니다. 나중에 필요하면 고쳐서 사용하세용
    userEnter u1 = new userEnter();
    @PostMapping("/client_enter")
    public userEnter userEnters(HttpServletRequest request){
        u1.setUserId(request.getParameter("userId"));
        u1.setUserLatitude(request.getParameter("userLatitude"));
        u1.setUserLongitude(request.getParameter("userLongitude"));
        return u1;
    }
    //바로위에서 입력받은 값들을 확인하기 위해서 맏는 컨트롤러
    @GetMapping("/client_enter")
    public userEnter showUserEnter(){
        return u1;
    }


}

