package dev.joid.backend.minecraft.demo.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ClientTestLog extends PrintStream {

	private static final List<String> WARNINGS = new CopyOnWriteArrayList<>();
	private static final List<String> ERRORS   = new CopyOnWriteArrayList<>();

	private final PrintStream target;

	private ClientTestLog(final PrintStream target) {
		super(target, true);
		this.target = target;
	}

	public static void install() {
		if (!(System.err instanceof ClientTestLog)) {
			System.setErr(new ClientTestLog(System.err));
		}
	}

	public static List<String> getWarnings() {
		return Collections.unmodifiableList(new ArrayList<>(ClientTestLog.WARNINGS));
	}

	public static int countErrors() {
		return ClientTestLog.ERRORS.size();
	}

	public static List<String> getErrorsSince(final int start) {
		return Collections.unmodifiableList(new ArrayList<>(ClientTestLog.ERRORS.subList(start, ClientTestLog.ERRORS.size())));
	}

	@Override
	public void println(final String line) {
		if (line != null && line.startsWith("[JOID]")) {
			ClientTestLog.WARNINGS.add(line);
		}

		if (line != null && (line.contains("Exception") || line.contains("Error") || line.startsWith("\tat "))) {
			ClientTestLog.ERRORS.add(line);
		}
		this.target.println(line);
	}

	@Override
	public void println(final Object value) {
		this.println(String.valueOf(value));
	}

}