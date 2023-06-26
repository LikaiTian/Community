package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    TrieNode root = new TrieNode();

    @PostConstruct
    private void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {

            String keyword;
            while ((keyword = reader.readLine())!=null){
                //添加到前缀树
                this.addKeyWord(keyword);
            }

        }catch (IOException e){
            logger.error("加载敏感词文件失败!"+e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树中
    private void addKeyWord(String keyword){
        TrieNode tempNode = root;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode=new TrieNode();
                //添加节点
                tempNode.addSubNode(c,subNode);
            }

            //设置结束标志
            if(i==keyword.length()-1){
                subNode.setKeywordEnd(true);
            }

            //指向子节点进入下一轮循环
            tempNode = subNode;
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = root;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (begin<text.length()){
            if(position<text.length()){
                char c = text.charAt(position);
                if(isSymbol(c)){
                    //若指针1位于根节点，将此符号计入结果，让指针2往下走
                    if(tempNode==root){
                        sb.append(c);
                        begin++;
                    }
                    position++;
                    continue;
                }

                //检查下级节点
                tempNode = tempNode.getSubNode(c);
                if(tempNode==null){
                    //以begin开头的不是敏感词
                    sb.append(text.charAt(begin));
                    begin++;
                    position=begin;
                    tempNode = root;
                }else if(tempNode.isKeywordEnd()){
                    //发现敏感词,begin开头position结尾
                    sb.append(REPLACEMENT);
                    position++;
                    begin=position;
                    tempNode = root;
                }else {
                    //检查下一个字符
                    position++;
                }
            }else{
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){

        //判断东亚字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80||c>0x9FFF);
    }

    //前缀树
    private class TrieNode{

        //关键词结束的标识
        private boolean isKeywordEnd = false;

        //子节点(key，是下级节点字符，value是下级节点)
        Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
