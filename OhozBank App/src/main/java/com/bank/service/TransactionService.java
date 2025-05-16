package com.bank.service;

public interface TransactionService {
	boolean deposit(long accountId, double amount, String performedBy);
	boolean withdraw(long accountId, double amount, String performedBy);
	boolean transfer(long fromAccountId, long toAccountId, double amount, String performedBy);
}
