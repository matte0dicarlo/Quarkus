package producer;

import org.apache.camel.Exchange;
import org.apache.camel.impl.engine.DefaultUnitOfWork;
import org.apache.camel.spi.InflightRepository;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spi.UnitOfWorkFactory;

public class CustomUnitOfWorkFactory implements UnitOfWorkFactory {

    private final String[] headerNames;

    public CustomUnitOfWorkFactory() {
        this.headerNames = null;
    }

    public CustomUnitOfWorkFactory(String[] headerNames) {
        this.headerNames = headerNames;
    }

    @Override
    public UnitOfWork createUnitOfWork(Exchange exchange) {
        if (exchange.getContext().isUseMDCLogging()) {
            InflightRepository inflightRepo = exchange.getContext().getInflightRepository();
            String pattern = exchange.getPattern().name();
            boolean allowUseOriginalMessage = exchange.getContext().isAllowUseOriginalMessage();
            boolean useBreadcrumb = exchange.getContext().isUseBreadcrumb();

            return new CustomMDCUnitOfWork(
                    exchange,
                    inflightRepo,
                    pattern,
                    allowUseOriginalMessage,
                    useBreadcrumb,
                    headerNames != null ? headerNames : new String[0]
            );
        } else {
            return new DefaultUnitOfWork(exchange);
        }
    }
}
