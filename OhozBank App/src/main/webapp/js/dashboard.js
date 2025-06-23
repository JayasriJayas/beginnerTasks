document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.getElementById("sidebar");
  const toggleBtn = document.getElementById("sidebarToggleBtn");
  const toggleIcon = toggleBtn?.querySelector("i");

  // Sidebar expand/collapse
  if (toggleBtn && sidebar && toggleIcon) {
    toggleBtn.addEventListener("click", () => {
      sidebar.classList.toggle("expanded");
      toggleIcon.classList.toggle("bx-chevron-right");
      toggleIcon.classList.toggle("bx-chevron-left");
    });
  }
  const profileIcon = document.getElementById("profileIcon");
    if (profileIcon) {
      profileIcon.addEventListener("click", () => {
        loadPartial(`${BASE_URL}/partials/profile.html`);
		loadCSS(`${BASE_URL}/css/profile.css`);
		
      });
    }
  // Load initial dashboard content
  loadPartial(`${BASE_URL}/partials/dashboard-content.html`);

  // Sidebar menu click handling
  document.querySelectorAll(".menu-item").forEach((link) => {
    const action = link.getAttribute("data-action");

    link.addEventListener("click", (e) => {
      e.preventDefault();

      document.querySelectorAll(".menu-item").forEach((l) => l.classList.remove("active"));
      link.classList.add("active");

      switch (action) {
        case "dashboard":
          loadPartial(`${BASE_URL}/partials/dashboard-content.html`);
          break;
        case "payment":
          loadPartial(`${BASE_URL}/partials/payment-form.html`);
          break;
        case "transaction":
          loadPartial(`${BASE_URL}/partials/transaction-table.html`);
          break;
        case "account":
          loadPartial(`${BASE_URL}/partials/account-list.html`);
          break;
        default:
          document.getElementById("dashboardContent").innerHTML = "<p>Invalid option.</p>";
      }
    });
  });
});

function loadPartial(path) {
  fetch(path)
    .then((res) => {
      if (!res.ok) throw new Error(`Failed to load: ${path}`);
      return res.text();
    })
    .then((html) => {
      const contentDiv = document.getElementById("dashboardContent");
      contentDiv.innerHTML = html;

      // Defer script/css loading until after DOM is updated
      requestAnimationFrame(() => {
        if (path.includes("transaction-table.html")) {
          loadCSS(`${BASE_URL}/css/transaction.css`);
		  loadScript(`${BASE_URL}/js/transaction.js`, () => {
		     initTransactionPage(); // call this only after transaction.js is loaded
		   });
        } else if (path.includes("payment-form.html")) {
          loadCSS(`${BASE_URL}/css/form.css`);
          loadScript(`${BASE_URL}/js/payform.js`);
        } else if (path.includes("account-list.html")) {
          loadCSS(`${BASE_URL}/css/account.css`);
          loadScript(`${BASE_URL}/js/account.js`);
        }
      });
    })
    .catch((err) => {
      console.error(err);
      document.getElementById("dashboardContent").innerHTML =
        "<p style='color: red;'>⚠️ Failed to load content.</p>";
    });
}

function loadScript(src) {
  if (document.querySelector(`script[src="${src}"]`)) return;

  const script = document.createElement("script");
  script.src = src;
  script.defer = true;
  document.body.appendChild(script);
}

function loadCSS(href) {
  if (document.querySelector(`link[href="${href}"]`)) return;

  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = href;
  document.head.appendChild(link);
}
