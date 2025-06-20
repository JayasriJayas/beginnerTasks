	const prevBtns = document.querySelectorAll(".btn-prev");
	const nextBtns = document.querySelectorAll(".btn-next");
	const progress = document.getElementById("progress");
	const formSteps = document.querySelectorAll(".form-step");
	const progressSteps = document.querySelectorAll(".progress-step");
	
	let formStepsNum = 0;
	
	nextBtns.forEach((btn) => {
	  btn.addEventListener("click", (e) => {
	    e.preventDefault();
	    if (validateCurrentStep()) {
	      formStepsNum++;
	      updateFormSteps();
	      updateProgressbar();
	    }
	  });
	});
	
	prevBtns.forEach((btn) => {
	  btn.addEventListener("click", (e) => {
	    e.preventDefault();
	    formStepsNum--;
	    updateFormSteps();
	    updateProgressbar();
	  });
	});
	
	function updateFormSteps() {
	  formSteps.forEach((formStep) => {
	    formStep.classList.remove("form-step-active");
	  });
	  formSteps[formStepsNum].classList.add("form-step-active");
	}
	
	function updateProgressbar() {
	  progressSteps.forEach((progressStep, idx) => {
	    if (idx <= formStepsNum) {
	      progressStep.classList.add("progress-step-active");
	    } else {
	      progressStep.classList.remove("progress-step-active");
	    }
	  });
	
	  const progressActive = document.querySelectorAll(".progress-step-active");
	  progress.style.width = ((progressActive.length - 1) / (progressSteps.length - 1)) * 100 + "%";
	}
	function registerFunction() {
	  document.querySelector(".login-form").style.display = "none";
	  document.querySelector(".register-form").style.display = "block";
	  document.querySelector(".title-login").style.display = "none";
	  document.querySelector(".title-register").style.display = "block";
	  const switchLinks = document.getElementById("switchLinks");
	  if (switchLinks) switchLinks.style.display = "none"; // ✅ Hide on register
	}

	function loginFunction() {
	  document.querySelector(".login-form").style.display = "block";
	  document.querySelector(".register-form").style.display = "none";
	  document.querySelector(".title-login").style.display = "block";
	  document.querySelector(".title-register").style.display = "none";
	  const switchLinks = document.getElementById("switchLinks");
	  if (switchLinks) switchLinks.style.display = "block"; // ✅ Show only on login
	}

	
	function validateCurrentStep() {
	  const currentStep = formSteps[formStepsNum];
	  const inputs = currentStep.querySelectorAll("input, select");
	  let valid = true;
	
	  for (const input of inputs) {
	    const value = input.value.trim();
	    const name = input.name;
	
	    if (!value) {
	      showToast(`${input.labels[0].textContent} is required.`);
	      valid = false;
	      break;
	    }
	
	    if ((name === "email" || name === "username") && !/^\S+@\S+\.\S+$/.test(value)) {
	      showToast("Enter a valid email address.");
	      valid = false;
	      break;
	    }
	
	    if (name === "phone" && !/^\d{10}$/.test(value)) {
	      showToast("Enter a valid 10-digit phone number.");
	      valid = false;
	      break;
	    }
	
	    if (name === "aadharNo" && !/^\d{12}$/.test(value)) {
	      showToast("Enter a valid 12-digit Aadhar number.");
	      valid = false;
	      break;
	    }
	
	    if (name === "panNo" && !/^[A-Z]{5}[0-9]{4}[A-Z]$/.test(value)) {
	      showToast("Enter a valid PAN (e.g. ABCDE1234F).");
	      valid = false;
	      break;
	    }
	
	    if (name === "annualIncome" && (isNaN(value) || Number(value) <= 0)) {
	      showToast("Annual income must be a positive number.");
	      valid = false;
	      break;
	    }
	
	    if (name === "password" && !isStrongPassword(value)) {
	      showToast("Password must be at least 8 characters and include uppercase, lowercase, digit, and special character.");
	      valid = false;
	      break;
	    }
		if (name === "branchId" && isNaN(value)) {
		      showToast("Branch ID must be a number.");
		      valid = false;
		      break;
		 }
	
		if (name === "username" && !/^\S+@\S+\.\S+$/.test(value)) {
			  showToast("Enter a valid email address as username.");
			  valid = false;
			  break;
			}
	
		 if (name === "password" && !isStrongPassword(value)) {
		      showToast("Password must be strong.");
		      valid = false;
		      break;
		    }
		  
	
	   
	    }
	  
	
	  if (formStepsNum === formSteps.length - 1 && valid) {
	    displayPreviewPopup();
	  }
	
	  return valid;
	}
	
	function isStrongPassword(password) {
	  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(password);
	}
	
	function showToast(message) {
	  const toastContainer = document.getElementById("toast-container");
	  if (!toastContainer) return;
	
	  const toast = document.createElement("div");
	  toast.className = "toast show";
	  toast.innerHTML = `
	    <span class="toast-icon">🔔</span>
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
	
	function displayPreviewPopup() {
		
	  const form = document.getElementById("signupForm");
	  const formData = new FormData(form);
	  const overlay = document.createElement("div");
	  overlay.className = "preview-overlay";
	
	  const previewBox = document.createElement("div");
	  previewBox.className = "preview-box";
	  previewBox.innerHTML = `<h3>Confirm Your Details</h3>`;
	
	  formData.forEach((value, key) => {
	    const p = document.createElement("p");
	    p.innerHTML = `<strong>${key}:</strong> ${value}`;
	    previewBox.appendChild(p);
	  });
	
	  const btns = document.createElement("div");
	  btns.className = "btns-group";
	  btns.innerHTML = `
	    <button class="btn btn-prev cancel-preview">Cancel</button>
	    <button class="btn btn-submit-preview">Submit</button>
	  `;
	
	  previewBox.appendChild(btns);
	  overlay.appendChild(previewBox);
	  document.body.appendChild(overlay);
	
	  btns.querySelector(".cancel-preview").addEventListener("click", () => {
	    document.body.removeChild(overlay);
	  });
	
	  btns.querySelector(".btn-submit-preview").addEventListener("click", () => {
	    submitRegistration();
	  });
	}
	
	function submitRegistration() {
	  const form = document.getElementById("signupForm");
	  const formData = new FormData(form);
	  const jsonObject = {};
	  formData.forEach((value, key) => { jsonObject[key] = value; });
	
	  fetch(BASE_URL + "/api/signup/request", {
	    method: "POST",
	    headers: { "Content-Type": "application/json" },
	    body: JSON.stringify(jsonObject)
	  })
	    .then(res => {
	      if (res.ok) return res.json();
	      else throw new Error("Signup failed");
	    })
	    .then(data => {
	      showToast("Signup successful! Wait for admin approval.");
	      document.querySelector(".preview-overlay").remove();
	      setTimeout(() => loginFunction(), 2000);
	    })
	    .catch(err => showToast(err.message));
	}
	
	function backToLogin() {
	  // Show login, hide register
	  document.querySelector(".login-form").style.display = "block";
	  document.querySelector(".register-form").style.display = "none";
	  document.querySelector(".title-login").style.display = "block";
	  document.querySelector(".title-register").style.display = "none";

	  const switchLinks = document.getElementById("switchLinks");
	  if (switchLinks) switchLinks.style.display = "block";

	  // ✅ Remove `form=register` from URL without reloading
	  const url = new URL(window.location);
	  url.searchParams.delete("form");
	  window.history.replaceState({}, document.title, url);
	}

	
	
	document.addEventListener("DOMContentLoaded", () => {
	  const previewBtn = document.querySelector(".btn-preview");
	  if (previewBtn) {
	    previewBtn.addEventListener("click", (e) => {
	      e.preventDefault();
	
	  
	      if (formStepsNum === formSteps.length - 1) {
	        const isValid = validateCurrentStep();
	        if (isValid) {
	          displayPreviewPopup(); 
	        }
	      } else {
	        showToast("Please complete all steps before previewing.", "error");
	      }
	    });
	  }
	});
	
