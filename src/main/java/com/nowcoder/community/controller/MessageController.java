package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    @ResponseBody
    public String getLetterList(Page page){
        Map<String,Object> res=new HashMap<>();
        User user=hostHolder.getUser();
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();

        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLettersCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));

                int targetId = user.getId() == message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId() ,null);


        res.put("letterUnreadCount",letterUnreadCount);
        res.put("conversations",conversations);
        return CommunityUtil.getJSONString(0,"成功！",res);
    }

    @RequestMapping(path = "letter/detail",method = RequestMethod.GET)
    @ResponseBody
    public String getLetterDetail(String conversationId,Page page){

        Map<String,Object> res = new HashMap<>();

        //分页信息
        page.setLimit(5);
        page.setRows(messageService.findLettersCount(conversationId));

        //
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();

        if(letterList!=null){
            for(Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("message",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        res.put("letters",letters);
        res.put("target",getLetterTarget(conversationId));
        return CommunityUtil.getJSONString(0,"成功!",res);
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==d0){
            return userService.findUserById(d1);
        }
        return userService.findUserById(d0);
    }
}