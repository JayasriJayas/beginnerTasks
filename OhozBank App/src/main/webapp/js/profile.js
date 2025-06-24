(function initProfilePanel() {
  const panel = document.getElementById("profileSlide");

  if (!panel) {
    console.warn(" profileSlide not found. Retrying...");
    setTimeout(initProfilePanel, 100);
    return;
  }

  console.log(" profileSlide loaded");

  // Load user data and inject image/name
  fetchProfileData(panel);

  // Close button
  panel.querySelector(".close-btn")?.addEventListener("click", () => {
    closeProfileSlide();
  });

  // Change Password
  panel.querySelector("#changePasswordBtn")?.addEventListener("click", () => {
	openModal(" Change Password", `
	  <form onsubmit="submitChangePassword(event)">
	    <label>Current Password:
	      <input type="password" name="currentPassword" required />
	    </label><br />
	    <label>New Password:
	      <input type="password" name="newPassword" required />
	    </label><br />
	    <label>Confirm New Password:
	      <input type="password" name="confirmPassword" required />
	    </label><br />
	    <div class="modal-profile-footer" style="margin-top: 1rem; text-align: right;">
		<button type="submit" class="profile-btn"><i class='bx bx-lock'></i> Change</button>
		<button type="button" class="profile-btn logout-btn" onclick="closeModal()"><i class='bx bx-x'></i> Cancel</button>

	    </div>
	  </form>
	`);

  });

  // Function to open the modal with dynamic content
  function openModal(title, bodyContent) {
    const modalContent = `
      <div class="modal-box" style="width: 400px; padding: 20px; border-radius: 10px; background-color: #fff; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
        <h3 style="font-size: 1.5rem; color: #2D3E4E; margin-bottom: 20px;">${title}</h3>
        ${bodyContent}
      </div>
    `;
    document.getElementById("modalOverlay").innerHTML = modalContent;
    document.getElementById("modalOverlay").classList.remove("hidden");
  }

  // View Profile Button
  panel.querySelector("#viewProfileBtn")?.addEventListener("click", async () => {
    const { user, customer } = await fetchUserProfileInfo();
    openModal("Profile Info", renderProfileView(user, customer));
  });

  // Render Profile View (Read-only)
  function renderProfileView(user, customer) {
    return `
      <div class="modal-profile-header">
        <h2>${user.name}</h2>
      </div>

      <div class="modal-profile-info">
        <div class="profile-info-item"><strong>Name:</strong> <span>${user.name}</span></div>
        <div class="profile-info-item"><strong>Username:</strong> <span>${user.username}</span></div>
        <div class="profile-info-item"><strong>Email:</strong> <span>${user.email}</span></div>
        <div class="profile-info-item"><strong>Phone:</strong> <span>${user.phone}</span></div>
        <div class="profile-info-item"><strong>Gender:</strong> <span>${user.gender}</span></div>
        <div class="profile-info-item"><strong>DOB:</strong> <span>${customer.dob}</span></div>
        <div class="profile-info-item"><strong>Occupation:</strong> <span>${customer.occupation}</span></div>
        <div class="profile-info-item"><strong>Annual Income:</strong> <span>₹${(customer.annualIncome || 0).toLocaleString()}</span></div>
        <div class="profile-info-item"><strong>Aadhar:</strong> <span>${customer.aadharNo}</span></div>
        <div class="profile-info-item"><strong>PAN:</strong> <span>${customer.panNo}</span></div>
        <div class="profile-info-item"><strong>Address:</strong> <span>${customer.address}</span></div>
        <div class="profile-info-item"><strong>Marital Status:</strong> <span>${customer.maritalStatus}</span></div>
      </div>

      <div class="modal-profile-footer">
        <button class="profile-btn" onclick="switchToEditProfile(${user.id}, ${customer.id})">
          <i class="bx bx-edit"></i> Edit
        </button>
        <button class="profile-btn logout-btn" onclick="closeModal()"><i class='bx bx-x'></i> Cancel</button>
      </div>
    `;
  }

  // Switch to Edit Profile (This is the key function that switches to edit mode)
  async function switchToEditProfile(userId, customerId) {
    // Fetch current user and customer details based on their ID
    const { user, customer } = await fetchUserProfileInfo(userId, customerId);
	console.log("Opening modal with form...");  // Add this to verify modal is being opened

    // Open the modal with the editable form
    openModal("Edit Profile", renderProfileEditForm(user, customer));
  }
  window.switchToEditProfile = switchToEditProfile;

  // Render Profile Edit Form (Editable fields)
  function renderProfileEditForm(user, customer) {
    return `
      <form onsubmit="submitProfileUpdate(event)">
        <label>Name: <input name="name" value="${user.name || ''}" required /></label><br />
        <label>Phone: <input name="phone" value="${user.phone || ''}" required /></label><br />
        <label>Email: <input name="email" value="${user.email || ''}" required /></label><br />
        <label>Gender:
          <select name="gender" required>
            <option value="MALE" ${user.gender === "MALE" ? "selected" : ""}>Male</option>
            <option value="FEMALE" ${user.gender === "FEMALE" ? "selected" : ""}>Female</option>
            <option value="OTHER" ${user.gender === "OTHER" ? "selected" : ""}>Other</option>
          </select>
        </label><br />
        <label>Address: <input name="address" value="${customer.address || ''}" /></label><br />
        <label>DOB: <input name="dob" value="${customer.dob || ''}" /></label><br />
        
        <!-- Read-only fields -->
        <fieldset disabled style="opacity: 0.6;">
          <label>Username: <input value="${user.username || ''}" /></label><br />
          <label>Aadhar: <input value="${customer.aadharNo || ''}" /></label><br />
          <label>PAN: <input value="${customer.panNo || ''}" /></label><br />
        </fieldset>
        
        <div  class="modal-profile-footer" style="margin-top:1rem; text-align:right;">
          <button type="submit" class="profile-btn"><i class='bx bx-save'></i> Save</button>
          <button type="button" class="profile-btn logout-btn" onclick="closeModal()"><i class='bx bx-x'></i> Cancel</button>
        </div>
      </form>
    `;
  }


  // Submit Profile Update
window.submitProfileUpdate=  async function submitProfileUpdate(event) {

    event.preventDefault(); // Prevent form from submitting the traditional way
	console.log("Form submitted!");
    const form = event.target; // The form element
    const formData = new FormData(form); // Collect form data
	console.log("entered");
    // Convert FormData to an object for easy access
    const updatedData = {
      name: formData.get("name").trim(),
      phone: Number(formData.get("phone").trim()),
      email: formData.get("email").trim(),
      gender: formData.get("gender"),
      address: formData.get("address").trim(),
      dob: formData.get("dob").trim(),
    };
	console.log("data:", updatedData); // Verify the updated data before sending it to the backend

    try {
      const res = await fetch(BASE_URL + "/api/edit/user", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(updatedData),
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to update profile.");
      }

      // 1. Close modal
      closeModal();

      // 2. Refresh profile panel (name/image)
      const panel = document.getElementById("profileSlide");
      if (panel) {
        await fetchProfileData(panel); // re-use the original function
      }

      // 3. Notify user
      alert("Profile updated successfully!");
    } catch (err) {
      console.error("Error updating profile:", err);
      alert(" " + err.message);
    }
  }


  // Close Modal
  function closeModal() {
    const modalOverlay = document.getElementById("modalOverlay");
    modalOverlay.classList.add("hidden");
    modalOverlay.innerHTML = ''; // Clear the modal content
  }


  // Logout
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

// 🔄 Fetch Profile Image + Name
// Fetch Profile Image + User Details
async function fetchProfileData(panel) {
  try {
    const res = await fetch(BASE_URL + "/api/profile/user", {
      method: "POST",
      credentials: "include", // Assuming you are using session/cookies for authentication
    });

    if (!res.ok) throw new Error("Failed to load profile");

    const data = await res.json();
    console.log("Profile data:", data);

    const user = data.user || {};
    const gender = (user.gender || "MALE").toUpperCase();

    const imageUrl = gender === "FEMALE"
      ? `${BASE_URL}/assets/girl.png`
      : `${BASE_URL}/assets/male.png`;

    const image = panel.querySelector("#userImage");
    const name = panel.querySelector("#userName");

    // Set the profile image and name dynamically
    if (image) image.src = imageUrl;
    if (name) name.innerText = user.name || "User";

    // Update profile info dynamically
    document.getElementById("userNameValue").innerText = user.name ;
    document.getElementById("userEmail").innerText = user.email ;
    document.getElementById("userPhone").innerText = user.phone ;
    document.getElementById("userGender").innerText = user.gender ;
    document.getElementById("userDOB").innerText = data.customer.dob ;


  } catch (err) {
    console.error("⚠️ Error loading profile:", err);
    panel.innerHTML = `<p style="padding: 1rem;">⚠️ Unable to load profile info.</p>`;
  }
}

//  Fetch full profile for view
async function fetchUserProfileInfo() {
  const res = await fetch(BASE_URL + "/api/profile/user", {
    method: "POST",
    credentials: "include"
  });
  if (!res.ok) throw new Error("Error fetching user data");

  const data = await res.json();
  return {
    user: data.user || {},
    customer: data.customer || {}
  };
}

async function submitChangePassword(event) {
  event.preventDefault();
  const form = event.target;

  const currentPassword = form.currentPassword.value.trim();
  const newPassword = form.newPassword.value.trim();
  const confirmPassword = form.confirmPassword.value.trim();

  if (newPassword !== confirmPassword) {
    alert(" New password and confirmation do not match.");
    return;
  }

  const payload = {
    currentPassword,
    newPassword,
    confirmPassword
  };

  try {
    const res = await fetch(BASE_URL + "/api/change-password/auth", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const errData = await res.json();
      throw new Error(errData.message || "Failed to change password.");
    }

    alert(" Password changed successfully.");
    closeModal();

  } catch (err) {
    console.error(" Error:", err);
    alert(" " + err.message);
  }
}


// Close the slide panel
function closeProfileSlide() {
  const panel = document.getElementById("profileSlide");
  if (!panel) return;
  panel.classList.remove("show");
}

window.closeProfileSlide = closeProfileSlide; // expose for inline use if needed
