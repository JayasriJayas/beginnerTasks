let adminCurrentPage = 1;
let adminEntriesPerPage = 10;
let adminTotalEntries = 0;

function initSuperAdminAdminList() {
  const entriesSelect = document.getElementById("adminEntriesPerPage");

  entriesSelect.addEventListener("change", (e) => {
    adminEntriesPerPage = parseInt(e.target.value);
    adminCurrentPage = 1;
    loadAdmins();
  });

  document.getElementById("adminPrevBtn").addEventListener("click", () => {
    if (adminCurrentPage > 1) {
      adminCurrentPage--;
      loadAdmins();
    }
  });

  document.getElementById("adminNextBtn").addEventListener("click", () => {
    const totalPages = Math.ceil(adminTotalEntries / adminEntriesPerPage);
    if (adminCurrentPage < totalPages) {
      adminCurrentPage++;
      loadAdmins();
    }
  });

  loadAdmins();
}

function loadAdmins() {
  fetch(`${BASE_URL}/api/get-all/admin`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      pageNumber: adminCurrentPage,
      pageSize: adminEntriesPerPage
    })
  })
    .then((res) => res.json())
    .then((data) => {
      const tbody = document.getElementById("adminTableBody");
      tbody.innerHTML = "";

      if (!data.data || data.data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6">No admins found.</td></tr>`;
        return;
      }

      data.data.forEach((admin, index) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${(adminCurrentPage - 1) * adminEntriesPerPage + index + 1}</td>
          <td>${admin.adminId}</td>
          <td>${admin.name}</td>
          <td>${admin.email}</td>
          <td>${admin.branchId}</td>
          <td>${admin.status || "-"}</td>
        `;
        tbody.appendChild(tr);
      });

      adminTotalEntries = data.total || 0;
      updateAdminPagination();
    })
    .catch((err) => {
      console.error("Error fetching admins:", err);
    });
}

function updateAdminPagination() {
  const totalPages = Math.ceil(adminTotalEntries / adminEntriesPerPage);
  const pageNumbers = document.getElementById("adminPageNumbers");
  pageNumbers.innerHTML = "";

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.className = "page-number" + (i === adminCurrentPage ? " active" : "");
    btn.textContent = i;
    btn.addEventListener("click", () => {
      adminCurrentPage = i;
      loadAdmins();
    });
    pageNumbers.appendChild(btn);
  }

  const start = (adminCurrentPage - 1) * adminEntriesPerPage + 1;
  const end = Math.min(adminCurrentPage * adminEntriesPerPage, adminTotalEntries);

  document.getElementById("adminShowingRange").textContent = `${start} to ${end}`;
  document.getElementById("adminTotalEntries").textContent = adminTotalEntries;

  document.getElementById("adminPrevBtn").disabled = adminCurrentPage === 1;
  document.getElementById("adminNextBtn").disabled = adminCurrentPage === totalPages;
}

// Expose globally
window.initSuperAdminAdminList = initSuperAdminAdminList;
