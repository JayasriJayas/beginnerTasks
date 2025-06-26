function showToast(message, type = "info") {
  const toastContainer = document.getElementById("toast-container");
  if (!toastContainer) return;

  const icons = {
    info: "bx bx-info-circle",
    success: "bx bx-check-circle",
    error: "bx bx-error-circle",
    warning: "bx bx-error"
  };

  const iconClass = icons[type] || icons.info;

  const toast = document.createElement("div");
  toast.className = `toast show ${type}`;
  toast.innerHTML = `
    <i class="toast-icon ${iconClass}"></i>
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
