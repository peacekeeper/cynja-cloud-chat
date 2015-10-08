package biz.neustar.clouds.chat;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import biz.neustar.clouds.chat.util.JsonUtil;
import xdi2.client.constants.XDIClientConstants;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.linkcontracts.instance.ConnectLinkContract;
import xdi2.core.security.ec25519.util.EC25519CloudNumberUtil;
import xdi2.core.security.ec25519.util.EC25519KeyPairGenerator;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class ConnecttocloudServlet extends HttpServlet {

	private static final long serialVersionUID = 8396021897147651818L;

	private static final XDIAddress XDI_ADD_RETURN_URI = XDIAddress.create("<#return><$uri>");

			private static final Gson gson = new GsonBuilder()
			.setDateFormat(DateFormat.FULL, DateFormat.FULL)
			.disableHtmlEscaping()
			.serializeNulls()
			.create();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		CloudName cloudName = CloudName.create(req.getParameter("cloudName"));
		CloudNumber appCloudNumber = CloudNumber.create(req.getParameter("appCloudNumber"));

		XDIDiscoveryClient discoveryClient = new XDIDiscoveryClient("http://dev-registry.respectnetwork.net:3081/registry");
		
		try {
			// step 1

			XDIDiscoveryResult r =
					discoveryClient.discover(cloudName.getXDIAddress(),
							XDIClientConstants.CONNECT_ENDPOINT_URI_TYPE);
			CloudNumber cloudNumber = r.getCloudNumber();
			URI connectAuthService = r.getXdiConnectEndpointUri();

			// step 2

			byte[] publicKey = new byte[32], privateKey = new byte[32];
			EC25519KeyPairGenerator.generateEC25519KeyPair(
					publicKey, privateKey);

			CloudNumber appSessionCloudNumber = EC25519CloudNumberUtil.createEC25519CloudNumber(XDIConstants.CS_INSTANCE_UNORDERED, publicKey);
			String appSessionPrivateKey =  String.valueOf(Hex.encodeHex(privateKey));

			// step 3

			MessageEnvelope me = new MessageEnvelope();
			Message m = me.createMessage(appSessionCloudNumber.getXDIAddress());
			m.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
			m.setLinkContractClass(ConnectLinkContract.class);
			m.setFromPeerRootXDIArc(appCloudNumber.getPeerRootXDIArc());
			m.setParameter(XDI_ADD_RETURN_URI, "http://localhost:3080/");
			m.createConnectOperation(
					XDIBootstrap.ALL_LINK_CONTRACT_TEMPLATE_ADDRESS);
			String connectRequest = URLEncoder.encode(
					me.getGraph().toString("XDI/JSON/QUAD"), "UTF-8");
			StringBuffer appConnectRequestUri = new StringBuffer();
			appConnectRequestUri.append(connectAuthService);
			appConnectRequestUri.append("?xdi=" + connectRequest);
			appConnectRequestUri.append("&key=" + cloudName.toString());
			appConnectRequestUri.append("&discovery=" + ((XDIHttpClient) discoveryClient.getRegistryXdiClient()).getXdiEndpointUri().toString());

			// response

			JsonObject jsonObject = new JsonObject();
			jsonObject.add("appSessionCloudNumber", gson.toJsonTree(appSessionCloudNumber.toString()));
			jsonObject.add("appSessionPrivateKey", gson.toJsonTree(appSessionPrivateKey));
			jsonObject.add("appConnectRequestUri", gson.toJsonTree(appConnectRequestUri));

			resp.setContentType("application/json");
			JsonUtil.write(resp.getWriter(), jsonObject);
		} catch (Exception ex) {

			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
