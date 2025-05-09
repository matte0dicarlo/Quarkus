package consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.MDCUnitOfWork;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spi.UnitOfWorkFactory;
import org.apache.camel.spi.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;



public class CustomMDCUnitOfWork extends MDCUnitOfWork {


    private static Logger logger = LoggerFactory.getLogger(CustomMDCUnitOfWork.class);


    static final String MDC_KEY_SERVICE_ID = "zoo.service";
    static final String MDC_KEY_TRACING_ID = "zoo.tracing_id";
    static final String MDC_KEY_COMPANY_ID = "zoo.company";
    static final String MDC_KEY_ROUTE_ID = "zoo.route";

    private final String originalServiceId;
    private final String originalTracingId;
    private final String originalCompanyId;
    private final String originalRouteId;

    public CustomMDCUnitOfWork(final Exchange exchange) {

        super(exchange,
                exchange.getContext().getInflightRepository(),
                exchange.getContext().getMDCLoggingKeysPattern(),
                exchange.getContext().isAllowUseOriginalMessage(),
                exchange.getContext().isUseBreadcrumb());

        logger.info("CustomMDCUnitOfWork::CustomMDCUnitOfWork enter");

        this.originalServiceId = MDC.get(MDC_KEY_SERVICE_ID);
        this.originalTracingId = MDC.get(MDC_KEY_TRACING_ID);
        this.originalCompanyId = MDC.get(MDC_KEY_COMPANY_ID);
        this.originalRouteId = MDC.get(MDC_KEY_ROUTE_ID);

        putMDCValues(exchange);

        logger.info("CustomMDCUnitOfWork::CustomMDCUnitOfWork exited");
    }



    protected void putMDCValues(final Exchange exchange) {
        MDC.put(MDC_KEY_SERVICE_ID, "A");
        MDC.put(MDC_KEY_TRACING_ID, "B");
        MDC.put(MDC_KEY_COMPANY_ID, "C");
        MDC.put(MDC_KEY_ROUTE_ID, "D");
    }

    @Override
    public UnitOfWork newInstance(final Exchange exchange) {
        return new CustomMDCUnitOfWork(exchange);
    }

    @Override
    public AsyncCallback beforeProcess(final Processor processor, final Exchange exchange, final AsyncCallback callback) {
        putMDCValues(exchange);
        return super.beforeProcess(processor, exchange, new CustomMDCCallback(callback));
    }

    @Override
    public void clear() {
        super.clear();

        if (this.originalServiceId != null) {
            MDC.put(MDC_KEY_SERVICE_ID, this.originalServiceId);
        } else {
            MDC.remove(MDC_KEY_SERVICE_ID);
        }

        if (this.originalTracingId != null) {
            MDC.put(MDC_KEY_TRACING_ID, this.originalTracingId);
        } else {
            MDC.remove(MDC_KEY_TRACING_ID);
        }

        if (this.originalCompanyId != null) {
            MDC.put(MDC_KEY_COMPANY_ID, this.originalCompanyId);
        } else {
            MDC.remove(MDC_KEY_COMPANY_ID);
        }

        if (this.originalRouteId != null) {
            MDC.put(MDC_KEY_ROUTE_ID, this.originalRouteId);
        } else {
            MDC.remove(MDC_KEY_ROUTE_ID);
        }
    }

    private static final class CustomMDCCallback implements AsyncCallback {

        private final AsyncCallback delegate;
        private final String serviceId;
        private final String tracingId;
        private final String companyId;
        private final String routeId;

        private CustomMDCCallback(AsyncCallback delegate) {
            this.delegate = delegate;
            this.serviceId = MDC.get(MDC_KEY_SERVICE_ID);
            this.tracingId = MDC.get(MDC_KEY_TRACING_ID);
            this.companyId = MDC.get(MDC_KEY_COMPANY_ID);
            this.routeId = MDC.get(MDC_KEY_ROUTE_ID);
        }

        public void done(boolean doneSync) {
            try {
                if (!doneSync) {
                    if (serviceId != null) {
                        MDC.put(MDC_KEY_SERVICE_ID, serviceId);
                    }
                    if (tracingId != null) {
                        MDC.put(MDC_KEY_TRACING_ID, tracingId);
                    }
                    if (companyId != null) {
                        MDC.put(MDC_KEY_COMPANY_ID, companyId);
                    }
                    if (routeId != null) {
                        MDC.put(MDC_KEY_ROUTE_ID, routeId);
                    }
                }
            } finally {
                delegate.done(doneSync);
            }
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}

@ApplicationScoped
class CustomMDCUnitOfWorkFactory implements UnitOfWorkFactory, UuidGenerator {

    @Override
    public UnitOfWork createUnitOfWork(final Exchange exchange) {
        return new CustomMDCUnitOfWork(exchange);
    }

    @Override
    public void afterPropertiesConfigured(final CamelContext camelContext) {
        //nothing to do
    }

    @Override
    public String generateUuid() {
        return "";
    }
}