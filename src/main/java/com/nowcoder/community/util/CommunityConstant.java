package com.nowcoder.community.util;

public interface CommunityConstant {

    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAILURE=2;

    int DEFAULT_EXPIRED_SECONDS = 3600*12;

    int REMEMBER_EXPIRED_SECONDS = 3600*100;

    int ENTITY_TYPE_POST = 1;

    int ENTITY_TYPE_COMMENT = 2;

    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";


    /**
     * 主题：删帖
     */
    String TOPIC_DELETE = "delete";


    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
