	package com.bank.models;
	
	import java.math.BigDecimal;
	
	import com.bank.enums.TransactionStatus;
	import com.bank.enums.TransactionType;
	
	public class Transaction {
		private long transactionId;
	    private Long accountId;
	    private long userId;
	    private Long transactionAccountId;
	    private BigDecimal amount;
	    private BigDecimal closingBalance;
	    private TransactionType type;
	    private long timestamp;
	    private TransactionStatus status;
	    private String description;
	    private String transactionMode;  
	    public String getTransactionMode() {
			return transactionMode;
		}
		public void setTransactionMode(String transactionMode) {
			this.transactionMode = transactionMode;
		}
		public String getReceiverBank() {
			return receiverBank;
		}
		public void setReceiverBank(String receiverBank) {
			this.receiverBank = receiverBank;
		}
		public String getReceiverIFSC() {
			return receiverIFSC;
		}
		public void setReceiverIFSC(String receiverIFSC) {
			this.receiverIFSC = receiverIFSC;
		}
		private String receiverBank;
	    private String receiverIFSC;
	    
	    public long getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(long transactionId) {
			this.transactionId = transactionId;
		}
		public Long getAccountId() {
			return accountId;
		}
		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}
		public Long getTransactionAccountId() {
			return transactionAccountId;
		}
		public void setTransactionAccountId(Long transactionAccountId) {
			this.transactionAccountId = transactionAccountId;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public BigDecimal getClosingBalance() {
			return closingBalance;
		}
		public void setClosingBalance(BigDecimal closingBalance) {
			this.closingBalance = closingBalance;
		}
		public TransactionType getType() {
			return type;
		}
		public void setType(TransactionType type) {
			this.type = type;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public TransactionStatus getStatus() {
			return status;
		}
		public void setStatus(TransactionStatus status) {
			this.status = status;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	
	
	}
