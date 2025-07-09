let accountCurrentPage = 1;
let accountEntriesPerPage = 10;
let accountTotalEntries = 0;
let accountSearchQuery = "";

function initAccountTable() {
  const entriesSelect = document.getElementById("entriesPerPage");
  const searchBtn = document.getElementById("searchBtn");
  const searchInput = document.getElementById("searchInput");

  // Handle change in entries per page
  entriesSelect.addEventListener("change", (e) => {
    accountEntriesPerPage = parseInt(e.target.value);
    accountCurrentPage = 1;
    loadAccounts();
  });

  // Handle search by account ID
  searchBtn.addEventListener("click", () => {
    accountSearchQuery = searchInput.value.trim();
    accountCurrentPage = 1;
    loadAccounts();
  });

  // Handle pagination - previous button
  document.getElementById("prevBtn").addEventListener("click", () => {
    if (accountCurrentPage > 1) {
      accountCurrentPage--;
      loadAccounts();
    }
  });

  // Handle pagination - next button
  document.getElementById("nextBtn").addEventListener("click", () => {
    const totalPages = Math.ceil(accountTotalEntries / accountEntriesPerPage);
    if (accountCurrentPage < totalPages) {
      accountCurrentPage++;
      loadAccounts();
    }
  });

  // Initial data load
  loadAccounts();
}
function loadAccounts() {
  const endpoint = accountSearchQuery
    ? `${BASE_URL}/api/search-accounts/account`
    : `${BASE_URL}/api/get-all/account`;

  const payload = accountSearchQuery
    ? {
        search: accountSearchQuery,
        pageNumber: accountCurrentPage,
        pageSize: accountEntriesPerPage
      }
    : {
        pageNumber: accountCurrentPage,
        pageSize: accountEntriesPerPage
      };

  fetch(endpoint, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  })
    .then((res) => res.json())
    .then((data) => {
      const tbody = document.getElementById("accountTableBody");
      tbody.innerHTML = "";

      const accounts = data.data || [];

      if (accounts.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6">No accounts found.</td></tr>`;
        document.getElementById("pageNumbers").innerHTML = "";
        document.getElementById("showingRange").textContent = "0 to 0";
        document.getElementById("totalEntries").textContent = "0";
        return;
      }

      // Render table rows
      accounts.forEach((acc) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td><i class='bx bx-credit-card'></i> ${acc.accountId}</td>
          <td><i class='bx bx-building'></i> ${acc.branchId}</td>
          <td><i class='bx bx-user'></i> ${acc.userId}</td>
          <td>₹${acc.balance.toLocaleString()}</td>
          <td>${formatStatus(acc.status)}</td>
          <td>${formatDate(acc.createdAt)}</td>
        `;
        tbody.appendChild(tr);
      });

      accountTotalEntries = data.total || 0;
      updateAccountPagination(accounts.length);
    })
    .catch((err) => {
      console.error("Error fetching accounts:", err);
      document.getElementById("accountTableBody").innerHTML = `<tr><td colspan="6">Failed to load accounts.</td></tr>`;
    });
}

function updateAccountPagination(displayedCount) {
  const totalPages = Math.ceil(accountTotalEntries / accountEntriesPerPage);
  const pageNumbers = document.getElementById("pageNumbers");
  pageNumbers.innerHTML = "";

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.className = "page-number" + (i === accountCurrentPage ? " active" : "");
    btn.textContent = i;
    btn.addEventListener("click", () => {
      accountCurrentPage = i;
      loadAccounts();
    });
    pageNumbers.appendChild(btn);
  }

  const start = (accountCurrentPage - 1) * accountEntriesPerPage + 1;
  const end = start + displayedCount - 1;

  document.getElementById("showingRange").textContent = `${start} to ${end}`;
  document.getElementById("totalEntries").textContent = accountTotalEntries;

  document.getElementById("prevBtn").disabled = accountCurrentPage === 1;
  document.getElementById("nextBtn").disabled = accountCurrentPage === totalPages || totalPages === 0;
}

function formatDate(timestamp) {
  const d = new Date(timestamp);
  const dateStr = d.toLocaleDateString("en-IN");
  const timeStr = d.toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true
  });
  return `${dateStr}<br><small>${timeStr}</small>`;
}

function formatStatus(status) {
  const iconMap = {
    "ACTIVE": `<i class='bx bx-check-circle' style="color:green"></i>`,
    "INACTIVE": `<i class='bx bx-block' style="color:red"></i>`
  };
  return iconMap[status] || status;
}

// Expose to HTML
window.initAccountTable = initAccountTable;
