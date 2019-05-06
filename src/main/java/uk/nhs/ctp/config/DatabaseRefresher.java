package uk.nhs.ctp.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DatabaseRefresher {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseRefresher.class);

	@Value("${config.path}")
	private String configPath;

	@Autowired
	private EntityManager entityManager;

	// Reset entire db on startup
	public void resetDatabase() throws IOException {
		runSql("create_tables.sql");

		Files.list(Paths.get(configPath + "sql/")).map(Path::getFileName).map(Path::toString)
				.filter(filename -> filename.startsWith("populate"))
				.filter(filename -> !filename.equals("populate_patients_table.sql")).forEach(this::runSql);

		runSql("populate_patients_table.sql");
	}

	private void runSql(String filename) {
		File providerRoutingFile = new File(configPath + "sql/" + filename);

		if (providerRoutingFile.exists()) {
			try {
				String[] sqls = Files.readAllLines(providerRoutingFile.toPath()).stream()
						.map(line -> line.replaceAll(";$", ";END_OF_STATEMENT;")).collect(Collectors.joining(" "))
						.split(";END_OF_STATEMENT;");

				LOG.info("Read " + filename);

				for (String sql : Arrays.asList(sqls)) {
					try {
						entityManager.createNativeQuery(sql).executeUpdate();
					} catch (Exception ex) {
						LOG.error("Error executing " + filename);
					}
				}

				LOG.info("Executed " + filename);

			} catch (IOException ex) {
				LOG.error("Error reading " + filename);

			}
		}
	}
}
