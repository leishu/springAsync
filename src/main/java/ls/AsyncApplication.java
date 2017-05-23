package ls;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by leishu on 17-5-22.
 */

@SpringBootApplication
public class AsyncApplication extends WebMvcConfigurerAdapter {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsyncApplication.class, args);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(timeout);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.setRegisterDefaultServlet(false);
        factory.setPersistSession(false);
        factory.setPort(port);

        factory.addServerCustomizers(server -> {
            QueuedThreadPool threadPool = (QueuedThreadPool)server.getThreadPool();
            threadPool.setMaxThreads(maxThreads);
            threadPool.setMinThreads(minThreads);

            ServerConnector connector = new ServerConnector(server, 2, 2);
            connector.setPort(port);
            connector.setAcceptQueueSize(200);
            connector.setReuseAddress(true);

            server.setConnectors(new Connector[]{connector});
        });

        return factory;
    }

    @Value("${jetty.maxThreads}")
    private Integer maxThreads;
    @Value("${jetty.minThreads}")
    private Integer minThreads;
    @Value("${jetty.port}")
    private Integer port;
    @Value("${jetty.timeout}")
    private Integer timeout;
}
