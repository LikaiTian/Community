package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/sire/admin/data";
    }

    @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
    @ResponseBody
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,@DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        long uv = dataService.calculateUV(start,end);
        Map<String,Object> map = new HashMap<>();
        map.put("uv",uv);
        return CommunityUtil.getJSONString(0,"UV OK!",map);
    }

    @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
    @ResponseBody
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,@DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        long dau = dataService.calculateDAU(start,end);
        Map<String,Object> map = new HashMap<>();
        map.put("dau",dau);
        return CommunityUtil.getJSONString(0,"DAU OK!",map);
    }
}
