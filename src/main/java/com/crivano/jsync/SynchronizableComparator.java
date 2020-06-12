package com.crivano.jsync;

import java.util.Comparator;

public class SynchronizableComparator implements Comparator<Synchronizable> {

	public int compare(Synchronizable o1, Synchronizable o2) {
		if (o1 == o2)
			return 0;
		int i = o1.getClass().getName().compareTo(o2.getClass().getName());
		if (i != 0)
			return i;
		return o1.getSyncKey().compareTo(o2.getSyncKey());
	}

}
