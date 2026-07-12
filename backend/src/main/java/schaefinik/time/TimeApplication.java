package schaefinik.time;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "schaefinik.time.auth",
        "schaefinik.time.common",
        "schaefinik.time.config",
        "schaefinik.time.controller",
        "schaefinik.time.project",
        "schaefinik.time.timeentry",
        "schaefinik.time.security",
        "schaefinik.time.user",
})
public class TimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeApplication.class, args);
    }
}
