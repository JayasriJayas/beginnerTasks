

let passwordModal, confirmPasswordInput;
let currentTransferType = "internal"; // default

function initPayform() {
  console.log("Payform initialized");

  // DOM References
  passwordModal = document.getElementById("passwordModal");
  confirmPasswordInput = document.getElementById("confirmPassword");

  populateUserAccounts();

  const transferForm = document.getElementById("transferForm");
  const fromAccountInput = document.getElementById("fromAccount");
  const toAccountInput = document.getElementById("toAccount");
  const amountInput = document.getElementById("amount");

  // Unified submit for both internal/external
  transferForm.addEventListener("submit", (e) => {
    e.preventDefault();

    window.submitTransfer = currentTransferType === "external"
      ? submitExternalTransfer
      : submitInternalTransfer;

    openModal();
  });

  // Expose to HTML buttons
  window.submitTransfer = submitInternalTransfer;
  window.openModal = openModal;
  window.closeModal = closeModal;
  window.switchForm = switchForm;
  window.togglePasswordVisibility = togglePasswordVisibility;
}

function openModal() {
  passwordModal.style.display = "flex";
  confirmPasswordInput.value = "";
}

function closeModal() {
  passwordModal.style.display = "none";
}

function switchForm(type, btn) {
  currentTransferType = type;

  // Update heading and button
  document.getElementById("transferFormTitle").innerText = type === "external" ? "External Transfer" : "Internal Transfer";
  document.getElementById("transferSubmitBtn").innerText = type === "external" ? "External Transfer" : "Internal Transfer";

  // Toggle external-specific fields
  const isExternal = type === "external";
  document.getElementById("receiverBankGroup").style.display = isExternal ? "block" : "none";
  document.getElementById("receiverIfscGroup").style.display = isExternal ? "block" : "none";
  document.getElementById("receiverBank").required = isExternal;
  document.getElementById("receiverIfsc").required = isExternal;

  // Reset form values
  document.getElementById("transferForm").reset();

  // Toggle button active
  document.querySelectorAll(".nav-btn").forEach(b => b.classList.remove("active"));
  btn.classList.add("active");
}

async function submitInternalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("fromAccount").value.trim();
  const transactionAccountId = document.getElementById("toAccount").value.trim();
  const amount = parseFloat(document.getElementById("amount").value);

  if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
    return showToast("Please fill in all the fields.", "error");
  }

  const isVerified = await verifyPassword(password);
  if (!isVerified) return;

  const res = await fetch(`${BASE_URL}/api/transfer/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, transactionAccountId, amount }),
  });

  const result = await res.json();
  if (res.ok) {
    showToast(result.message || "Transfer successful!", "success");
    document.getElementById("transferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed.", "error");
  }
}

async function submitExternalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("fromAccount").value.trim();
  const transactionAccountId = document.getElementById("toAccount").value.trim();
  const receiverBank = document.getElementById("receiverBank").value.trim();
  const receiverIFSC = document.getElementById("receiverIfsc").value.trim();
  const amount = parseFloat(document.getElementById("amount").value);

  if (!password || !accountId || !transactionAccountId || !receiverBank || !receiverIFSC || isNaN(amount)) {
    return showToast("Please fill in all the external fields.", "error");
  }

  const isVerified = await verifyPassword(password);
  if (!isVerified) return;

  const res = await fetch(`${BASE_URL}/api/external-transfer/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, transactionAccountId, receiverBank, receiverIFSC, amount }),
  });

  const result = await res.json();
  if (res.ok) {
    showToast(result.message || "External transfer successful!", "success");
    document.getElementById("transferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed.", "error");
  }
}

async function verifyPassword(password) {
  const res = await fetch(`${BASE_URL}/api/check-password/auth`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ password }),
  });

  const data = await res.json();
  if (!res.ok || data.status !== "SUCCESS") {
    showToast(data.message || "Invalid password.", "error");
    return false;
  }

  return true;
}

function togglePasswordVisibility() {
  const pwd = document.getElementById("confirmPassword");
  const icon = document.getElementById("togglePassword");
  if (pwd.type === "password") {
    pwd.type = "text";
    icon.classList.remove("bx-hide");
    icon.classList.add("bx-show");
  } else {
    pwd.type = "password";
    icon.classList.remove("bx-show");
    icon.classList.add("bx-hide");
  }
}

function populateUserAccounts() {
  const fromAccountSelect = document.getElementById("fromAccount");

  fetch(`${BASE_URL}/api/get-accounts/account`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  })
    .then(res => res.json())
    .then(accounts => {
      const options = accounts.map(acc =>
        `<option value="${acc.accountId}">Account ${acc.accountId}</option>`
      ).join("");
      fromAccountSelect.innerHTML = `<option value="">Select Account</option>` + options;
    })
    .catch(err => {
      console.error("Failed to load accounts:", err);
      showToast("Unable to fetch accounts.", "error");
    });
}

function showToast(message, type = "info") {
  const container = document.getElementById("toast-container");
  if (!container) return;

  const icons = {
    info: "bx bx-info-circle",
    success: "bx bx-check-circle",
    error: "bx bx-error-circle",
    warning: "bx bx-error"
  };

  const toast = document.createElement("div");
  toast.className = `toast show ${type}`;
  toast.innerHTML = `
    <i class="toast-icon ${icons[type] || icons.info}"></i>
    <span class="toast-msg">${message}</span>
    <span class="toast-close" onclick="this.parentElement.remove()">&times;</span>
    <div class="toast-timer"></div>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
	