package com.crivano.jsync;

import java.io.Serializable;
import java.lang.reflect.Field;

public interface Synchronizable {
	default public String getSyncKey() {
		try {
			for (Field fld : SyncUtils.getFieldList(this.getClass())) {
				if (!"id".equals(fld.getName()))
					continue;
				Serializable id = (Long) fld.get(this);
				if (id == null)
					continue;
				return this.getClass().getName() + ": " + id.toString();

			}
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			new RuntimeException(e);
		}
		return this.getClass().getName() + ": " + this.toString();
	}

	// Retorna zero se o elemento for independente de qualquer outro, ou 1, 2,
	// 3, etc conforme distância para o elemento independente mais longinquo.
	default public int getSyncDependencyLevel() {
		return SyncUtils.getDependencyLevel(this);
	}

	default public boolean isSyncSimilar(Synchronizable obj, int level) {
		return SyncUtils.alike(this, obj, level);
	}

	default public String getSyncDescription() {
		return this.getClass().getName() + ": " + this.getSyncKey();
	}
}
