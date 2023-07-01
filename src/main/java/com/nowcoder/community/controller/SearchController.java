package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;


    /**
     * 根据关键词查找对应帖子
     * @param keyword
     * @param page
     * @return
     */
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    @ResponseBody
    public String search(String keyword, Page page){
        //定义存返回信息的 map
        HashMap<String,Object> res = new HashMap<>();

        //基于service中定义的方法搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
            elasticSearchService.searchDiscussPost(keyword, page.getCurrent()-1,page.getLimit());

        //聚合数据方便前端进行显示（帖子，发布帖子的用户，帖子的点赞数）
        List<Map<String,Object>> discussPosts = new ArrayList<>();

        if(searchResult!=null){
            for(DiscussPost post:searchResult){
                Map<String,Object> map = new HashMap<>();

                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                discussPosts.add(map);
            }
        }
        res.put("discussPosts", discussPosts);
        page.setRows(searchResult==null?0:(int) searchResult.getTotalElements());
        res.put("page",page);
        return CommunityUtil.getJSONString(0,"根据关键词在es中搜索成功！",res);
    }
}
