window.initDashboardContent = async function initDashboardContent() {
  console.log("Dashboard content initialized");

  loadTopBar();
  loadAccountCards();
  loadInsights();
  loadRecentTransactions();

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
      document.getElementById("userRole").innerHTML = `<i class='bx bx-user'></i> ${roleMap[roleId] || "Unknown"}`;

      const now = new Date();
      const formattedDate = now.toLocaleDateString("en-IN", {
        weekday: "short", day: "numeric", month: "short", year: "numeric"
      });
      const formattedTime = now.toLocaleTimeString("en-IN", {
        hour: "2-digit", minute: "2-digit", hour12: true
      });

      document.getElementById("welcomeDate").innerHTML = `<i class='bx bx-time'></i> ${formattedDate} · ${formattedTime}`;
    } catch (err) {
      console.error("Failed to load user profile:", err);
      document.getElementById("welcomeText").innerText = "Welcome, User!";
      document.getElementById("userRole").innerText = "Unknown Role";
      document.getElementById("welcomeDate").innerText = "--";
    }
  }

  async function loadAccountCards() {
    const container = document.getElementById("accountCardContainer");
    if (!container) return;

    try {
      const res = await fetch(`${BASE_URL}/api/get-accounts/account`, {
        method: "POST",
        credentials: "include"
      });

      const accounts = await res.json();
      container.innerHTML = "";

      if (!accounts || accounts.length === 0) {
        container.innerHTML = `<p>No active accounts found.</p>`;
        return;
      }

      accounts.forEach(account => {
        const card = document.createElement("div");
        card.className = "account-card";

        card.innerHTML = `
          <div class="account-info">
            <span class="account-number">
              <i class='bx bx-id-card'></i> A/C No: ${account.accountId}
            </span>
            <span class="account-meta">
              <i class='bx bx-buildings'></i> Branch: ${account.branchId} · 
              <i class='bx bx-badge-check'></i> 
              <span class="status-badge">${account.status}</span>
            </span>
          </div>
          <button class="balance-btn" onclick="toggleBalance(${account.accountId}, this)">
            <i class='bx bx-show'></i> View Balance
          </button>
        `;
        container.appendChild(card);
      });
    } catch (err) {
      console.error("Error loading accounts:", err);
      showToast("Error loading accounts", "error");
    }
  }

  window.toggleBalance = async function toggleBalance(accountId, btn) {
    const isHidden = btn.innerHTML.includes("bx-show");

    if (!isHidden) {
      btn.innerHTML = `<i class='bx bx-show'></i> View Balance`;
      return;
    }

    try {
      btn.disabled = true;
      btn.innerHTML = `<i class='bx bx-loader bx-spin'></i> Loading`;

      const res = await fetch(`${BASE_URL}/api/balance/account`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ accountId: Number(accountId) })
      });

      const data = await res.json();
      const balance = data.balance ?? "N/A";

      btn.innerHTML = `<i class='bx bx-hide'></i> ₹ ${balance.toLocaleString("en-IN")}`;
      showToast("Balance fetched successfully!", "success");
    } catch (err) {
      console.error("Error fetching balance:", err);
      btn.innerHTML = `<i class='bx bx-error'></i> Error`;
      showToast("Error fetching balance", "error");
    } finally {
      btn.disabled = false;
    }
  };

  async function loadInsights() {
    const insightsContainer = document.getElementById("insightsContainer");
    const accountSelect = document.getElementById("accountSelect");

    if (!insightsContainer || !accountSelect) {
      console.error("Insights container or account select not found.");
      showToast("Insights container or account select not found.", "error");
      return;
    }

    try {
      const res = await fetch(`${BASE_URL}/api/get-accounts/account`, {
        method: "POST",
        credentials: "include"
      });
      const accounts = await res.json();

      accountSelect.innerHTML = `<option value="ALL">All Accounts</option>`;
      accounts.forEach(acc => {
        const option = document.createElement("option");
        option.value = acc.accountId;
        option.text = `A/C ${acc.accountId}`;
        accountSelect.appendChild(option);
      });
    } catch (err) {
      console.error("Failed to load accounts:", err);
      showToast("Failed to load accounts", "error");
    }

    fetchAndDisplayInsights("ALL");

    accountSelect.addEventListener("change", (e) => {
      const selectedAccountId = e.target.value;
      fetchAndDisplayInsights(selectedAccountId);
    });

    async function fetchAndDisplayInsights(accountId) {
      const isAll = accountId === "ALL";

      try {
		let balanceData = { totalBalance: 0 };

		try {
		  if (isAll) {
		    // All accounts → use total balance endpoint (no payload)
		    const res = await fetch(`${BASE_URL}/api/total-balance/account`, {
		      method: "POST",
		      credentials: "include"
		    });
		    balanceData = await res.json();
		  } else {
		    // Specific account → use individual account balance endpoint
		    const res = await fetch(`${BASE_URL}/api/balance/account`, {
		      method: "POST",
		      credentials: "include",
		      headers: { "Content-Type": "application/json" },
		      body: JSON.stringify({ accountId: Number(accountId) })
		    });
		    const singleAcc = await res.json();
		    balanceData.totalBalance = singleAcc.balance ?? 0;
		  }
		} catch (err) {
		  console.error("Error loading balance:", err);
		  showToast("Failed to fetch balance", "error");
		}


        // Credit & Debit: Only send accountId if selected
        const commonOptions = {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" }
        };
        const payload = isAll ? undefined : JSON.stringify({ accountId: Number(accountId) });

        const creditRes = await fetch(`${BASE_URL}/api/total-income/transaction`, {
          ...commonOptions,
          ...(payload && { body: payload })
        });
        const creditData = await creditRes.json();

        const debitRes = await fetch(`${BASE_URL}/api/total-expense/transaction`, {
          ...commonOptions,
          ...(payload && { body: payload })
        });
        const debitData = await debitRes.json();

        const totalCredits = creditData.totalIncome || 0;
        const totalDebits = debitData.totalExpense || 0;

        document.querySelector("#totalBalance p").textContent = `₹${(balanceData.totalBalance ?? 0).toLocaleString("en-IN")}`;
        document.querySelector("#thisMonthCredits p").textContent = `₹${totalCredits.toLocaleString("en-IN")}`;
        document.querySelector("#thisMonthDebits p").textContent = `₹${totalDebits.toLocaleString("en-IN")}`;

        renderChart(totalCredits, totalDebits);

      } catch (err) {
        console.error("Error fetching insights:", err);
        showToast("Failed to fetch insights", "error");
      }
    }

    let chartInstance = null;
    function renderChart(credits, debits) {
      const ctx = document.getElementById('insightChart').getContext('2d');
      if (chartInstance) chartInstance.destroy();

      chartInstance = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Credits', 'Debits'],
          datasets: [{
            data: [credits, debits],
            backgroundColor: ['#E09E50', '#2D3E4E'],
            borderColor: '#fff',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          plugins: {
            tooltip: {
              callbacks: {
                label: (ctx) => `${ctx.label}: ₹${ctx.parsed.toLocaleString("en-IN")}`
              }
            },
            legend: {
              position: "top"
            }
          }
        }
      });
    }
  }

  async function loadRecentTransactions() {
    const tbody = document.getElementById("recentTxnBody");
    if (!tbody) return;

    try {
      const res = await fetch(`${BASE_URL}/api/recent-transactions/transaction`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ limit: 10 })
      });

      const data = await res.json();
      tbody.innerHTML = "";

      if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5">No recent transactions.</td></tr>`;
        return;
      }

      data.forEach(txn => {
        const date = new Date(txn.timestamp).toLocaleString("en-IN", {
          day: "2-digit", month: "short", year: "numeric",
          hour: "2-digit", minute: "2-digit"
        });

        const typeClass = {
          TRANSFER: "badge-transfer",
          DEPOSIT: "badge-deposit",
          WITHDRAWAL: "badge-withdrawal"
        }[txn.type] || "badge-default";

        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${txn.accountId}</td>
          <td>${date}</td>
          <td><span class="type-badge ${typeClass}">${txn.type}</span></td>
          <td>₹${txn.amount.toLocaleString("en-IN")}</td>
          <td>
            ${txn.transactionAccountId ? `<i class='bx bx-right-arrow-alt'></i> ${txn.transactionAccountId}` : "-"}
          </td>
        `;
        tbody.appendChild(tr);
      });

    } catch (err) {
      console.error("Error loading recent transactions", err);
      tbody.innerHTML = `<tr><td colspan="5" style="color:red;">Failed to load</td></tr>`;

      showToast("Failed to load recent transactions", "error");
    }
  }
};
