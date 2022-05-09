package mx.tec.com.controller;

import java.util.Optional;

import javax.annotation.Resource;
import javax.security.sasl.AuthenticationException;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.tec.com.manager.LoginManager;
import mx.tec.com.manager.UserManager;
import mx.tec.com.vo.CredentialsVO;
import mx.tec.com.vo.JsonWebTokenVO;
import mx.tec.com.vo.LoginResponse;
import mx.tec.com.vo.UserVO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
@Validated
public class UserController {

	@Resource
	private UserManager userManager;
	
	@Resource
	private LoginManager loginManager;
	
	@PostMapping("/add")
	public ResponseEntity<UserVO> addUser(@RequestBody UserVO user) {
		Optional<UserVO> saved = userManager.addUser(user);
		if (saved.isPresent()) {
			return new ResponseEntity<> (saved.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<> (new UserVO(), HttpStatus.BAD_REQUEST);	
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> createAuthenticationToken(@RequestBody CredentialsVO credentials){
		JsonWebTokenVO webtoken = loginManager.authenticate(credentials);
		long userId = userManager.findUserIdByUsername(credentials.getUsername());
		
		if(userId < 0) {
			return new ResponseEntity<>(new LoginResponse(), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(new LoginResponse(webtoken, userId));
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> onSecurityException (final AuthenticationException ae) {
		//log.error("Invalid Credentials ", ae);
		return new ResponseEntity<>(ae.getMessage(), HttpStatus.UNAUTHORIZED);

	}
	
}