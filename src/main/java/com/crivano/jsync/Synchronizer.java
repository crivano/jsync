package com.crivano.jsync;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Synchronizer {
	private static Logger logger = LoggerFactory.getLogger(SyncUtils.class);
	private SynchronizableComparator sc = new SynchronizableComparator();
	private OperationComparator ic = new OperationComparator();

	private SortedSet<Synchronizable> setNew = new TreeSet<>(sc);
	private SortedSet<Synchronizable> setOld = new TreeSet<>(sc);

	public void addNew(Synchronizable obj) {
		setNew.add(obj);
	}

	public void addOld(Synchronizable obj) {
		setOld.add(obj);
	}

	private OperationList nestingBatch() {
		OperationList list = new OperationList();
		Iterator<Synchronizable> iNew = setNew.iterator();
		Iterator<Synchronizable> iOld = setOld.iterator();

		Synchronizable oOld = null;
		Synchronizable oNew = null;

		if (iOld.hasNext())
			oOld = iOld.next();
		if (iNew.hasNext())
			oNew = iNew.next();
		while (oOld != null || oNew != null) {
			if ((oOld == null) || (oNew != null && sc.compare(oOld, oNew) > 0)) {
				list.add(new Operation(Operation.Operator.INSERT, oNew, null));
				if (iNew.hasNext())
					oNew = iNew.next();
				else
					oNew = null;
			} else if (oNew == null || (oOld != null && sc.compare(oNew, oOld) > 0)) {
				list.add(new Operation(Operation.Operator.DELETE, null, oOld));
				if (iOld.hasNext())
					oOld = iOld.next();
				else
					oOld = null;
			} else {
				if (oOld == null) {
					int i = 0;
				}
				if (!oNew.isSyncSimilar(oOld, 0))
					list.add(new Operation(Operation.Operator.UPDATE, oNew, oOld));
				if (iNew.hasNext())
					oNew = iNew.next();
				else
					oNew = null;
				if (iOld.hasNext())
					oOld = iOld.next();
				else
					oOld = null;
			}
		}
		return list;
	}

	public OperationList sync() {
		OperationList l = nestingBatch();
		Collections.sort(l, ic);

		// Add to the map all old items, then replace with new items that have changes
		Map<String, Synchronizable> mapAux = new HashMap<String, Synchronizable>();
		for (Synchronizable s : setOld)
			mapAux.put(s.getSyncKey(), s);
		for (Operation i : l) {
			logger.error(i.getDescription() + " - level " + i.getDependencyLevel());
			switch (i.getOperator()) {
			case INSERT:
				mapAux.put(i.getNew().getSyncKey(), i.getNew());
				break;
			case UPDATE:
				mapAux.remove(i.getOld().getSyncKey());
				mapAux.put(i.getNew().getSyncKey(), i.getNew());
				break;
			case DELETE:
				mapAux.remove(i.getOld().getSyncKey());
				break;
			}
		}
		for (Synchronizable s : setNew)
			reconnect(mapAux, s);
		return l;
	}

	public void sync(OperatorWithHistory opr) {
		OperationList l = sync();
		for (Operation i : l)
			opr.init(i);
		for (Operation i : l) {
			switch (i.getOperator()) {
			case INSERT:
				opr.insert(i.getNew());
				break;
			case UPDATE:
				opr.update(i.getOld(), i.getNew());
				break;
			case DELETE:
				opr.insert(i.getOld());
				break;
			}
		}
	}

	// Connect new items to old items that did not change
	public void reconnect(Map<String, Synchronizable> map, Synchronizable s) {
		try {
			for (Field fld : SyncUtils.getFieldList(s.getClass())) {
				Object o = fld.get(s);
				if (o != null && o instanceof Synchronizable) {
					Synchronizable ss = map.get(((Synchronizable) o).getSyncKey());
					// Only if the old item exists
					if (ss != null)
						fld.set(s, ss);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
