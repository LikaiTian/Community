package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.Oneway;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"关注成功");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"取关成功");
    }

    @RequestMapping(path = "/followees",method = RequestMethod.GET)
    @ResponseBody
    public String getFollowees(int userId, Page page){
        Map<String,Object> res = new HashMap<>();
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在！");
        }
        res.put("user",user);
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        List<Map<String,Object>> userList = followService.findFollowees(userId, page);
        if(userList!=null){
            for(Map<String,Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        res.put("userList",userList);
        return CommunityUtil.getJSONString(0,"chengg",res);
    }

    @RequestMapping(path = "/followers",method = RequestMethod.GET)
    @ResponseBody
    public String getFollowers(int userId, Page page){
        Map<String,Object> res = new HashMap<>();
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在！");
        }
        res.put("user",user);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        List<Map<String,Object>> userList = followService.findFollowers(userId, page);
        if(userList!=null){
            for(Map<String,Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        res.put("userList",userList);
        return CommunityUtil.getJSONString(0,"成功",res);
    }

    private boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }
}
