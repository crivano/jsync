package com.crivano.jsync;

import java.util.Comparator;

public class OperationComparator implements Comparator<Operation> {

	public int compare(Operation o1, Operation o2) {
		int i = Integer.valueOf(o1.getDependencyLevel()).compareTo(o2.getDependencyLevel());
		if (i != 0)
			return i;
		i = o1.getOperator().compareTo(o2.getOperator());
		if (i != 0)
			return i;
		return Integer.valueOf(o1.hashCode()).compareTo(o2.hashCode());
	}

}
