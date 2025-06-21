<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String currentPage = request.getRequestURI();
  boolean isLoginPage = currentPage.endsWith("login.jsp");
  boolean isDashboard = currentPage.endsWith("dashboard.jsp");
%>

<header class="header" id="header">
  <nav class="nav container">

    <!--  Logo -->
    <a href="<%= request.getContextPath() %>/login.jsp" class="nav__logo">
      <img src="<%= request.getContextPath() %>/assets/logo.png" alt="OHOZ Logo" class="nav__logo-img" />
    </a>

    <!--  Navigation Menu -->
    <div class="nav__menu" id="nav-menu">
      <% if (!isDashboard) { %>
      <ul class="nav__list">
        <li class="nav__item">
          <a href="<%= request.getContextPath() %>/login.jsp?form=login" class="nav__link" data-tooltip="Home">
            <i class="ri-home-4-line"></i>
            <span>Home</span>
          </a>
        </li>
        <li class="nav__item">
          <a href="<%= request.getContextPath() %>/login.jsp?form=register" class="nav__link" data-tooltip="Register">
            <i class="ri-user-add-line"></i>
            <span>Register</span>
          </a>
        </li>
      </ul>
      <% } %>

      <!-- Close Button (Mobile) -->
      <div class="nav__close" id="nav-close">
        <i class="ri-close-line"></i>
      </div>
    </div>

    <!-- Profile Icon for Dashboard only -->
    <% if (isDashboard) { %>
      <div class="nav__profile" id="profileIcon" title="Profile">
        <i class="bx bx-user-circle"></i>
      </div>
    <% } %>

    <!-- Hamburger Toggle -->
    <div class="nav__toggle" id="nav-toggle">
      <i class="ri-menu-line"></i>
    </div>

  </nav>
</header>
x