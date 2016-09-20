package com.client;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

	public void hello() {
		Response response;
		try {
			response = Request.Get("http://localhost:9080/dubbo/api/userinfo/hello").execute();
			response.returnContent().asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	static CloseableHttpClient httpClient = null;
	public void hello2(){
		HttpGet get=new HttpGet("http://localhost:9080/dubbo/api/userinfo/hello");
		try {
			HttpResponse httpResponse=httpClient.execute(get);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				EntityUtils.consume(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			get.completed();
		}
	}

	static RequestConfig requestConfig = null;
	static PoolingHttpClientConnectionManager cm =null;
	static {
		requestConfig = RequestConfig.custom()
				.build();
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加到200
        cm.setMaxTotal(200);
        // 将每个路由基础的连接增加到20
        cm.setDefaultMaxPerRoute(20);
        
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();
	}
}
