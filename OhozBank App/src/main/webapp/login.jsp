<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - OHO Bank</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/login.css">
       <script>
        const BASE_URL = "<%= request.getContextPath() %>";
       
    </script>
</head>
<body>
    <div class="login-container">
        <h2>OhOz Bank Login</h2>
        <form id="loginForm">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" placeholder="Enter your username" required />

            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Enter your password" required />

            <div class="show-password">
                <input type="checkbox" id="showPwd" onclick="togglePassword()"> 
                <label for="showPwd">Show Password</label>
            </div>

            <button type="submit" id="loginBtn">Login</button>
            <div id="errorMsg" style="color:red;"></div>
        </form>
    </div>

    <script>
   
    	console.log("at another script tag");
    	console.log(temp);
        function togglePassword() {
            const pwd = document.getElementById("password");
            pwd.type = pwd.type === "password" ? "text" : "password";
        }

        document.getElementById("loginForm").addEventListener("submit", async function(event) {
            event.preventDefault();
            const loginBtn = document.getElementById("loginBtn");
            const errorDiv = document.getElementById("errorMsg");
            errorDiv.textContent = "";

            const data = {
                username: document.getElementById("username").value,
                password: document.getElementById("password").value
            };

            loginBtn.disabled = true;
            loginBtn.innerText = "Logging in...";

            try {
            
           		const response = await fetch(BASE_URL+'/api/login/user', {

                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                });
            	
                if (response.ok) {
                    alert("Login Successful");
                    window.location.href = `${BASE_URL}/dashboard.jsp`;
                } else {
                    const contentType = response.headers.get("Content-Type");
                    if (contentType && contentType.includes("application/json")) {
                        const error = await response.json();
                        errorDiv.textContent = "Login Failed: " + error.message;
                    } else {
                        const raw = await response.text();
                        errorDiv.textContent = "Unexpected Response: " + raw;
                    }
                }
            } catch (err) {
                errorDiv.textContent = "Network Error: " + err.message;
            }

            loginBtn.disabled = false;
            loginBtn.innerText = "Login";
        });
    </script>
</body>
</html>
