/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.utils;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class StopWatch {

	private static final Random rng = new Random();
	private final Map<String, SWEntry> watches = Maps.newLinkedHashMap();

	public boolean isRunning(String id) {
		if (watches.containsKey(id)) {
			return getEntry(id).isRunning();
		}
		return false;
	}

	public int countRunning() {
		int i = 0;
		for (SWEntry e : watches.values()) {
			if (e.isRunning()) {
				i++;
			}
		}
		return i;
	}

	public void assertNoneRunning() {
		if (countRunning() > 0) {
			throw new IllegalStateException("one or more stop watches are still running");
		}
	}

	public void clear() {
		watches.clear();
	}

	public void reset() {
		for (SWEntry e : watches.values()) {
			e.reset();
		}
	}

	public void reset(String id) {
		getEntry(id).reset();
	}

	public long start(String id) {
		Objects.requireNonNull(id);
		SWEntry e = watches.get(id);
		if (e == null) {
			e = new SWEntry(id);
			watches.put(id, e);
		}
		return e.start();
	}

	public void stopAll() {
		for (SWEntry e : watches.values()) {
			if (e.token != null) {
				stop(e.id, e.token);
			}
		}
	}

	public void stop(String id, long token) {
		getEntry(id).stop(token);
	}

	public void measure(String id, boolean allowReentrant, Runnable runnable) {
		measure(id, allowReentrant, () -> {
			runnable.run();
			return 0;
		});
	}

	public <T> T measure(String id, boolean allowReentrant, Supplier<T> supplier) {
		if (allowReentrant && isRunning(id)) {
			return supplier.get();
		} else {
			final long token = start(id);
			try {
				return supplier.get();
			} finally {
				stop(id, token);
			}
		}
	}

	public long elapsed(String id, TimeUnit desiredUnit) {
		return getEntry(id).sw.elapsed(desiredUnit);
	}

	public double elapsedDbl(String id, TimeUnit desiredUnit) {
		final double millisPerDesiredUnit = TimeUnit.MILLISECONDS.convert(1, desiredUnit);
		final double elapsed = getEntry(id).sw.elapsed(TimeUnit.MILLISECONDS) / millisPerDesiredUnit;
		return elapsed;
	}

	public void printElapsed() {
		System.out.println(toString(TimeUnit.SECONDS, System.lineSeparator()));
	}

	public String toString(TimeUnit desiredUnit, String separator) {
		int maxIdLen = 0;
		long maxElapsed = 0;
		for (SWEntry e : watches.values()) {
			maxIdLen = Math.max(maxIdLen, e.id.length());
			maxElapsed = Math.max(maxElapsed, e.sw.elapsed(TimeUnit.MILLISECONDS));
		}
		final StringBuilder sb = new StringBuilder();
		for (SWEntry e : watches.values()) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(e.id);
			for (int i = e.id.length(); i < maxIdLen; i++) {
				sb.append(' ');
			}
			sb.append(": ");
			sb.append(Double.toString(elapsedDbl(e.id, desiredUnit)));
			sb.append(" (");
			sb.append(Long.toString(Math.round(e.sw.elapsed(TimeUnit.MILLISECONDS) / ((double) maxElapsed) * 100.0)));
			sb.append("%)");
		}
		return sb.toString();
	}

	private SWEntry getEntry(String id) {
		final SWEntry e = watches.get(id);
		if (e == null) {
			throw new IllegalArgumentException("no stop watch found for given id: " + id);
		}
		return e;
	}

	private static final class SWEntry {
		final String id;
		final Stopwatch sw;
		Long token;
		Thread thread;

		SWEntry(String id) {
			this.id = id;
			this.sw = Stopwatch.createUnstarted();
		}

		boolean isRunning() {
			return token != null;
		}

		void reset() {
			sw.reset();
			token = null;
			thread = null;
		}

		long start() {
			if (isRunning()) {
				throw new IllegalStateException("call to #start() while stop watch is running; id: " + id);
			}
			token = rng.nextLong();
			thread = Thread.currentThread();
			sw.start();
			return token;
		}

		@SuppressWarnings("hiding")
		void stop(long token) {
			if (!isRunning()) {
				throw new IllegalStateException(
						"call to #stop() while stop watch isn't running; id: " + id);
			}
			if (!Objects.equals(token, this.token)) {
				throw new IllegalArgumentException(
						"call to #stop() with incorrect token (mismatch of #start() and #stop() calls); id: " + id);
			}
			// if (!Thread.currentThread().equals(this.thread)) {
			// throw new IllegalArgumentException(
			// "method #stop() called from different thread than method #start(); id: " + id);
			// }
			this.sw.stop();
			this.token = null;
			this.thread = null;
		}
	}
}
