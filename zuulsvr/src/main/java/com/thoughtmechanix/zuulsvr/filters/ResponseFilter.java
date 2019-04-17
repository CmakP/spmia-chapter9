package com.thoughtmechanix.zuulsvr.filters;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

/**
 * Because Zuul is now Spring Cloud Sleuth-enabled, you can access tracing information from within your ResponseFilter
 * by autowiring in the Tracer class into the ResponseFilter.
 */
@Component
public class ResponseFilter extends ZuulFilter{
    private static final int  FILTER_ORDER=1;
    private static final boolean  SHOULD_FILTER=true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    // The Tracer class is the entry point to access trace and span ID information.
    // It allows you to access information about the current Spring Cloud Sleuth trace being executed.
    @Autowired
    Tracer tracer;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //tracer.getCurrentSpan().traceIdString() allows you to retrieve as a String the current trace ID for the transaction underway
        ctx.getResponse().addHeader("tmx-correlation-id", tracer.getCurrentSpan().traceIdString());

        return null;
    }
}
