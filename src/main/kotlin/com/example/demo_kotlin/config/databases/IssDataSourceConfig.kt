package com.example.demo_kotlin.config.databases

import jakarta.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.boot.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.demo_kotlin.repositories.iss"],
    entityManagerFactoryRef = "issEntityManagerFactory",
    transactionManagerRef = "issTransactionManager"
)
class IssDataSourceConfig {

    @Bean(name = ["issDataSourceProperties"])
    @ConfigurationProperties("spring.datasource.iss")
    fun issDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean(name = ["issDataSource"])
    fun issDataSource(
        @Qualifier("issDataSourceProperties") issProperties: DataSourceProperties
    ): DataSource {
        return issProperties.initializeDataSourceBuilder().build()
    }

    @Bean(name = ["issEntityManagerFactory"])
    fun issEntityManagerFactory(
        @Qualifier("issDataSource") dataSource: DataSource,
        builder: EntityManagerFactoryBuilder
    ): LocalContainerEntityManagerFactoryBean {
        val properties = mutableMapOf<String, String>()
        properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"

        return builder
            .dataSource(dataSource)
            .packages("com.example.demo_kotlin.models.iss")
            .persistenceUnit("iss")
            .properties(properties)
            .build()
    }

    @Bean(name = ["issTransactionManager"])
    fun issTransactionManager(
        @Qualifier("issEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}