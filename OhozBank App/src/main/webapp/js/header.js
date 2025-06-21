// ✅ header.js

// Get URL parameter from query string
function getQueryParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
}

function handleFormFromURL() {
  const formType = getQueryParam("form");
  if (formType === "register") {
    registerFunction();
  } else {
    loginFunction();
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const navToggle = document.getElementById("nav-toggle");
  const navClose = document.getElementById("nav-close");
  const navMenu = document.getElementById("nav-menu");
  const profileIcon = document.getElementById("profileIcon");

  if (navToggle) {
    navToggle.addEventListener("click", () => {
      navMenu.classList.add("show-menu");
    });
  }

  if (navClose) {
    navClose.addEventListener("click", () => {
      navMenu.classList.remove("show-menu");
    });
  }

  // ✅ Handle form type from URL param
  handleFormFromURL();

  // ✅ Add tooltip support for nav links
  const navLinks = document.querySelectorAll(".nav__link[data-tooltip]");
  navLinks.forEach(link => {
    const tooltipText = link.getAttribute("data-tooltip");
    link.setAttribute("title", tooltipText);
  });

  // ✅ Profile icon click handler
  if (profileIcon) {
    profileIcon.addEventListener("click", () => {
      fetch(BASE_URL + "/api/profile/user")
        .then(res => res.json())
        .then(data => {
          alert(`Welcome, ${data.name}`);
          // Extend this to show a profile modal if needed
        })
        .catch(err => {
          console.error("Failed to load profile:", err);
        });
    });
  }

  // ✅ Sidebar toggle functionality
  const sidebarToggle = document.getElementById("sidebarToggle");
  const sidebar = document.getElementById("sidebar");
  const header = document.getElementById("header");
  const main = document.getElementById("main");

  if (sidebarToggle && sidebar && header && main) {
    sidebarToggle.addEventListener("click", () => {
      sidebar.classList.toggle("show-sidebar");
      header.classList.toggle("left-pd");
      main.classList.toggle("left-pd");
    });
  }
});

// ✅ Handle back/forward navigation
window.addEventListener("pageshow", handleFormFromURL);
