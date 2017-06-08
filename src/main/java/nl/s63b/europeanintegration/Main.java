package nl.s63b.europeanintegration;

import com.google.common.base.Predicates;
import javafx.application.Application;
import nl.s63b.europeanintegration.application.EUApplication;
import nl.s63b.europeanintegration.jms.TopicGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Kevin.
 */
@EntityScan({"Project", "com.S63B.domain.Entities"})
@EnableJpaRepositories({"nl", "com.S63B.domain.Entities"})
@ComponentScan(basePackages = {"nl", "com.S63B.domain.Entities"})
@SpringBootApplication
@EnableSwagger2
public class Main {
    private static EUApplication application;

    @Autowired
    public Main(EUApplication application){
        this.application = application;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        application.initiate();
        System.out.println("SpringBoot started");
    }

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("European Integration Swagger")
                .description("European Integration Swagger")
                .version("2.0")
                .build();
    }
}