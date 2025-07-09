<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String userRole = (String) session.getAttribute("role"); // Assuming userRole is stored in session
%>
<!-- Sidebar -->
<nav class="sidebar" id="sidebar">
  <button class="toggle-btn" id="sidebarToggleBtn">
    <i class='bx bx-chevron-right'></i>
  </button>

  <% if ("ADMIN".equals(userRole)) { %>
 
    <a href="#" class="menu-item" data-action="dashboard-admin" data-tooltip="Dashboard">
      <i class='bx bx-grid-alt'></i><span>Dashboard</span>
    </a>
    <a href="#" class="menu-item" data-action="admin-request-list" data-tooltip="Requests">
      <i class='bx bx-git-branch'></i><span>Requests</span>
    </a>
    <a href="#" class="menu-item" data-action="admin-accountrequest-list" data-tooltip="AccountRequests">
      <i class='bx bx-user'></i><span>AccountRequest</span>
    </a>
     <a href="#" class="menu-item" data-action="admin-payment" data-tooltip="Payment">
      <i class='bx bx-wallet'></i><span>Payment</span>
    </a>
    <a href="#" class="menu-item" data-action="admin-transactions" data-tooltip="Transactions">
      <i class='bx bx-credit-card'></i><span>Transaction</span>
    </a>
     <a href="#" class="menu-item" data-action="admin-accounts" data-tooltip="Accounts">
      <i class='bx bx-id-card'></i> <span>Accounts</span>
    </a>
    
  <% } else if ("SUPERADMIN".equals(userRole)) { %>
   
 <a href="#" class="menu-item" data-action="dashboard-superadmin" data-tooltip="Dashboard">
   <i class='bx bx-grid-alt'></i><span>Dashboard</span>
</a>
<a href="#" class="menu-item" data-action="superadmin-request-list" data-tooltip="Requests">
    <i class='bx bx-git-branch'></i><span>Request</span>
</a>
<a href="#" class="menu-item" data-action="superadmin-accountrequest-list" data-tooltip="AccountRequests">
  <i class='bx bx-archive'></i><span>AccountRequests</span>
</a>
<a href="#" class="menu-item" data-action="admin-list" data-tooltip="Admins">
  <i class='bx bx-user'></i><span>Admin</span>
</a>
<a href="#" class="menu-item" data-action="branch-list" data-tooltip="Branch">
  <i class='bx bx-map-pin'></i><span>Branch</span>
</a>
<a href="#" class="menu-item" data-action="superadmin-payment" data-tooltip="Payment">
  <i class='bx bx-money'></i><span>Payment</span>
</a>
<a href="#" class="menu-item" data-action="superadmin-transactions" data-tooltip="Transactions">
  <i class='bx bx-credit-card-front'></i><span>Transaction</span>
</a>
 <a href="#" class="menu-item" data-action="superadmin-accounts" data-tooltip="Accounts">
     <i class='bx bx-id-card'></i> </i><span>Accounts</spsan>
    </a>

       
  <% } else { %>
    <!-- User Sidebar -->
    <a href="#" class="menu-item" data-action="dashboard" data-tooltip="Dashboard">
      <i class='bx bx-grid-alt'></i><span>Dashboard</span>
    </a>
    <a href="#" class="menu-item" data-action="payment" data-tooltip="Payment">
      <i class='bx bx-credit-card'></i><span>Payment</span>
    </a>
    <a href="#" class="menu-item" data-action="transaction" data-tooltip="Transactions">
      <i class='bx bx-transfer-alt'></i><span>Transaction</span>
    </a>
    <a href="#" class="menu-item" data-action="account" data-tooltip="Account">
       <i class='bx bxs-user-detail'></i><span>Account</span>
    </a>
  <% } %>
</nav>
