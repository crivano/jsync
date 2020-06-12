package com.crivano.jsync;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public abstract class SynchronizableSupport implements Synchronizable, Serializable {
	private Long syncId;
	private Long syncInitialId;
	private String syncExternalId;
	private Date syncDateBegin;
	private Date syncDateEnd;
	private String syncBatchId;



}
