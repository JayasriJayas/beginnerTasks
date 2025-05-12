import java.sql.Connection;
import java.time.Instant;

import com.bank.connection.DBConnectionPool;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.mapper.CustomerMapper;
import com.bank.mapper.UserMapper;
import com.bank.models.Account;
import com.bank.models.Customer;
import com.bank.models.User;

@Override
public boolean approveRequestAndCreateUser(long requestId, long adminId) throws Exception {
    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
        conn.setAutoCommit(false);

        Request req = getRequestById(requestId);
        
        User user = UserMapper.fromRequest(req);
        long userId = insertUser(conn, user);

        Customer customer = CustomerMapper.fromRequest(req, userId);
        insertCustomer(conn, customer);
        Account account = new Account();
        account.setUserId(userId);
        account.setBranchId(req.getBranchId());
        account.setBalance(0L);
        account.setStatus(UserStatus.ACTIVE);
        account.setCreatedAt(Instant.now());
        account.setModifiedBy("admin-" + adminId);

        insertAccount(conn, account);

        updateRequestStatus(conn, requestId, adminId, "APPROVED");

        conn.commit();
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
    
}
public long insertUser(Connection conn, User user) throws Exception {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("user", "username", "password", "email", "phone", "gender", "role", "status", "createdAt", "modifiedBy")
      .values(user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(),
              user.getGender(), user.getRole().name(), user.getStatus().name(),
              user.getCreatedAt(), user.getModifiedBy());

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.insertAndGetId(qb.build(), qb.getParameters());
}
public boolean insertCustomer(Connection conn, Customer customer) throws Exception {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("customer", "userId", "aadharNo", "panNo", "address", "dob", "maritalStatus", "occupation", "annualIncome")
      .values(customer.getUserId(), customer.getAadharNo(), customer.getPanNo(), customer.getAddress(),
              customer.getDob(), customer.getMaritalStatus(), customer.getOccupation(), customer.getAnnualIncome());

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.execute(qb.build(), qb.getParameters()) > 0;
}
public boolean insertAccount(Connection conn, Account account) throws Exception {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("account", "userId", "branchId", "balance", "status", "createdAt", "modifiedBy")
      .values(account.getUserId(), account.getBranchId(), account.getBalance(), account.getStatus(),
              account.getCreatedAt(), account.getModifiedBy());

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.execute(qb.build(), qb.getParameters()) > 0;
}
public boolean updateRequestStatus(Connection conn, long requestId, long adminId, String status) throws Exception {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.update("request")
      .set("status", status)
      .set("processedBy", adminId)
      .set("processedTimestamp", System.currentTimeMillis())
      .where("id = " + requestId);

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.execute(qb.build(), qb.getParameters()) > 0;
}


