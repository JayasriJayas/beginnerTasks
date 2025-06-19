
function togglePassword() {
  const pwd = document.getElementById("password");
  const eye = document.getElementById("toggleEye");
  if (pwd.type === "password") {
    pwd.type = "text";
    eye.classList.remove("bx-show");
    eye.classList.add("bx-hide");
  } else {
    pwd.type = "password";
    eye.classList.remove("bx-hide");
    eye.classList.add("bx-show");
  }
}

document.getElementById("toggleEye").addEventListener("click", togglePassword);
document.getElementById("loginForm").addEventListener("submit", async function (event) {
  event.preventDefault();
  const loginBtn = document.getElementById("loginBtn");

  const data = {
    username: document.getElementById("username").value,
    password: document.getElementById("password").value,
    rememberMe: document.getElementById("rememberMe").checked,
  };

  loginBtn.disabled = true;
  loginBtn.innerText = "Logging in...";

  try {
    const response = await fetch(`${BASE_URL}/api/login/user`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
	  credentials: "include",
    });

    if (response.ok) {
      showToast("Login Successful", "success");
      setTimeout(() => window.location.href = `${BASE_URL}/dashboard.jsp`, 1000);
    } else {
      const contentType = response.headers.get("Content-Type");
      if (contentType && contentType.includes("application/json")) {
        const error = await response.json();
        showToast("Login Failed: " + error.message, "error");
      } else {
        const raw = await response.text();
        showToast("Unexpected Response: " + raw, "error");
      }
    }
  } catch (err) {
    showToast("Network Error: " + err.message, "error");
  }

  loginBtn.disabled = false;
  loginBtn.innerText = "Login";
});
