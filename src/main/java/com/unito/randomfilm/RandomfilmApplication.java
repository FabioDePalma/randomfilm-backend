package com.unito.randomfilm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class RandomfilmApplication {

	public static void main(String[] args) {
		SpringApplication.run(RandomfilmApplication.class, args);
	}


	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ApplicationRunner dbCheck(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				System.out.println("✅ Connessione al DB riuscita: " + conn.getMetaData().getURL());
				System.out.println("🛠 Driver: " + conn.getMetaData().getDriverName());
				System.out.println("🧱 Autocommit: " + conn.getAutoCommit());
			} catch (Exception e) {
				System.err.println("❌ Errore connessione DB: " + e.getMessage());
			}
		};
	}



//	@Bean
//	public ApplicationRunner omdbApiCheck(RestTemplate restTemplate, @Value("${omdb.api.key}") String apiKey) {
//		return args -> {
//			try {
//				String testTitle = "inception";
//				String url = "http://www.omdbapi.com/?t=" + testTitle + "&apikey=" + apiKey;
//
//				var response = restTemplate.getForObject(url, String.class);
//
//				if (response != null && response.contains("\"Response\":\"True\"")) {
//					System.out.println("Risposta OK);
//				} else {
//					System.err.println("risposta ok ma qualcosa non va ");
//				}
//			} catch (Exception e) {
//				System.err.println("Errore" + e.getMessage());
//			}
//		};
//	}
}
