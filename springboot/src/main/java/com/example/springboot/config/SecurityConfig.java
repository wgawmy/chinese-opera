package com.example.springboot.config;

import com.example.springboot.common.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration //配置类
@EnableWebSecurity // 开启Spring Security的功能 代替了 implements WebSecurityConfigurerAdapter
public class SecurityConfig {

    @Autowired
    AuthenticationConfiguration authenticationConfiguration;//获取AuthenticationManager

    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置Spring Security的过滤链。
     *
     * @param http 用于构建安全配置的HttpSecurity对象。
     * @return 返回配置好的SecurityFilterChain对象。
     * @throws Exception 如果配置过程中发生错误，则抛出异常。
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护
                .csrf(csrf -> csrf.disable())
                // 设置会话创建策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置授权规则   指定/login路径.允许匿名访问(未登录可访问已登陆不能访问). 其他路径需要身份认证
                .authorizeHttpRequests(auth -> auth
                        // 完全公开的接口（不需要登录）
                        .requestMatchers(
                                "/user/login",           // 登录接口
                                "/code",                 // 验证码接口
                                "/user/register",        // 注册接口
                                "/file/upload",          // 文件上传

                                // 戏曲相关接口（游客可访问）
                                "/opera/list",           // 获取戏曲列表
                                "/opera/tag",            // 按标签获取戏曲
                                "/opera/{id}",           // 获取单个戏曲详情

                                // 音频相关接口
                                "/audio/getaudiobytag",  // 按标签获取音频

                                // 热门和访问相关
                                "/access/hot",           // 热门列表

                                // 评论相关（游客可查看）
                                "/comments/{operaId}",   // 获取评论列表

                                // 商店相关
                                "/shop-type/**",         // 商店类型
                                "/shop/**",              // 商店相关接口

                                // 静态资源
                                "/upload/**"             // 上传文件访问
                        )
                        .permitAll()  // 改为 permitAll() 允许所有人访问（包括已登录和未登录用户）

                        .anyRequest().authenticated())
                //开启跨域访问
                .cors(withDefaults())
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 构建并返回安全过滤链
        return http.build();
    }
}
