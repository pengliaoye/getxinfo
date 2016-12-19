package com.getxinfo.util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.io.FileUtils;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class WebProxy {

	public static void main(String[] args) {
		File file = new File("C:/Users/pgy/Desktop/下载图片/miliyo.txt");
		HttpProxyServer server = DefaultHttpProxyServer.bootstrap()
				.withAddress(new InetSocketAddress("172.19.0.6", 8888))
				//.withManInTheMiddle(new SelfSignedMitmManager())
				.withFiltersSource(new HttpFiltersSourceAdapter() {

					@Override
					public int getMaximumResponseBufferSizeInBytes() {
						return 10 * 1024 * 1024;
					}

					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						return new HttpFiltersAdapter(originalRequest, ctx) {

							@Override
							public HttpObject serverToProxyResponse(HttpObject httpObject) {
								String uri = this.originalRequest.getUri();
								if (uri.indexOf("search/online") > 0) {
									if (httpObject instanceof HttpContent) {
										HttpContent content = (HttpContent) httpObject;
										String data = content.content().toString(CharsetUtil.UTF_8);
										JsonNode resp = JsonUtils.readTree(data);
										ArrayNode items = (ArrayNode) resp.get("list");
										for (JsonNode item : items) {
											String url = item.get("face_url").asText();
											try {
												FileUtils.writeStringToFile(file, url + "\n", "UTF-8", true);
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								}
								return httpObject;
							}
						};
					}
				}).start();
	}

}
