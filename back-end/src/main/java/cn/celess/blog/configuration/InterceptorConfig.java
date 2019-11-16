package cn.celess.blog.configuration;

import cn.celess.blog.configuration.filter.AuthenticationFilter;
import cn.celess.blog.configuration.filter.MultipleSubmitFilter;
import cn.celess.blog.configuration.filter.VisitorRecord;
import cn.celess.blog.configuration.listener.SessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 小海
 * @Date: 2019/10/18 14:19
 * @Description:
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MultipleSubmitFilter()).addPathPatterns("/*");
        registry.addInterceptor(authenticationFilter()).addPathPatterns("/**");

        // visitor 输出信息杂乱 暂时放弃使用
//        registry.addInterceptor(new VisitorRecord()).addPathPatterns("/*");
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

//    // session listener register bean
//    @Bean
//    public ServletListenerRegistrationBean<SessionListener> servletListenerRegistrationBean() {
//        ServletListenerRegistrationBean<SessionListener> slrBean = new ServletListenerRegistrationBean<SessionListener>();
//        slrBean.setListener(new SessionListener());
//        return slrBean;
//    }
}
