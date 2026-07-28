package schaefinik.time;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import schaefinik.time.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class MappingDebugConfigTest {

    @InjectMocks
    private MappingDebugConfig sut;

}
