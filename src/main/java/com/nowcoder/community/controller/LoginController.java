package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

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

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    @ResponseBody
    public void getKaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        //生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image,"png",os);

    }
}
