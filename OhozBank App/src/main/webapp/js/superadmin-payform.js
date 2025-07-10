let passwordModal, confirmPasswordInput;
let currentAction = null;

function initAdminPayform() {
  console.log("Payform Initialized");

  passwordModal = document.getElementById("passwordModal");
  confirmPasswordInput = document.getElementById("confirmPassword");


document.getElementById("transferForm")?.addEventListener("submit", (e) => {
  e.preventDefault();
  openModal(); // triggers password modal
});



document.getElementById("universalForm")?.addEventListener("submit", (e) => {
  e.preventDefault();
  openModal();
});


window.submitTransfer = () => {
  switch (currentAction) {
    case "internal":
    case "external":
      return submitUniversalTransfer();
    case "deposit":
    case "withdraw":
      return submitUniversal(currentAction);
  }
};



  window.closeModal = closeModal;
}

function openModal() {
  passwordModal.style.display = "flex";
  confirmPasswordInput.value = "";
}

function closeModal() {
  passwordModal.style.display = "none";
}

//function switchForm(formId, button) {
//  const allForms = document.querySelectorAll(".form-wrapper");
//  const allButtons = document.querySelectorAll(".transfer-nav .nav-btn");
//
//  allForms.forEach(form => {
//    form.style.display = "none";
//    form.style.opacity = "0";
//  });
//
//  allButtons.forEach(btn => btn.classList.remove("active"));
//
//  const targetForm = document.getElementById(formId);
//  if (targetForm) {
//    targetForm.style.display = "block";
//    setTimeout(() => (targetForm.style.opacity = "1"), 10);
//  }
//
//  if (button) button.classList.add("active");
//}
function switchForm(formId, button) {
  const allForms = document.querySelectorAll(".form-wrapper");
  const allButtons = document.querySelectorAll(".transfer-nav .nav-btn");

  // ✅ Reset all forms before switching
  allForms.forEach(form => form.reset());

  // Hide all forms
  allForms.forEach(form => {
    form.style.display = "none";
    form.style.opacity = "0";
  });

  // Remove active state from all buttons
  allButtons.forEach(btn => btn.classList.remove("active"));

  if (formId === "depositForm" || formId === "withdrawForm") {
    currentAction = formId === "depositForm" ? "deposit" : "withdraw";

    const form = document.getElementById("universalForm");
    form.style.display = "block";
    setTimeout(() => (form.style.opacity = "1"), 10);

    const label = currentAction.charAt(0).toUpperCase() + currentAction.slice(1);
    document.getElementById("universalFormTitle").innerText = label;
    document.getElementById("universalSubmitBtn").innerText = label;
  }

  else if (formId === "internalTransferForm" || formId === "externalTransferForm") {
    currentAction = formId === "internalTransferForm" ? "internal" : "external";

    const isExternal = currentAction === "external";
    const form = document.getElementById("transferForm");
    form.style.display = "block";
    setTimeout(() => (form.style.opacity = "1"), 10);

    document.getElementById("transferFormTitle").innerText = isExternal ? "External Transfer" : "Internal Transfer";
    document.getElementById("transferSubmitBtn").innerText = isExternal ? "External Transfer" : "Internal Transfer";

    // Show/hide external fields and required toggle
    document.getElementById("receiverBankGroup").style.display = isExternal ? "block" : "none";
    document.getElementById("receiverIfscGroup").style.display = isExternal ? "block" : "none";
    document.getElementById("receiverBank").required = isExternal;
    document.getElementById("receiverIfsc").required = isExternal;
  }

  // Activate button
  if (button) button.classList.add("active");
}


async function verifyPassword(password) {
  const res = await fetch(`${BASE_URL}/api/check-password/auth`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ password })
  });
  const data = await res.json();
  return { ok: res.ok, data };
}

async function submitInternalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("fromAccount").value;
  const transactionAccountId = document.getElementById("toAccount").value;
  const amount = parseFloat(document.getElementById("amount").value);

  if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
    return showToast("Please fill in all internal transfer fields", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const res = await fetch(`${BASE_URL}/api/transfer/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, transactionAccountId, amount })
  });
  const result = await res.json();

  if (res.ok) {
    showToast(result.message || "Internal transfer successful!", "success");
    document.getElementById("internalTransferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed", "error");
  }
}

async function submitExternalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("extFromAccount").value;
  const transactionAccountId = document.getElementById("extToAccount").value;
  const receiverBank = document.getElementById("receiverBank").value.trim();
  const receiverIFSC = document.getElementById("receiverIfsc").value.trim();
  const amount = parseFloat(document.getElementById("extAmount").value);

  if (!password || !accountId || !transactionAccountId || !receiverBank || !receiverIFSC || isNaN(amount)) {
    return showToast("Please fill in all external transfer fields", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const res = await fetch(`${BASE_URL}/api/external-transfer/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, transactionAccountId, receiverBank, receiverIFSC, amount })
  });
  const result = await res.json();

  if (res.ok) {
    showToast(result.message || "External transfer successful!", "success");
    document.getElementById("externalTransferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed", "error");
  }
}

async function submitDeposit() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("depositAccount").value;
  const amount = parseFloat(document.getElementById("depositAmount").value);

  if (!password || !accountId || isNaN(amount)) {
    return showToast("Please fill in all deposit fields", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const res = await fetch(`${BASE_URL}/api/deposit/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, amount })
  });
  const result = await res.json();

  if (res.ok) {
    showToast(result.message || "Deposit successful!", "success");
    document.getElementById("depositForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Deposit failed", "error");
  }
}

async function submitWithdraw() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("withdrawAccount").value;
  const amount = parseFloat(document.getElementById("withdrawAmount").value);

  if (!password || !accountId || isNaN(amount)) {
    return showToast("Please fill in all withdraw fields", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const res = await fetch(`${BASE_URL}/api/withdraw/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, amount })
  });
  const result = await res.json();

  if (res.ok) {
    showToast(result.message || "Withdraw successful!", "success");
    document.getElementById("withdrawForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Withdraw failed", "error");
  }
}
async function submitUniversal(type) {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("universalAccount").value;
  const amount = parseFloat(document.getElementById("universalAmount").value);

  if (!password || !accountId || isNaN(amount)) {
    return showToast(`Please fill in all ${type} fields`, "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const url = `${BASE_URL}/api/${type}/transaction`;
  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ accountId, amount })
  });
  const result = await res.json();

  if (res.ok) {
    showToast(result.message || `${type.charAt(0).toUpperCase() + type.slice(1)} successful!`, "success");
    document.getElementById("universalForm").reset();
    closeModal();
  } else {
    showToast(result.message || `${type.charAt(0).toUpperCase() + type.slice(1)} failed`, "error");
  }
}
async function submitUniversalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("transferFromAccount").value;
  const transactionAccountId = document.getElementById("transferToAccount").value;
  const amount = parseFloat(document.getElementById("transferAmount").value);
  const isExternal = currentAction === "external";
  const receiverBank = document.getElementById("receiverBank").value.trim();
  const receiverIFSC = document.getElementById("receiverIfsc").value.trim();

  if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
    return showToast(`Please fill in all required ${currentAction} fields`, "error");
  }

  if (isExternal && (!receiverBank || !receiverIFSC)) {
    return showToast("Please enter external bank details", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const payload = {
    accountId,
    transactionAccountId,
    amount,
    ...(isExternal && { receiverBank, receiverIFSC })
  };

  const endpoint = isExternal ? "external-transfer" : "transfer";

  const res = await fetch(`${BASE_URL}/api/${endpoint}/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  const result = await res.json();

  if (res.ok) {
    showToast(result.message || `${currentAction} transfer successful!`, "success");
    document.getElementById("transferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed", "error");
  }
}
async function submitUniversalTransfer() {
  const password = confirmPasswordInput.value.trim();
  const accountId = document.getElementById("transferFromAccount").value;
  const transactionAccountId = document.getElementById("transferToAccount").value;
  const amount = parseFloat(document.getElementById("transferAmount").value);
  const isExternal = currentAction === "external";
  const receiverBank = document.getElementById("receiverBank").value.trim();
  const receiverIFSC = document.getElementById("receiverIfsc").value.trim();

  if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
    return showToast("Please fill in all required fields", "error");
  }

  if (isExternal && (!receiverBank || !receiverIFSC)) {
    return showToast("Please enter external bank details", "error");
  }

  const { ok, data } = await verifyPassword(password);
  if (!ok || data.status !== "SUCCESS") return showToast(data.message || "Invalid password", "error");

  const payload = {
    accountId,
    transactionAccountId,
    amount,
    ...(isExternal && { receiverBank, receiverIFSC })
  };

  const endpoint = isExternal ? "external-transfer" : "transfer";

  const res = await fetch(`${BASE_URL}/api/${endpoint}/transaction`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  const result = await res.json();

  if (res.ok) {
    showToast(result.message || `${currentAction} transfer successful`, "success");
    document.getElementById("transferForm").reset();
    closeModal();
  } else {
    showToast(result.message || "Transfer failed", "error");
  }
}
ssss


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

window.initPayform = initPayform;
