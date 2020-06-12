package com.crivano.jsync;

import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.jsync.Synchronizable;

public class Bar implements Synchronizable {
	@IgnoreForSimilarity
	Long id;
	Foo foo;
	boolean changed;

	public Bar(Long id, Foo foo, boolean changed) {
		super();
		this.id = id;
		this.foo = foo;
		this.changed = changed;
	}

}
