<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>OHO Bank - Login / Register</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  
  <!-- BOXICONS -->
  <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
  
  <!-- CSS -->
  <link rel="stylesheet" href="css/login.css">
  <link rel="stylesheet" href="css/register.css">
  <link rel="stylesheet" href="css/toast.css">

  <script>
    const BASE_URL = "<%= request.getContextPath() %>";
  </script>
</head>
<body>

<%@ include file="toast.jsp" %>

<div class="wrapper">
  <div class="form-header">
    <div class="titles">
      <div class="title-login" style="display: block;">Login</div>
      <div class="title-register" style="display: none;">Register</div>
    </div>
  </div>
 

  <!-- LOGIN FORM -->
  <form class="login-form" id="loginForm">
    <div class="input-box">
      <input type="text" class="input-field" id="username" name="username" required>
      <label for="username" class="label top">Username</label>
      <i class='bx bx-user icon'></i>
    </div>
    <div class="input-box">
      <input type="password" class="input-field" id="password" name="password" required>
      <label for="password" class="label top">Password</label>
      <i class='bx bx-show icon toggle-eye' id="toggleEye"></i>
    </div>
    <div class="form-cols">
      <div class="col-1">
        <input type="checkbox" id="rememberMe">
        <label for="rememberMe">Remember Me</label>
      </div>
    </div>
    <div class="input-box">
      <button class="btn-submit" id="loginBtn" type="submit">Login <i class='bx bx-log-in'></i></button>
    </div>
    <div id="errorMsg" style="color:red; text-align:center;"></div>
    <div class="switch-form">
      <span>Don't have an account? <a href="#" onclick="registerFunction()">Register</a></span>
    </div>
  </form>

  <!-- REGISTER FORM -->
   
  <form class="register-form" id="signupForm" style="display:none">
  <div class="back-icon" onclick="backToLogin()">
        <i class="bx bx-arrow-back"></i>
    </div>
    <div class="progressbar">
      <div class="progress" id="progress"></div>
      <div class="progress-step progress-step-active" data-title="Personal"></div>
      <div class="progress-step" data-title="Contact"></div>
      <div class="progress-step" data-title="Identity"></div>
      <div class="progress-step" data-title="Account"></div>
    </div>

    <!-- Step 1 -->
    <div class="form-step form-step-active">
      <div class="input-group">
        <label for="reg-name">Full Name</label>
        <input type="text" name="name" id="reg-name" required>
      </div>
        <div class="input-group">
        <label for="reg-dob">Date of Birth</label>
        <input type="date" name="dob" id="reg-dob" required>
      </div>
      <div class="input-group">
        <label for="reg-gender">Gender</label>
        <select id="reg-gender" name="gender" required>
          <option value="">Select Gender</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
          <option value="OTHER">Other</option>
        </select>
      </div>
       <div class="input-group">
        <label for="reg-address">Address</label>
        <input type="text" name="address" id="reg-address" required>
      </div>
      <div class="input-group">
       <button type="button" class="btn btn-next">Next</button>

      </div>
    </div>

    <!-- Step 2 -->
    <div class="form-step">
      <div class="input-group">
        <label for="reg-phone">Phone</label>
        <input type="text" name="phone" id="reg-phone" required>
      </div>
      <div class="input-group">
        <label for="reg-email">Email</label>
        <input type="email" name="email" id="reg-email" required>
      </div>
      <div class="btns-group">
        <button type="button"class="btn btn-prev">Previous</button>
        <button type="button" class="btn btn-next">Next</button>

      </div>
    </div>

    <!-- Step 3 -->
    <div class="form-step">
    
      <div class="input-group">
        <label for="reg-marital">Marital Status</label>
        <select id="reg-marital" name="maritalStatus" required>
          <option value="">Select</option>
          <option>Single</option>
          <option>Married</option>
        </select>
      </div>
        <div class="input-group">
        <label for="reg-occupation">Occupation</label>
        <input type="text" name="occupation" id="reg-occupation" required>
      </div>
      <div class="input-group">
        <label for="reg-income">Annual Income</label>
        <input type="number" name="annualIncome" id="reg-income" required>
      </div>
      <div class="input-group">
        <label for="reg-aadhar">Aadhar Number</label>
        <input type="text" name="aadharNo" id="reg-aadhar" required>
      </div>
      <div class="input-group">
        <label for="reg-pan">PAN Number</label>
        <input type="text" name="panNo" id="reg-pan" required>
      </div>
      <div class="btns-group">
      <button type="button"class="btn btn-prev">Previous</button>
       <button type="button" class="btn btn-next">Next</button>

      </div>
    </div>

    <!-- Step 4 -->
    <div class="form-step">
     
    
      
      <div class="input-group">
        <label for="reg-branch">Branch ID</label>
        <input type="number" name="branchId" id="reg-branch" required>
      </div>
      <div class="input-group">
        <label for="reg-username">Username</label>
        <input type="text" name="username" id="reg-username" required>
      </div>
      <div class="input-group">
        <label for="reg-password">Password</label>
        <input type="password" name="password" id="reg-password" required>
      </div>
      <div class="btns-group">
        <button type="button"class="btn btn-prev">Previous</button>
       <button type="button" class="btn btn-preview">Preview</button>

      </div>
    </div>
  </form>
</div>

<script src="js/register.js"></script>
<script src="js/login.js"></script>
</body>
</html>
