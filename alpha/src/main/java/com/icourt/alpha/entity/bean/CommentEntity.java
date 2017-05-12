package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description  评论模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class CommentEntity implements Serializable {

    public List<CommentItemEntity> items;

    public static class CommentItemEntity implements Serializable {
        public String id;
        public String content;
        public long createTime;
        public CreateUser createUser;

        public static class CreateUser implements Serializable {
            public String userId;
            public String userName;
            public String pic;
        }
    }

}
