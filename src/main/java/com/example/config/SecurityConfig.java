package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService; 
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	
	/** セキュリティの対象外を設定 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		//セキュリティを適用しない
		web
			.ignoring()
				.antMatchers("/webjars/**")
				.antMatchers("/css/**")
				.antMatchers("/js/**");
	}
	
	/** セキュリティの各種設定 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//ログイン不要ページの設定
	http
		.authorizeRequests()
			.antMatchers("/login").permitAll() //ログイン画面は常に直リンクOK
			.antMatchers("/user/signup").permitAll() //新規登録画面は常に直リンクOK
			.anyRequest().authenticated(); //それ以外は直リンクNG

		
	http
		.formLogin()
			.loginProcessingUrl("/login")//ログイン処理のパス
			.loginPage("/login")//ログインページの指定
			.failureUrl("/login?error")//ログイン失敗時の遷移先
			.usernameParameter("userId")//ログインページのユーザーID
			.passwordParameter("password")//ログインページのパスワード
	        .defaultSuccessUrl("/products/top", true)//成功後の遷移先
	        .and()
	        //ログアウト時はログイン画面に遷移する
	        .logout().logoutSuccessUrl("/login").permitAll();
	
	http
		.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login?logout");
	
		
	}

	/** 認証の設定 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		PasswordEncoder encoder = passwordEncoder();
		//インメモリ認証
		/** auth
			.inMemoryAuthentication()
				.withUser("user")//userを追加
					.password(encoder.encode("user"));*/
		
		/** ユーザーデータで認証 */
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(encoder);
	}
	
	
	
}
