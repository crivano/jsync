package com.crivano.jsync;

import java.util.List;

import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.jsync.Synchronizable;

public class Foo implements Synchronizable {
	@IgnoreForSimilarity
	Long id;
	List<Bar> bars;
	boolean a;
	boolean b;

	public Foo(Long id, List<Bar> bars, boolean changed) {
		super();
		this.id = id;
		this.bars = bars;
		this.a = changed;
	}
}
