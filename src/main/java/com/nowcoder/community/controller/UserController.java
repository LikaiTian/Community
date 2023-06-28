package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller

@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    @ResponseBody
    public Object getSettingPage(){
        return "settingPage";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public Object uploadHeader(MultipartFile headerImage){
        Map<String,Object> map = new HashMap<>();
        if(headerImage==null){
            map.put("error","您没有选择图片！");
            return map;
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        //生成随机文件名
        filename = CommunityUtil.generateUUID()+suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath+"/"+filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败； "+ e.getMessage());
        }

        //更新当前用户的头像路径（web访问路径）
        User user = hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+filename;

        System.out.println(user.getId());
        System.out.println(headerUrl);
        userService.updateHeader(user.getId(),headerUrl);

        map.put("success","上传头像成功！");
        return map;
    }
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    @ResponseBody
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //个人主页
    @RequestMapping(path = "/profile",method = RequestMethod.GET)
    @ResponseBody
    public String getProfilePage(int userId){
        Map<String,Object> map = new HashMap<>();
        User user = userService.findUserById(userId);
        if(user==null){
            throw new IllegalArgumentException("用户不存在！");
        }
        map.put("user",user);
        int likeCount = likeService.findUserLikeCount(userId);
        map.put("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);

        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        map.put("followeeCount",followeeCount);
        map.put("followerCount",followerCount);
        map.put("hasFollowed",hasFollowed);
        return CommunityUtil.getJSONString(0,"查询成功！",map);
    }
}
