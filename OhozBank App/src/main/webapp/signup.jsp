<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up - OHO Bank</title>
    <style>
        body {
            background-color: #E8ECEB;
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 500px;
            margin: 50px auto;
            background: #fff;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 {
            color: #2D3E4E;
            text-align: center;
        }
        .progress-bar {
            display: flex;
            margin-bottom: 20px;
        }
        .step {
            flex: 1;
            padding: 10px;
            text-align: center;
            border-bottom: 4px solid #ccc;
            color: #ccc;
        }
        .step.active {
            border-color: #E09E50;
            color: #E09E50;
            font-weight: bold;
        }
        .form-step {
            display: none;
        }
        .form-step.active {
            display: block;
        }
        input, select {
            width: 100%;
            padding: 12px;
            margin-top: 10px;
            margin-bottom: 15px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }
        button {
            background-color: #8CBDB9;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
        }
        button:hover {
            background-color: #71a8a3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Create Your Account</h2>
        <div class="progress-bar">
            <div class="step active" id="step1Bar">Personal</div>
            <div class="step" id="step2Bar">Identity</div>
            <div class="step" id="step3Bar">Account</div>
        </div>
        <form id="signupForm">
            <!-- Step 1 -->
            <div class="form-step active" id="step1">
                <input type="text" name="name" placeholder="Full Name" required>
                <input type="email" name="email" placeholder="Email" required>
                <input type="text" name="phone" placeholder="Phone Number" required>
                <select name="gender" required>
                    <option value="">Select Gender</option>
                    <option>MALE</option>
                    <option>FEMALE</option>
                    <option>OTHER</option>
                </select>
                <input type="date" name="dob" required>
                <select name="maritalStatus" required>
                    <option value="">Marital Status</option>
                    <option>Single</option>
                    <option>Married</option>
                </select>
                <button type="button" onclick="nextStep(2)">Next</button>
            </div>

            <!-- Step 2 -->
            <div class="form-step" id="step2">
                <input type="text" name="aadharNo" placeholder="Aadhar Number" required>
                <input type="text" name="panNo" placeholder="PAN Number" required>
                <input type="text" name="address" placeholder="Address" required>
                <input type="text" name="occupation" placeholder="Occupation" required>
                <input type="number" name="annualIncome" placeholder="Annual Income" required>
                <button type="button" onclick="nextStep(3)">Next</button>
            </div>

            <!-- Step 3 -->
            <div class="form-step" id="step3">
                <input type="text" name="username" placeholder="Username" required>
                <input type="password" name="password" placeholder="Password" required>
                <input type="number" name="branchId" placeholder="Branch ID" required>
                <button type="button" onclick="submitForm()">Submit</button>
            </div>
        </form>
    </div>

    <script>
        function nextStep(step) {
            document.querySelectorAll(".form-step").forEach(div => div.classList.remove("active"));
            document.getElementById("step" + step).classList.add("active");

            document.querySelectorAll(".step").forEach(div => div.classList.remove("active"));
            document.getElementById("step" + step + "Bar").classList.add("active");
        }

        function submitForm() {
            const form = document.getElementById("signupForm");
            const formData = new FormData(form);
            const jsonObject = {};
            formData.forEach((value, key) => { jsonObject[key] = value; });

            fetch("/signup/request", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(jsonObject)
            })
            .then(res => {
                if (res.ok) return res.json();
                else throw new Error("Signup failed");
            })
            .then(data => {
                alert("Signup successful! Wait for admin approval.");
                window.location.href = "login.jsp";
            })
            .catch(err => alert(err.message));
        }
    </script>
</body>
</html>
