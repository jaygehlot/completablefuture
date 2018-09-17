package com.nurkiewicz.reactive;

import com.nurkiewicz.reactive.stackoverflow.LoadFromStackOverflowTask;
import com.nurkiewicz.reactive.util.AbstractFuturesTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class S01_Introduction extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S01_Introduction.class);

	/**
	 * Broken abstraction - blocking method calls
	 */
	@Test
	public void blockingCall() throws Exception {
		final String title = client.mostRecentQuestionAbout("java");
		log.debug("Most recent Java question: '{}'", title);
	}

	@Test
	public void executorService() throws Exception {
		final Callable<String> task = () -> client.mostRecentQuestionAbout("java");

		//this code is non-blocking and will provide a handle on something that will be returned in the
		//future, either immediately, in a few seconds, minutes or days
		//promising the user that they will get a string in the future
		final Future<String> javaQuestionFuture = executorService.submit(task);
		//...

		//can only interact with a Future using a get()
		final String javaQuestion = javaQuestionFuture.get();
		log.debug("Found: '{}'", javaQuestion);
	}

	/**
	 * Composing is impossible
	 */
	@Test
	public void waitForFirstOrAll() throws Exception {

		//these two lines are non-blocking, it doesn't block the main thread
		final Future<String> java = findQuestionsAbout("java");
		final Future<String> scala = findQuestionsAbout("scala");


		//???

		final String s = java.get();
		final String s1 = scala.get();
	}

	private Future<String> findQuestionsAbout(String tag) {
		final Callable<String> task = new LoadFromStackOverflowTask(client, tag);
		return executorService.submit(task);
	}

}

