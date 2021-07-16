package com.thoughtmechanix.zuulsvr;


import com.thoughtmechanix.zuulsvr.utils.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@SpringBootApplication
// 使服务称为一个Zuul 网关代理服务器
/**
 * 这里还有一个很重要的点：
 * @EnableZuulProxy：
 * 会集成服务发现 服务，完整的实现Zuul所需要的功能组件
 *
 * @EnableZuulServer:
 * 此注解创建一个Zuul服务器，它不会加载任何Zuul 反向代理过滤器，也不会使用Netflix Eureka 进行服务发现
 * 开发人员想要构建自己的路由服务，而不使用任何 Zuul 预置的功能时 会使用@EnableZuulServer
 * 举例来讲，当开发人员需要使用 Zuul 与 Eureka 之外的其他服务发现引擎(如Consul)进行集成的时候
 */
@EnableZuulProxy
public class ZuulServerApplication {

    @LoadBalanced // 注解表明 这个 RestTemplate 将要使用 Ribbon
    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            // 这里面将上下文信息 都传递下去
            // 将拦截器添加进去
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    /**
     *  Zuul 的核心是反向代理 ，反向代理是一个中间服务器 它位于尝试访问资源的客户端与资源本身之间
     *  客户端甚至不知道它正与 代理之外的服务器进行通信
     *
     *  反向代理负责捕获 客户端的请求 然后代表客户端 调用远程资源，这个过程包含了 负责均衡 Ribbon的功能
     *  方式有：
     *  通过服务发现自动映射路由 ：以在Eureka中的服务ID 作为映射服务的依据
     *  使用服务发现手动映射路由  ：修改制定的 映射服务路径
     *  使用静态URL手动映射路由 ：指向未通过Eureka服务发现引擎注册的服务的URL
     *
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplication.class, args);
    }
}

