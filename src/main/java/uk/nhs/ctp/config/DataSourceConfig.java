package uk.nhs.ctp.config;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.util.DriverDataSource;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager", basePackages = "uk.nhs.ctp")
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${datasource.showSql:false}")
	private boolean showSql;

	@Bean()
	public DriverDataSource dataSource() {
		Properties properties = new Properties();
		final DriverDataSource dataSource = new DriverDataSource(dataSourceUrl, Driver.class.getName(), properties, username, password);
		
//		dataSource.setValidationQuery("select 1 as dbcp_connection_test");
//		dataSource.setTestOnBorrow(true);

		return dataSource;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
		final Database database = Database.valueOf("MYSQL");

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(showSql);
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setDatabase(database);

		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("uk.nhs.ctp");
		factory.setDataSource(dataSource);
		factory.afterPropertiesSet();

		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
