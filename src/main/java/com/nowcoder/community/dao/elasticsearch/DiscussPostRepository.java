package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
//@Repository注解是spring默认的访问数据的注解
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
