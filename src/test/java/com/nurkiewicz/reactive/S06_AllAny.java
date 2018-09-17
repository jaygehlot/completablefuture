package com.nurkiewicz.reactive;

import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class S06_AllAny extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S06_AllAny.class);

	/**
	 * allOf() waits for all of the Futures results to return before
	 * returning the result of them
	 * @throws Exception
	 */
	@Test
	public void allOf() throws Exception {
		final CompletableFuture<String> java = questions("java");//.exceptionally(); could log exceptions for any of these
		final CompletableFuture<String> scala = questions("scala");//.exceptionally(); could log exceptions for any of these
		final CompletableFuture<String> clojure = questions("clojure");
		final CompletableFuture<String> groovy = questions("groovy");


		//expect back a CompletableFuture of type String - CompletableFuture<String> but
		//CompletableFuture<Void> is returned because
		final CompletableFuture<Void> allCompleted =
				CompletableFuture.allOf(
						java, scala, clojure, groovy
				);

		//at this point all the Futures from above will be complete and will have returned a value
		//or an exception
		allCompleted.thenRun(() -> {
			try {
				log.debug("Loaded: {}", java.get());
				log.debug("Loaded: {}", scala.get());
				log.debug("Loaded: {}", clojure.get());
				log.debug("Loaded: {}", groovy.get());
			} catch (InterruptedException | ExecutionException e) {
				log.error("", e);
			}
		});
	}

	/**
	 * anyOf() will take a number of Futures and return the result of
	 * the very first of them and all the others are discarded
	 *
	 * java, scala, clojure, groovy --> if scala is the first to respond and responds with an exception,
	 * 									this exception is propagated, even if java, clojure and groovy
	 * 									returned with successful results, scala was the first to respond,
	 * 									and the others are ignored.
	 * 								its the resulf of the first one that gets propagated downstream
	 *
	 * @throws Exception
	 */
	@Test
	public void anyOf() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");
		final CompletableFuture<String> clojure = questions("clojure");
		final CompletableFuture<String> groovy = questions("groovy");

		//A string is returned, instead of an Object.
		final CompletableFuture<Object> firstCompleted =
				CompletableFuture.anyOf(
						java, scala, clojure, groovy
				);

		firstCompleted.thenAccept((Object result) -> {
			log.debug("First: {}", result);
		});
	}

}

