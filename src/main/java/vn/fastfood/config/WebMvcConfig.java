package vn.fastfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver(); // là class chuyên dùng cho JSP
        bean.setViewClass(JstlView.class); // JstlView để hỗ trợ JSTL
        bean.setPrefix("/WEB-INF/view/"); // Đường dẫn thư mục chứa JSP
        bean.setSuffix(".jsp"); // Đuôi file view
        return bean;
    }
    // prefix + viewName + suffix
    // khi controller trả về: return "view"
    // ==> spring sẽ tự động chuyển thành "/WEB-INF/view/home.jsp"

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }
    // kết nối cấu hình với Spring ở trên

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");
        registry.addResourceHandler("/images/**").addResourceLocations("/resources/images/");
        registry.addResourceHandler("/client/**").addResourceLocations("/resources/client/");
        registry.addResourceHandler("/staff/**").addResourceLocations("/resources/staff/");
        registry.addResourceHandler("/manager/**").addResourceLocations("/resources/manager/");
        registry.addResourceHandler("/admin/**").addResourceLocations("/resources/admin/");
    }
    // Khi gọi trên website là /css/style.css
    // -> spring lấy file /resource/css/style.css

    // Quản lý Request | Cho phép Những request có đủ quyền và chặn những cái k có
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor()).excludePathPatterns("/**/*.css", "/**/*.js");
    }
}