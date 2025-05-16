package producer;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.apache.camel.impl.engine.MDCUnitOfWork;
import org.apache.camel.spi.InflightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class CustomMDCUnitOfWork extends MDCUnitOfWork {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected final String[] headerNames;

   public CustomMDCUnitOfWork(
            Exchange exchange,
            InflightRepository inflightRepository,
            String pattern,
            boolean allowUseOriginalMessage,
            boolean useBreadcrumb,
            String[] headerNames) {

        super(exchange, inflightRepository, pattern, allowUseOriginalMessage, useBreadcrumb);
        this.headerNames = headerNames;
    }

    protected String[] getHeaderNames() {
        return headerNames;
    }

    private void addOptional(Exchange exchange, String headerName) {
        try {
            String value = exchange.getIn().getHeader(headerName, String.class);
            if (value != null) {
                MDC.put(headerName, value);
                LOG.trace("Added header to MDC: {}={}", headerName, value);
            }
        } catch (TypeConversionException e) {
            LOG.warn("Failed to convert header '{}' to String: {}", headerName, e.getMessage(), e);
        }
    }

    @Override
    public AsyncCallback beforeProcess(Processor processor, Exchange exchange, AsyncCallback callback) {
        for (String headerName : getHeaderNames()) {
            addOptional(exchange, headerName);
        }
        return super.beforeProcess(processor, exchange, callback);
    }

    @Override
    public void clear() {
        super.clear();
        for (String headerName : getHeaderNames()) {
            MDC.remove(headerName);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CustomMDCUnitOfWork [");
        for (String headerName : getHeaderNames()) {
            sb.append(headerName).append(';');
        }
        return sb.append(']').toString();
    }
}
