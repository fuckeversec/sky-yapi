import java.time.LocalDate;
import java.util.List;
import com.sky.api.rpc.Response;
import com.sky.api.rpc.request.UserRequest;
import com.sky.api.rpc.response.UserResponse;
import com.sky.api.service.UserServiceFacade;
import lombok.extern.slf4j.Slf4j;
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
 * UserController
 *
 * @author sky
 * @since 2020 02 16
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserServiceFacade userServiceFacade;

    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public Response<UserResponse> test(@PathVariable Long id) {}

    @RequestMapping("/test/1/{id}")
    public Response<UserResponse> test1(@PathVariable Long id) {}

    @RequestMapping(value = {"/test/2/{id}", "/test/3/{id}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Response<UserResponse> test2(@PathVariable Long id) {}

    @RequestMapping(value = "/test/4/{id}", method = {GET, POST})
    public Response<UserResponse> test4(@PathVariable Long id) {}

    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test5(@RequestParam Long id) {}

    /**
     * Test 6 response.
     *
     * @param id the id
     * @return the response
     */
    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test6(@RequestParam(value = "fake_value", required = false) Long id) {}

    /**
     * Test 7 response.
     *
     * @param id the id
     * @param gender the gender
     * @return the response
     */
    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test7(@RequestParam(name = "fake_name", required = true) Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test8(@RequestParam("fake_name") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test9(@PathVariable Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public Response<UserResponse> test10(@PathVariable("id") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public List<UserResponse> test11(@PathVariable("id") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public List test12(@PathVariable("id") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public List<UserResponse> test13(@PathVariable("id") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public List<String> test14(@PathVariable("id") Long id,
            @RequestParam GenderEnum gender) {}

    @GetMapping("/test/5/{id}")
    public List<String> test15(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {}

    @GetMapping(value = "/{id}")
    public Response<UserResponse> byId(@PathVariable Long id) {}

    @PutMapping(value = "//", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody UserResponse user) {}

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveUser(@RequestBody UserRequest user) {}

}