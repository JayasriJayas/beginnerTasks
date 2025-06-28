let branchCurrentPage = 1;
let branchEntriesPerPage = 10;
let branchTotalEntries = 0;

function initSuperAdminBranchList() {
  const entriesSelect = document.getElementById("branchEntriesPerPage");

  entriesSelect.addEventListener("change", (e) => {
    branchEntriesPerPage = parseInt(e.target.value);
    branchCurrentPage = 1;
    loadBranches();
  });

  document.getElementById("branchPrevBtn").addEventListener("click", () => {
    if (branchCurrentPage > 1) {
      branchCurrentPage--;
      loadBranches();
    }
  });

  document.getElementById("branchNextBtn").addEventListener("click", () => {
    const totalPages = Math.ceil(branchTotalEntries / branchEntriesPerPage);
    if (branchCurrentPage < totalPages) {
      branchCurrentPage++;
      loadBranches();
    }
  });

  loadBranches();
}

function loadBranches() {
  fetch(`${BASE_URL}/api/get-all/branch`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      pageNumber: branchCurrentPage,
      pageSize: branchEntriesPerPage
    })
  })
    .then((res) => res.json())
    .then((data) => {
      const tbody = document.getElementById("branchTableBody");
      tbody.innerHTML = "";

      if (!data.data || data.data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6">No branches found.</td></tr>`;
        return;
      }

      data.data.forEach((branch, index) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${(branchCurrentPage - 1) * branchEntriesPerPage + index + 1}</td>
          <td>${branch.branchId}</td>
          <td>${branch.branchName}</td>
          <td>${branch.location}</td>
          <td>${branch.ifscCode}</td>
          <td>${branch.contact}</td>
        `;
        tbody.appendChild(tr);
      });

      branchTotalEntries = data.total || 0;
      updateBranchPagination();
    })
    .catch((err) => {
      console.error("Error fetching branches:", err);
    });
}

function updateBranchPagination() {
  const totalPages = Math.ceil(branchTotalEntries / branchEntriesPerPage);
  const pageNumbers = document.getElementById("branchPageNumbers");
  pageNumbers.innerHTML = "";

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.className = "page-number" + (i === branchCurrentPage ? " active" : "");
    btn.textContent = i;
    btn.addEventListener("click", () => {
      branchCurrentPage = i;
      loadBranches();
    });
    pageNumbers.appendChild(btn);
  }

  const start = (branchCurrentPage - 1) * branchEntriesPerPage + 1;
  const end = Math.min(branchCurrentPage * branchEntriesPerPage, branchTotalEntries);

  document.getElementById("branchShowingRange").textContent = `${start} to ${end}`;
  document.getElementById("branchTotalEntries").textContent = branchTotalEntries;

  document.getElementById("branchPrevBtn").disabled = branchCurrentPage === 1;
  document.getElementById("branchNextBtn").disabled = branchCurrentPage === totalPages;
}

// ✅ Make available globally
window.initSuperAdminBranchList = initSuperAdminBranchList;
