package com.thoughtmechanix.zuulsvr.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

// 确保所有出站REST 调用都具有来自 UserContext 的关联ID
// 这个拦截器是需要放入到一个调用发起的组件中的，所以需要定义一个RestTemplate,在这里面添加 过滤器
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    // 此拦截为请求拦截 即当发出对外的HTTP请求之前  可以修改一些请求参数之类的行为
    // 入 将上下文中需要用的traceId进行传递下去

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();
        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

        return execution.execute(request, body);
    }
}