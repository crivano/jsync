package com.crivano.jsync;

public interface OperatorWithHistory<T extends Synchronizable> {
	T insert(T oNew);

	T remove(T oOld);

	T update(T oOld, T oNew);

	default void init(Operation<T> opr) {
	}
}
