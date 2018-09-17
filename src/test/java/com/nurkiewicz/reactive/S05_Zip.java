package com.nurkiewicz.reactive;

import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class S05_Zip extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S05_Zip.class);

	/**
	 * Basically combining two futures together
	 * @throws Exception
	 */
	@Test
	public void thenCombine() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		//this is not blocking code, but it will register a Callback and eventually when
		//the result of both - java and scala are returned, it will execute

		//no blocking involved, just a callback that we register and once both return we
		//return a result

		final CompletableFuture<Integer> both = java.
				thenCombine(scala, (String javaTitle, String scalaTitle) ->
						javaTitle.length() + scalaTitle.length()
				);

		//take two futures, combine them with each other, when they are complete

		both.thenAccept(length -> log.debug("Total length: {}", length));
	}

	/**
	 * Whichever CompletableFuture finishes first is use, so EITHER of the Futures
	 * depending on which finishes first
	 *
	 * An example of using this would be that, if you are calling 2 servers and
	 * you don't really care about the response from either because both return the same response
	 * its only the first one that responds is the one of interest. So that is the one that
	 * is called.
	 * @throws Exception
	 */
	@Test
	public void either() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		final CompletableFuture<String> both = scala.
				applyToEither(java, title -> title.toUpperCase());

		both.thenAccept(title -> log.debug("First: {}", title));
	}


}

