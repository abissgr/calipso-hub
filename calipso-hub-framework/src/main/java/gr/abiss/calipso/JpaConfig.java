package gr.abiss.calipso;

import com.restdude.config.AbstractPersistenceJPAConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({"com.restdude", "gr.abiss.calipso"})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.restdude", "gr.abiss.calipso"},
        repositoryFactoryBeanClass = com.restdude.domain.base.repository.ModelRepositoryFactoryBean.class,
        repositoryBaseClass = com.restdude.domain.base.repository.BaseRepositoryImpl.class
)
public class JpaConfig extends AbstractPersistenceJPAConfig {
}
