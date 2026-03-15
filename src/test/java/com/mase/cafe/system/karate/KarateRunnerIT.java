package com.mase.cafe.system.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class KarateRunnerIT {

	@LocalServerPort
	int randomServerPort;

	@Karate.Test
	Karate testAllApiFeatures() {
		System.setProperty("local.server.port", String.valueOf(randomServerPort));

		return Karate.run("classpath:karate");
	}
}