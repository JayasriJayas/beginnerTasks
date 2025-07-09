

  (function initProfilePanel() {
    const panel = document.getElementById("profileSlide");
    if (!panel) {
      console.warn("profileSlide not found. Retrying...");
      setTimeout(initProfilePanel, 100);
      return;
    }

    console.log("profileSlide loaded");
    fetchProfileData(panel);
	// Close the profile slide when clicking outside the card
	panel.addEventListener("click", function (event) {
	  const isCard = event.target.closest(".profile-slide-card");
	  if (!isCard) {
	    closeProfileSlide();
	  }
	});



    panel.querySelector(".close-btn")?.addEventListener("click", closeProfileSlide);
	const passwordToggles = panel.querySelectorAll('.password-toggle');
	 passwordToggles.forEach(icon => {
	   icon.addEventListener('click', () => {
	     const fieldId = icon.getAttribute('data-target');
	     togglePasswordVisibility(fieldId);  // Toggle password visibility for the corresponding field
	   });
	 });


    panel.querySelector("#changePasswordBtn")?.addEventListener("click", () => {
      openModal("Change Password", `
		<form onsubmit="submitChangePassword(event)">
		<label>Current Password:
		  <input type="password" name="currentPassword" id="currentPassword" required />
		 
		</label><br />
		<label>New Password:
		  <input type="password" name="newPassword" id="newPassword" required />
		  
		</label><br />
		<label>Confirm New Password:
		  <input type="password" name="confirmPassword" id="confirmPassword" required />
		  
		</label><br />

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

//    panel.querySelector("#logoutBtn")?.addEventListener("click", async () => {
//      try {
//        const res = await fetch(BASE_URL + "/api/logout/auth", {
//          method: "GET",
//          credentials: "include",
//        });
//        if (!res.ok) throw new Error("Logout successfull");
//        window.location.href = BASE_URL + "/login.jsp";
//      } catch (err) {
//        showToast("Logout failed.","error");
//        console.error(err);
//      }
//    });


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
    const confirmLogout = confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      const res = await fetch(BASE_URL + "/api/logout/auth", {
        method: "GET",
        credentials: "include",
      });

      if (!res.ok) throw new Error("Logout failed");

      window.location.href = BASE_URL + "/login.jsp";
    } catch (err) {
      showToast("Logout failed.", "error");
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

    // Common fields (all users)
    document.getElementById("editName").value = user.name || "";
    document.getElementById("editEmail").value = user.email || "";
    document.getElementById("editPhone").value = user.phone || "";
    document.getElementById("editGender").value = user.gender || "";

    // Role-specific fields
    if (roleId === 1 || roleId === 2) {
      // Admin/Superadmin: Only phone and gender are editable
      ["editDOB", "editAddress", "editOccupation", "editMaritalStatus", "editIncome", "editAadhar", "editPan"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.closest("li")?.remove(); // Remove these fields from Admin/Superadmin
      });

      // Allow phone and gender fields to be editable
      document.getElementById("editPhone").removeAttribute("readonly");
      document.getElementById("editPhone").removeAttribute("disabled");
      document.getElementById("editGender").removeAttribute("readonly");
      document.getElementById("editGender").removeAttribute("disabled");
      
    } else {
      // User: Populate all customer-specific fields
      document.getElementById("editDOB").value = customer.dob || "";
      document.getElementById("editAddress").value = customer.address || "";
      document.getElementById("editOccupation").value = customer.occupation || "";
      document.getElementById("editMaritalStatus").value = customer.maritalStatus || "";
      document.getElementById("editIncome").value = customer.annualIncome || "";
      document.getElementById("editAadhar").value = customer.aadharNo || "-";
      document.getElementById("editPan").value = customer.panNo || "-";

      // Allow all fields for User to be editable
      ["editDOB", "editAddress", "editOccupation", "editMaritalStatus", "editIncome", "editAadhar", "editPan"].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
          el.removeAttribute("readonly");
          el.removeAttribute("disabled");
        }
      });
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

showToast("Profile updated successfully!", "success");
    const panel = document.getElementById("profileSlide");
    if (panel) await fetchProfileData(panel);

    panel.querySelectorAll("input, textarea").forEach((el) => {
      el.setAttribute("readonly", true);
      if (el.tagName.toLowerCase() !== "textarea") el.setAttribute("disabled", true);
    });
    panel.querySelector("#updateProfileBtn")?.style.setProperty("display", "none");
  } catch (err) {
    console.error("Error updating profile:", err);
    showToast(" " + err.message,);
  }
}


//// Password visibility toggle
//function togglePasswordVisibility(fieldId) {
//  const passwordField = document.getElementById(fieldId);
//  const eyeIcon = document.querySelector(`.password-toggle[data-target="${fieldId}"]`);
//
//  if (!passwordField || !eyeIcon) {
//    console.error("Password field or eye icon not found for id:", fieldId);
//    return;
//  }
//
//  if (passwordField.type === "password") {
//    passwordField.type = "text"; // Show password
//    eyeIcon.classList.remove("bx-show"); // Change to 'bx-hide'
//    eyeIcon.classList.add("bx-hide");
//  } else {
//    passwordField.type = "password"; // Hide password
//    eyeIcon.classList.remove("bx-hide"); // Change to 'bx-show'
//    eyeIcon.classList.add("bx-show");
//  }
//}

async function submitChangePassword(event) {
  event.preventDefault();
  const form = event.target;

  const currentPassword = form.currentPassword.value.trim();
  const newPassword = form.newPassword.value.trim();
  const confirmPassword = form.confirmPassword.value.trim();

  if (newPassword !== confirmPassword) {
    showToast("New password and confirmation do not match.", "error");
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

    showToast("Password changed successfully.", "success");
    closeModal();
  } catch (err) {
    console.error("Error:", err);
    showToast(err.message, "error");
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

