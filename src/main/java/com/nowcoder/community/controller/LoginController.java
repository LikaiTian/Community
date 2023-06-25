package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    @ResponseBody
    public Object getRegisterPage(){
        return "主页面";
    }

    @RequestMapping(path = "/register",method=RequestMethod.POST)
    @ResponseBody
    public Object register(User user) throws MessagingException {
        Map<String,Object> map = userService.register(user);
        if(map==null||map.isEmpty()){
            Map<String,Object> msg = new HashMap<>();
            msg.put("msg","注册成功，我们已向你的邮箱放松了激活邮件，请尽快激活!");
            return msg;
        }
        return map;
    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    @ResponseBody
    public Object activation(@PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId, code);
        Map<String,Object> map=new HashMap<>();
        if(result==ACTIVATION_SUCCESS){
            map.put("successMsg",result);
            return map;
        }
        if(result==ACTIVATION_REPEAT){
            map.put("repeatMsg",result);
            return map;
        }
        map.put("failureMsg",result);
        return map;
    }
}
