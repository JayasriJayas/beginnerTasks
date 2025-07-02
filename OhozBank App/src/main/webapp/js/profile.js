
(function initProfilePanel() {
  const panel = document.getElementById("profileSlide");
  if (!panel) {
    console.warn("profileSlide not found. Retrying...");
    setTimeout(initProfilePanel, 100);
    return;
  }

  console.log("profileSlide loaded");
  fetchProfileData(panel);


  panel.querySelector(".close-btn")?.addEventListener("click", closeProfileSlide);


  panel.querySelector("#changePasswordBtn")?.addEventListener("click", () => {
    openModal("Change Password", `
      <form onsubmit="submitChangePassword(event)">
        <label>Current Password:<input type="password" name="currentPassword" required /></label><br />
        <label>New Password:<input type="password" name="newPassword" required /></label><br />
        <label>Confirm New Password:<input type="password" name="confirmPassword" required /></label><br />
        <div class="modal-profile-footer" style="text-align: right; margin-top: 1rem;">
          <button type="submit" class="profile-btn"><i class='bx bx-lock'></i> Change</button>
          <button type="button" class="profile-btn logout-btn" onclick="closeModal()"><i class='bx bx-x'></i> Cancel</button>
        </div>
      </form>
    `);
  });

  panel.querySelector("#editProfileBtn")?.addEventListener("click", () => {
    panel.querySelectorAll("input, textarea").forEach((el) => {
      if (!["editEmail", "editAadhar", "editPan"].includes(el.id)) {
        el.removeAttribute("readonly");
        el.removeAttribute("disabled");
      }
    });
    panel.querySelector("#updateProfileBtn")?.style.setProperty("display", "block");
  });


  panel.querySelector("#updateProfileBtn")?.addEventListener("click", submitProfileUpdate);

  panel.querySelector("#logoutBtn")?.addEventListener("click", async () => {
    try {
      const res = await fetch(BASE_URL + "/api/logout/auth", {
        method: "GET",
        credentials: "include",
      });
      if (!res.ok) throw new Error("Logout failed");
      window.location.href = BASE_URL + "/login.jsp";
    } catch (err) {
      alert("Logout failed.");
      console.error(err);
    }
  });
})();

async function fetchProfileData(panel) {
  try {
    const res = await fetch(BASE_URL + "/api/profile/user", {
      method: "POST",
      credentials: "include",
    });

    if (!res.ok) throw new Error("Failed to load profile");
    const data = await res.json();
    const user = data.user || {};
    const customer = data.customer || {};
    const roleId = user.roleId;

    const gender = (user.gender || "MALE").toUpperCase();
    const imageUrl = gender === "FEMALE"
      ? `${BASE_URL}/assets/girl.png`
      : `${BASE_URL}/assets/male.png`;

    panel.querySelector("#userImage").src = imageUrl;
    panel.querySelector("#userName").innerText = user.name || "User";

    // Common fields
    document.getElementById("editName").value = user.name || "";
    document.getElementById("editEmail").value = user.email || "";
    document.getElementById("editPhone").value = user.phone || "";
    document.getElementById("editGender").value = user.gender || "";

    if (roleId === 1 || roleId === 2) {
      // Admin/Superadmin: Remove extra fields
      ["editDOB", "editAddress", "editOccupation", "editMaritalStatus", "editIncome", "editAadhar", "editPan"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.closest("li")?.remove();
      });
    } else {
      // User: populate customer-only fields
      document.getElementById("editDOB").value = customer.dob || "";
      document.getElementById("editAddress").value = customer.address || "";
      document.getElementById("editOccupation").value = customer.occupation || "";
      document.getElementById("editMaritalStatus").value = customer.maritalStatus || "";
      document.getElementById("editIncome").value = customer.annualIncome || "";
      document.getElementById("editAadhar").value = customer.aadharNo || "-";
      document.getElementById("editPan").value = customer.panNo || "-";
    }
  } catch (err) {
    console.error("Error loading profile:", err);
    panel.innerHTML = `<p style="padding: 1rem; color:red;">⚠️ Unable to load profile info.</p>`;
  }
}

async function submitProfileUpdate(event) {
  event.preventDefault?.();
  const formData = {
    name: document.getElementById("editName").value.trim(),
    phone: document.getElementById("editPhone").value.trim(),
    gender: document.getElementById("editGender").value.trim(),
    dob: document.getElementById("editDOB")?.value.trim() || null,
    address: document.getElementById("editAddress")?.value.trim() || null,
    occupation: document.getElementById("editOccupation")?.value.trim() || null,
    maritalStatus: document.getElementById("editMaritalStatus")?.value.trim() || null,
    annualIncome: document.getElementById("editIncome")?.value.trim() || null
  };

  try {
    const res = await fetch(BASE_URL + "/api/edit/user", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(formData)
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || "Failed to update profile.");
    }

    alert("Profile updated successfully!");
    const panel = document.getElementById("profileSlide");
    if (panel) await fetchProfileData(panel);

    panel.querySelectorAll("input, textarea").forEach((el) => {
      el.setAttribute("readonly", true);
      if (el.tagName.toLowerCase() !== "textarea") el.setAttribute("disabled", true);
    });
    panel.querySelector("#updateProfileBtn")?.style.setProperty("display", "none");
  } catch (err) {
    console.error("Error updating profile:", err);
    alert(" " + err.message);
  }
}

async function submitChangePassword(event) {
  event.preventDefault();
  const form = event.target;

  const currentPassword = form.currentPassword.value.trim();
  const newPassword = form.newPassword.value.trim();
  const confirmPassword = form.confirmPassword.value.trim();

  if (newPassword !== confirmPassword) {
    alert("New password and confirmation do not match.");
    return;
  }

  const payload = { currentPassword, newPassword, confirmPassword };

  try {
    const res = await fetch(BASE_URL + "/api/change-password/auth", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || "Failed to change password.");
    }

    alert("Password changed successfully.");
    closeModal();
  } catch (err) {
    console.error("Error:", err);
    alert(" " + err.message);
  }
}

function closeProfileSlide() {
  const panel = document.getElementById("profileSlide");
  panel?.classList.remove("show");
}

function closeModal() {
  const modalOverlay = document.getElementById("modalOverlay");
  modalOverlay.classList.add("hidden");
  modalOverlay.innerHTML = '';
}

function openModal(title, bodyHTML) {
  const modalContent = `
    <div class="modal-box" style="width: 400px; padding: 20px; border-radius: 10px; background-color: #fff; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
      <h3 style="font-size: 1.5rem; color: #2D3E4E; margin-bottom: 20px;">${title}</h3>
      ${bodyHTML}
    </div>
  `;
  const overlay = document.getElementById("modalOverlay");
  overlay.innerHTML = modalContent;
  overlay.classList.remove("hidden");
}
