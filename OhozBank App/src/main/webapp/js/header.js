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
});

// ✅ Handle back/forward navigation
window.addEventListener("pageshow", handleFormFromURL);