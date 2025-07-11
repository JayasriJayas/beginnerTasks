	// Handle the toast display
	function showToast(message, type = "info") {
	  const toastContainer = document.getElementById("toast-container");
	  if (!toastContainer) return;
	
	  const icons = {
	    info: "bx bx-info-circle",
	    success: "bx bx-check-circle",
	    error: "bx bx-error-circle",
	    warning: "bx bx-error"
	  };
	
	  const iconClass = icons[type] || icons.info;
	
	  const toast = document.createElement("div");
	  toast.className = `toast show ${type}`;
	  toast.innerHTML = `
	    <i class="toast-icon ${iconClass}"></i>
	    <span class="toast-msg">${message}</span>
	    <span class="toast-close" onclick="this.parentElement.remove()">&times;</span>
	    <div class="toast-timer"></div>
	  `;
	
	  toastContainer.appendChild(toast);
	
	  setTimeout(() => {
	    toast.classList.remove("show");
	    setTimeout(() => toast.remove(), 300);
	  }, 3500);
	}
	
	
	async function initAccountPage() {
	  document.getElementById("statusFilter")?.addEventListener("change", loadPendingRequests);
	
	  setInterval(loadPendingRequests, 30000);
	
	  initAccountList();
	  loadPendingRequests();
	}
	
	async function initAccountList() {
	
	const container = document.getElementById("accountSlider");
	
	  if (!container) {
	    console.warn("account-list not found.");
	    return;
	  }
	
	  try {
	    const res = await fetch(`${BASE_URL}/api/get-accounts/account`, {
	      method: "POST",
	      credentials: "include"
	    });
	
	    if (!res.ok) {
	      const errorData = await res.json();
	      throw new Error(errorData.message || "Failed to fetch accounts");
	    }
	
	    const accounts = await res.json();
	
	    if (!Array.isArray(accounts) || accounts.length === 0) {
	      container.innerHTML = `<p style="text-align:center;"> No accounts available.</p>`;
	      return;
	    }
	
	    container.innerHTML = "";
	    accounts.forEach(account => {
	      container.appendChild(renderAccountCard(account));
	    });
	  } catch (err) {
	    console.error("Error fetching accounts:", err);
	    container.innerHTML = `<p style="color:red;">Unable to load account data.</p>`;
	    showToast(err.message || "Unable to load account data.", "error");
	  }
	}
	
	// Render account card
	function renderAccountCard(account) {
	  const card = document.createElement("div");
	  card.className = "account-card";
	
	  const accNoDisplay = maskAccountId(account.accountId);
	  const createdDate = formatDate(account.createdAt);
	  const modifiedDate = formatDate(account.modifiedAt);
	
	  card.innerHTML = `
	    <div class="account-header">
	      <h3><i class='bx bx-credit-card-alt'></i> Account  ${accNoDisplay}</h3>
	      <span class="badge ${account.status === "ACTIVE" ? "active" : ""}">
	        <i class='bx ${account.status === "ACTIVE" ? "bx-check-shield" : "bx-error-circle"}'></i> ${account.status}
	      </span>
	    </div>
	
	    <div class="account-details">
	      ${renderField("Account ID", account.accountId)}
	      ${renderField("Branch ID", account.branchId)}
	      ${renderField("Current Balance", formatCurrency(account.balance))}
	      ${renderField("Created At", createdDate)}
	      ${renderField("Last Modified", modifiedDate)}
	      ${renderField("Modified By (Admin ID)", account.modifiedBy)}
	    </div>
	  `;
	
	  return card;
	}
	
	function renderField(label, value) {
	  const icons = {
	    "Account ID": "bx-hash",
	    "Branch ID": "bx-building-house",
	    "Current Balance": "bx-wallet",
	    "Created At": "bx-calendar",
	    "Last Modified": "bx-time",
	    "Modified By (Admin ID)": "bx-user-check"
	  };
	
	  const icon = icons[label] ? `<i class='bx ${icons[label]}'></i>` : "";
	  return `
	    <div class="field">
	      <label>${icon} ${label}</label>
	      <p>${value ?? "--"}</p>
	    </div>
	  `;
	}
	
	function maskAccountId(id) {
	  return "XXXX-" + String(id).padStart(4, "0");
	}
	
	function formatCurrency(amount) {
	  return typeof amount === "number" ? `₹${amount.toLocaleString("en-IN")}` : "--";
	}
	
	function formatDate(timestamp) {
	  if (!timestamp) return "--";
	  const date = new Date(Number(timestamp));
	  return date.toLocaleDateString("en-IN", {
	    day: "2-digit",
	    month: "short",
	    year: "numeric"
	  });
	}
	
	// Update requestNewAccount to use dropdown modal
	async function requestNewAccount() {
	  try {
	    const res = await fetch(`${BASE_URL}/api/all-branch/branch`, {
	      method: "GET",
	      credentials: "include"
	    });
	
	    if (!res.ok) throw new Error("Failed to fetch branches");
	
	    const data = await res.json();
	    const branches = data.branches || [];
	
	    const modal = document.createElement("div");
	    modal.className = "modal-overlay";
	    modal.innerHTML = `
	      <div class="modal-box">
	        <h3>Select Branch</h3>
	        <select id="branchSelect">
	          <option value="">-- Choose a branch --</option>
	          ${branches.map(b => `<option value="${b.branchId}">${b.branchId} - ${b.branchName}</option>`).join("")}
	        </select>
	        <div class="modal-buttons">
	          <button type="button" onclick="confirmAccountRequest()">Request</button>
	          <button type="button" onclick="this.closest('.modal-overlay').remove()">Cancel</button>
	        </div>
	      </div>
	    `;
	
	    document.body.appendChild(modal);
	
	    window.confirmAccountRequest = async function () {
	      const selectedBranchId = document.getElementById("branchSelect").value;
	      if (!selectedBranchId) {
	        showToast("Please select a branch.", "warning");
	        return;
	      }
	
	      try {
	        const res = await fetch(`${BASE_URL}/api/request/account-request`, {
	          method: "POST",
	          credentials: "include",
	          headers: { "Content-Type": "application/json" },
	          body: JSON.stringify({ branchId: Number(selectedBranchId) })
	        });
	
	        const result = await res.json();
	        if (!res.ok) throw new Error(result.message || "Request failed");
	
	        showToast("Account request sent successfully.", "success");
	        
			console.log("Removing modal overlay...");
			setTimeout(() => {
			  document.querySelectorAll(".modal-overlay").forEach(el => el.remove());
			}, 100);
	
	        loadPendingRequests();
	      } catch (err) {
	        console.error("Error submitting request:", err);
	        showToast(err.message || "Request failed.", "error");
	      }
	    };
	
	  } catch (err) {
	    console.error("Error loading branches:", err);
	    showToast("Unable to load branches", "error");
	  }
	}
	
	async function loadPendingRequests() {
	  const body = document.getElementById("pendingRequestsBody");
	  const filter = document.getElementById("statusFilter")?.value;
	  if (!body) return;
	
	  try {
	    const res = await fetch(`${BASE_URL}/api/pending/account-request`, {
	      method: "POST",
	      credentials: "include",
	      headers: { "Content-Type": "application/json" },
	      body: JSON.stringify({ status: filter })
	    });
	
	    if (res.status === 403) {
	      body.innerHTML = `<tr><td colspan="4" style="color:red;">Unauthorized (403). Please log in.</td></tr>`;
	      return;
	    }
	
	    if (!res.ok) {
	      const errorData = await res.json();
	      throw new Error(errorData.message || "Failed to fetch pending requests");
	    }
	
	    let requests = await res.json();
	    if (!Array.isArray(requests)) return;
	
	    requests.sort((a, b) => b.createdAt - a.createdAt);
	    body.innerHTML = "";
	
	    if (requests.length === 0) {
	      body.innerHTML = `<tr><td colspan="4" style="text-align:center; opacity: 0.75;">No pending requests.</td></tr>`;
	      return;
	    }
	
	    requests.forEach(req => {
	      const statusIcon = {
	        PENDING: "bx-time",
	        APPROVED: "bx-check-circle",
	        REJECTED: "bx-x-circle"
	      }[req.status] || "bx-info-circle";
	
	      const row = document.createElement("tr");
	      row.innerHTML = `
	        <td>${req.branchId}</td>
	        <td>${formatDateTime(req.createdAt)}</td>
	        <td>${req.requestId}</td>
	        <td>
	          <span class="status-badge ${req.status.toLowerCase()}">
	            <i class='bx ${statusIcon}'></i> ${req.status}
	          </span>
	        </td>
	      `;
	      body.appendChild(row);
	    });
	  } catch (err) {
	    console.error("Error loading pending requests:", err);
	    body.innerHTML = `<tr><td colspan="4" style="color:red;">No pending requests.</td></tr>`;
	    
	  }
	}
	
	function formatDateTime(timestamp) {
	  if (!timestamp) return "--";
	  const date = new Date(Number(timestamp));
	  return date.toLocaleString("en-IN", {
	    dateStyle: "medium",
	    timeStyle: "short"
	  });
	}
	function scrollAccounts(direction) {
	  const container = document.querySelector(".account-scroll-container");
	  const scrollAmount = 350; // pixels
	
	  if (direction === "left") {
	    container.scrollBy({ left: -scrollAmount, behavior: "smooth" });
	  } else {
	    container.scrollBy({ left: scrollAmount, behavior: "smooth" });
	  }
	}
	function updateScrollButtons() {
	  const container = document.querySelector(".account-scroll-container");
	  const leftBtn = document.querySelector(".scroll-btn.left");
	  const rightBtn = document.querySelector(".scroll-btn.right");
	
	  leftBtn.style.display = container.scrollLeft > 0 ? "inline-block" : "none";
	  rightBtn.style.display = container.scrollLeft + container.clientWidth < container.scrollWidth ? "inline-block" : "none";
	}
	
	document.querySelector(".account-scroll-container")?.addEventListener("scroll", updateScrollButtons);
	window.addEventListener("resize", updateScrollButtons);
	
	
	
	window.initAccountPage = initAccountPage;
	window.requestNewAccount = requestNewAccount;
