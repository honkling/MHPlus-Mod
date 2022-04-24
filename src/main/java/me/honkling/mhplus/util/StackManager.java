package me.honkling.mhplus.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackManager {

	public interface StackRunnable {
		public void run(Object... parameters);
	}

	private Map<String, List<StackRunnable>> stacks;
	private static StackManager instance;

	private StackManager() {
		stacks = new HashMap<>();
		instance = this;
	}

	public static StackManager getInstance() {
		if (instance == null)
			return new StackManager();

		return instance;
	}

	public void emit(String stackKey, Object... parameters) {
		List<StackRunnable> stack = stacks.get(stackKey);

		for (StackRunnable runnable : stack) {
			runnable.run(parameters);
		}
	}

	public void subscribe(String stackKey, StackRunnable runnable) {
		List<StackRunnable> stack = stacks.get(stackKey);
		stack.add(runnable);
		stacks.put(stackKey, stack);
	}
}
