function initAccountPage() {
  console.log("✅ Account page initialized");

document.getElementById("statusFilter")?.addEventListener("change", loadPendingRequests);

// Auto refresh every 30 sec
setInterval(loadPendingRequests, 30000);

initAccountList();
loadPendingRequests();

async function initAccountList() {
  const container = document.querySelector(".account-list");
  if (!container) {
    console.warn("account-list not found.");
    return;
  }

  try {
    const res = await fetch(`${BASE_URL}/api/get-accounts/account`, {
      method: "POST",
      credentials: "include"
    });

    if (!res.ok) throw new Error("Failed to fetch accounts");

    const accounts = await res.json();

    if (!Array.isArray(accounts) || accounts.length === 0) {
      container.innerHTML = `<p style="text-align:center;"> No accounts available.</p>`;
      return;
    }

    container.innerHTML = ""; // clear existing

    accounts.forEach(account => {
      container.appendChild(renderAccountCard(account));
    });

  } catch (err) {
    console.error(" Error fetching accounts:", err);
    container.innerHTML = `<p style="color:red;"> Unable to load account data.</p>`;
  }
}

function renderAccountCard(account) {
  const card = document.createElement("div");
  card.className = "account-card";

  const accNoDisplay = maskAccountId(account.accountId);
  const createdDate = formatDate(account.createdAt);
  const modifiedDate = formatDate(account.modifiedAt);

  card.innerHTML = `
    <div class="account-header">
      <h3><i class='bx bx-credit-card-alt'></i> Account #${accNoDisplay}</h3>
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

async function requestNewAccount() {
  const branchId = prompt("Enter the Branch ID for the new account:");

  if (!branchId || isNaN(branchId)) {
    alert(" Invalid Branch ID.");
    return;
  }

  try {
    const res = await fetch(`${BASE_URL}/api/request/account-request`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ branchId: Number(branchId) })
    });

    if (!res.ok) throw new Error("Failed to request account");

    alert(" Account request sent. Awaiting approval.");
    loadPendingRequests(); // refresh pending requests table

  } catch (err) {
    console.error(" Error requesting account:", err);
    alert(" Unable to send account request.");
  }
}

async function loadPendingRequests() {
  const body = document.getElementById("pendingRequestsBody");
  const filter = document.getElementById("statusFilter")?.value || "ALL";

  if (!body) return;

  try {
    const res = await fetch(`${BASE_URL}/api/pending/account-request`, {
      method: "GET",
      credentials: "include"
    });

    if (res.status === 403) {
      body.innerHTML = `<tr><td colspan="4" style="color:red;">Unauthorized (403). Please log in.</td></tr>`;
      return;
    }

    if (!res.ok) throw new Error("Failed to fetch pending requests");

    let requests = await res.json();
    if (!Array.isArray(requests)) return;

    if (filter !== "ALL") {
      requests = requests.filter(req => req.status === filter);
    }

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
    console.error(" Error loading pending requests:", err);
    body.innerHTML = `<tr><td colspan="4" style="color:red;">Failed to load.</td></tr>`;
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
}




window.initAccountPage = initAccountPage;
// 🔁 MOVED OUTSIDE
async function requestNewAccount() {
  const branchId = prompt("Enter the Branch ID for the new account:");

  if (!branchId || isNaN(branchId)) {
    alert(" Invalid Branch ID.");
    return;
  }

  try {
    const res = await fetch(`${BASE_URL}/api/request/account-request`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ branchId: Number(branchId) })
    });

    if (!res.ok) throw new Error("Failed to request account");

    alert(" Account request sent. Awaiting approval.");
    loadPendingRequests(); // make sure this is accessible globally too

  } catch (err) {
    console.error(" Error requesting account:", err);
    alert(" Unable to send account request.");
  }
}

window.requestNewAccount = requestNewAccount;
