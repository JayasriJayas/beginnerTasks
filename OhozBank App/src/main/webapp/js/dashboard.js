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

  // 👤 Profile icon click
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

  // Initial dashboard load
  loadPartial(
    `${BASE_URL}/partials/dashboard-content.html`,
    `${BASE_URL}/css/dashboard.css`,
    `${BASE_URL}/js/dashboard-content.js`,
    () => initDashboardContent()
  );

  // Sidebar menu item handling
  document.querySelectorAll(".menu-item").forEach((link) => {
    const action = link.getAttribute("data-action");

    link.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".menu-item").forEach((l) => l.classList.remove("active"));
      link.classList.add("active");

      switch (action) {
        case "dashboard":
          loadPartial(
            `${BASE_URL}/partials/dashboard-content.html`,
            `${BASE_URL}/css/dashboard.css`,
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

        case "transaction":
          loadPartial(
            `${BASE_URL}/partials/transaction-table.html`,
            `${BASE_URL}/css/transaction.css`,
            `${BASE_URL}/js/transaction.js`,
            () => initTransactionPage()
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

        default:
          document.getElementById("dashboardContent").innerHTML =
            "<p style='color: red;'>Invalid menu option</p>";
      }
    });
  });
});

// 📦 Load Partial HTML + CSS + JS Dynamically
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

// 📄 Utility: Load JS only once
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

// 🎨 Utility: Load CSS only once
function loadCSS(href) {
  if (document.querySelector(`link[href="${href}"]`)) return;

  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = href;
  document.head.appendChild(link);
}

// Modal utilities (can be reused globally)
function openModal(title, bodyHTML) {
  document.getElementById("modalTitle").innerText = title;
  document.getElementById("modalBody").innerHTML = bodyHTML;
  document.getElementById("modalOverlay").classList.remove("hidden");
}

function closeModal() {
  document.getElementById("modalOverlay").classList.add("hidden");
  document.getElementById("modalBody").innerHTML = '';
}
