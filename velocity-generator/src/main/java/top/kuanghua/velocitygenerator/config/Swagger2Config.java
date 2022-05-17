package top.kuanghua.velocitygenerator.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Component
@EnableSwagger2 //开启在线接口文档
//配置包扫描
public class Swagger2Config {
    private boolean swagger_is_enable = true;
    //    @Value("${.ipAddr}")
//    private String ipAddr;
    @Value("${server.port}")
    private String port;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swagger_is_enable)
                //.host(this.ipAddr + ":" + this.port)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //.apis(basePackage("top.kuanghua.swagger.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建 api文档的详细信息函数
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("标题：swagger文档")
                .description("描述：新一代的微服务架构")
                .contact(new Contact("jzfai", null, "869653722@qq.com"))
                .version("版本号:2.8")
                .build();
    }

    /**
     * http://localhost:8080/swagger-ui.html
     */
}
