package com.nurkiewicz.reactive;

import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class S08_ErrorHandling extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S08_ErrorHandling.class);

	/**
	 * Futures will have a result of type T
	 * or an Exception
	 * @throws Exception
	 */
	@Test
	public void exceptionsShortCircuitFuture() throws Exception {
		final CompletableFuture<String> questions = questions("php");

		//this will not return anything because an exception is thrown
		//exception is completely SWALLOWED here
		questions.thenApply(r -> {
			log.debug("Success!");
			return r;
		});
		questions.get();
	}

	/**
	 * Here, we can return a fallback value, in case of an exception
	 * Not only log an exception
	 *
	 * If we use .thenApply() we wouldn't get this the error support
	 *
	 * In this example, we take the exception and return it into some meaningful value,
	 * and not just sit there any cry, because of the exception
	 * @throws Exception
	 */
	@Test
	public void handleExceptions() throws Exception {
		//given
		final CompletableFuture<String> questions = questions("php");

		//when
		final CompletableFuture<String> recovered = questions
//				.thenApply()
//				.thenApply()	if an exception is thrown anywhere here, the remainder of the methods are not called
				//.thenApply()	but the first .handle() is called and the exception is thrown
//				.thenCompose()
				//if throwable is not null then an exception is thrown
				.handle((result, throwable) -> {
					if (throwable != null) {
						log.error("...some error to due not find php question");
						return "No PHP today due to: " + throwable;
					} else {
						return result.toUpperCase();
					}
				});

		//then
		log.debug("Handled: {}", recovered.get());
	}

	/**
	 * .exceptionally() does the the same thing as above (.handle()) but doesn't
	 * cater for the successful result, only the unsuccessful result.
	 *
	 * If no errors are thrown, then
	 * 		.exceptionally(throwable -> "Sorry, try again later");
	 *
	 * this result isn't executed
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldHandleExceptionally() throws Exception {
		//given
		final CompletableFuture<String> questions = questions("php");

		//when
		final CompletableFuture<String> recovered = questions
				.exceptionally(throwable -> "Sorry, try again later");

		//then
		log.debug("Done: {}", recovered.get());
	}

}

