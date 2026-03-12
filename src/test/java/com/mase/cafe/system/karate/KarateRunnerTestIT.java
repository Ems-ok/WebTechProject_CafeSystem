package com.mase.cafe.system.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KarateRunnerTestIT {

	@LocalServerPort
	int randomServerPort;

	@Karate.Test
	Karate testLogin() {
		System.setProperty("local.server.port", String.valueOf(randomServerPort));
		return Karate.run("01_auth.feature").relativeTo(getClass());
	}
	@Karate.Test
	Karate testUsers() {
		System.setProperty("local.server.port", String.valueOf(randomServerPort));
		return Karate.run("02_users.feature").relativeTo(getClass());
	}
	@Karate.Test
	Karate testLoginSuccessfully() {
		System.setProperty("local.server.port", String.valueOf(randomServerPort));
		return Karate.run("03_login_success.feature").relativeTo(getClass());
	}

}


