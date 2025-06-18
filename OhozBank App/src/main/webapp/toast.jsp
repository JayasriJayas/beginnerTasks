<!-- Toast Container (For multiple stacked toasts) -->
<div id="toast-container" class="toast-container"></div>

<script>
function showToast(message, type = "info") {
    const container = document.getElementById("toast-container");

    const toast = document.createElement("div");
    toast.className = `toast show ${type}`;

    const icon = document.createElement("i");
    icon.className = "bx toast-icon";
    if (type === "success") icon.classList.add("bx-check-circle");
    else if (type === "error") icon.classList.add("bx-error");
    else icon.classList.add("bx-info-circle");

    const msg = document.createElement("span");
    msg.className = "toast-message";
    msg.textContent = message;

    const closeBtn = document.createElement("span");
    closeBtn.className = "toast-close";
    closeBtn.innerHTML = "&times;";
    closeBtn.onclick = () => toast.remove();

    const timer = document.createElement("div");
    timer.className = "toast-timer";

    toast.appendChild(icon);
    toast.appendChild(msg);
    toast.appendChild(closeBtn);
    toast.appendChild(timer);

    container.appendChild(toast);

    // Start shrinking timer
    setTimeout(() => toast.classList.add("hide"), 2800);
    setTimeout(() => toast.remove(), 3500);
}
</script>
