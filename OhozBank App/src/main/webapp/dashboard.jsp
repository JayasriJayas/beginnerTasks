<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <title>Dashboard</title>

  <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />

  <!-- Stylesheets -->
  <link rel="stylesheet" href="<%= ctx %>/css/dheader.css">
  <link rel="stylesheet" href="<%= ctx %>/css/transaction.css">
  <link rel="stylesheet" href="<%= ctx %>/css/sidebar.css">
  <link rel="stylesheet" href="<%= ctx %>/css/form.css">
</head>

<body>

  <!-- Includes -->
  <%@ include file="includes/header.jsp" %>
  <%@ include file="includes/sidebar.jsp" %>

  <!-- Modal HTML -->
  <div id="modalOverlay" class="modal-overlay hidden">
    <div class="modal-box">
      <h3 id="modalTitle"></h3>
      <div id="modalBody"></div>
      <div style="text-align: right; margin-top: 1rem;">
        <button class="profile-btn logout-btn" onclick="closeModal()">Close</button>
      </div>
    </div>
  </div>

  <!-- Main Content -->
  <main class="dashboard-container" id="main">
    <div id="dashboardContent" class="dashboard-content-box"></div>
  </main>

  <!-- Scripts -->
  <script>
    const BASE_URL = "<%= ctx %>";
  </script>
  <script src="<%= ctx %>/js/dashboard.js"></script>
  <script src="<%= ctx %>/js/toast.js"></script>
  

</body>
</html>
