package com.getxinfo.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.getxinfo.authentication.UaaAuthenticationDetailsSource;
import com.getxinfo.service.ImplicitConnectionSignup;
import com.getxinfo.service.SimpleSocialUsersDetailService;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	OAuth2ClientContext oauth2ClientContext;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/login/**", "/auth/**").permitAll()
				.antMatchers("/verify_user").anonymous()
				.antMatchers("/**").authenticated()
			.and()
				.formLogin()
					.loginPage("/login")
					.authenticationDetailsSource(authenticationDetailsSource())
					.successHandler(successHandler())
					.failureHandler(failureHandler())
			.and()
				.logout().deleteCookies("JSESSIONID")
			.and()						
				.csrf().csrfTokenRepository(csrfTokenRepository())
			.and()
				.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
				.apply(new SpringSocialConfigurer());		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/create_account*", "/h2-console/**", "/vendor/**", "/resources/**", "/assets/**");
		web.ignoring().antMatchers("/upload.html", "/cors/**", "/img/**", "/form");
	}
	
	@Bean
	public UaaAuthenticationDetailsSource authenticationDetailsSource(){
		return new UaaAuthenticationDetailsSource();
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	@Bean
	@ConfigurationProperties("github")
	ClientResources githubClientResources() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("facebook")
	ClientResources facebookClientResources() {
		return new ClientResources();
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(facebookClientResources(), "/login/facebook"));
		filters.add(ssoFilter(githubClientResources(), "/login/github"));
		filter.setFilters(filters);
		return filter;
	}

	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter = new OAuth2ClientAuthenticationProcessingFilter(
				path);
		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
				client.getClient().getClientId());
		tokenServices.setRestTemplate(oAuth2RestTemplate);
		oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
		return oAuth2ClientAuthenticationFilter;
	}

	class ClientResources {
		private OAuth2ProtectedResourceDetails client = new AuthorizationCodeResourceDetails();
		private ResourceServerProperties resource = new ResourceServerProperties();

		public OAuth2ProtectedResourceDetails getClient() {
			return client;
		}

		public ResourceServerProperties getResource() {
			return resource;
		}
	}

	private AuthenticationSuccessHandler successHandler() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse, Authentication authentication)
					throws IOException, ServletException {
				httpServletResponse.getWriter().append("OK");
				httpServletResponse.setStatus(200);
			}
		};
	}

	private AuthenticationFailureHandler failureHandler() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse, AuthenticationException e)
					throws IOException, ServletException {
				httpServletResponse.getWriter().append(e.getMessage());
				httpServletResponse.setStatus(401);
			}
		};
	}

	private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();
					if (cookie == null || token != null && !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}
	
	@Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SimpleSocialUsersDetailService(userDetailsService());
	}
	
	@Bean
	public ConnectionSignUp connectionSignUp() {
		return new ImplicitConnectionSignup(dataSource);
	}
}