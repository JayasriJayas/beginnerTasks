console.log("✅ payform.js script loaded");

const contextPath = BASE_URL || "";

const transferForm = document.querySelector(".form-wrapper");
const passwordModal = document.getElementById("passwordModal");
const confirmPasswordInput = document.getElementById("confirmPassword");
const fromAccountInput = document.getElementById("fromAccount");
const toAccountInput = document.getElementById("toAccount");
const amountInput = document.getElementById("amount");

// Attach event listener to form
if (transferForm) {
  transferForm.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent form from submitting normally
    openModal();
  });
} else {
  console.error("❌ Form with class 'form-wrapper' not found.");
}

function openModal() {
  if (passwordModal) {
    passwordModal.style.display = "flex";
    if (confirmPasswordInput) {
      confirmPasswordInput.value = "";
    }
  }
}

function closeModal() {
  if (passwordModal) {
    passwordModal.style.display = "none";
  }
}

async function submitTransfer() {
  const password = confirmPasswordInput?.value.trim();
  const accountId = fromAccountInput?.value.trim();
  const transactionAccountId = toAccountInput?.value.trim();
  const amount = parseFloat(amountInput?.value);

  if (!password) {
    alert("Please enter your password.");
    return;
  }

  if (!accountId || !transactionAccountId || isNaN(amount)) {
    alert("Please fill in all transfer details.");
    closeModal();
    return;
  }

  try {
    // Step 1: Verify password
    const verifyRes = await fetch(`${contextPath}/api/check-password/auth`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ password }),
    });

    const verifyData = await verifyRes.json();

    if (!verifyRes.ok || verifyData.status !== "SUCCESS") {
      alert(verifyData.message || "Password verification failed.");
      closeModal();
      return;
    }

    alert("✅ Password verified successfully!");
    console.log("➡️ Password verification response:", verifyData);
    console.log("🔁 Proceeding to transfer...");

    // Step 2: Perform fund transfer
    const transferRes = await fetch(`${contextPath}/api/transfer/transaction`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ accountId, transactionAccountId, amount }),
    });

    console.log("🔢 Sending transfer data:", { accountId, transactionAccountId, amount });

    const transferData = await transferRes.json();

    if (transferRes.ok) {
      alert(transferData.message || "✅ Funds transferred successfully!");
      closeModal();
      transferForm.reset();
    } else {
      alert(transferData.message || "❌ Fund transfer failed.");
    }
  } catch (error) {
    console.error("Transfer error:", error);
    alert("An unexpected error occurred. Please try again.");
  }
}

window.submitTransfer = submitTransfer;
window.closeModal = closeModal;
