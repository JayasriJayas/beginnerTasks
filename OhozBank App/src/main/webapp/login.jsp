<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>OHO Bank - Login / Register</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css" rel="stylesheet">
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
  
  


  <link rel="stylesheet" href="css/login.css" />
  <link rel="stylesheet" href="css/register.css" />
  <link rel="stylesheet" href="css/header.css" />
  <link rel="stylesheet" href="css/toast.css" />
  <script>
    const BASE_URL = "<%=request.getContextPath()%>";
  </script>
</head>
<body>

  <%@ include file="header.jsp" %>

  <main class="main-container">

<div class="left-section">
  <div class="left-text">
    <h2>Welcome to OHOZ Bank</h2>
    <p>Secure. Smart. Seamless Banking for Everyone.</p>
  </div>
  <img src="<%= request.getContextPath() %>/assets/loginpic.png" alt="Login Cartoon" class="login-image" />
</div>


    <div class="right-section">
      <div class="wrapper">

        <div class="form-header">
          <h2 class="title-login">Login to Ohoz Bank</h2>
          <h2 class="title-register" style="display: none;">Create Your Account</h2>
        </div>

        <form id="loginForm" class="login-form">
          <div class="input-box">
            <input type="text" id="username" class="input-field" required />
            <label class="label">Username (Email)</label>
          </div>
          <div class="input-box">
            <input type="password" id="password" class="input-field" required />
            <label class="label">Password</label>
            <i class="bx bx-show icon" id="toggleEye"></i>
          </div>
          <div class="form-cols">
            <label><input type="checkbox" id="rememberMe" >Remenber me</label>
          
          </div>
          <button type="submit" class="btn-submit" id="loginBtn">Login</button>
        </form>

        <!-- Register Form -->
        <form id="signupForm" class="register-form" style="display: none;">
          <!-- Back Button -->
          <div class="back-icon" onclick="backToLogin()">
            <i class="bx bx-arrow-back"></i>
          </div>

          <!-- Progress Bar -->
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
              <input type="number" name="phone" id="reg-phone" required>
            </div>
            <div class="input-group">
              <label for="reg-email">Email</label>
              <input type="email" name="email" id="reg-email" required>
            </div>
            <div class="btns-group">
              <button type="button" class="btn btn-prev">Previous</button>
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
              <input type="number" name="aadharNo" id="reg-aadhar" required>
            </div>
            <div class="input-group">
              <label for="reg-pan">PAN Number</label>
              <input type="text" name="panNo" id="reg-pan" required>
            </div>
            <div class="btns-group">
              <button type="button" class="btn btn-prev">Previous</button>
              <button type="button" class="btn btn-next">Next</button>
            </div>
          </div>

          <!-- Step 4 -->
          <div class="form-step">
            <div class="input-group">
              <label for="reg-branch">Branch ID</label>
 				 <select name="branchId" id="reg-branch" required>
   				 <option value="">Select Branch</option>
				 </select>
            </div>
            <div class="input-group">
              <label for="reg-username">Username</label>
              <input type="text" name="username" id="reg-username" required>
            </div>
            <div class="input-group">
              <label for="reg-password">Password</label>
              <input type="password" name="password" id="reg-password" required>
                <i class="bx bx-show icon" id="toggleEyeRegister"></i> 
            </div>
            <div class="btns-group">
              <button type="button" class="btn btn-prev">Previous</button>
              <button type="button" class="btn btn-preview">Preview</button>
            </div>
          </div>
        </form>

     <div class="switch-form" id="switchLinks">
  <p>
    Don’t have an account? <a href="#" onclick="registerFunction()">Register</a>
  </p>
</div>

        <div id="errorMsg"></div>
      </div>
    </div>
  </main>

  <div id="toast-container" class="toast-container"></div>

  <script src="js/login.js"></script>
  <script src="js/register.js"></script>
  <script src="js/header.js"></script>

  
</body>
</html>
