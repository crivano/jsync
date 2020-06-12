package com.crivano.jsync;

import java.util.ArrayList;

public class OperationList extends ArrayList<Operation> {

	@Override
	public String toString() {
		String s = "";
		for (Operation i : this) {
			s += i.getOperator() + " - " + (i.getNew() == null ? "null" : i.getNew().getSyncKey()) + " - "
					+ (i.getOld() == null ? "null" : i.getOld().getSyncKey()) + "\n";
		}
		return s;
	}

}
