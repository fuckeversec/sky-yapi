import java.time.LocalDate;
import java.util.List;
import com.sky.api.rpc.Response;
import com.sky.api.rpc.request.UserRequest;
import com.sky.api.rpc.response.UserResponse;
import com.sky.api.service.UserServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * UserControllerForVariable
 *
 * @author sky
 * @since 2021 04 20
 */
@Slf4j
@RestController
@RequestMapping(UserControllerForVariable.PATH_PREFIX + "/user")
public class UserControllerForVariable {

    public final static String PATH_PREFIX = "/prefix";

    @Autowired
    private UserServiceFacade userServiceFacade;

    /**
     * Test 6 response.
     *
     * @param id the id
     * @return the response
     */
    @GetMapping("/test")
    public Response<UserResponse> test(@RequestParam(value = "fake_value", required = false) Long id) {}

}