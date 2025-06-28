window.initDashboardContent = async function initDashboardContent() {
  console.log(" Dashboard content initialized");

  const userNameEl = document.getElementById("userName");
  if (userNameEl) userNameEl.innerText = "Jayasri"; // or fetch dynamically

  const today = new Date();
  const dateEl = document.getElementById("welcomeDate");
  if (dateEl) {
    dateEl.innerText = today.toLocaleDateString("en-IN", {
      weekday: "short",
      day: "numeric",
      month: "short",
      year: "numeric"
    });
  }

  // Load account cards
  loadAccountCards();
  loadInsights();

    async function loadInsights() {
      const insightsContainer = document.getElementById("insightsContainer");
      const accountSelect = document.getElementById("accountSelect");

      if (!insightsContainer || !accountSelect) {
        console.error("Insights container or account select not found.");
        showToast("Insights container or account select not found.", "error");
        return;
      }

      // Load user accounts for dropdown
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

      // Fetch and display insights for ALL accounts initially
      fetchAndDisplayInsights("ALL");

      // When an account is selected, update the insights
      accountSelect.addEventListener("change", (e) => {
        const selectedAccountId = e.target.value;
        fetchAndDisplayInsights(selectedAccountId);
      });

      // Fetch the insights data and update the UI
      async function fetchAndDisplayInsights(accountId) {
        try {
          const isAll = accountId === "ALL";

          const balanceRes = await fetch(`${BASE_URL}/api/total-balance/account`, {
            method: "POST",
            credentials: "include"
          });
          const balanceData = await balanceRes.json();

          const creditRes = await fetch(`${BASE_URL}/api/total-income/transaction`, {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: isAll ? null : JSON.stringify({ accountId: parseInt(accountId) })
          });
          const creditData = await creditRes.json();

          const debitRes = await fetch(`${BASE_URL}/api/total-expense/transaction`, {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: isAll ? null : JSON.stringify({ accountId: parseInt(accountId) })
          });
          const debitData = await debitRes.json();

          const totalCredits = creditData.totalIncome || 0;
          const totalDebits = debitData.totalExpense || 0;

          // Update the DOM with fetched data
          document.getElementById("totalBalance").innerHTML =
            `<p><strong>Total Balance:</strong> ₹${balanceData.totalBalance ?? "N/A"}</p>`;
          document.getElementById("thisMonthCredits").innerHTML =
            `<p><strong>This Month's Credits:</strong> ₹${totalCredits}</p>`;
          document.getElementById("thisMonthDebits").innerHTML =
            `<p><strong>This Month's Debits:</strong> ₹${totalDebits}</p>`;

          // Render the pie chart with the updated data
          renderChart(totalCredits, totalDebits);
        } catch (err) {
          console.error("Error fetching insights:", err);
          showToast("Failed to fetch insights", "error");
        }
      }

      // Function to render the pie chart
      let chartInstance = null;
	  function renderChart(credits, debits) {
	    const ctx = document.getElementById('insightChart').getContext('2d');

	    if (chartInstance) chartInstance.destroy(); // Destroy previous chart if it exists

	    chartInstance = new Chart(ctx, {
	      type: 'pie',
	      data: {
	        labels: ['Credits', 'Debits'],
	        datasets: [{
	          data: [credits, debits],
	          backgroundColor: ['#d4edda', '#fff3cd'],
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
 


  async function loadAccountCards() {
    const container = document.getElementById("accountCardContainer");
    if (!container) return;

    try {
      const res = await fetch(`${BASE_URL}/api/get-accounts/account`, {
        method: "POST",
        credentials: "include"
      });

      if (!res.ok) {
        const errData = await res.json();
        showToast(errData.message || "Failed to fetch accounts", "error");
        throw new Error("Failed to fetch accounts");
      }

      const accounts = await res.json();
      container.innerHTML = "";

      if (accounts.length === 0) {
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
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ accountId })
      });
	
      if (!res.ok) {
        const errData = await res.json();
        showToast(errData.message || "Failed to fetch balance", "error");
        throw new Error("Failed to fetch balance");
      }

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
  }

  async function loadRecentTransactions() {
    const tbody = document.getElementById("recentTxnBody");
    if (!tbody) return;

    try {
      const res = await fetch(`${BASE_URL}/api/recent-transactions/transaction`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ limit: 10 })
      });

      if (!res.ok) {
        const errData = await res.json();
        showToast(errData.message || "Failed to load transactions", "error");
        throw new Error("Failed to load transactions");
      }

      const data = await res.json();
      tbody.innerHTML = "";

      if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5">No recent transactions.</td></tr>`;
        return;
      }

      data.forEach(txn => {
        const date = new Date(txn.timestamp).toLocaleString("en-IN", {
          day: "2-digit",
          month: "short",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit"
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

  // Call this after defining
  loadRecentTransactions();
}

window.initDashboardContent = initDashboardContent;


