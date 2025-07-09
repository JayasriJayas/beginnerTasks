let passwordModal, confirmPasswordInput;

function initPayform() {
  console.log("Payform initialized");

  populateUserAccounts(); // 🔄 Call this once during init

  const transferForm = document.getElementById("internalTransferForm");
  const externalForm = document.getElementById("externalTransferForm");

  passwordModal = document.getElementById("passwordModal");
  confirmPasswordInput = document.getElementById("confirmPassword");

  const fromAccountInput = document.getElementById("fromAccount");
  const toAccountInput = document.getElementById("toAccount");
  const amountInput = document.getElementById("amount");

  // Internal Transfer
  transferForm.addEventListener("submit", (e) => {
    e.preventDefault();
    window.submitTransfer = submitTransfer;
    openModal();
  });

  // External Transfer
  externalForm.addEventListener("submit", (e) => {
    e.preventDefault();
    window.submitTransfer = submitExternalTransfer;
    openModal();
  });

  function openModal() {
    if (passwordModal) {
      passwordModal.style.display = "flex";
      confirmPasswordInput.value = ""; // Clear previous password
    }
  }

  async function submitTransfer() {
    const password = confirmPasswordInput.value.trim();
    const accountId = fromAccountInput.value.trim();
    const transactionAccountId = toAccountInput.value.trim();
    const amount = parseFloat(amountInput.value);

    if (!password || !accountId || !transactionAccountId || isNaN(amount)) {
      showToast("Please fill in all the fields.", "error");
      return;
    }

    try {
      const verifyRes = await fetch(`${BASE_URL}/api/check-password/auth`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ password }),
      });

      const verifyData = await verifyRes.json();
      if (!verifyRes.ok || verifyData.status !== "SUCCESS") {
        showToast(verifyData.message || "Invalid password.", "error");
        return;
      }

      const res = await fetch(`${BASE_URL}/api/transfer/transaction`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ accountId, transactionAccountId, amount }),
      });

      const result = await res.json();

      if (res.ok) {
        showToast(result.message || "Transfer completed successfully!", "success");
        transferForm.reset();
        closeModal();
      } else {
        showToast(result.message || "Transfer failed. Please try again.", "error");
      }

    } catch (err) {
      console.error("Error:", err);
      showToast("Unexpected error occurred. Please try again.", "error");
    }
  }

  function submitExternalTransfer() {
    const password = confirmPasswordInput.value.trim();
    const accountId = parseInt(document.getElementById("extFromAccount").value.trim());
    const transactionAccountId = parseInt(document.getElementById("extToAccount").value.trim());
    const receiverBank = document.getElementById("receiverBank").value.trim();
    const receiverIFSC = document.getElementById("receiverIfsc").value.trim();
    const amount = parseFloat(document.getElementById("extAmount").value.trim());

    const payload = { accountId, transactionAccountId, receiverBank, receiverIFSC, amount };
    console.log("External Transfer Input:", { password, ...payload });

    if (
      !password ||
      !accountId || isNaN(accountId) ||
      !transactionAccountId || isNaN(transactionAccountId) ||
      !receiverBank ||
      !receiverIFSC ||
      isNaN(amount)
    ) {
      showToast("Please fill in all external transfer fields.", "error");
      return;
    }

    // Step 1: Verify password
    fetch(`${BASE_URL}/api/check-password/auth`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ password }),
    })
      .then((verifyRes) => verifyRes.json().then((data) => ({ ok: verifyRes.ok, data })))
      .then(({ ok, data }) => {
        if (!ok || data.status !== "SUCCESS") {
          showToast(data.message || "Invalid password.", "error");
          throw new Error("Password verification failed");
        }

        // Step 2: Submit external transfer
        console.log("Sending POST to:", `${BASE_URL}/api/external-transfer/transaction`);
        console.log("Payload:", payload);

        return fetch(`${BASE_URL}/api/external-transfer/transaction`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify(payload),
        });
      })
      .then((res) => res.json().then((data) => ({ ok: res.ok, data })))
      .then(({ ok, data }) => {
        if (ok) {
          showToast(data.message || "External transfer successful!", "success");
          document.getElementById("externalTransferForm").reset();
          closeModal();
        } else {
          showToast(data.message || "External transfer failed.", "error");
        }
      })
      .catch((err) => {
        console.error("Error:", err);
        showToast("Unexpected error in external transfer.", "error");
      });
  }



 

  function closeModal() {
    if (passwordModal) passwordModal.style.display = "none";
  }
  function switchForm(formIdToShow, btn) {
    const forms = document.querySelectorAll(".form-wrapper");
    const buttons = document.querySelectorAll(".nav-btn");

    // Hide all forms
    forms.forEach(form => {
      form.style.opacity = "0";
      setTimeout(() => {
        form.style.display = "none";
      }, 300);
    });

    // Show the selected form
    const targetForm = document.getElementById(formIdToShow);
    if (targetForm) {
      setTimeout(() => {
        targetForm.style.display = "block";
        setTimeout(() => {
          targetForm.style.opacity = "1";
        }, 50);
      }, 300);
    }

    // Update nav button styles
    buttons.forEach(button => button.classList.remove("active"));
    btn.classList.add("active");
  }

  

  window.submitTransfer = submitTransfer;
  window.closeModal = closeModal;
  window.switchForm = switchForm;

}

//  Populates both dropdowns
function populateUserAccounts() {
  const fromAccountSelect = document.getElementById("fromAccount");
  const extFromAccountSelect = document.getElementById("extFromAccount");

  fetch(`${BASE_URL}/api/get-accounts/account`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to fetch accounts");
      return res.json();
    })
    .then((accounts) => {
      const options = accounts.map(acc =>
        `<option value="${acc.accountId}">Account ${acc.accountId}</option>`
      ).join("");

      if (fromAccountSelect) {
        fromAccountSelect.innerHTML = `<option value="">Select Account</option>` + options;
      }
      if (extFromAccountSelect) {
        extFromAccountSelect.innerHTML = `<option value="">Select Account</option>` + options;
      }
    })
    .catch((err) => {
      console.error("Error loading user accounts:", err);
      showToast("Unable to load account list", "error");
    });
}
function togglePasswordVisibility() {
  const passwordField = document.getElementById("confirmPassword");
  const toggleIcon = document.getElementById("togglePassword");

  if (passwordField.type === "password") {
    passwordField.type = "text";
    toggleIcon.classList.remove("bx-hide");
    toggleIcon.classList.add("bx-show");
  } else {
    passwordField.type = "password";
    toggleIcon.classList.remove("bx-show");
    toggleIcon.classList.add("bx-hide");
  }
}

