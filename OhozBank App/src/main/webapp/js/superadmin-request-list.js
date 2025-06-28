function initSuperAdminRequestList() {
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;
  let requestData = [];

  console.log("Superadmin Request List initialized");

  const fromDateInput = document.getElementById("fromDate");
    const toDateInput = document.getElementById("toDate");
    const entriesSelect = document.getElementById("entriesPerPage");

    setDefaultFilters(fromDateInput, toDateInput, entriesSelect);

    document.getElementById("filterBtn").addEventListener("click", () => {
      currentPage = 1;
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
    });

    entriesSelect.addEventListener("change", (e) => {
      entriesPerPage = parseInt(e.target.value);
      currentPage = 1;
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
    });

    document.getElementById("prevBtn").addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
      }
    });

    document.getElementById("nextBtn").addEventListener("click", () => {
      const totalPages = Math.ceil(totalEntries / entriesPerPage);
      if (currentPage < totalPages) {
        currentPage++;
        loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
      }
    });

    loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
  

  function setDefaultFilters(fromInput, toInput, entriesSelect) {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    fromInput.value = firstDay.toISOString().split("T")[0];
    toInput.value = lastDay.toISOString().split("T")[0];
    entriesSelect.value = "10";
    entriesPerPage = 10;
  }

  function loadRequests(from = "", to = "", page = 1, size = 10) {
    const payload = {
      fromDate: from,
      toDate: to,
      pageNumber: page,
      pageSize: size
    };

    fetch(`${BASE_URL}/api/list/request`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
      .then((res) => res.json())
      .then((data) => {
        if (!Array.isArray(data.data)) {
          throw new Error("Invalid response format");
        }

        requestData = data.data;
        totalEntries = data.total || requestData.length;
        currentPage = data.page || 1;
        entriesPerPage = data.size || size;
        renderTable();
      })
      .catch((err) => {
        console.error("❌ Failed to load requests:", err);
        document.getElementById("requestTableBody").innerHTML =
          "<tr><td colspan='7'>Failed to load data.</td></tr>";
      });
  }

  function renderTable() {
    const tbody = document.getElementById("requestTableBody");
    tbody.innerHTML = "";

    if (!requestData.length) {
      tbody.innerHTML = `<tr><td colspan="7">No requests found.</td></tr>`;
      return;
    }

    requestData.forEach((item, idx) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${(currentPage - 1) * entriesPerPage + idx + 1}</td>
        <td>${item.username || "-"}</td>
        <td>${item.name || "-"}</td>
        <td>${item.email || "-"}</td>
        <td>${item.branchId || "-"}</td>
        <td>${formatStatus(item.status)}</td>
        <td>${formatDateTime(item.requestTimestamp)}</td>
      `;
      tbody.appendChild(tr);
    });

    updatePaginationControls();
  }

  function updatePaginationControls() {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement("button");
      btn.className = "page-number" + (i === currentPage ? " active" : "");
      btn.textContent = i;
      btn.addEventListener("click", () => {
        currentPage = i;
        loadRequests(
          document.getElementById("fromDate").value,
          document.getElementById("toDate").value,
          currentPage,
          entriesPerPage
        );
      });
      pageNumbers.appendChild(btn);
    }

    const showingRange = document.getElementById("showingRange");
    const totalEntriesEl = document.getElementById("totalEntries");

    const start = (currentPage - 1) * entriesPerPage + 1;
    const end = Math.min(currentPage * entriesPerPage, totalEntries);

    showingRange.textContent = `${start} to ${end}`;
    totalEntriesEl.textContent = totalEntries;

    document.getElementById("prevBtn").disabled = currentPage === 1;
    document.getElementById("nextBtn").disabled = currentPage === totalPages;
  }

  function formatDateTime(ts) {
    if (!ts || isNaN(ts)) return "-";
    const date = new Date(Number(ts));
    const dateStr = date.toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric"
    });
    const timeStr = date.toLocaleTimeString("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    });
    return `${dateStr}<br><span style="font-size: 12px; color: #555;">${timeStr}</span>`;
  }

  function formatStatus(status) {
    const color = {
      PENDING: "orange",
      APPROVED: "green",
      REJECTED: "red"
    }[status?.toUpperCase()] || "gray";

    return `<span style="color: ${color}; font-weight: 600;">${status}</span>`;
  }
  }
// Expose globally so dashboard.js can call it
window.initSuperAdminRequestList = initSuperAdminRequestList;
