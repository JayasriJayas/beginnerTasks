function initAdminRequestList() {
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;
  let requestData = [];

  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");
  const entriesSelect = document.getElementById("entriesPerPage");

  setDefaultDates();

  document.getElementById("filterBtn").addEventListener("click", () => {
    currentPage = 1;
    fetchData();
  });

  entriesSelect.addEventListener("change", (e) => {
    entriesPerPage = parseInt(e.target.value);
    currentPage = 1;
    fetchData();
  });

  document.getElementById("prevBtn").addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      fetchData();
    }
  });

  document.getElementById("nextBtn").addEventListener("click", () => {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    if (currentPage < totalPages) {
      currentPage++;
      fetchData();
    }
  });

  function setDefaultDates() {
    const now = new Date();
    const first = new Date(now.getFullYear(), now.getMonth(), 1);
    const last = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    fromDateInput.value = first.toISOString().split("T")[0];
    toDateInput.value = last.toISOString().split("T")[0];
  }

  function fetchData() {
    const payload = {
      fromDate: fromDateInput.value,
      toDate: toDateInput.value,
      pageNumber: currentPage,
      pageSize: entriesPerPage
    };

    fetch(`${BASE_URL}/api/list/request`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(data => {
        requestData = data.data || [];
        totalEntries = data.total || requestData.length;
        renderTable();
      })
      .catch(err => {
        console.error("Failed to load requests", err);
        document.getElementById("adminRequestTableBody").innerHTML =
          "<tr><td colspan='4'>Failed to load data.</td></tr>";
      });
  }

  function renderTable() {
    const tbody = document.getElementById("adminRequestTableBody");
    tbody.innerHTML = "";

    if (!requestData.length) {
      tbody.innerHTML = `<tr><td colspan='4'>No requests found.</td></tr>`;
      return;
    }

    requestData.forEach((req, idx) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${(currentPage - 1) * entriesPerPage + idx + 1}</td>
        <td>${req.requestType || "-"}</td>
        <td>${formatStatus(req.status)}</td>
        <td>${formatDateTime(req.requestTimestamp)}</td>
      `;
      tbody.appendChild(tr);
    });

    updatePagination();
  }

  function updatePagination() {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    const pageContainer = document.getElementById("pageNumbers");
    pageContainer.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement("button");
      btn.textContent = i;
      btn.className = i === currentPage ? "active" : "";
      btn.addEventListener("click", () => {
        currentPage = i;
        fetchData();
      });
      pageContainer.appendChild(btn);
    }

    const start = (currentPage - 1) * entriesPerPage + 1;
    const end = Math.min(currentPage * entriesPerPage, totalEntries);

    document.getElementById("showingRange").textContent = `${start} to ${end}`;
    document.getElementById("totalEntries").textContent = totalEntries;

    document.getElementById("prevBtn").disabled = currentPage === 1;
    document.getElementById("nextBtn").disabled = currentPage === totalPages;
  }

  function formatDateTime(ts) {
    const date = new Date(Number(ts));
    const d = date.toLocaleDateString("en-IN");
    const t = date.toLocaleTimeString("en-IN", { hour: '2-digit', minute: '2-digit' });
    return `${d}<br><span style="font-size: 12px; color: #555;">${t}</span>`;
  }

  function formatStatus(status) {
    const colors = { PENDING: "orange", APPROVED: "green", REJECTED: "red" };
    const color = colors[status?.toUpperCase()] || "gray";
    return `<span style="color: ${color}; font-weight: 600;">${status}</span>`;
  }

  fetchData();
}

window.initAdminRequestList = initAdminRequestList;
