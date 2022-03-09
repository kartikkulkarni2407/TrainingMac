package io.javabrains.betterreads_data_loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import connection.DataStaxAstraProperties;
import io.javabrains.betterreads_data_loader.author.Author;
import io.javabrains.betterreads_data_loader.author.AuthorRepository;
import io.javabrains.betterreads_data_loader.book.Book;
import io.javabrains.betterreads_data_loader.book.BookRepository;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	@Autowired
	AuthorRepository authorRepository;
	@Autowired
	BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);

	}

	private void initAuthor() {
		Path path = Paths.get(authorDumpLocation);

		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				String JsonString = line.substring(line.indexOf("{"));
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(JsonString);

					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonalName(jsonObject.optString("personal_name"));
					author.setId(jsonObject.optString("key").replace("/authors", ""));
					System.out.println("saving author..." + author.getId() + " " + author.getName());
					authorRepository.save(author);
				} catch (JSONException e) {
										e.printStackTrace();
				}

			});

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void initworks() {
		Path path = Paths.get(authorDumpLocation);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				String JsonString = line.substring(line.indexOf("{"));
				try {

					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(JsonString);
						Book book = new Book();
						book.setId(jsonObject.getString("key").replace("/WORKS/", ""));
						JSONObject descriptionObject = jsonObject.optJSONObject("description");
						if (descriptionObject != null)

						{
							book.setDescription(descriptionObject.optString("value"));
						}

						JSONArray coversJsonArray = jsonObject.optJSONArray("covers");
						if (coversJsonArray != null) {
							List<String> coverIds = new ArrayList<>();
							for (int i = 0; i < coversJsonArray.length(); i++) {
								coverIds.add(coversJsonArray.getString(i));
							}
							book.setCoverIds(coverIds);
						}

						JSONArray authorsjsonArr = jsonObject.optJSONArray("authors");

						if (authorsjsonArr != null) {
							List<String> authorids = new ArrayList<>();
							for (int i = 0; i < authorsjsonArr.length(); i++) {
								String authorId = authorsjsonArr.getJSONObject(i).getJSONObject("author")
										.getString("key").replace("/authors/", "");
								authorids.add(authorId);
							}
							book.setAuthorIds(authorids);

							authorids.stream().map(id -> authorRepository.findById(id)).map(optionalAuthor -> {
								if (!optionalAuthor.isPresent())
									return "unknown Author";
								else
									return optionalAuthor.get().getName();
							}).collect(Collectors.toList());

						}

						JSONObject publishedDateObject = jsonObject.optJSONObject("created");
						if (publishedDateObject != null) {
							book.setPublishedDate(LocalDate.parse(publishedDateObject.optString("value"), dateFormat));
						}

						book.setName(jsonObject.optString("title"));
						System.out.println("saving author..." + book.getId() + " " + book.getName());
						bookRepository.save(book);

					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@PostConstruct
	public void start() {
		/*
		 * Author author = new Author();
		 * author.setId("1");
		 * author.setName("Authorname1");
		 * author.setPersonalName("personalName1");
		 * authorRepository.save(author);
		 */
		System.out.println(authorDumpLocation);
		 initAuthor();
		initworks();

	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astaPropoerties) {
		Path bundle = astaPropoerties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
