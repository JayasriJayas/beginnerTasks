window.initSuperadminDashboard = function () {
  loadTopBar();
  loadMetrics(); 
  loadMonthlyTransactionPie();
  loadBranchBarCharts(); 

};

async function loadTopBar() {
  try {
    const res = await fetch(`${BASE_URL}/api/profile/user`, {
      method: "POST",
      credentials: "include"
    });

    const { user } = await res.json();
    const { name, roleId } = user;

    const roleMap = {
      1: "Super Admin",
      2: "Admin",
      3: "Customer"
    };


	document.getElementById("userName").textContent = name;



    document.getElementById("userRole").innerHTML = `
      <i class='bx bx-user'></i> ${roleMap[roleId] || "Unknown"}
    `;

    const now = new Date();

    const formattedDate = now.toLocaleDateString("en-IN", {
      weekday: "short",  
      day: "numeric",    
      month: "short",   
      year: "numeric"    
    });

    const formattedTime = now.toLocaleTimeString("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true       
    });

    document.getElementById("currentDateTime").innerHTML = `
      <i class='bx bx-time'></i> ${formattedDate} · ${formattedTime}
    `;

  } catch (err) {
    console.error("Failed to load user profile:", err);
    document.getElementById("welcomeText").innerText = "Welcome, User!";
    document.getElementById("userRole").innerText = "Unknown Role";
    document.getElementById("currentDateTime").innerText = "--";
  }
  document.addEventListener("click", (e) => {
    if (e.target.id === "addAdminBtn") openAddAdminModal();
    if (e.target.id === "addBranchBtn") openAddBranchModal();
  });

  function openAddAdminModal() {
	const formHTML = `
	  <form id="addAdminForm" class="modal-form">
	    <div class="form-group">
	      <input name="username" type="email" required />
	      <label>Username (email)</label>
	    </div>
	    <div class="form-group">
	      <input name="password" type="password" required />
	      <label>Password</label>
	    </div>
	    <div class="form-group">
	      <input name="name" type="text" required />
	      <label>Full Name</label>
	    </div>
	    <div class="form-group">
	      <input name="email" type="email" required />
	      <label>Email</label>
	    </div>
	    <div class="form-group">
	      <input name="phone" type="tel" required />
	      <label>Phone Number</label>
	    </div>
	    <div class="form-group">
	      <select name="gender" required>
	        <option value="">Select Gender</option>
	        <option value="MALE">Male</option>
	        <option value="FEMALE">Female</option>
	      </select>
	    </div>
	    <div class="form-group">
	      <input name="branchId" type="number" required />
	      <label>Branch ID</label>
	    </div>
	    <button type="submit" class="btn-submit">Add Admin</button>
	  </form>
	`;


    openModal("Add New Admin", formHTML);

    document.getElementById("addAdminForm").addEventListener("submit", async function (e) {
      e.preventDefault();

      const form = new FormData(e.target);
      const payload = Object.fromEntries(form.entries());
      payload.phone = Number(payload.phone);
      payload.branchId = Number(payload.branchId);

      try {
        const res = await fetch(`${BASE_URL}/api/add-admin/admin`, {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (!res.ok) throw new Error(result.message || "Failed to add admin");

        showToast("Admin added successfully", "success");
        closeModal();

      } catch (err) {
        showToast(err.message, "error");
      }
    });
  }

  function openAddBranchModal() {
    const formHTML = `
      <form id="addBranchForm" class="modal-form">
        <input name="branchName" type="text" placeholder="Branch Name" required />
        <input name="ifscCode" type="text" placeholder="IFSC Code" required />
        <input name="location" type="text" placeholder="Location" required />
        <input name="contact" type="tel" placeholder="Contact Number" required />
        <button type="submit" class="btn-submit">Add Branch</button>
      </form>
    `;

    openModal("Add New Branch", formHTML);

    document.getElementById("addBranchForm").addEventListener("submit", async function (e) {
      e.preventDefault();

      const form = new FormData(e.target);
      const payload = Object.fromEntries(form.entries());
      payload.contact = Number(payload.contact);

      try {
        const res = await fetch(`${BASE_URL}/api/add/branch`, {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (!res.ok) throw new Error(result.message || "Failed to add branch");

        showToast("Branch added successfully", "success");
        closeModal();

      } catch (err) {
        showToast(err.message, "error");
      }
    });
  }
}
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

      const [
        branchesRes, adminsRes, acctReqRes, userReqRes,
        usersRes, accountsRes
      ] = await Promise.all(
        Object.values(endpoints).map(path =>
          fetch(`${BASE_URL}${path}`, {
            method: "GET",
            credentials: "include"
          }).then(res => res.json())
        )
      );

      document.getElementById("branchCount").innerText = branchesRes.totalBranches ?? "--";
      document.getElementById("adminCount").innerText = adminsRes.totalAdmins ?? "--";
      document.getElementById("accountRequestCount").innerText = acctReqRes.PENDING ?? "--";
      document.getElementById("userRequestCount").innerText = userReqRes.PENDING ?? "--";
      document.getElementById("userCount").innerText = usersRes.totalUsers ?? "--";
      document.getElementById("accountCount").innerText = accountsRes.totalAccounts ?? "--";

      // Handle click navigation
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
      showToast("Failed to load dashboard metrics", "error");
    }

}
async function loadMonthlyTransactionPie() {
  try {
    const res = await fetch(`${BASE_URL}/api/total-type/transaction`, {
      method: "GET",
      credentials: "include"
    });

    const data = await res.json();

    const deposit = data.DEPOSIT ?? 0;
    const transfer = data.TRANSFER ?? 0;
    const withdrawal = data.WITHDRAWAL ?? 0;

    renderPieChart(deposit, transfer, withdrawal);

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
        borderColor: "#fff",
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: "bottom"
        },
        tooltip: {
          callbacks: {
            label: ctx => `${ctx.label}: ₹${ctx.parsed.toLocaleString("en-IN")}`
          }
        }
      }
    }
  });
}
async function loadBranchBarCharts() {
  try {
    const [fundsRes, txnRes] = await Promise.all([
      fetch(`${BASE_URL}/api/branch-funds/branch`, {
        method: "GET",
        credentials: "include"
      }).then(res => res.json()),
      fetch(`${BASE_URL}/api/transaction-count/transaction`, {
        method: "GET",
        credentials: "include"
      }).then(res => res.json())
    ]);

    // Sort and pick top 10 by funds
    const topFunds = [...fundsRes]
      .sort((a, b) => b.totalFunds - a.totalFunds)
      .slice(0, 10);

    const fundLabels = topFunds.map(b => b.branchName);
    const fundData = topFunds.map(b => b.totalFunds);

    renderBarChart("branchFundsChart", fundLabels, fundData, "Total Funds (₹)", "#8CBDB9");

    // Transaction count
    const txnLabels = txnRes.map(b => b.branchName);
    const txnData = txnRes.map(b => b.transactionCount);

    renderBarChart("branchTransactionChart", txnLabels, txnData, "Transaction Count", "#E09E50");

  } catch (err) {
    console.error("Failed to load branch charts:", err);
    showToast("Failed to load bar charts", "error");
  }
}

function renderBarChart(canvasId, labels, data, label, color) {
  const ctx = document.getElementById(canvasId).getContext("2d");

  new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [{
        label,
        data,
        backgroundColor: color
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (val) => canvasId === "branchFundsChart" ? `₹${val.toLocaleString("en-IN")}` : val
          }
        }
      },
      plugins: {
        legend: {
          display: false
        },
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



