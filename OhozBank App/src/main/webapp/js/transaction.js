function initTransactionPage() {
  console.log("✅ Transaction page initialized");

  // DOM References
  const pageNumbersContainer = document.getElementById("pageNumbers");
  const accountSelect = document.getElementById("accountFilter");
  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");
  const entriesSelect = document.getElementById("entriesPerPage");
  const tbody = document.getElementById("transactionBody");
  const showingRange = document.getElementById("showingRange");
  const totalEntriesEl = document.getElementById("totalEntries");
  const prevBtn = document.getElementById("prevBtn");
  const nextBtn = document.getElementById("nextBtn");

  let currentTransactionType = "ALL";
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;

  // 1️ Init
  populateAccounts();
  setDefaultFilters();
  attachFilterListeners();

  // 2️ Populate Account Dropdown (with 'All Accounts')
  async function populateAccounts() {
    try {
      const response = await fetch(BASE_URL + "/api/get-accounts/account", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include"
      });

      if (!response.ok) throw new Error("Failed to fetch accounts");

      const accounts = await response.json();
      accountSelect.innerHTML = `<option value="">All Accounts</option>`;
      accounts.forEach(acc => {
        const opt = document.createElement("option");
        opt.value = acc.accountId;
        opt.textContent = `Account ${acc.accountId}`;
        accountSelect.appendChild(opt);
      });

      showToast("Accounts loaded successfully!", "success");
    } catch (err) {
      console.error("Error loading accounts", err);
      showToast("Error loading accounts", "error");
    }
  }

  // 3️ Set default filters (date + entries)
  function setDefaultFilters() {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    fromDateInput.value = firstDay.toISOString().split("T")[0];
    toDateInput.value = lastDay.toISOString().split("T")[0];
    entriesSelect.value = "10";
  }

  // Tab Navigation (All / Received / Transfer / Deposit / Withdraw)
  document.querySelectorAll(".transaction-tabs button").forEach((btn) => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".transaction-tabs button").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");

      currentTransactionType = btn.textContent.toUpperCase(); // RECEIVED, etc.
      currentPage = 1;
      loadFilteredTransactions();
    });
  });

  // Filter listeners
  function attachFilterListeners() {
    accountSelect.addEventListener("change", loadFilteredTransactions);
    fromDateInput.addEventListener("change", loadFilteredTransactions);
    toDateInput.addEventListener("change", loadFilteredTransactions);
    entriesSelect.addEventListener("change", () => {
      entriesPerPage = parseInt(entriesSelect.value);
      currentPage = 1;
      loadFilteredTransactions();
    });

    prevBtn.addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        loadFilteredTransactions();
      }
    });

    nextBtn.addEventListener("click", () => {
      const totalPages = Math.ceil(totalEntries / entriesPerPage);
      if (currentPage < totalPages) {
        currentPage++;
        loadFilteredTransactions();
      }
    });
  }

  // 6️⃣ Load with current filters
  function loadFilteredTransactions() {
    const accountId = accountSelect.value;
    const fromDate = fromDateInput.value;
    const toDate = toDateInput.value;

    if (!fromDate || !toDate) return;

    const payload = {
      fromDate,
      toDate,
      pageNumber: currentPage,
      pageSize: entriesPerPage
    };

    if (accountId) {
      payload.accountId = parseInt(accountId);
    }

    loadTransactions(payload);
  }

  // 7️⃣ Fetch transactions with dynamic endpoint
  async function loadTransactions(payload) {
    tbody.innerHTML = "";

    let endpoint = "/api/statement/transaction"; // default

    const hasAccount = !!payload.accountId;

    if (currentTransactionType === "RECEIVED") {
      endpoint = hasAccount ? "/api/received-account/transaction" : "/api/received-all/transaction";
    } else if (currentTransactionType === "DEPOSIT") {
      endpoint = hasAccount ? "/api/deposit-account/transaction" : "/api/deposit-all/transaction";
    } else if (currentTransactionType === "WITHDRAW") {
      endpoint = hasAccount ? "/api/withdraw-account/transaction" : "/api/withdraw-all/transaction";
    } else if (currentTransactionType === "TRANSFER") {
      endpoint = hasAccount ? "/api/transfer-account/transaction" : "/api/transfer-all/transaction";
    }

    try {
      const res = await fetch(BASE_URL + endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload)
      });

      const result = await res.json();
      const transactions = result.data || [];
      totalEntries = result.total || 0;

      if (transactions.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7">No transactions found.</td></tr>`;
        showToast("No transactions found.", "info");
      } else {
        transactions.forEach(tx => {
          const row = document.createElement("tr");
          const typeColor = tx.type === "DEPOSIT" || tx.type === "RECEIVED" ? "green" : "red";
          const sign = typeColor === "green" ? "+" : "-";
          const txAccount = tx.transactionAccountId ? `#${tx.transactionAccountId}` : "-";

          const typeIcon = {
            "DEPOSIT": `<i class='bx bx-download' style="color:green" title="Deposit"></i>`,
            "WITHDRAWAL": `<i class='bx bx-upload' style="color:red" title="Withdraw"></i>`,
            "TRANSFER": `<i class='bx bx-transfer-alt' style="color:orange" title="Transfer"></i>`,
            "RECEIVED": `<i class='bx bx-receipt' style="color:blue" title="Received"></i>`
          }[tx.type] || tx.type;

          row.innerHTML = `
            <td><i class='bx bx-credit-card' title="Account ID"></i> ${tx.accountId}</td>
            <td>${formatTimestamp(tx.timestamp)}</td>
            <td><i class='bx bx-user' title="Transaction Account"></i> ${txAccount}</td>
            <td style="color: ${typeColor}">${sign} ₹${tx.amount.toLocaleString()}</td>
            <td>${typeIcon}</td>
            <td>₹${tx.closingBalance.toLocaleString()}</td>
            <td>${getStatusIcon(tx.status)}</td>
          `;
          tbody.appendChild(row);
        });
        showToast("Transactions loaded successfully!", "success");
      }

      updatePaginationDisplay();
    } catch (err) {
      console.error("Failed to load transactions:", err);
      tbody.innerHTML = `<tr><td colspan="7">Error loading transactions.</td></tr>`;
      showToast("Error loading transactions", "error");
    }
  }

  // 8️⃣ Pagination
  function updatePaginationDisplay() {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    pageNumbersContainer.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement("button");
      btn.className = "page-number";
      btn.textContent = i;
      if (i === currentPage) btn.classList.add("active");

      btn.addEventListener("click", () => {
        currentPage = i;
        loadFilteredTransactions();
      });

      pageNumbersContainer.appendChild(btn);
    }

    const start = (currentPage - 1) * entriesPerPage + 1;
    const end = Math.min(start + entriesPerPage - 1, totalEntries);
    showingRange.textContent = `${start} to ${end}`;
    totalEntriesEl.textContent = totalEntries;

    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;
  }

  // Format Timestamp
  function formatTimestamp(ms) {
    const date = new Date(ms);
    const dateStr = date.toLocaleDateString("en-IN");
    const timeStr = date.toLocaleTimeString("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    });
    return `${dateStr}<br><small>${timeStr}</small>`;
  }

  // Status Icons
  function getStatusIcon(status) {
    if (status === "SUCCESS") return `<i class='bx bx-check-circle' style="color:green" title="Success"></i>`;
    if (status === "FAILED") return `<i class='bx bx-error-circle' style="color:red" title="Failed"></i>`;
    return `<i class='bx bx-time' style="color:orange" title="Pending"></i>`;
  }
}

window.initTransactionPage = initTransactionPage;
