document.addEventListener("DOMContentLoaded", () => {
  const BASE = typeof BASE_URL !== 'undefined' ? BASE_URL : "";
  const profileIcon = document.getElementById("profileIcon");
  const toggle = document.getElementById("header-toggle");
  const sidebar = document.getElementById("sidebar");
  const header = document.getElementById("header");
  const main = document.getElementById("main");

  // Sidebar toggle
  if (toggle && sidebar && header) {
    toggle.addEventListener("click", () => {
      sidebar.classList.toggle("show-sidebar");
      header.classList.toggle("left-pd");
      if (main) main.classList.toggle("left-pd");
    });
  }

  // Profile click
  if (profileIcon) {
    profileIcon.addEventListener("click", () => {
      fetch(BASE + "/api/profile/user")
        .then(res => res.json())
        .then(data => {
          alert(`Welcome, ${data.name}`);
        })
        .catch(err => {
          console.error("Failed to load profile:", err);
        });
    });
  }

  fetch(BASE + "/api/profile/user", {
  method: 'POST',  // Specify the request method as POST
  headers: {
    'Content-Type': 'application/json'  // Tell the server you're sending JSON data
  },
  body: JSON.stringify({ userId: "1234" }) // You can include the data you want to send in the request body
})
.then(res => res.json())
.then(user => {
  const nameEl = document.getElementById("profileName");
  const emailEl = document.getElementById("profileEmail");

  if (nameEl) nameEl.innerText = user.name;
  if (emailEl) emailEl.innerText = user.email;

  const role = user.role?.toLowerCase() || "unknown";
  loadDashboardForRole(role);
})
.catch(err => {
  console.error("Profile load error:", err);
  const content = document.getElementById("dashboardContent");
  if (content) content.innerText = "Unable to load dashboard.";
});


  // Sidebar menu navigation
  document.querySelectorAll(".sidebar__link").forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      const action = e.currentTarget.getAttribute("data-action");

      // Remove active class from all links
      document.querySelectorAll(".sidebar__link").forEach(l => l.classList.remove("active-link"));
      e.currentTarget.classList.add("active-link");

      if (action === "payment") {
        loadPaymentForm(BASE);
      } else if (action === "dashboard") {
        loadDashboardForRole("user"); // or call API again to get role
      } else if (action === "statement") {
        document.getElementById("dashboardContent").innerHTML = "<h3>Transaction Statement Page</h3>";
      }
    });
  });
});

// Load dashboard based on role
function loadDashboardForRole(role) {
  const content = document.getElementById("dashboardContent");
  if (!content) return;

  switch (role) {
    case "superadmin":
      content.innerHTML = "<h2>Superadmin Dashboard</h2><p>Manage all branches and admins.</p>";
      break;
    case "admin":
      content.innerHTML = "<h2>Admin Dashboard</h2><p>Manage users of your branch.</p>";
      break;
    case "user":
      content.innerHTML = "<h2>User Dashboard</h2><p>Welcome to your account!</p>";
      break;
    default:
      content.innerHTML = "<p>Unknown role.</p>";
  }
}

// Load Payment Form
function loadPaymentForm(BASE) {
  const container = document.getElementById("dashboardContent");
  container.innerHTML = `
    <h2>Transfer Funds</h2>
    <form id="paymentForm" class="payment-form">
      <div class="form-group">
        <label for="sourceAccount">Your Account</label>
        <select id="sourceAccount" required></select>
      </div>
      <div class="form-group">
        <label for="targetAccount">Beneficiary Account ID</label>
        <input type="text" id="targetAccount" required placeholder="Enter recipient account ID" />
      </div>
      <div class="form-group">
        <label for="amount">Amount</label>
        <input type="number" id="amount" required placeholder="Enter amount to transfer" />
      </div>
      <button type="submit">Transfer</button>
    </form>
  `;

 fetch(BASE + "/api/get-accounts/account", {
  method: 'POST',  // Specify the request method as POST
  headers: {
    'Content-Type': 'application/json',  // Tell the server you're sending JSON data
    // Add any necessary headers here, like Authorization if needed
  },
  body: JSON.stringify({ userId: "1234" })  // Send the necessary data in the request body (e.g., userId)
})
  .then(res => res.json())
  .then(data => {
    const select = document.getElementById("sourceAccount");
    // Check if accounts are returned in the data
    if (data && Array.isArray(data)) {
      data.forEach(account => {
        const opt = document.createElement("option");
        opt.value = account.accountId;
        opt.textContent = `${account.accountId} - ${account.accountType}`;
        select.appendChild(opt);
      });
    }
  })
  .catch(err => {
    console.error("Error fetching accounts:", err);
    // Handle any errors if the request fails
  });


  // Form submission
  document.getElementById("paymentForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const from = document.getElementById("sourceAccount").value;
    const to = document.getElementById("targetAccount").value;
    const amount = document.getElementById("amount").value;

    const payload = {
      fromAccount: from,
      toAccount: to,
      amount: parseFloat(amount)
    };

    fetch(BASE + "/api/transfer/transaction", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(data => {
        alert("Transaction Successful!");
        document.getElementById("paymentForm").reset();
      })
      .catch(err => {
        alert("Transaction Failed. Please try again.");
        console.error(err);
      });
  });
}
