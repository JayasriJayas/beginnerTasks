<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.customer.servlet.User" %>

<html>
<head>
	<link rel="stylesheet" href="css/users.css">
    <title>User Management</title>
</head>
<body>
    <h2>Users List</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Surname</th>
            <th>Username</th>
            <th>Password</th>
            <th>Date of Birth</th>
            <th>Gender</th>
            <th>Address</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Actions</th>
        </tr>
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
            <td><%= user.getPassword() %></td>
            <td><%= user.getDob() %></td>
            <td><%= user.getGender() %></td>
            <td><%= user.getAddress() %></td>
            <td><%= user.getEmail() %></td>
            <td><%= user.getPhone() %></td>
            <td>
                <form action="RegisterServlet" method="post">
                    <input type="hidden" name="id" value="<%= user.getId() %>">
                    <button type="submit" name="delete">Delete</button>
                </form>
                <form action="RegisterServlet" method="get">
    				<input type="hidden" name="id" value="<%= user.getId() %>">
    				<button type="submit" name="edit">Edit</button>
				</form>

            </td>
        </tr>
        <%
                }
            }
        %>
    </table>
</body>
</html>
