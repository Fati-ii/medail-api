package jeu.athlete.medail;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MedailApplicationTests {

	@Test
	void main() {
		MedailApplication.main(new String[] {"--server.port=0"});
	}

	@Test
	void contextLoads() {
	}

}
