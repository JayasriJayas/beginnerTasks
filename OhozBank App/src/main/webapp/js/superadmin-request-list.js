// superadmin-request-list.js
function initSuperAdminRequestList() {
	fetchStatusCounts();
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;
  let requestData = [];

  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");
  const entriesSelect = document.getElementById("entriesPerPage");

  setDefaultFilters(fromDateInput, toDateInput, entriesSelect);

  document.getElementById("filterBtn").addEventListener("click", () => {
    currentPage = 1;
    loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	fetchStatusCounts();
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

  document.getElementById("approveBtn").addEventListener("click", handleBulkApprove);
  document.getElementById("rejectBtn").addEventListener("click", handleBulkReject);

  document.getElementById("selectAll").addEventListener("change", function () {
    const checkboxes = document.querySelectorAll(".rowCheckbox");
    checkboxes.forEach(cb => cb.checked = this.checked);
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

	 const statusDropdown = document.getElementById("statusFilter");
	 if (statusDropdown) {
	   statusDropdown.value = "PENDING";
	   statusDropdown.addEventListener("change", () => {
	      currentPage = 1;
	      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	    });
	 }
  }

  function loadRequests(from = "", to = "", page = 1, size = 10) {
    const payload = {
      fromDate: from,
      toDate: to,
      pageNumber: page,
      pageSize: size,
      ...(statusFilter.value ? { status: statusFilter.value } : {}) 
    };

    fetch(`${BASE_URL}/api/list/request`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(data => {
        requestData = data.data || [];
        totalEntries = data.total || requestData.length;
        currentPage = data.page || 1;
        entriesPerPage = data.size || size;
        renderTable();
      })
      .catch(err => {
        console.error("❌ Failed to load requests:", err);
        document.getElementById("requestTableBody").innerHTML =
          "<tr><td colspan='9'>Failed to load data.</td></tr>";
      });
  }

  function renderTable() {
    const tbody = document.getElementById("requestTableBody");
    tbody.innerHTML = "";

    if (!requestData.length) {
      tbody.innerHTML = `<tr><td colspan="9">No requests found.</td></tr>`;
      return;
    }

    requestData.forEach((item, idx) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><input type="checkbox" class="rowCheckbox" value="${item.id}" /></td>
        <td>${(currentPage - 1) * entriesPerPage + idx + 1}</td>
        <td>${item.username || "-"}</td>
        <td>${item.name || "-"}</td>
        <td>${item.email || "-"}</td>
        <td>${item.branchId || "-"}</td>
        <td>${formatStatus(item.status)}</td>
        <td>${formatDateTime(item.requestTimestamp)}</td>
		<td>${addActionButtons(item.id, item.status)}</td>
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
        loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
      });
      pageNumbers.appendChild(btn);
    }

    document.getElementById("showingRange").textContent = `${(currentPage - 1) * entriesPerPage + 1} to ${Math.min(currentPage * entriesPerPage, totalEntries)}`;
    document.getElementById("totalEntries").textContent = totalEntries;

    document.getElementById("prevBtn").disabled = currentPage === 1;
    document.getElementById("nextBtn").disabled = currentPage === totalPages;
  }

  function getSelectedIds() {
    return Array.from(document.querySelectorAll(".rowCheckbox:checked"))
      .map(cb => parseInt(cb.value));
  }

  function handleBulkApprove() {
    const selected = getSelectedIds();
    if (!selected.length) return alert("Select at least one request.");

    const url = selected.length === 1 ? "/api/approve/request" : "/api/multiple-approve/request";
    const body = selected.length === 1 ? { id: selected[0] } : selected;

    fetch(`${BASE_URL}${url}`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    }).then(res => res.json()).then(() => {
      alert("Approval successful");
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	  fetchStatusCounts()
    });
  }

  function handleBulkReject() {
    const selected = getSelectedIds();
    if (!selected.length) return alert("Select at least one request.");
    const reason = prompt("Enter rejection reason:");
    if (!reason) return;

    const url = selected.length === 1 ? "/api/reject/request" : "/api/multiple-reject/request";
    const body = selected.length === 1
      ? { id: selected[0], rejectionReason: reason }
      : selected.map(id => ({ id, rejectionReason: reason }));

    fetch(`${BASE_URL}${url}`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    }).then(res => res.json()).then(() => {
      alert("Rejection successful");
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	  fetchStatusCounts()
    });
  }


  window.viewRequest = (id) => {
     fetch(`${BASE_URL}/api/view-details/request`, {
       method: "POST",
       credentials: "include",
       headers: { "Content-Type": "application/json" },
       body: JSON.stringify({ id })
     })
       .then((res) => res.json())
       .then((data) => {
         const html = `
           <div class="detail-row"><strong>Name:</strong> ${data.name}</div>
           <div class="detail-row"><strong>Email:</strong> ${data.email}</div>
           <div class="detail-row"><strong>Phone:</strong> ${data.phone}</div>
           <div class="detail-row"><strong>Gender:</strong> ${data.gender}</div>
           <div class="detail-row"><strong>DOB:</strong> ${data.dob}</div>
           <div class="detail-row"><strong>Occupation:</strong> ${data.occupation}</div>
           <div class="detail-row"><strong>Annual Income:</strong> ₹${data.annualIncome}</div>
           <div class="detail-row"><strong>Aadhar No:</strong> ${data.aadharNo}</div>
           <div class="detail-row"><strong>PAN No:</strong> ${data.panNo}</div>
           <div class="detail-row"><strong>Address:</strong> ${data.address}</div>
           <div class="detail-row"><strong>Marital Status:</strong> ${data.maritalStatus}</div>
         `;

         document.getElementById("requestDetailsContent").innerHTML = html;
         document.getElementById("requestModalOverlay").classList.remove("hidden");
       })
       .catch((err) => {
         console.error("❌ View request error:", err);
         alert("Unable to fetch request details.");
       });
   };

   window.closeRequestModal = () => {
     const modal = document.getElementById("requestModalOverlay");
     if (modal) {
       modal.classList.add("hidden");
       document.getElementById("requestDetailsContent").innerHTML = "";
     }
   };



  window.approveRequest = (id) => {
    fetch(`${BASE_URL}/api/approve/request`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id })
    }).then(res => res.json()).then(() => {
      alert("Approved successfully");
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	  fetchStatusCounts()
    });
  };

  window.rejectRequest = (id) => {
    const reason = prompt("Enter rejection reason:");
    if (!reason) return;

    fetch(`${BASE_URL}/api/reject/request`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id, rejectionReason: reason })
    }).then(res => res.json()).then(() => {
      alert("Rejected successfully");
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	  fetchStatusCounts()
    });
  };
  function formatDateTime(ts) {
    if (!ts || isNaN(ts)) return "-";
    const date = new Date(Number(ts));
    const dateStr = date.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
    const timeStr = date.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: true });
    return `${dateStr}<br><span style="font-size: 12px; color: #555;">${timeStr}</span>`;
  }

  function formatStatus(status) {
    const color = { PENDING: "orange", APPROVED: "green", REJECTED: "red" }[status?.toUpperCase()] || "gray";
    return `<span style="color: ${color}; font-weight: 600;">${status}</span>`;
  }

  function addActionButtons(itemId, status) {
    const viewBtn = `<button onclick="viewRequest(${itemId})">🔍</button>`;
    if (status?.toUpperCase() === "PENDING") {
      const approveBtn = `<button onclick="approveRequest(${itemId})">✔</button>`;
      const rejectBtn = `<button onclick="rejectRequest(${itemId})">❌</button>`;
      return `${viewBtn} ${approveBtn} ${rejectBtn}`;
    } else {
      return viewBtn;
    }
  }
  function fetchStatusCounts() {
    fetch(`${BASE_URL}/api/status-counts/request`, {
      method: "GET",
      credentials: "include"
    })
      .then(res => res.json())
      .then(data => {
        document.getElementById("pendingCount").textContent = data.PENDING || 0;
        document.getElementById("approvedCount").textContent = data.APPROVED || 0;
        document.getElementById("rejectedCount").textContent = data.REJECTED || 0;
      })
      .catch(err => console.error("Error fetching status counts", err));
  }


}

window.initSuperAdminRequestList = initSuperAdminRequestList;
