function initPayform() {
  console.log("Payform initialized");

  const transferForm = document.querySelector(".form-wrapper");
  const passwordModal = document.getElementById("passwordModal");
  const confirmPasswordInput = document.getElementById("confirmPassword");
  const fromAccountInput = document.getElementById("fromAccount");
  const toAccountInput = document.getElementById("toAccount");
  const amountInput = document.getElementById("amount");

  if (!transferForm) {
    console.error(" Form not found!");
    return;
  }

  transferForm.addEventListener("submit", (e) => {
    e.preventDefault();
    openModal();
  });

  function openModal() {
    if (passwordModal) {
      passwordModal.style.display = "flex";
      if (confirmPasswordInput) confirmPasswordInput.value = "";
    }
  }

  async function submitTransfer() {
    const password = confirmPasswordInput?.value.trim();
    const accountId = fromAccountInput?.value.trim();
    const transactionAccountId = toAccountInput?.value.trim();
    const amount = parseFloat(amountInput?.value);

    if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
      showToast("Please fill in all the fields.", "error"); // Error Toast
      return;
    }

    try {
      // Step 1: Verify password
      const verifyRes = await fetch(`${BASE_URL}/api/check-password/auth`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ password }),
      });

      const verifyData = await verifyRes.json();
      if (!verifyRes.ok || verifyData.status !== "SUCCESS") {
        showToast(verifyData.message || "Invalid password.", "error"); // Error Toast
        return;
      }

      // Step 2: Transfer
      const res = await fetch(`${BASE_URL}/api/transfer/transaction`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ accountId, transactionAccountId, amount }),
      });

      const result = await res.json();

      if (res.ok) {
        showToast(result.message || "Transfer completed successfully!", "success"); // Success Toast
        transferForm.reset();
        closeModal();
      } else {
        showToast(result.message || "Transfer failed. Please try again.", "error"); // Error Toast
      }

    } catch (err) {
      console.error("Error:", err);
      showToast("Unexpected error occurred. Please try again.", "error"); // Error Toast
    }
  }

  function closeModal() {
    if (passwordModal) passwordModal.style.display = "none";
  }

  window.submitTransfer = submitTransfer;
  window.closeModal = closeModal;
}

window.initPayform = initPayform;

// Toast Function (Make sure this function is in the global scope or imported)
function showToast(message, type = "info") {
  const toastContainer = document.getElementById("toast-container");
  if (!toastContainer) return;

  const icons = {
    info: "bx bx-info-circle",
    success: "bx bx-check-circle",
    error: "bx bx-error-circle",
    warning: "bx bx-error"
  };

  const iconClass = icons[type] || icons.info;

  const toast = document.createElement("div");
  toast.className = `toast show ${type}`;
  toast.innerHTML = `
    <i class="toast-icon ${iconClass}"></i>
    <span class="toast-msg">${message}</span>
    <span class="toast-close" onclick="this.parentElement.remove()">&times;</span>
    <div class="toast-timer"></div>
  `;

  toastContainer.appendChild(toast);

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
