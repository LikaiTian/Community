package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 参数四个：
     * entityType（可能是帖子也可能是评论），entityId（帖子id或者评论id）
     * content（评论的内容），targetId（被评论的帖子或者被回复的评论的用户id）
     * @param comment
     * @return
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addComment(Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return CommunityUtil.getJSONString(0,"添加评论成功！");
    }
}
