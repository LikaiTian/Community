package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {


    @Value("${server.servlet.context-path}")
    private String contextPath;

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
    public void getKaptcha(@ApiIgnore HttpServletResponse response,@ApiIgnore HttpSession session) throws IOException {
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

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    @ResponseBody
    public Object login(String username,String password,String code,
                        boolean rememberme,@ApiIgnore HttpSession session,
                        @ApiIgnore HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();

        //从session中取得验证码
        String kaptcha = (String) session.getAttribute("kaptcha");

        //检查验证码
        if(StringUtils.isBlank(kaptcha)
        ||StringUtils.isBlank(code)
        ||!kaptcha.equals(code)){
            map.put("kaptchaMsg","验证码错误!");
            return map;
        }

        //检查账号、密码
        int expiredSeconds=rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
            Map<String,Object> map0 = userService.login(username,password,expiredSeconds);
            if(map0.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map0.get("ticket").toString());
                cookie.setPath(contextPath);
                cookie.setMaxAge(expiredSeconds);
                response.addCookie(cookie);
                map.put("loginMsg","登录成功！");
                return map;
            }else{
                return map0;
            }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    @ResponseBody
    public Object logout(@ApiIgnore @CookieValue("ticket") String ticket){
        userService.logout(ticket);
        Map<String,Object> map = new HashMap<>();
        map.put("logoutMsg","退出登录成功！");
        return map;
    }
}
