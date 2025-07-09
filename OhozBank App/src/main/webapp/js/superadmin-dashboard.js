
	
	window.initSuperadminDashboard = function () {
	  loadTopBar();
	  loadMetrics();
	  loadMonthlyTransactionPie();
	  loadBranchBarCharts();
	
	  document.getElementById("topBar")?.addEventListener("click", (e) => {
	    if (e.target.id === "addAdminBtn") openAddAdminModal();
	    if (e.target.id === "addBranchBtn") openAddBranchModal();
	  });
	};
	
	// ------------------- TopBar -------------------
	async function loadTopBar() {
	  try {
	    const res = await fetch(`${BASE_URL}/api/profile/user`, {
	      method: "POST",
	      credentials: "include"
	    });
	
	    const { user } = await res.json();
	    const { name, roleId } = user;
	
	    const roleMap = { 1: "Super Admin", 2: "Admin", 3: "Customer" };
	    document.getElementById("userName").textContent = name;
	    document.getElementById("userRole").innerHTML = `<i class='bx bx-user'></i> ${roleMap[roleId] || "Unknown"}`;
	
	    const now = new Date();
	    const formattedDate = now.toLocaleDateString("en-IN", {
	      weekday: "short", day: "numeric", month: "short", year: "numeric"
	    });
	    const formattedTime = now.toLocaleTimeString("en-IN", {
	      hour: "2-digit", minute: "2-digit", hour12: true
	    });
	
	    document.getElementById("currentDateTime").innerHTML = `<i class='bx bx-time'></i> ${formattedDate} · ${formattedTime}`;
	  } catch (err) {
	    console.error("Failed to load user profile:", err);
	    document.getElementById("welcomeText").innerText = "Welcome, User!";
	    document.getElementById("userRole").innerText = "Unknown Role";
	    document.getElementById("currentDateTime").innerText = "--";
	  }
	}
	
	// ------------------- Metrics -------------------
	async function loadMetrics() {
	  try {
	    const endpoints = {
	      branchCount: "/api/total-branches/branch",
	      adminCount: "/api/total-admins/admin",
	      accountRequestCount: "/api/status-counts/account-request",
	      userRequestCount: "/api/status-counts/request",
	      userCount: "/api/total-users/user",
	      accountCount: "/api/total-accounts/account"
	    };
	
	    const responses = await Promise.all(Object.values(endpoints).map(path =>
	      fetch(`${BASE_URL}${path}`, {
	        method: "GET",
	        credentials: "include"
	      }).then(res => res.json())
	    ));
	
	    const [branches, admins, acctReq, userReq, users, accounts] = responses;
	
	    document.getElementById("branchCount").innerText = branches.totalBranches ?? "--";
	    document.getElementById("adminCount").innerText = admins.totalAdmins ?? "--";
	    document.getElementById("accountRequestCount").innerText = acctReq.PENDING ?? "--";
	    document.getElementById("userRequestCount").innerText = userReq.PENDING ?? "--";
	    document.getElementById("userCount").innerText = users.totalUsers ?? "--";
	    document.getElementById("accountCount").innerText = accounts.totalAccounts ?? "--";
	
	    document.querySelectorAll(".metric-card.clickable").forEach(card => {
	      card.addEventListener("click", () => {
	        const action = card.getAttribute("data-action");
	        if (action) {
	          document.querySelector(`.menu-item[data-action="${action}"]`)?.click();
	        }
	      });
	    });
	  } catch (err) {
	    console.error("Failed to load metrics:", err);
		window.location.href="./login.jsp";
	    showToast("Failed to load dashboard metrics", "error");
	  }
	}
	
	// ------------------- Pie Chart -------------------
	async function loadMonthlyTransactionPie() {
	  try {
	    const res = await fetch(`${BASE_URL}/api/total-type/transaction`, {
	      method: "GET", credentials: "include"
	    });
	
	    const data = await res.json();
	    renderPieChart(data.DEPOSIT ?? 0, data.TRANSFER ?? 0, data.WITHDRAWAL ?? 0);
	  } catch (err) {
	    console.error("Failed to load pie chart data:", err);
	    showToast("Failed to load transaction type pie chart", "error");
	  }
	}
	
	let pieInstance;
	function renderPieChart(deposit, transfer, withdrawal) {
	  const ctx = document.getElementById("superadminPieChart").getContext("2d");
	  if (pieInstance) pieInstance.destroy();
	
	  pieInstance = new Chart(ctx, {
	    type: "pie",
	    data: {
	      labels: ["Deposit", "Transfer", "Withdrawal"],
	      datasets: [{
	        data: [deposit, transfer, withdrawal],
	        backgroundColor: ["#8CBDB9", "#E09E50", "#2D3E4E"],
	        borderColor: "#fff", borderWidth: 2
	      }]
	    },
	    options: {
	      responsive: true,
	      plugins: {
	        legend: { position: "bottom" },
	        tooltip: {
	          callbacks: {
	            label: ctx => `${ctx.label}: ₹${ctx.parsed.toLocaleString("en-IN")}`
	          }
	        }
	      }
	    }
	  });
	  document.getElementById("depositAmt").textContent = deposit.toLocaleString("en-IN");
	    document.getElementById("transferAmt").textContent = transfer.toLocaleString("en-IN");
	    document.getElementById("withdrawAmt").textContent = withdrawal.toLocaleString("en-IN");
	}
	
	
	// ------------------- Bar Charts -------------------
	async function loadBranchBarCharts() {
	  try {
	    const [fundsRes, txnRes] = await Promise.all([
	      fetch(`${BASE_URL}/api/branch-funds/branch`, { method: "GET", credentials: "include" }).then(res => res.json()),
	      fetch(`${BASE_URL}/api/transaction-count/transaction`, { method: "GET", credentials: "include" }).then(res => res.json())
	    ]);
	
	    const topFunds = [...fundsRes].sort((a, b) => b.totalFunds - a.totalFunds).slice(0, 10);
	    renderBarChart("branchFundsChart", topFunds.map(b => b.branchName), topFunds.map(b => b.totalFunds), "Total Funds (₹)", "#8CBDB9");
	    renderBarChart("branchTransactionChart", txnRes.map(b => b.branchName), txnRes.map(b => b.transactionCount), "Transaction Count", "#E09E50");
	
	  } catch (err) {
	    console.error("Failed to load bar charts:", err);
	    showToast("Failed to load branch charts", "error");
	  }
	}
	
	function renderBarChart(canvasId, labels, data, label, color) {
	  const ctx = document.getElementById(canvasId).getContext("2d");
	  new Chart(ctx, {
	    type: "bar",
	    data: {
	      labels,
	      datasets: [{ label, data, backgroundColor: color }]
	    },
	    options: {
	      responsive: true,
	      scales: {
	        y: {
	          beginAtZero: true,
	          ticks: {
	            callback: val => canvasId === "branchFundsChart" ? `₹${val.toLocaleString("en-IN")}` : val
	          }
	        }
	      },
	      plugins: {
	        legend: { display: false },
	        tooltip: {
	          callbacks: {
	            label: ctx =>
	              canvasId === "branchFundsChart"
	                ? `₹${ctx.parsed.y.toLocaleString("en-IN")}`
	                : `${ctx.parsed.y} transactions`
	          }
	        }
	      }
	    }
	  });
	}
	
	// ------------------- Admin Modal -------------------
	// In superadmin-branch-list.js
	function openDashboardAdminModal() {
	  const modal = document.getElementById("dashboardAdminModal");
	  modal.classList.remove("hidden");  // This removes the hidden class and displays the modal
	  fetchBranches();
	}



	function closeDashboardAdminModal() {
	  const modal = document.getElementById("dashboardAdminModal"); // Corrected
	  modal.classList.add("hidden");
	}

	document.getElementById("addAdminBtn").addEventListener("click", openDashboardAdminModal);
	document.querySelector(".close-icon").addEventListener("click", closeDashboardAdminModal);

	document.getElementById("addAdminForm").addEventListener("submit", function (e) {
	  e.preventDefault();

	  const username = document.getElementById("username").value;
	  const password = document.getElementById("password").value;
	  const name = document.getElementById("name").value;
	  const email = document.getElementById("email").value;
	  const phone = document.getElementById("phone").value;
	  const gender = document.getElementById("gender").value;
	  const branchId = document.getElementById("branchId").value;

	  if (!validateUsername(username)) {
	    showToast("Username is invalid. It should be an email address.","error");
	    return;
	  }

	  if (!validatePassword(password)) {
	    showToast("Password must be at least 8 characters long and include a mix of letters, numbers, and special characters.","error");
	    return;
	  }

	  if (!validateEmail(email)) {
	    showToast("Please enter a valid email address.","error");
	    return;
	  }

	  if (!validatePhone(phone)) {
	    showToast("Phone number must be a valid 10-digit number.","error");
	    return;
	  }

	  const adminData = {
	    username,
	    password,
	    name,
	    email,
	    phone,
	    gender,
	    branchId
	  };

	  fetch(`${BASE_URL}/api/add-admin/admin`, {
	    method: "POST",
	    headers: {
	      "Content-Type": "application/json"
	    },
	    credentials: "include",
	    body: JSON.stringify(adminData)
	  })
	    .then((res) => res.json())
	    .then((data) => {
	      if (data.status === "SUCCESS") {
	        alert("Admin added successfully");
	        closeAddAdminModal();
	        loadAdmins();
	      } else {
	        alert("Failed to add admin");
	      }
	    })
	    .catch((err) => {
	      console.error("Error adding admin:", err);
	      alert("An error occurred while adding the admin");
	    });
	});


	function validateUsername(username) {
	  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	  return re.test(username);
	}

	function validateEmail(email) {
	  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	  return re.test(email);
	}
	function validatePassword(password) {
	  const re = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
	  return re.test(password);
	}

	function validatePhone(phone) {
	  const re = /^[0-9]{10}$/;
	  return re.test(phone);
	}

	async function fetchBranches() {
	  try {
	    const res = await fetch(`${BASE_URL}/api/without-admin/branch`, {
	      method: "GET",
	      credentials: "include",
	      headers: {
	        "Content-Type": "application/json"
	      }
	    });

	    const data = await res.json();
	    if (!res.ok) throw new Error(data.message || "Failed to fetch branches");

	    const branchSelect = document.getElementById("branchId");

	    branchSelect.innerHTML = '';

	    const defaultOption = document.createElement("option");
	    defaultOption.value = "";
	    defaultOption.textContent = "Select Branch";
	    branchSelect.appendChild(defaultOption);


	    Object.entries(data.branches).forEach(([branchId, branchName]) => {
	      const option = document.createElement("option");
	      option.value = branchId; 
	      option.textContent = `${branchName} (${branchId})`; 
	      branchSelect.appendChild(option);
	    });

	  } catch (err) {
	    console.error("Error fetching branches:", err);
	    showToast("Failed to load branches.", "error");
	  }
	}

	fetchBranches();

//	function openAddAdminModal() {
//	  const formHTML = `	<div class="modal-header">
//		   <h2>Add New Admin</h2>
//		   <span class="modal-close" onclick="closeModal()">&times;</span>
//		 </div>
//		 <form id="addAdminForm" class="modal-form">
//		   <div class="form-group">
//		     <input name="username" type="email" required />
//		     <label>Username (email)</label>
//		   </div>
//		   <div class="form-group">
//		     <input name="password" type="password" required />
//		     <label>Password</label>
//		   </div>
//		   <div class="form-group">
//		     <input name="name" type="text" required />
//		     <label>Full Name</label>
//		   </div>
//		   <div class="form-group">
//		     <input name="email" type="email" required />
//		     <label>Email</label>
//		   </div>
//		   <div class="form-group">
//		     <input name="phone" type="tel" required pattern="^[0-9]{10}$" />
//		     <label>Phone Number</label>
//		   </div>
//		   <div class="form-group">
//		     <select name="gender" required>
//		       <option value="">Select Gender</option>
//		       <option value="MALE">Male</option>
//		       <option value="FEMALE">Female</option>
//		     </select>
//		   </div>
//		   <div class="form-group">
//		     <select name="branchId" id="branchId" required></select>
//		     <label>Branch</label>
//		   </div>
//		   <button type="submit" class="btn-submit">Add Admin</button>
//		 </form>`;
//	  openModal("Add Admin", formHTML);
//	
//	  requestAnimationFrame(() => {
//	    const form = document.getElementById("addAdminForm");
//	    if (!form) return console.error("addAdminForm not found");
//	
//	    fetchBranches();
//	
//	    form.addEventListener("submit", async function (e) {
//	      e.preventDefault();
//	      clearToasts();
//	
//	      const payload = Object.fromEntries(new FormData(form).entries());
//	      let isValid = true;
//	
//	      if (!validateEmail(payload.username)) { showToast("Invalid Username", "error"); isValid = false; }
//	      if (payload.password.length < 6) { showToast("Password too short", "error"); isValid = false; }
//	      if (payload.name.trim() === "") { showToast("Name required", "error"); isValid = false; }
//	      if (!validateEmail(payload.email)) { showToast("Invalid Email", "error"); isValid = false; }
//	      if (!payload.phone.match(/^[0-9]{10}$/)) { showToast("Invalid Phone", "error"); isValid = false; }
//	      if (!payload.gender) { showToast("Select gender", "error"); isValid = false; }
//	
//	      if (!isValid) return;
//	
//	      try {
//	        const res = await fetch(`${BASE_URL}/api/add-admin/admin`, {
//	          method: "POST",
//	          credentials: "include",
//	          headers: { "Content-Type": "application/json" },
//	          body: JSON.stringify(payload)
//	        });
//	
//	        const result = await res.json();
//	        if (!res.ok) throw new Error(result.message);
//	        showToast("Admin added successfully", "success");
//	        closeModal();
//	      } catch (err) {
//	        showToast(err.message, "error");
//	      }
//	    });
//	  });
//	}
//	

	// ------------------- Branch Modal -------------------
//	function openAddBranchModal() {
//	  const formHTML = `	 <div class="modal-header">
//		      <h2>Add New Branch</h2>
//		      <span class="modal-close" onclick="closeModal()">&times;</span>
//		    </div>
//		    <form id="addBranchForm" class="modal-form">
//		      <div class="form-group">
//		        <input name="branchName" type="text" placeholder="Branch Name" required />
//		      </div>
//		      <div class="form-group">
//		        <input name="ifscCode" type="text" placeholder="IFSC Code" required 
//		               pattern="^[A-Z]{4}0[A-Z0-9]{6}$"
//		               title="IFSC Code must be in the format: XXXX0YYYYYY (4 letters, 0, 6 digits)" />
//		      </div>
//		      <div class="form-group">
//		        <input name="location" type="text" placeholder="Location" required />
//		      </div>
//		      <div class="form-group">
//		        <input name="contact" type="number" placeholder="Contact Number" required />
//		      </div>
//		      <button type="submit" class="btn-submit">Add Branch</button>
//		    </form>
//	 `;
//	  openModal("Add Branch", formHTML);
//	
//	  requestAnimationFrame(() => {
//	    const form = document.getElementById("addBranchForm");
//	    if (!form) return console.error("addBranchForm not found");
//	
//	    form.addEventListener("submit", async function (e) {
//	      e.preventDefault();
//	      const payload = Object.fromEntries(new FormData(form).entries());
//	      payload.contact = Number(payload.contact);
//	
//	      try {
//	        const res = await fetch(`${BASE_URL}/api/add/branch`, {
//	          method: "POST",
//	          credentials: "include",
//	          headers: { "Content-Type": "application/json" },
//	          body: JSON.stringify(payload)
//	        });
//	
//	        const result = await res.json();
//	        if (!res.ok) throw new Error(result.message);
//	        showToast("Branch added successfully", "success");
//	        closeModal();
//	      } catch (err) {
//	        showToast(err.message, "error");
//	      }
//	    });
//	  });
//	}
function openDashboardBranchModal() {
  const modal = document.getElementById("dashboardBranchModal");
  if (modal) {
    modal.classList.remove("hidden"); 
  } else {
    console.error("Modal element not found!");
  }
}



function closeDashboardBranchModal() {
  const modal = document.getElementById("dashboardBranchModal");  // Corrected ID
  modal.classList.add("hidden");
}


document.getElementById("addBranchForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const branchName = document.getElementById("branchName").value;
  const ifscCode = document.getElementById("ifscCode").value;
  const location = document.getElementById("location").value;
  const contact = document.getElementById("contact").value;

  const branchData = {
    branchName,
    ifscCode,
    location,
    contact,
  };

  
  fetch(`${BASE_URL}/api/add/branch`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(branchData),
  })
   .then((response) => response.json())
      .then((data) => {
        
        if (data.status === "SUCCESS") {
          alert("Branch added successfully!");
          closeAddBranchModal();
          loadBranches();
        } else {
          alert("Failed to add branch! " + data.message);
        }
      })
      .catch((error) => {
        console.error("Error adding branch:", error);
        alert("An error occurred while adding the branch.");
      });
  });

	
	// ------------------- Helpers -------------------
	function validateEmail(email) {
	  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	  return re.test(email);
	}
	
	function clearToasts() {
	  document.querySelectorAll(".toast").forEach((toast) => toast.remove());
	}
	
	async function fetchBranches() {
	  try {
	    const res = await fetch(`${BASE_URL}/api/without-admin/branch`, {
	      method: "GET",
	      credentials: "include",
	      headers: { "Content-Type": "application/json" }
	    });
	
	    const data = await res.json();
	    const branchSelect = document.getElementById("branchId");
	    if (!branchSelect) return;
	
	    branchSelect.innerHTML = '<option value="">Select Branch</option>';
	    Object.entries(data.branches).forEach(([branchId, branchName]) => {
	      const option = document.createElement("option");
	      option.value = branchId;
	      option.textContent = `${branchId} - ${branchName}`;
	      branchSelect.appendChild(option);
	    });
	  } catch (err) {
	    showToast(err.message, "error");
	  }
	}
