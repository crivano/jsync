package com.crivano.jsync;

public class Operation<T extends Synchronizable> {
	public enum Operator {
		INSERT, UPDATE, DELETE
	}

	private T oNew;
	private T oOld;
	private Operator operator;
	private int dependencyLevel = -1;

	public Operation(Operator operacao, T novo, T antigo) {
		this.operator = operacao;
		this.oNew = novo;
		this.oOld = antigo;
	}

	public T getNew() {
		return oNew;
	}

	public void setNew(T oNew) {
		this.oNew = oNew;
	}

	public T getOld() {
		return oOld;
	}

	public void setOld(T oOld) {
		this.oOld = oOld;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperacao(Operator operator) {
		this.operator = operator;
	}

	public int getDependencyLevel() {
		if (dependencyLevel != -1)
			return dependencyLevel;
		if (getOperator() == Operator.INSERT)
			dependencyLevel = Integer.valueOf(getNew().getSyncDependencyLevel());
		if (getOperator() == Operator.UPDATE)
			dependencyLevel = Integer.valueOf(getNew().getSyncDependencyLevel());
		if (getOperator() == Operator.DELETE)
			dependencyLevel = Integer.valueOf(getOld().getSyncDependencyLevel());
		if (dependencyLevel == -1)
			throw new Error("Invalid operation.");
		return dependencyLevel;
	}

	public String getDescription() {
		if (getOperator() == Operator.INSERT) {
			return "inserting: " + getNew().getSyncDescription() + " (" + getDependencyLevel() + ")";
		}
		if (getOperator() == Operator.DELETE) {
			return "deleting: " + getOld().getSyncDescription() + " (" + getDependencyLevel() + ")";
		}
		if (getOperator() == Operator.UPDATE) {
			return "updating: " + getNew().getSyncDescription() + " (" + getDependencyLevel() + ")";
		}
		return null;
	}
}
