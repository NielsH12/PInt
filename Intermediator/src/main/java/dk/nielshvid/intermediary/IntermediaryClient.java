package dk.nielshvid.intermediary;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Cookie;

public class IntermediaryClient{

	private static final String REST_URI_Freezer = "http://ffu.freezer.com";

	private static Client client = ClientBuilder.newClient();

	public static Response FreezerPUTinsert(UriInfo info, String body, HttpHeaders headers){
		Invocation.Builder builder = client
				.target(REST_URI_Freezer)
				.path("insert")
				.request()
				.header("Content-Type", "text/plain");

		for (Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()){
			builder.cookie(entry.getValue());
		}

		return builder.put(Entity.json(body));
	}

	public static Response FreezerGETretrieve(UriInfo info, String body, HttpHeaders headers){
		Invocation.Builder builder = client
				.target(REST_URI_Freezer)
				.path("retrieve")
				.request()
				.header("Content-Type", "application/json");

		for (Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()){
			builder.cookie(entry.getValue());
		}

		return builder.get();
	}

	public static Response FreezerGETquerySample(UriInfo info, String body, HttpHeaders headers){
		Invocation.Builder builder = client
				.target(REST_URI_Freezer)
				.path("querysample")
				.request()
				.header("Content-Type", "application/json");

		for (Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()){
			builder.cookie(entry.getValue());
		}

		return builder.get();
	}

}
