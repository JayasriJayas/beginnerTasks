function initSuperAdminAccountRequestList() {
  fetchStatusCounts();
  let currentPage = 1;
  let entriesPerPage = 10;
  let totalEntries = 0;
  let requestData = [];

  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");
  const entriesSelect = document.getElementById("entriesPerPage");
  const statusFilter = document.getElementById("statusFilter");
  const approveBtn = document.getElementById("approveBtn");
  const rejectBtn = document.getElementById("rejectBtn");
  
  toggleActionButtons(statusFilter.value);
  
  setDefaultFilters(fromDateInput, toDateInput, entriesSelect);
  fromDateInput.addEventListener("change", () => {
    toDateInput.min = fromDateInput.value;

    // Optional: auto-correct toDate if it's before fromDate
    if (toDateInput.value < fromDateInput.value) {
      toDateInput.value = fromDateInput.value;
    }

    currentPage = 1;
    loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
  });

  toDateInput.addEventListener("change", () => {
    fromDateInput.max = toDateInput.value;

    
    if (fromDateInput.value > toDateInput.value) {
      fromDateInput.value = toDateInput.value;
    }

    currentPage = 1;
    loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
  });


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

  approveBtn.addEventListener("click", handleBulkApprove);
  rejectBtn.addEventListener("click", handleBulkReject);

  document.getElementById("selectAll").addEventListener("change", function () {
    const checkboxes = document.querySelectorAll(".rowCheckbox");
    checkboxes.forEach(cb => cb.checked = this.checked);
  });
  
  loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);

  statusFilter.addEventListener("change", (e) => {
    toggleActionButtons(e.target.value);
  });

  function toggleActionButtons(status) {
    if (status === "PENDING") {
      approveBtn.style.display = "inline-block";
      rejectBtn.style.display = "inline-block";
    } else {
      approveBtn.style.display = "none";
      rejectBtn.style.display = "none";
    }
  }

  function setDefaultFilters(fromInput, toInput, entriesSelect) {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    fromInput.value = firstDay.toISOString().split("T")[0];
    toInput.value = lastDay.toISOString().split("T")[0];
    entriesSelect.value = "10";
    entriesPerPage = 10;

    fromInput.max = toInput.value;
    toInput.min = fromInput.value;

    const statusDropdown = document.getElementById("statusFilter");
    if (statusDropdown) {
      statusDropdown.value = "PENDING";
      statusDropdown.addEventListener("change", () => {
        currentPage = 1;
        loadRequests(fromInput.value, toInput.value, currentPage, entriesPerPage);
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

    fetch(`${BASE_URL}/api/list/account-request`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(data => {
        requestData = data.data || [];
        totalEntries = data.total || requestData.length;
        currentPage = data.page || 1;
        entriesPerPage = data.size || size;
        toggleActionButtons(statusFilter.value);  // Update button visibility based on status filter
        renderTable();
      })
      .catch(err => {
        console.error("Failed to fetch requests", err);
        document.getElementById("accountRequestTableBody").innerHTML = `<tr><td colspan='9'>Failed to load data.</td></tr>`;
        approveBtn.disabled = true;
        rejectBtn.disabled = true;  // Disable buttons if there's an error loading data
      });
  }


  function renderTable() {
    const tbody = document.getElementById("accountRequestTableBody");
    tbody.innerHTML = "";

    // Disable buttons if no transactions are available
    if (!requestData.length) {
      tbody.innerHTML = `<tr><td colspan='9'>No requests found.</td></tr>`;
	  document.getElementById("showingRange").textContent = `0 to 0 of 0 entries`;
	  document.getElementById("totalEntries").textContent = `0`;
      // Disable buttons when there are no transactions
      approveBtn.disabled = true;
      rejectBtn.disabled = true;
      return;
    }

    // Enable buttons when there are transactions
    approveBtn.disabled = false;
    rejectBtn.disabled = false;

    let pendingRequestsExist = false;  // Flag to check if there are any 'PENDING' requests
	let approvedRequestsExist = false;  // Flag to check if there are any 'APPROVED' requests
	let rejectedRequestsExist = false; 

    requestData.forEach((item, idx) => {
      if (item.status === "PENDING") {
        pendingRequestsExist = true;  // Found at least one 'PENDING' request
      }
	  if (item.status === "APPROVED") approvedRequestsExist = true;
	  if (item.status === "REJECTED") rejectedRequestsExist = true;

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><input type="checkbox" class="rowCheckbox" value="${item.requestId}" /></td>
        <td>${(currentPage - 1) * entriesPerPage + idx + 1}</td>
        <td>${item.userId || "-"}</td>
        <td>${item.branchId || "-"}</td>
        <td>${formatStatus(item.status)}</td>
        <td>${formatDateTime(item.createdAt)}</td>
        <td>${addActionButtons(item.requestId, item.status)}</td>
      `;
      tbody.appendChild(tr);
    });

    // If no 'PENDING' requests are found, display a message
	if (statusFilter.value === "PENDING" && !pendingRequestsExist) {
	   tbody.innerHTML = `<tr><td colspan='9'>No pending requests.</td></tr>`;
	 } else if (statusFilter.value === "APPROVED" && !approvedRequestsExist) {
	   tbody.innerHTML = `<tr><td colspan='9'>No approved requests.</td></tr>`;
	 } else if (statusFilter.value === "REJECTED" && !rejectedRequestsExist) {
	   tbody.innerHTML = `<tr><td colspan='9'>No rejected requests.</td></tr>`;
	 }

    updatePaginationControls();
  }


  function updatePaginationControls() {
    const totalPages = Math.ceil(totalEntries / entriesPerPage);
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";
	if (totalEntries === 0) {
	  document.getElementById("showingRange").textContent = `0 to 0 of 0 entries`;
	  document.getElementById("totalEntries").textContent = `0`;
	  document.getElementById("prevBtn").disabled = true;
	  document.getElementById("nextBtn").disabled = true;
	  return;
	}

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

  window.viewRequest = async function(requestId) {
    console.log("viewclicked", requestId);

    const request = requestData.find(r => r.requestId === requestId);
    if (!request || !request.userId) {
      showToast("User ID not found for this request", "error");
      return;
    }

    const userId = request.userId;

    try {
      const res = await fetch(`${BASE_URL}/api/get-profile/user`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ userId: userId })  // 
      });

      const data = await res.json();
      const { user, customer } = data;

      if (!user || !customer) {
        showToast("No user data found.", "warning");
        return;
      }

      const modalContent = `
        <div class="modal-box">
          <h3>Request Details</h3>
          <p><strong>Username:</strong> ${user.username}</p>
          <p><strong>Name:</strong> ${user.name}</p>
          <p><strong>Email:</strong> ${user.email}</p>
          <p><strong>Phone:</strong> ${user.phone}</p>
          <p><strong>Gender:</strong> ${user.gender}</p>
          <p><strong>Date of Birth:</strong> ${customer.dob}</p>
          <p><strong>Aadhar No:</strong> ${customer.aadharNo}</p>
          <p><strong>PAN No:</strong> ${customer.panNo}</p>
          <p><strong>Occupation:</strong> ${customer.occupation}</p>
          <p><strong>Annual Income:</strong> ₹${customer.annualIncome.toLocaleString()}</p>
          <p><strong>Address:</strong> ${customer.address}</p>
          <p><strong>Branch ID:</strong> ${user.branchId}</p>
          <p><strong>Marital Status:</strong> ${customer.maritalStatus}</p>
          <div class="modal-footer">
            <button class="btn" onclick="closeModal()">Close</button>
          </div>
        </div>
      `;

	  const modalOverlay = document.getElementById("modalOverlay");
	      modalOverlay.innerHTML = modalContent;
	      modalOverlay.classList.remove("hidden");

    } catch (err) {
      console.error("Error loading user profile:", err);
      showToast("Error loading user profile", "error");
    }
  };


  // Single approve request action
  window.approveRequest = function(itemId) {
	
    console.log("Approve button clicked for ID:", itemId);

    const confirmationMessage = `Are you sure you want to approve this request?`;
    showConfirmationModal("Confirm Approval", confirmationMessage, () => {
      const url = "/api/approve-account/account-request";
      const body = { id: itemId };

      fetch(`${BASE_URL}${url}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      })
        .then(res => res.json())
        .then(() => {
          showToast("Approval successful", "success");
          closeModal();
          loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
          fetchStatusCounts();
        })
        .catch(err => {
          console.error("Error approving request:", err);
          showToast("Error approving request.", "error");
        });
    });
  };

  // Helper functions for rendering, status, and date formatting


  window.rejectRequest = function (itemId) {
    const modalContent = `
      <div class="modal-box">
        <h3>Enter Rejection Reason</h3>
        <textarea id="rejectionReason" placeholder="Please provide a reason for rejection" rows="4"></textarea>
        <div class="modal-footer">
          <button id="confirmRejectBtn" class="btn">Confirm Reject</button>
          <button onclick="closeModal()" class="btn">Cancel</button>
        </div>
      </div>
    `;

    const modalOverlay = document.getElementById("modalOverlay");
    modalOverlay.innerHTML = modalContent;
    modalOverlay.classList.remove("hidden");

    document.getElementById("confirmRejectBtn").addEventListener("click", () => {
      const reason = document.getElementById("rejectionReason").value.trim();
      if (!reason) {
        showToast("Rejection reason is required.", "warning");
        return;
      }

      const url = "/api/reject/account-request";
      const body = { id: itemId, rejectionReason: reason };

      fetch(`${BASE_URL}${url}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      })
        .then(res => res.json())
        .then(() => {
          showToast("Rejection successful", "success");
          closeModal();
          loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
          fetchStatusCounts();
        })
        .catch(err => {
          console.error("Error rejecting request:", err);
          showToast("Error rejecting request.", "error");
        });
    });
  };

  function handleBulkApprove() {
    const selected = getSelectedIds();
    if (!selected.length) return showToast("Select at least one request.","warning");
    const confirmationMessage = `Are you sure you want to approve ${selected.length} selected request(s)?`;
    showConfirmationModal("Confirm Approval", confirmationMessage, () => {
      const url = selected.length === 1 ? "/api/approve-account/account-request" : "/api/multiple-approve/account-request";
      const body = selected.length === 1 ? { id: selected[0] } : selected;

      fetch(`${BASE_URL}${url}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      }).then(res => res.json()).then(() => {
        showToast("Approval successful", "success");
        loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
        fetchStatusCounts();
      }).catch(err => {
        console.error("Error approving requests:", err);
        showToast("Error approving requests.", "error");
      });
    });
  }

  function showConfirmationModal(title, message, confirmCallback) {
    const modalContent = `
      <div class="modal-box">
        <h3>${title}</h3>
        <p>${message}</p>
        <div class="modal-footer">
          <button id="confirmBtn" class="btn">Confirm</button>
          <button onclick="closeModal()" class="btn">Cancel</button>
        </div>
      </div>
    `;
    const modalOverlay = document.getElementById("modalOverlay");
    modalOverlay.innerHTML = modalContent;
    modalOverlay.classList.remove("hidden");

    document.getElementById("confirmBtn").addEventListener("click", () => {
      confirmCallback();  // Execute the confirmation action (approval)
      closeModal();
	  loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
	  fetchStatusCounts();
    });
  }

  function handleBulkReject() {
    const selected = getSelectedIds();
    if (!selected.length) return showToast("Select at least one request.", "warning");

    // Show modal to enter rejection reason
    showRejectionReasonModal(selected);
  }

  function showRejectionReasonModal(selected) {
    const modalContent = `
      <div class="modal-box">
        <h3>Enter Rejection Reason</h3>
        <textarea id="rejectionReason" placeholder="Please provide a reason for rejection" rows="4"></textarea>
        <div class="modal-footer">
          <button id="confirmRejectBtn" class="btn">Confirm Reject</button>
          <button onclick="closeModal()" class="btn">Cancel</button>
        </div>
      </div>
    `;

    const modalOverlay = document.getElementById("modalOverlay");
    modalOverlay.innerHTML = modalContent;
    modalOverlay.classList.remove("hidden");

    // Handle confirm reject
    document.getElementById("confirmRejectBtn").addEventListener("click", () => {
      const reason = document.getElementById("rejectionReason").value.trim();
      if (!reason) {
        showToast("Rejection reason is required.", "warning");
        return;
      }

      rejectRequests(selected, reason);
      closeModal();
    });
  }

  function rejectRequests(selected, reason) {
    const url = selected.length === 1 ? "/api/reject/account-request" : "/api/multiple-reject/account-request";
    const body = selected.length === 1
      ? { id: selected[0], rejectionReason: reason }
      : selected.map(id => ({ id, rejectionReason: reason }));

    fetch(`${BASE_URL}${url}`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    }).then(res => res.json()).then(() => {
      showToast("Rejection successful", "success");
      loadRequests(fromDateInput.value, toDateInput.value, currentPage, entriesPerPage);
      fetchStatusCounts();
    }).catch(err => {
      console.error("Error rejecting requests:", err);
      showToast("Error rejecting requests.", "error");
    });
  }

  function fetchStatusCounts() {
    fetch(`${BASE_URL}/api/status-counts/account-request`, {
      method: "GET",
      credentials: "include"
	      })
	        .then(res => res.json())
	        .then(data => {
	          document.getElementById("pendingCount").textContent = data.PENDING || 0;
	          document.getElementById("approvedCount").textContent = data.APPROVED || 0;
	          document.getElementById("rejectedCount").textContent = data.REJECTED || 0;
	        })
	        .catch(err => {
	          console.error("Error fetching status counts:", err);
	        });
	    }

	    
	    function formatStatus(status) {
	      const color = { PENDING: "orange", APPROVED: "green", REJECTED: "red" }[status?.toUpperCase()] || "gray";
	      return `<span style="color: ${color}; font-weight: 600;">${status}</span>`;
	    }

	    function formatDateTime(ts) {
	      if (!ts || isNaN(ts)) return "-";
	      const date = new Date(Number(ts));
	      const dateStr = date.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
	      const timeStr = date.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: true });
	      return `${dateStr}<br><span style="font-size: 12px; color: #555;">${timeStr}</span>`;
	    }

	    function addActionButtons(requestId, status) {
	      const viewBtn = `<button onclick="viewRequest(${requestId})"><i class='bx bx-show'></i> </button>`;
	      let buttons = viewBtn;
	      if (status === "PENDING") {
	        buttons += `<button onclick="approveRequest(${requestId})"><i class='bx bx-check'></i> </button>`;
	        buttons += `<button onclick="rejectRequest(${requestId})"><i class='bx bx-x'></i> </button>`;
	      }
	      return buttons;
	    }

	  

		function closeModal() {
		  const modalOverlay = document.getElementById("modalOverlay");
		  if (modalOverlay) {
		    modalOverlay.classList.add("hidden");
		  }
		  const requestModalOverlay = document.getElementById("requestModalOverlay");
		  if (requestModalOverlay) {
		    requestModalOverlay.classList.add("hidden");
		  }
		}


	    window.initSuperAdminAccountRequestList = initSuperAdminAccountRequestList;
	  }

	  window.addEventListener("load", initSuperAdminAccountRequestList);

