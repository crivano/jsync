JSync
====

![](https://github.com/crivano/jsync/workflows/Java%20CI/badge.svg)

JSync is a simple open-source library that analyses two sets of POJOs, that may be linked to form graphs, and returns the operations that are necessary to go from the former graph to the latter.

It may seem to be an easy task, but it's not so. Detection of changes require a nesting batch algorithm involving all objects on both graphs. And, the resulting list of operations should be ordered based on the level of dependency of each object.

There are many applications for this kind of synchronization, but the most common is to record changes in a database. For that, the current objects on the database can be provided as the "old" graph and the "new" graph can be filled with the desired outcome. JSync will create a list of operations that may be added to the database to update the graph. In this situation, operations will be executed only if differences are detected.

Example / Usage
====
Melhods ```addOld``` and ```addNew``` should be used to populate both graphs with objects. Then, ```sync``` method can be called to produce a list of operations. 

Some annotations may be added to POJOs in order to ignore fields both for the similarity check and for the computing of dependency levels.


A complete example can be found among the unit tests:

```java
public class Foo implements Synchronizable {
	@IgnoreForSimilarity
	Long id;
	@IgnoreForDependencyLevel
	List<Bar> bars;
	boolean a;
	boolean b;
}

public class Bar implements Synchronizable {
	@IgnoreForSimilarity
	Long id;
	Foo foo;
	boolean changed;
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
```
