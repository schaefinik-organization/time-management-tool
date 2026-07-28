package schaefinik.time.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import schaefinik.time.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class WebConfigTest {

    @InjectMocks
    private WebConfig sut;

    private String allowedOrigins;

}