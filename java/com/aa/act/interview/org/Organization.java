package com.aa.act.interview.org;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Organization {

	private final Position root;
	private final AtomicInteger counter = new AtomicInteger();
	
	public Organization() {
		root = createOrganization();
	}
	
	protected abstract Position createOrganization();
	
	/**
	 * hire the given person as an employee in the position that has that title
	 * 
	 * @param person
	 * @param title
	 * @return the newly filled position or empty if no position has that title
	 */
	public Result<Position, String> hire(Name person, String title) {
		Optional<Position> optionalPosition = getPosition(root, title);
		if (optionalPosition.isPresent()) {
			Position position = optionalPosition.get();
			if (!position.isFilled()) {
				position.setEmployee(Optional.of(new Employee(counter.incrementAndGet(), person)));
				return new Result<>(position, null);
			} else {
				return new Result<>(null, "The position is already filled.");
			}
		} else {
			return new Result<>(null, "The requested position was not found.");
		}
	}

	private Optional<Position> getPosition(Position position, String title) {
		if (position.getTitle().equals(title)) {
			return Optional.of(position);
		}
		for (Position directReport : position.getDirectReports()) {
			Optional<Position> result = getPosition(directReport, title);
			if (result.isPresent()) {
				return result;
			}
		}
		return Optional.empty();
	}
	public class Result<T, E> {
		private final T result;
		private final E error;

		public Result(T result, E error) {
			this.result = result;
			this.error = error;
		}

		public boolean hasError() {
			return error != null;
		}

		public T getResult() {
			return result;
		}

		public E getError() {
			return error;
		}
	}

	@Override
	public String toString() {
		return printOrganization(root, "");
	}
	
	private String printOrganization(Position pos, String prefix) {
		StringBuffer sb = new StringBuffer(prefix + "+-" + pos.toString() + "\n");
		for(Position p : pos.getDirectReports()) {
			sb.append(printOrganization(p, prefix + "\t"));
		}
		return sb.toString();
	}
}
