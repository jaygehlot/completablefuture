package com.nurkiewicz.reactive;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class S09_AsyncHttp extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S09_AsyncHttp.class);

	@Test
	public void asyncHttpWithCallbacks() throws Exception {
		loadTag(
				"java",
				response -> log.debug("Got: {}", response),
				throwable -> log.error("Mayday!", throwable)
		);
		TimeUnit.SECONDS.sleep(5);
	}

	public void loadTag(String tag, Consumer<String> onSuccess, Consumer<Throwable> onError) throws IOException {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.prepareGet("http://stackoverflow.com/questions/tagged/" + tag).execute(
				new AsyncCompletionHandler<Void>() {

					@Override
					public Void onCompleted(Response response) throws Exception {
						onSuccess.accept(response.getResponseBody());
						return null;
					}

					@Override
					public void onThrowable(Throwable t) {
						onError.accept(t);
					}
				}
		);
	}
}
