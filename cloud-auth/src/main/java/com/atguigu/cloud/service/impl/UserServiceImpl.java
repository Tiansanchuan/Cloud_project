package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.entities.User;
import com.atguigu.cloud.mapper.UserMapper;
import com.atguigu.cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public int add(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    public int delete(Long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int update(User user) {
        // 注意：updateSelective 需要 user.id 不为空
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }


    @Override
    public List<User> getAll() {
        return userMapper.selectAll();
    }

    @Override
    public Long verify(String username,String password) {
        Example example = new Example(User.class);
        // 创建Criteria对象，用于构建查询条件
        Example.Criteria criteria = example.createCriteria();
        // 添加查询条件：用户名和密码匹配
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("passwordHash", password);
        // 查询数据库，返回满足条件的记录数量
        List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()){
            return null;
        }else{
            //后期换成判断使用状态
            return users.get(0).getId();
        }
    }




}
