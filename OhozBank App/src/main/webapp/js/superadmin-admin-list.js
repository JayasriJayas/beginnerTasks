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
        tbody.innerHTML = `<tr><td colspan="9">No admins found.</td></tr>`;
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
          <td>${admin.phone || "-"}</td>
          <td>${formatDateTime(admin.createdAt)}</td>
          <td>${admin.modifiedBy}</td>
          <td>${formatDateTime(admin.modifiedAt)}</td>
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

function formatDateTime(ts) {
  if (!ts || isNaN(ts)) return "-";
  const date = new Date(Number(ts));
  const dateStr = date.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
  const timeStr = date.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: true });
  return `${dateStr}<br><span style="font-size: 12px; color: #555;">${timeStr}</span>`;
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
// Open Modal
function openAddAdminModal() {
  const modal = document.getElementById("addAdminModal");
  modal.classList.remove("hidden");
}

// Close Modal
function closeAddAdminModal() {
  const modal = document.getElementById("addAdminModal");
  modal.classList.add("hidden");
}

document.getElementById("addAdminForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  const name = document.getElementById("name").value;
  const email = document.getElementById("email").value;
  const phone = document.getElementById("phone").value;
  const gender = document.getElementById("gender").value;
  const branchId = document.getElementById("branchId").value;

  const adminData = {
    username,
    password,
    name,
    email,
    phone,
    gender,
    branchId
  };

  fetch(`${BASE_URL}/api/add-admin/admin`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(adminData)
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.status === "SUCCESS") {
        alert("Admin added successfully");
        closeAddAdminModal();
        loadAdmins(); 
      } else {
        alert("Failed to add admin");
      }
    })
    .catch((err) => {
      console.error("Error adding admin:", err);
      alert("An error occurred while adding the admin");
    });
});

window.initSuperAdminAdminList = initSuperAdminAdminList;
