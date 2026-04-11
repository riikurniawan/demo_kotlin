package com.example.demo_kotlin.config

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
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.demo_kotlin.repositories.portal"],
    entityManagerFactoryRef = "portalEntityManagerFactory",
    transactionManagerRef = "portalTransactionManager"
)
class PortalDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.portal")
    fun portalDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean
    fun portalDataSource(
        @Qualifier("portalDataSourceProperties") portalProperties: DataSourceProperties
    ): DataSource {
        return portalProperties.initializeDataSourceBuilder().build()
    }

    @Primary
    @Bean
    fun portalEntityManagerFactory(
        @Qualifier("portalDataSource") dataSource: DataSource,
        builder: EntityManagerFactoryBuilder
    ): LocalContainerEntityManagerFactoryBean {
        val properties = mutableMapOf<String, String>()
        properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"

        return builder
            .dataSource(dataSource)
            .packages("com.example.demo_kotlin.models.portal")
            .persistenceUnit("portal")
            .properties(properties)
            .build()
    }

    @Primary
    @Bean
    fun portalTransactionManager(
        @Qualifier("portalEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}