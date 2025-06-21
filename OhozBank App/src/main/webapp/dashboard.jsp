<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>OHOZ Bank - Dashboard</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <!-- Icons -->
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/4.2.0/remixicon.min.css" />

  <!-- Styles -->
  <link rel="stylesheet" href="css/header.css" />
  <link rel="stylesheet" href="css/sidebar.css" />
  <link rel="stylesheet" href="css/dashboard.css" />
</head>
<body>

  <%@ include file="header.jsp" %>

  <!-- ========== MAIN CONTENT ========== -->
  <main class="dashboard-container" id="main">
    <div id="dashboardContent"></div>
  </main>

  <!-- ========== SIDEBAR ========== -->
  <nav class="sidebar" id="sidebar">
    <div class="sidebar__container">

      <!-- User Info -->
      <div class="sidebar__user">
       
        <div class="sidebar__info">
          <h3 id="profileName">User Name</h3>
          <span id="profileEmail">user@email.com</span>
        </div>
      </div>

      <!-- Menu Items -->
      <div class="sidebar__content">
        <div>
          <h3 class="sidebar__title">MENU</h3>
          <div class="sidebar__list">
            <a href="#" class="sidebar__link active-link" data-action="dashboard">
              <i class="ri-dashboard-fill"></i>
              <span>Dashboard</span>
            </a>
            <a href="#" class="sidebar__link" data-action="payment">
              <i class="ri-wallet-3-fill"></i>
              <span>Payment</span>
            </a>
            <a href="#" class="sidebar__link" data-action="statement">
              <i class="ri-file-list-3-fill"></i>
              <span>Transaction Statement</span>
            </a>
          </div>
        </div>
      </div>

    </div>
  </nav>

  <script>
    const BASE_URL = "<%= request.getContextPath() %>";
  </script>
  <script src="js/dashboard.js"></script>
</body>
</html>
