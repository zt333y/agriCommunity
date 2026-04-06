package com.example.agricommunity.entity;

import lombok.Data;

@Data // 这个注解会自动生成 Getter/Setter，不用手写了
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    /**
     * 角色：0-居民, 1-农户, 2-团长, 3-管理员
     */
    private Integer role;
}