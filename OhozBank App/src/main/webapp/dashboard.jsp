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
  <link rel="stylesheet" href="<%= ctx %>/css/modal.css" />

  <link rel="stylesheet" href="css/toast.css" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>

  <%@ include file="includes/header.jsp" %>	  
  <%@ include file="includes/sidebar.jsp" %>

  <div id="modalOverlay" class="modal-overlay hidden">
    <div class="modal-box">

    
      <h3 id="modalTitle"></h3>
      <div id="modalBody"></div>
     
    </div>
  </div>


  <main class="dashboard-container" id="main">
    <div id="dashboardContent" class="dashboard-content-box"></div>
  </main>


  <div id="toast-container" class="toast-container"></div>

  <footer>
    <div style="text-align: center; padding: 10px;">
      <p>&copy; 2025 Your Company Name. All rights reserved.</p>
    </div>
  </footer>
	
	    
	
	
	  
	
	  <!-- Scripts -->
	  <script>
	    const BASE_URL = "<%= ctx %>";
	  </script>
	  <script>
 		 const userRole = "<%= session.getAttribute("role") %>";
	  </script>
	  <script src="<%= ctx %>/js/dashboard.js"></script>
	  <!-- Include the Toast JS file -->
	<script src="<%= ctx %>/js/toast.js"></script>
	  
	 
	  
	
	</body>
	</html>
