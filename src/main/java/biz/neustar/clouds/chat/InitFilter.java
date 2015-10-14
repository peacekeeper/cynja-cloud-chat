package biz.neustar.clouds.chat;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.websocket.DeploymentException;

import xdi2.discovery.XDIDiscoveryClient;
import biz.neustar.clouds.chat.websocket.WebSocketEndpoint;


public class InitFilter implements Filter {

	public static XDIDiscoveryClient XDI_DISCOVERY_CLIENT;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		try {

			XDI_DISCOVERY_CLIENT = new XDIDiscoveryClient(filterConfig.getInitParameter("discovery"));
			
			WebSocketEndpoint.install(filterConfig.getServletContext());
		} catch (DeploymentException ex) {

			throw new ServletException(ex.getMessage(), ex);
		}
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	}
}
