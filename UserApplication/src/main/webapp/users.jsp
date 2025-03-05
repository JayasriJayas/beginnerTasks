<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.customer.model.User" %>

<html>
<head>
	<link rel="stylesheet" href="css/users.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <title>User Management</title>
    <script>
    function confirmDelete() {
        return confirm("Are you sure you want to delete this user?");
    }
	</script>
    
</head>
<body>
	<header>
    <a href=register.jsp><img src="assets/logo.png"/></a>
    
    <div class="header-links">
        <a href="register.jsp" class="btn">
            <i class="fa-solid fa-user-plus"></i> Add User
        </a>
        <a href="register.jsp" class="btn">
            <i class="fa-solid fa-house"></i> Home
        </a>
    </div>
</header>


    	<div class="table">
    	<section class="table-head">
        	<h2>Users List</h2>
    	</section>
    <section class="table-body">
    <table border="1">
        <thead>
            <th>ID</th>
            <th>First Name</th>
            <th>Surname</th>
            <th>Username</th>
            <th>Date of Birth</th>
            <th>Gender</th>
            <th>Address</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Edit Action</th>
            <th>Delete Action</th>
        </thead>
        <%
            List<User> users = (List<User>) request.getAttribute("userList");
            if (users != null) {
                for (User user : users) {
        %>
        <tr>
            <td><%= user.getId() %></td>
            <td><%= user.getFirstname() %></td>
            <td><%= user.getSurname() %></td>
            <td><%= user.getUsername() %></td>
            <td><%= user.getDob() %></td>
            <td><%= user.getGender() %></td>
            <td><%= user.getAddress() %></td>
            <td><%= user.getEmail() %></td>
            <td><%= user.getPhone() %></td>
            <td>
    			<form action="RegisterServlet" method="get" >
        			<input type="hidden" name="id" value="<%= user.getId() %>">
        			<input type="hidden" name="action" value="edit">
        			<button type="submit"><i class="fa-solid fa-pen" ></i> <span class="tittle">Edit User</span></button>
    			</form>
             </td>
             <td>
                <form action="RegisterServlet" method="get"onsubmit="return confirmDelete();">
    				<input type="hidden" name="id" value="<%= user.getId() %>">
    				<input type="hidden" name="action" value="delete">
    				<button type="submit"><i class="fa-solid fa-trash" ></i> <span class="tittle">Delete user</span></button>
				</form>

            </td>
        </tr>
        <%
                }
            }
        %>
    </table>
    </section>
</div>

</body>
</html>
