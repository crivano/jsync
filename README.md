Project JLogic
====
JLogic is a simple open-source library for evaluating boolean expressions in java. It is specifically designed to be used as a business rules framework, and to be able to "explain" why a certain conclusion was reached.

Main goals are:
- To be fast
- To be easily readable
- To provide a String representing the reason why an expression evaluates do true or false

Example / Usage
====
A basic propositional expression is built out of the types `And`, `Or` and `Not`.  All of these extend the base type Expression.  For example,

```java
Expression e = And.of(new CanFoo(true),
		Or.of(new CanFoo2(false), new CanBar2(true)));
assertTrue(JLogic.eval(e));
assertEquals("foo _and_ bar2", JLogic.explain(e, true));
```

Classes CanFoo, CanFoo2 and CanBar2 represent specific business rules, all necessary parameters are provided at the in the constructor. In our simple demo, they receive only one parameter, that states whether they should succeed or fail. Here is the implementation of CanFoo:

```java
public class CanFoo implements Expression {
	boolean b;

	public CanFoo(boolean b) {
		this.b = b;
	}

	public boolean eval() {
		return this.b;
	}

	public String explain(boolean result) {
		return JLogic.explain("foo", result);
	}
}
```

A false situation and a true one may be expressed different ways. JLogic.explain is a very simple function that prefixes the first parameter with "_not_" when explaining a false result. Of course, you can write your own explanations.

```java
public static String explain(String explanation, boolean result) {
	if (result)
		return explanation;
	else
		return NOT + explanation;
}
```

Building
====

JLogic is built with Maven.  To build from source,

```bash
> mvn package
```

generates a snapshot jar target/jlogic-0.0.1-SNAPSHOT.jar.

To run the test suite locally,

```bash
> mvn test
```

Development
====

JLogic is very much in-development, and is in no way, shape, or form guaranteed to be stable or bug-free.  Bugs, suggestions, or pull requests are all very welcome.

License
====
Copyright 2016 Renato Crivano

Licensed under the Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0
# jsync
