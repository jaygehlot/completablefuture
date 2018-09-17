package com.nurkiewicz.reactive;

import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class S03_Map extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S03_Map.class);

	@Test
	public void oldSchool() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->
								client.mostRecentQuestionsAbout("java"),
						executorService
				);

		final Document document = java.get();       //this is blocking code
		final Element element = document.
				select("a.question-hyperlink").get(0);
		final String title = element.text();
		final int length = title.length();
		log.debug("Length: {}", length);
	}

	/**
	 * Callback hell, doesn't compose
	 */
	@Test
	public void callbacksCallbacksEverywhere() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->
								client.mostRecentQuestionsAbout("java"),
						executorService
				);

		//this method is non-blocking, it doesn't wait
		//thenAccept is a callback, and the callback receives whats inside the document
		java.thenAccept(document ->
				log.debug("Downloaded: {}", document));
	}

	@Test
	public void thenApply() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->
								client.mostRecentQuestionsAbout("java"),
						executorService
				);

		//once we have the tag back, we have traversed the document and selected
		//the tag we are interested in, and that returns an Element
		final CompletableFuture<Element> titleElement =
				java.thenApply((Document doc) ->
						doc.select("a.question-hyperlink").get(0));

		//we can then use .thenApply and get the string from that element
		//===========================================================================
		//IN EACH CASE THE CONTENTS OF THE COMPLETABLEFUTURE are passed in
		//===========================================================================
		final CompletableFuture<String> titleText =
				titleElement.thenApply(Element::text);

		//then we can get the from that string
		//calling string.length() on the contents of the Future
		final CompletableFuture<Integer> length =
				titleText.thenApply(String::length);

		log.debug("Length: {}", length.get());
	}

	@Test
	/**
	 * THE WHOLE POINT OF ALL THE CODE ABOVE IS TO AVOID BLOCKING!
	 * THE AIM IS TO GET NON-BLOCKING, REACTIVE CODE
	 * THE COMPLETABLEFUTURE TELLS US THAT WE WILL GET A RESPONSE AND WHEN WE DO, SOMETIME IN THE FUTURE,
	 * THEN TO PROCESS THE OUTPUT OF IT
	 *
	 * Can chain everything together, so that length is then parsed
	 */
	public void thenApplyChained() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->
								client.mostRecentQuestionsAbout("java"),
						executorService
				);

		//if the .thenApply() methods are removed, its normal Java transformation
		//being applied at each stage
		//to a CompletableFuture which isn't returned just yet, but will be in the Future
		final CompletableFuture<Integer> length = java.
				thenApply(doc -> doc.select("a.question-hyperlink").get(0)).
				thenApply(Element::text).
				thenApply(String::length);


		log.debug("Length: {}", length.get());  //WE ONLY BLOCK AT THIS POINT, WHEN WE CALL .GET()
	}

}

