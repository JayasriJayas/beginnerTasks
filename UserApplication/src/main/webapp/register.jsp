	<%@ page language="java" contentType="text/html; charset=UTF-8"
	    pageEncoding="UTF-8"%>
	<%@ page import="com.customer.model.User" %>
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
	    <script>
	        function validateForm() {
	            var firstname = document.getElementById("firstname").value.trim();
	            var surname = document.getElementById("surname").value.trim();
	            var username = document.getElementById("username").value.trim();
	            var password = document.getElementById("pass").value;
	            var confirmPass = document.getElementById("confirmpass").value;
	            var email = document.getElementById("mail").value.trim();
	            var phone = document.getElementById("phone").value.trim();
	            var address = document.getElementById("address").value.trim();
	            var dob = document.getElementById("date").value.trim();
	            var genderMale = document.getElementById("male").checked;
	            var genderFemale = document.getElementById("female").checked;
	
	            var passwordPattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
	            var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
	            var phonePattern = /^[0-9]{10}$/;
	
	            if (!firstname || !surname || !username || !password || !confirmPass || !email || !phone || !address || !dob || (!genderMale && !genderFemale)) {
	                alert("All fields are required. Please fill in all details");
	                return false;
	            }
	
	            if (!passwordPattern.test(password)) {
	                alert("Password must be at least 8 characters long, include one uppercase letter, one number, and one special character.");
	                return false;
	            }
	
	            if (password !== confirmPass) {
	                alert("Password and Confirm Password do not match.");
	                return false;
	            }
	
	           
	            if (!emailPattern.test(email)) {
	                alert("Please enter a valid email address.");
	                return false;
	            }
	            if (!phonePattern.test(phone)) {
	                alert("Phone number must be exactly 10 digits.");
	                return false;
	            }
	
	            return true;
	        }
	        function togglePassword() {
	            let passwordField = document.getElementById("pass");
	            let eyeIcon = document.getElementById("eyeIcon");

	            if (passwordField.type === "password") {
	                passwordField.type = "text";
	                eyeIcon.classList.remove("fa-eye");
	                eyeIcon.classList.add("fa-eye-slash");
	            } else {
	                passwordField.type = "password";
	                eyeIcon.classList.remove("fa-eye-slash");
	                eyeIcon.classList.add("fa-eye");
	            }
	        }

	        function toggleConfirm() {
	            let confirmField = document.getElementById("confirmpass");
	            let confirmEyeIcon = document.getElementById("confirmEyeIcon");

	            if (confirmField.type === "password") {
	                confirmField.type = "text";
	                confirmEyeIcon.classList.remove("fa-eye");
	                confirmEyeIcon.classList.add("fa-eye-slash");
	            } else {
	                confirmField.type = "password";
	                confirmEyeIcon.classList.remove("fa-eye-slash");
	                confirmEyeIcon.classList.add("fa-eye");
	            }
	        }
	    </script>
	</head>
	<body>
	    <header>
    <a href="#"><img src="assets/logo.png"/></a>
    <div class="header-links">
        <form class="btn" action="RegisterServlet" method="get">
            <input type="hidden" name="action" value="list">
            <button type="submit"><i class="fa-solid fa-circle-user"></i><span>View</span></button>
        </form>
        <a href="#" class="btn">
            <i class="fa-solid fa-house"></i> Home
        </a>
    </div>
</header>

	    <div class="box"> 
	        
	        <div class="imgbox">
	            <img id="sign-up" src="assets/register.png">
	        </div>
	        <div class="inputbox">
	      <%
	    		User user = (User) request.getAttribute("user");
		  %>
		  <% String errorMessage = (String) request.getAttribute("error"); %>
			<% if (errorMessage != null) { %>
	  			  <div class="error-message">
	        	  <%= errorMessage %>
	    		  </div>
			<% } %>
		
		<form class="input-form" action="RegisterServlet" method="post" onsubmit="return validateForm()">
		<div class="head-div">
	            <img src="assets/profile.avif" alt="Profile">
	            <h2>Welcome</h2>
	            </div>
	            <div class="input-container">
	    <input type="hidden" name="id" value="<%= (user != null) ? user.getId() : "" %>">
	    
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-pen-to-square"></i></div>
	        <input type="text" id="firstname" name="firstname" placeholder="First Name" 
	            value="<%= (user != null) ? user.getFirstname() : "" %>" required>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-pen-to-square"></i></div>
	        <input type="text" id="surname" name="surname" placeholder="Surname"  value="<%= (user != null) ? user.getUsername() : "" %>"required>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-user"></i></div>
	        <input type="text" id="username" name="username" placeholder="Username" 
	            value="<%= (user != null) ? user.getUsername() : "" %>" required>
	    </div>
	
	    <div class="input-div">
	    	 <div class="i"><i class="fa-solid fa-lock"></i></div>
    		<input type="password" id="pass" name="password" placeholder="Password" value="<%= (user != null) ? user.getPassword() : "" %>"required >
    		<span class="i eye" onclick="togglePassword()">
      			  <i id="eyeIcon" class="fa fa-eye"></i>
   			 </span>
		</div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-lock"></i></div>
	        <input type="password" id="confirmpass" name="confirmpass" placeholder="Confirm Password" value="<%= (user != null) ? user.getPassword() : "" %>"required>
	         
	        <span class="i eye" onclick="toggleConfirm()">
      			  <i id="confirmEyeIcon" class="fa fa-eye"></i>
   			 </span>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-calendar-days"></i></div>
	        <input type="date" id="date" name="dob" placeholder="Date of birth" 
	            value="<%= (user != null) ? user.getDob() : "" %>" required>
	    </div>
	
	    <div class="input-div">
	    	<span>
	        <label>Gender</label><br>
	        <input type="radio" id="male" name="gender" value="Male" 
	            <%= (user != null && "Male".equals(user.getGender())) ? "checked" : "" %>> Male
	        <input type="radio" id="female" name="gender" value="Female" 
	            <%= (user != null && "Female".equals(user.getGender())) ? "checked" : "" %>> Female
	        </span>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-house"></i></div>
	        <label for="address">Address</label>
	        <textarea id="address" name="address" cols="80" rows="5" placeholder="Address" required>
	            <%= (user != null) ? user.getAddress() : "" %>
	        </textarea>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-envelope"></i></div>
	        <input type="email" id="mail" name="email" placeholder="Email" 
	            value="<%= (user != null) ? user.getEmail() : "" %>" required>
	    </div>
	
	    <div class="input-div">
	        <div class="i"><i class="fa-solid fa-phone"></i></div>
	        <input type="number" id="phone" name="phone" placeholder="Phone No" maxlength="10" 
	            value="<%= (user != null) ? user.getPhone() : "" %>" required>
	    </div>
	
	    <input id="button" type="submit" value="<%= (user != null) ? "Update": "Register" %>">
	        </div>
	</form>
		 
	        </div>
	        </div>
	        
	</body>
	
	</html>

