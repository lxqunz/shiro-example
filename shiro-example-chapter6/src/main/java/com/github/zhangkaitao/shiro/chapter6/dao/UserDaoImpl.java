package com.github.zhangkaitao.shiro.chapter6.dao;

import com.github.zhangkaitao.shiro.chapter6.JdbcTemplateUtils;
import com.github.zhangkaitao.shiro.chapter6.entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate = JdbcTemplateUtils.jdbcTemplate();

    /**
     * 创建并取得生成的id
     * @param user
     * @return
     */
    public User createUser(final User user) {
        final String sql = "insert into sys_users(username, password, salt, locked) values(?,?,?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement psst = connection.prepareStatement(sql, new String[] { "id" });
                psst.setString(1, user.getUsername());
                psst.setString(2, user.getPassword());
                psst.setString(3, user.getSalt());
                psst.setBoolean(4, user.getLocked());
                return psst;
            }
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public void updateUser(User user) {
        String sql = "update sys_users set username=?, password=?, salt=?, locked=? where id=?";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getSalt(), user.getLocked(), user.getId());
    }

    public void deleteUser(Long userId) {
        String sql = "delete from sys_users where id=?";
        jdbcTemplate.update(sql, userId);
    }

    /**
     * 添加用户-角色关系
     * @param userId
     * @param roleIds
     */
    public void correlationRoles(Long userId, Long... roleIds) {
        if(roleIds == null || roleIds.length == 0) {
            return;
        }
        String sql = "insert into sys_users_roles(user_id, role_id) values(?,?)";
        for(Long roleId : roleIds) {
            if(!exists(userId, roleId)) { //查找表中没有重复的权限
                jdbcTemplate.update(sql, userId, roleId);
            }
        }
    }

    /**
     * 解除用户角色
     * @param userId
     * @param roleIds
     */
    public void uncorrelationRoles(Long userId, Long... roleIds) {
        if(roleIds == null || roleIds.length == 0) {
            return;
        }
        String sql = "delete from sys_users_roles where user_id=? and role_id=?";
        for(Long roleId : roleIds) {
            if(exists(userId, roleId)) {
                jdbcTemplate.update(sql, userId, roleId);
            }
        }
    }

    /**
     * 查找表中有没有重复的角色
     * @param userId
     * @param roleId
     * @return
     */
    private boolean exists(Long userId, Long roleId) {
        String sql = "select count(1) from sys_users_roles where user_id=? and role_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, roleId) != 0;
    }


    /**
     * 取得一个用户
     * @param userId
     * @return
     */
    public User findOne(Long userId) {
        String sql = "select id, username, password, salt, locked from sys_users where id=?";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), userId);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    /**
     *
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        String sql = "select id, username, password, salt, locked from sys_users where username=?";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), username);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    /**
     * 取得用户角色
     * @param username
     * @return
     */
    public Set<String> findRoles(String username) {
        String sql = "select role from sys_users u, sys_roles r,sys_users_roles ur where u.username=? and u.id=ur.user_id and r.id=ur.role_id";
        return new HashSet(jdbcTemplate.queryForList(sql, String.class, username));
    }

    /**
     * 查用户权限
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username) {
        //TODO 此处可以优化，比如查询到role后，一起获取roleId，然后直接根据roleId获取即可
        String sql = "select permission from sys_users u, sys_roles r, sys_permissions p, sys_users_roles ur, sys_roles_permissions rp where u.username=? and u.id=ur.user_id and r.id=ur.role_id and r.id=rp.role_id and p.id=rp.permission_id";
        return new HashSet(jdbcTemplate.queryForList(sql, String.class, username));
    }
}
