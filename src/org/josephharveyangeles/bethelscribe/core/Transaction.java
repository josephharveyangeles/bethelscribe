package org.josephharveyangeles.bethelscribe.core;

public interface Transaction<T extends TransactionRequest> {

	public void execute(T requestData) throws TransactionException;
}
