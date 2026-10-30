package project.judginger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;


@SpringBootApplication
public class JudgingerApplication {
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(JudgingerApplication.class, args);
		// run();
	}

}
