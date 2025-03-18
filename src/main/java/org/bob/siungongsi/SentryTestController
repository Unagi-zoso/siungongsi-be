import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.sentry.Sentry;

@RestController
@RequestMapping("/test")
public class SentryTestController {

    @GetMapping("/error")
    public String generateError() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            Sentry.captureException(e);
            return "Exception sent to Sentry!";
        }
        return "No error";
    }
}
