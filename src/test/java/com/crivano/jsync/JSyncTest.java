package com.crivano.jsync;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.crivano.jsync.Operation;
import com.crivano.jsync.Operation.Operator;
import com.crivano.jsync.Synchronizer;

public class JSyncTest {

	@Test
	public void test11Similar() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Foo newFoo1 = new Foo(1L, null, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addNew(newFoo1);
		List<Operation> l = sync.sync();
		assertEquals(0, l.size());
	}

	@Test
	public void test11NotSimilar() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Foo newFoo1 = new Foo(1L, null, true);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addNew(newFoo1);
		List<Operation> l = sync.sync();
		assertEquals(1, l.size());
		assertEquals(Operator.UPDATE, l.get(0).getOperator());
		assertEquals(oldFoo1, l.get(0).getOld());
		assertEquals(newFoo1, l.get(0).getNew());

	}

	@Test
	public void test12Similar() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Foo newFoo1 = new Foo(1L, null, false);
		Foo newFoo2 = new Foo(2L, null, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addNew(newFoo1);
		sync.addNew(newFoo2);
		List<Operation> l = sync.sync();
		assertEquals(1, l.size());
		assertEquals(Operator.INSERT, l.get(0).getOperator());
	}

	@Test
	public void test22Similar() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Bar oldBar1 = new Bar(1L, oldFoo1, false);
		Foo newFoo1 = new Foo(1L, null, false);
		Bar newBar1 = new Bar(1L, newFoo1, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addOld(oldBar1);
		sync.addNew(newFoo1);
		sync.addNew(newBar1);
		List<Operation> l = sync.sync();
		assertEquals(0, l.size());
	}

	@Test
	public void test22BarChanged() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Bar oldBar1 = new Bar(1L, oldFoo1, false);
		Foo newFoo1 = new Foo(1L, null, false);
		Bar newBar1 = new Bar(1L, newFoo1, true);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addOld(oldBar1);
		sync.addNew(newFoo1);
		sync.addNew(newBar1);
		List<Operation> l = sync.sync();
		assertEquals(1, l.size());
		assertEquals(Operator.UPDATE, l.get(0).getOperator());
		assertEquals(oldBar1, l.get(0).getOld());
		assertEquals(newBar1, l.get(0).getNew());
		assertEquals(newBar1.foo, oldFoo1);
		assertEquals(1, l.get(0).getDependencyLevel());
	}

	@Test
	public void test22FooChanged() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Bar oldBar1 = new Bar(1L, oldFoo1, false);
		Foo newFoo1 = new Foo(1L, null, true);
		Bar newBar1 = new Bar(1L, newFoo1, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addOld(oldBar1);
		sync.addNew(newFoo1);
		sync.addNew(newBar1);
		List<Operation> l = sync.sync();
		assertEquals(2, l.size());
		assertEquals(Operator.UPDATE, l.get(0).getOperator());
		assertEquals(oldFoo1, l.get(0).getOld());
		assertEquals(newFoo1, l.get(0).getNew());
		assertEquals(0, l.get(0).getDependencyLevel());
		assertEquals(Operator.UPDATE, l.get(1).getOperator());
		assertEquals(oldBar1, l.get(1).getOld());
		assertEquals(newBar1, l.get(1).getNew());
		assertEquals(newBar1.foo, newFoo1);
		assertEquals(1, l.get(1).getDependencyLevel());
	}

	@Test
	public void test23BarChanged() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Bar oldBar1 = new Bar(1L, oldFoo1, false);
		Foo newFoo1 = new Foo(1L, null, false);
		Bar newBar1 = new Bar(1L, newFoo1, true);
		Bar newBar2 = new Bar(2L, newFoo1, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addOld(oldBar1);
		sync.addNew(newFoo1);
		sync.addNew(newBar1);
		sync.addNew(newBar2);
		List<Operation> l = sync.sync();
		assertEquals(2, l.size());
		assertEquals(Operator.INSERT, l.get(0).getOperator());
		assertEquals(newBar2, l.get(0).getNew());
		assertEquals(newBar2.foo, oldFoo1);
		assertEquals(1, l.get(0).getDependencyLevel());
		assertEquals(Operator.UPDATE, l.get(1).getOperator());
		assertEquals(oldBar1, l.get(1).getOld());
		assertEquals(newBar1, l.get(1).getNew());
		assertEquals(newBar1.foo, oldFoo1);
		assertEquals(1, l.get(1).getDependencyLevel());
	}

	@Test
	public void test23FooChanged() {
		Foo oldFoo1 = new Foo(1L, null, false);
		Bar oldBar1 = new Bar(1L, oldFoo1, false);
		Foo newFoo1 = new Foo(1L, null, true);
		Bar newBar1 = new Bar(1L, newFoo1, false);
		Bar newBar2 = new Bar(2L, newFoo1, false);
		Synchronizer sync = new Synchronizer();
		sync.addOld(oldFoo1);
		sync.addOld(oldBar1);
		sync.addNew(newFoo1);
		sync.addNew(newBar1);
		sync.addNew(newBar2);
		List<Operation> l = sync.sync();
		assertEquals(3, l.size());
		assertEquals(Operator.UPDATE, l.get(0).getOperator());
		assertEquals(oldFoo1, l.get(0).getOld());
		assertEquals(newFoo1, l.get(0).getNew());
		assertEquals(0, l.get(0).getDependencyLevel());
		assertEquals(Operator.INSERT, l.get(1).getOperator());
		assertEquals(newBar2, l.get(1).getNew());
		assertEquals(newBar2.foo, newFoo1);
		assertEquals(1, l.get(1).getDependencyLevel());
		assertEquals(Operator.UPDATE, l.get(2).getOperator());
		assertEquals(oldBar1, l.get(2).getOld());
		assertEquals(newBar1, l.get(2).getNew());
		assertEquals(newBar1.foo, newFoo1);
		assertEquals(1, l.get(2).getDependencyLevel());
	}

}
