function initSuperAdminAccountRequestList() {
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;

  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");
  const entriesSelect = document.getElementById("entriesPerPage");

  setDefaultDateRange();

  document.getElementById("filterBtn").addEventListener("click", () => {
    currentPage = 1;
    loadData();
  });

  entriesSelect.addEventListener("change", (e) => {
    entriesPerPage = parseInt(e.target.value);
    currentPage = 1;
    loadData();
  });

  document.getElementById("prevBtn").addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      loadData();
    }
  });

  document.getElementById("nextBtn").addEventListener("click", () => {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    if (currentPage < totalPages) {
      currentPage++;
      loadData();
    }
  });

  loadData();

  function setDefaultDateRange() {
    const now = new Date();
    const start = new Date(now.getFullYear(), now.getMonth(), 1);
    const end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    fromDateInput.value = start.toISOString().split("T")[0];
    toDateInput.value = end.toISOString().split("T")[0];
  }

  function loadData() {
    fetch(`${BASE_URL}/api/list/account-request`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({
        fromDate: fromDateInput.value,
        toDate: toDateInput.value,
        pageNumber: currentPage,
        pageSize: entriesPerPage
      })
    })
      .then(res => res.json())
      .then(data => {
        totalEntries = data.total || data.data.length;
        renderTable(data.data);
      })
      .catch(err => {
        console.error("Failed to fetch requests", err);
        document.getElementById("accountRequestTableBody").innerHTML =
          `<tr><td colspan='8'>Failed to load data.</td></tr>`;
      });
  }

  function renderTable(data) {
    const tbody = document.getElementById("accountRequestTableBody");
    tbody.innerHTML = "";

    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan='8'>No requests found.</td></tr>`;
      return;
    }

    data.forEach((item, idx) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${(currentPage - 1) * entriesPerPage + idx + 1}</td>
        <td>${item.requestId}</td>
        <td>${item.userId}</td>
        <td>${item.branchId}</td>
        <td>${formatStatus(item.status)}</td>
        <td>${formatDateTime(item.requestTimestamp)}</td>
      `;
      tbody.appendChild(tr);
    });

    updatePagination();
  }

  function updatePagination() {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement("button");
      btn.textContent = i;
      btn.className = i === currentPage ? "active" : "";
      btn.addEventListener("click", () => {
        currentPage = i;
        loadData();
      });
      pageNumbers.appendChild(btn);
    }

    const start = (currentPage - 1) * entriesPerPage + 1;
    const end = Math.min(currentPage * entriesPerPage, totalEntries);

    document.getElementById("showingRange").textContent = `${start} to ${end}`;
    document.getElementById("totalEntries").textContent = totalEntries;

    document.getElementById("prevBtn").disabled = currentPage === 1;
    document.getElementById("nextBtn").disabled = currentPage === totalPages;
  }

  function formatDateTime(timestamp) {
    if (!timestamp) return "-";
    const date = new Date(Number(timestamp));
    return date.toLocaleDateString("en-IN") +
      '<br><span style="font-size: 12px; color: #666;">' +
      date.toLocaleTimeString("en-IN", { hour: '2-digit', minute: '2-digit', hour12: true }) +
      '</span>';
  }

  function formatStatus(status) {
    const color = {
      PENDING: "orange",
      APPROVED: "green",
      REJECTED: "red"
    }[status?.toUpperCase()] || "gray";
    return `<span style="color: ${color}; font-weight: bold;">${status}</span>`;
  }
}

window.initSuperAdminAccountRequestList = initSuperAdminAccountRequestList;