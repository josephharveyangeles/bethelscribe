package org.josephharveyangeles.bethelscribe.core;

public class TransactionException extends Exception {

	private static final long serialVersionUID = -6662966156679798253L;

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
