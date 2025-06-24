function initPayform() {
  console.log("✅ Payform initialized");

  const transferForm = document.querySelector(".form-wrapper");
  const passwordModal = document.getElementById("passwordModal");
  const confirmPasswordInput = document.getElementById("confirmPassword");
  const fromAccountInput = document.getElementById("fromAccount");
  const toAccountInput = document.getElementById("toAccount");
  const amountInput = document.getElementById("amount");

  if (!transferForm) {
    console.error("❌ Form not found!");
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
      alert("Fill in all fields.");
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
        alert(verifyData.message || "Invalid password.");
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
      alert(result.message || "Transfer done");

      if (res.ok) {
        transferForm.reset();
        closeModal();
      }
    } catch (err) {
      console.error(err);
      alert("Unexpected error");
    }
  }

  function closeModal() {
    if (passwordModal) passwordModal.style.display = "none";
  }

  window.submitTransfer = submitTransfer;
  window.closeModal = closeModal;
}

window.initPayform = initPayform;
