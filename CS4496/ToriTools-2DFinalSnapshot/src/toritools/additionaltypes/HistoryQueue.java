package toritools.additionaltypes;

import java.util.LinkedList;

/**
 * Queue with a size limit. Use push();
 * 
 * @author toriscope
 * 
 * @param <T>
 */
public class HistoryQueue<T> extends LinkedList<T> {

	private static final long serialVersionUID = 1L;

	private final int MAX_HISTORY;

	public HistoryQueue(final int MAX_HISTORY) {
		this.MAX_HISTORY = Math.max(1, MAX_HISTORY);
	}

	/**
	 * Push to first index, remove last if size is above max.
	 */
	@Override
	public void push(final T latest) {
		super.addFirst(latest);
		if (super.size() > MAX_HISTORY) {
			super.removeLast();
		}
	}
}