package mx.tec.com.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationEntryPoint jsonWebTokenAuthenticationEntryPoint;
	
	@Lazy
	@Autowired
	private JsonWebTokenRequestFilter requestFilter;
	
	@Autowired
	private UserDetailsService userService;
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
    		.csrf()
    		.disable()
    		.authorizeRequests()
            	.antMatchers(HttpMethod.POST, "/user/add", "/user/login")
            	.permitAll()
            	.anyRequest().authenticated()
            .and()
            	.exceptionHandling()
            	.authenticationEntryPoint(jsonWebTokenAuthenticationEntryPoint)
            .and()
            	.sessionManagement()
            	.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    	
    	http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
