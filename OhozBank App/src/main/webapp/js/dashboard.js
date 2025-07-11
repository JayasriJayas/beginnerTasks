
let currentPage = null;

document.addEventListener("DOMContentLoaded", () => {
	
	const hashAction = window.location.hash.replace("#", "");

	if (hashAction) {
	 
	  loadContentByAction(hashAction, false);  
	} else {
	 
	  loadDashboardContent(userRole);
	}
  const sidebar = document.getElementById("sidebar");
  const toggleBtn = document.getElementById("sidebarToggleBtn");
  const toggleIcon = toggleBtn?.querySelector("i");



  if (toggleBtn && sidebar && toggleIcon) {
    toggleBtn.addEventListener("click", () => {
      sidebar.classList.toggle("expanded");
      toggleIcon.classList.toggle("bx-chevron-right");
      toggleIcon.classList.toggle("bx-chevron-left");
    });
  }

  const profileIcon = document.getElementById("profileIcon");
  if (profileIcon) {
    profileIcon.addEventListener("click", async () => {
      const existing = document.getElementById("profileSlide");
      if (existing) {
        existing.classList.add("show");
        return;
      }

      const res = await fetch(`${BASE_URL}/partials/profile.html`);
      const html = await res.text();

      const wrapper = document.createElement("div");
      wrapper.innerHTML = html;
      document.body.appendChild(wrapper.firstElementChild);

      loadCSS(`${BASE_URL}/css/profile.css`);
      loadScript(`${BASE_URL}/js/profile.js`);
    });
  }


  loadDashboardContent(userRole);

  document.querySelectorAll(".menu-item").forEach((link) => {
    const action = link.getAttribute("data-action");

    link.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".menu-item").forEach((l) => l.classList.remove("active"));
      link.classList.add("active");

      loadContentByAction(action);
    });
  });
});

// Load Dashboard Content Based on User Role
function loadDashboardContent(role) {
  switch(role) {
    case "ADMIN":
      loadPartial(
        `${BASE_URL}/partials/admin-dashboard.html`,
        `${BASE_URL}/css/admin-dashboard.css`,
        `${BASE_URL}/js/admin-dashboard.js`,
        () => initAdminDashboard()
      );
      break;

    case "SUPERADMIN":
      loadPartial(
        `${BASE_URL}/partials/superadmin-dashboard.html`,
        `${BASE_URL}/css/superadmin-dashboard.css`,
        `${BASE_URL}/js/superadmin-dashboard.js`,
        () => initSuperadminDashboard()
      );
	 
      break;

    default:
      loadPartial(
        `${BASE_URL}/partials/dashboard-content.html`,
        `${BASE_URL}/css/dashboard-content.css`,
        `${BASE_URL}/js/dashboard-content.js`,
        () => initDashboardContent()
      );
  }
}

//  Load Content Based on Sidebar Menu Action
function loadContentByAction(action, pushToHistory = true) {
	if (currentPage === action) {
	    console.log(`Already on '${action}', skipping reload.`);
	    return;
	  }

	  currentPage = action;
	  if (pushToHistory) {
	      history.pushState({ action }, "", `#${action}`);
	    }

	
  switch(action) {
    case "dashboard-admin":
      loadPartial(
        `${BASE_URL}/partials/admin-dashboard.html`,
        `${BASE_URL}/css/admin-dashboard.css`,
        `${BASE_URL}/js/admin-dashboard.js`,
        () => initAdminDashboard()
      );
      break;

    case "dashboard-superadmin":
      loadPartial(
        `${BASE_URL}/partials/superadmin-dashboard.html`,
        `${BASE_URL}/css/superadmin-dashboard.css`,
        `${BASE_URL}/js/superadmin-dashboard.js`,
        () => initSuperadminDashboard()
      );
      break;
	  
	  case "superadmin-request-list":
	      loadPartial(
	        `${BASE_URL}/partials/superadmin-request-list.html`,
	        `${BASE_URL}/css/superadmin-request-list.css`,
	        `${BASE_URL}/js/superadmin-request-list.js`,
	        () => initSuperAdminRequestList()
	      );
	  break;
	  case "superadmin-accountrequest-list":
	        loadPartial(
	          `${BASE_URL}/partials/superadmin-accountrequest-list.html`,
	          `${BASE_URL}/css/superadmin-accountrequest-list.css`,
	          `${BASE_URL}/js/superadmin-accountrequest-list.js`,
	          () => initSuperAdminAccountRequestList()
	        );
	    break;
	 case "admin-list":
			      loadPartial(
			        `${BASE_URL}/partials/superadmin-admin-list.html`,
			        `${BASE_URL}/css/superadmin-admin-list.css`,
			        `${BASE_URL}/js/superadmin-admin-list.js`,
			        () => initSuperAdminAdminList()
			      );
	 break;
	 case "branch-list":
	 		      loadPartial(
	 		        `${BASE_URL}/partials/superadmin-branch-list.html`,
	 		        `${BASE_URL}/css/superadmin-branch-list.css`,
	 		        `${BASE_URL}/js/superadmin-branch-list.js`,
	 		        () => initSuperAdminBranchList()
	 		      );
	  break;
	 
	   case "admin-accountrequest-list":
	         loadPartial(
	           `${BASE_URL}/partials/superadmin-accountrequest-list.html`,
	           `${BASE_URL}/css/superadmin-accountrequest-list.css`,
	           `${BASE_URL}/js/superadmin-accountrequest-list.js`,
	           () => initSuperAdminAccountRequestList()
	         );
	     break;
		 case "admin-request-list":
		       loadPartial(
		         `${BASE_URL}/partials/superadmin-request-list.html`,
		         `${BASE_URL}/css/superadmin-request-list.css`,
		         `${BASE_URL}/js/superadmin-request-list.js`,
		          () => initSuperAdminRequestList()
		       );
		   break;
	 
    case "dashboard":
      loadPartial(
        `${BASE_URL}/partials/dashboard-content.html`,
        `${BASE_URL}/css/dashboard-content.css`,
        `${BASE_URL}/js/dashboard-content.js`,
        () => initDashboardContent()
      );
      break;

    case "payment":
      loadPartial(
        `${BASE_URL}/partials/payment-form.html`,
        `${BASE_URL}/css/form.css`,
        `${BASE_URL}/js/payform.js`,
        () => initPayform()
      );
      break;
	  case "admin-payment":
	        loadPartial(
	          `${BASE_URL}/partials/superadmin-payform.html`,
	          `${BASE_URL}/css/superadmin-payform.css`,
	          `${BASE_URL}/js/superadmin-payform.js`,
	          () => initAdminPayform()
	        );
	        break;
	  case "superadmin-payment":
	      loadPartial(
		  `${BASE_URL}/partials/superadmin-payform.html`,
          `${BASE_URL}/css/superadmin-payform.css`,
	       `${BASE_URL}/js/superadmin-payform.js`,
		   () => initAdminPayform()
	       );
	       break;

    case "transaction":
      loadPartial(
        `${BASE_URL}/partials/transaction-table.html`,
        `${BASE_URL}/css/transaction.css`,
        `${BASE_URL}/js/transaction.js`,
        () => initTransactionPage()
      );
      break;
	  case "admin-transactions":
	          loadPartial(
	            `${BASE_URL}/partials/superadmin-transaction.html`,
	            `${BASE_URL}/css/transaction.css`,
	            `${BASE_URL}/js/superadmin-transaction.js`,
	            () => initAdminTransactionPage()
	          );
	          break;
	    case "superadmin-transactions":
	        loadPartial(
	  	  `${BASE_URL}/partials/superadmin-transaction.html`,
	           `${BASE_URL}/css/superadmin-transaction.css`,
	         `${BASE_URL}/js/superadmin-transaction.js`,
	  	   () => initAdminTransactionPage()
	         );
	         break;
	


    case "account":
      loadPartial(
        `${BASE_URL}/partials/account.html`,
        `${BASE_URL}/css/account.css`,
        `${BASE_URL}/js/account.js`,
        () => initAccountPage()
      );
      break;
	  case "admin-accounts":
	        loadPartial(
	          `${BASE_URL}/partials/superadmin-accounts.html`,
	          `${BASE_URL}/css/superadmin-accounts.css`,
	          `${BASE_URL}/js/superadmin-accounts.js`,
	          () => initAccountTable()
	        );
	        break;
	    case "superadmin-accounts":
			  loadPartial(
			      `${BASE_URL}/partials/superadmin-accounts.html`,
			       `${BASE_URL}/css/superadmin-accounts.css`,
			       `${BASE_URL}/js/superadmin-accounts.js`,
			        () => initAccountTable()
			      );
			      break;
		
	  

    default:
      document.getElementById("dashboardContent").innerHTML =
        "<p style='color: red;'>Invalid menu option</p>";
  }
}

function loadPartial(path, cssPath, jsPath, initCallback) {
  fetch(path)
    .then((res) => {
      if (!res.ok) throw new Error(`Failed to load: ${path}`);
      return res.text();
    })
    .then((html) => {
      const contentDiv = document.getElementById("dashboardContent");
      contentDiv.innerHTML = html;

      requestAnimationFrame(() => {
        removeAllCSS(); 
        loadCSS(cssPath); 
       loadScript(jsPath, initCallback); 
      });
    })
    .catch((err) => {
      console.error(err);
      document.getElementById("dashboardContent").innerHTML =
        "<p style='color: red;'>⚠️ Failed to load content.</p>";
    });
}

// Utility: Load JS only once
function loadScript(src, callback) {
  if (document.querySelector(`script[src="${src}"]`)) {
    if (callback) callback();
    return;
  }

  const script = document.createElement("script");
  script.src = src;
  script.defer = false;
  script.onload = callback;
  document.body.appendChild(script);
}

// Utility: Load CSS only once
function loadCSS(href) {
  if (document.querySelector(`link[href="${href}"]`)) return;

  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = href;
  document.head.appendChild(link);
}

function removeAllCSS() {
	const removableCSS = [
	  "admin-dashboard.css",
	  "superadmin-dashboard.css",
	  "superadmin-request-list.css",
	  "superadmin-accountrequest-list.css",
	  "superadmin-transaction.css",
	  "superadmin-user-list.css",
	  "admin-list.css",
	  "admin-accountrequest-list",
	  "superadmin-branch-list.css",
	  "superadmin-transactions.css",
	  "dashboard.css",
	  "payform.css",
	  "transaction.css",
	  "account.css",
	  "superadmin-accounts.css"
	];


  const links = document.querySelectorAll('link[rel="stylesheet"]');
  links.forEach(link => {
    const href = link.getAttribute("href");
    if (href && removableCSS.some(css => href.includes(css))) {
      link.parentElement.removeChild(link);
    }
  });
}

function openModal(title, bodyHTML) {
  document.getElementById("modalTitle").innerText = title;
  document.getElementById("modalBody").innerHTML = bodyHTML;
  document.getElementById("modalOverlay").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("modalOverlay").classList.add("hidden");
  document.getElementById("modalBody").innerHTML = '';
}
window.addEventListener("popstate", (e) => {
  if (e.state?.action) {
    loadContentByAction(e.state.action, false);
  } else {
    loadDashboardContent(userRole);
  }
});

