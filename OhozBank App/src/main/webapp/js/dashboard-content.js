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
      
      if (!insightsContainer) {
        console.error("insightsContainer not found.");
        showToast("Insights container not found.", "error");
        return;
      }

      try {
        const totalBalanceRes = await fetch(`${BASE_URL}/api/total-balance/account`, {
          method: "POST",
          credentials: "include"
        });
        const totalBalanceData = await totalBalanceRes.json();

        const totalCreditsRes = await fetch(`${BASE_URL}/api/total-income/transaction`, {
          method: "POST",
          credentials: "include"
        });
        const totalCreditsData = await totalCreditsRes.json();

        const totalDebitsRes = await fetch(`${BASE_URL}/api/total-expense/transaction`, {
          method: "POST",
          credentials: "include"
        });
        const totalDebitsData = await totalDebitsRes.json();
		console.log(totalCreditsData); // Check what this returns
		console.log(totalDebitsData);  // Check what this returns
        if (!totalBalanceRes.ok || !totalCreditsRes.ok || !totalDebitsRes.ok) {
          throw new Error("Failed to fetch insights");
        }

		insightsContainer.innerHTML = `
		     <p>Total Balance: ₹${totalBalanceData.totalBalance || "N/A"}</p>
		     <p>This Month's Credits: ₹${totalCreditsData.totalIncome || "N/A"}</p>
		     <p>This Month's Debits: ₹${totalDebitsData.totalExpense || "N/A"}</p>
		     <canvas id="insightChart"></canvas>
		   `;

        // Call the chart rendering function
        renderChart(totalCreditsData.totalIncome, totalDebitsData.totalExpense);
        
      } catch (err) {
        console.error("Error fetching insights:", err);
        showToast("Error fetching insights", "error");
      }
    

    // Render the pie chart for insights
    function renderChart(credits, debits) {
      const ctx = document.getElementById('insightChart').getContext('2d');
      const chart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: ['Credits', 'Debits'],
          datasets: [{
            data: [credits, debits],
            backgroundColor: ['#d4edda', '#fff3cd']
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
            },
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


