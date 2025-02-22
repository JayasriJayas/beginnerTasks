<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@100;200;300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    <title>Register</title>
</head>
<body>
   <h1>User Management System</h1>
    
    <a href="users.jsp">View All Users</a>
    
    <br><br>
    <div class="box"> 
        
        <div class="imgbox">
            <img id="sign-up" src="assets/register.png">
        </div>
        <div class="inputbox">
         <form action="RegisterServlet" method="post">
            <div class="head-div">
            <img src="assets/profile.avif" alt="Profile">
            <h2>Welcome</h2>
            </div>
            <div class="input-container">
            <div class="input-div">
                <div class="i"><i class="fa-solid fa-pen-to-square"></i></div>
                <input type="text" id="firstname" name="firstname" placeholder="First Name" value="<%= request.getParameter("firstname") != null ? request.getParameter("firstname") : "" %>" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-pen-to-square"></i></div>
                <input type="text" id="surname" name="surname" placeholder="Surname" value="<%= request.getParameter("surname") != null ? request.getParameter("surname") : "" %>" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-user"></i></div>
                <input type="text" id="username" name="username" placeholder="Username" value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-lock"></i></div>
                <input type="password" id="pass" name="password" placeholder="Password" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-lock"></i></div>
                <input type="password" id="confirmpass" name="confirmpass" placeholder="Confirm Password" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-calendar-days"></i></div>
                <input type="date" id="date" placeholder="Date of birth" name="dob" value="<%= request.getParameter("dob") != null ? request.getParameter("dob") : "" %>" required>
            </div>

            <div class="input-div">
                <label>Gender</label><br>
                <input type="radio" id="male" name="gender" value="Male" <%= "Male".equals(request.getParameter("gender")) ? "checked" : "" %>> Male
                <input type="radio" id="female" name="gender" value="Female" <%= "Female".equals(request.getParameter("gender")) ? "checked" : "" %>> Female
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-house"></i></div>
                <label for="address">Address</label>
                <textarea id="address" name="address" cols="80" rows="5" placeholder="Address" required><%= request.getParameter("address") != null ? request.getParameter("address") : "" %></textarea>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-envelope"></i></div>
                <input type="email" id="mail" name="email" placeholder="Email" value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>" required>
            </div>

            <div class="input-div">
                <div class="i"><i class="fa-solid fa-phone"></i></div>
                <input type="number" id="phone" name="phone" placeholder="Phone No" maxlength="10" value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>" required>
            </div>

            <input id="button" type="submit" value="Register">
        </div>
        </form> 
        </div>
    
    </div>
</body>
</html>
