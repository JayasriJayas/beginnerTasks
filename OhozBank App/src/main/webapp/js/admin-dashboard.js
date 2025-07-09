window.initAdminDashboard = function () {
  loadTopBar();
  loadMetrics();
  loadTransactionPieChart();
  loadDailyTransactionChart(); 
  loadAccountSummaryChart();  
};


async function loadTopBar() {
  try {
    const res = await fetch(`${BASE_URL}/api/profile/user`, {
      method: "POST",
      credentials: "include"
    });

    const { user } = await res.json();
    const { name, roleId } = user;

    const roleMap = {
      1: "Super Admin",
      2: "Admin",
      3: "Customer"
    };

    document.getElementById("userName").textContent = name;
    document.getElementById("userRole").innerHTML = `
      <i class='bx bx-user'></i> ${roleMap[roleId] || "Unknown"}
    `;

    const now = new Date();
    const formattedDate = now.toLocaleDateString("en-IN", {
      weekday: "short",  
      day: "numeric",    
      month: "short",   
      year: "numeric"    
    });

    const formattedTime = now.toLocaleTimeString("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true       
    });

    document.getElementById("currentDateTime").innerHTML = `
      <i class='bx bx-time'></i> ${formattedDate} · ${formattedTime}
    `;
  } catch (err) {
    console.error("Failed to load user profile:", err);
    document.getElementById("welcomeText").innerText = "Welcome, Admin!";
    document.getElementById("userRole").innerText = "Admin";
    document.getElementById("currentDateTime").innerText = "--";
  }

  updateTime();
  setInterval(updateTime, 60000);
}

function updateTime() {
  const now = new Date();
  const formattedDate = now.toLocaleDateString("en-IN", {
    weekday: "short",  
    day: "numeric",    
    month: "short",   
    year: "numeric"    
  });

  const formattedTime = now.toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true       
  });

  document.getElementById("currentDateTime").innerHTML = `
    <i class='bx bx-time'></i> ${formattedDate} · ${formattedTime}
  `;
}

async function loadMetrics() {
  try {
    const endpoints = {
      accountRequestCount: "/api/status-counts/account-request",
      userRequestCount: "/api/status-counts/request",
      userCount: "/api/total-users/user",
      accountCount: "/api/total-accounts/account"
    };

    const [
      acctReqRes, userReqRes, usersRes, accountsRes
    ] = await Promise.all(
      Object.values(endpoints).map(path =>
        fetch(`${BASE_URL}${path}`, {
          method: "GET",
          credentials: "include"
        }).then(res => res.json())
      )
    );

    document.getElementById("accountRequestCount").innerText = acctReqRes.PENDING ?? "--";
    document.getElementById("userRequestCount").innerText = userReqRes.PENDING ?? "--";
    document.getElementById("userCount").innerText = usersRes.totalUsers ?? "--";
    document.getElementById("accountCount").innerText = accountsRes.totalAccounts ?? "--";

    document.querySelectorAll(".metric-card.clickable").forEach(card => {
      card.addEventListener("click", () => {
        const action = card.getAttribute("data-action");
        if (action) {
          const menuItem = document.querySelector(`.menu-item[data-action="${action}"]`);
          if (menuItem) {
            menuItem.click();
          }
        }
      });
    });
  } catch (err) {
    console.error("Failed to load metrics:", err);
    showToast("Failed to load dashboard metrics", "error");
  }
}

async function loadTransactionPieChart() {
  try {
    const res = await fetch(`${BASE_URL}/api/total-type/transaction`, {
      method: "GET",
      credentials: "include"
    });
    
    const data = await res.json();

    
    const deposit = data.DEPOSIT ?? 0;
    const transfer = data.TRANSFER ?? 0;
    const withdrawal = data.WITHDRAWAL ?? 0;

    renderPieChart(deposit, transfer, withdrawal);
  } catch (err) {
    console.error("Failed to load transaction data for the pie chart:", err);
    showToast("Failed to load transaction data", "error");
  }
}

window.initAdminDashboard = function () {
  loadTopBar();
  loadMetrics();
  loadTransactionPieChart();  
  loadDailyTransactionChart();
  loadAccountSummaryChart();
};


function renderPieChart(deposit, transfer, withdrawal) {
  const ctx = document.getElementById("transactionPieChart").getContext("2d");

  new Chart(ctx, {
    type: "pie",
    data: {
      labels: ["Deposit", "Transfer", "Withdrawal"],
      datasets: [{
        data: [deposit, transfer, withdrawal],
        backgroundColor: ["#8CBDB9", "#E09E50", "#2D3E4E"],
        borderColor: "#fff",
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: "bottom"
        },
        tooltip: {
          callbacks: {
            label: (ctx) => {
              return `${ctx.label}: ₹${ctx.parsed.toLocaleString("en-IN")}`;
            }
          }
        }
      }
    }
  });
}


function getLastThreeMonthsDateRange() {
  const today = new Date();
  const fromDate = new Date(today);
  fromDate.setMonth(today.getMonth() - 3); 
  const toDate = today.toISOString().split('T')[0];
  const fromDateFormatted = fromDate.toISOString().split('T')[0];

  return { fromDate: fromDateFormatted, toDate: toDate };
}

async function loadDailyTransactionChart() {
  try {
    const { fromDate, toDate } = getLastThreeMonthsDateRange();
    const res = await fetch(`${BASE_URL}/api/daily-counts/transaction`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        fromDate: fromDate,
        toDate: toDate
      }),
      credentials: "include"
    });

    const data = await res.json();
    const labels = data.map(item => item.date);
    const transactionCounts = data.map(item => item.transactionCount);

    renderLineChart(labels, transactionCounts); 
  } catch (err) {
    console.error("Failed to load daily transaction data:", err);
    showToast("Failed to load daily transaction data", "error");
  }
}

function renderLineChart(labels, transactionCounts) {
  const ctx = document.getElementById("dailyTransactionLineChart").getContext("2d");

  new Chart(ctx, {
    type: "line",
    data: {
      labels: labels, 
      datasets: [{
        label: "Daily Transaction Counts", 
        data: transactionCounts,
        borderColor: "#E09E50", 
        backgroundColor: "rgba(224, 158, 80, 0.1)", 
        borderWidth: 2,
        tension: 0.2
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false }, 
        tooltip: {
          callbacks: {
            label: (ctx) => `Transactions: ${ctx.parsed.y}` 
          }
        }
      },
      scales: {
        y: { beginAtZero: true } 
      }
    }
  });
}


async function loadAccountSummaryChart() {
  try {
    const res = await fetch(`${BASE_URL}/api/account-summary/transaction`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ limit: 0 }),
      credentials: "include"
    });

    const data = await res.json();
    const labels = data.map(item => `Account ${item.accountId}`);
    const depositData = data.map(item => item.deposit);
    const transferData = data.map(item => item.transfer);
    const withdrawalData = data.map(item => item.withdrawal);

    renderBarChart(labels, depositData, transferData, withdrawalData); 
  } catch (err) {
    console.error("Failed to load account summary data:", err);
    showToast("Failed to load account summary data", "error");
  }
}

// Render the account summary bar chart (stacked)
function renderBarChart(labels, depositData, transferData, withdrawalData) {
  const ctx = document.getElementById("accountSummaryBarChart").getContext("2d");

  new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Deposit",
          data: depositData,
          backgroundColor: "#8CBDB9",
          borderColor: "#2D3E4E",
          borderWidth: 1
        },
        {
          label: "Transfer",
          data: transferData,
          backgroundColor: "#E09E50",
          borderColor: "#2D3E4E",
          borderWidth: 1
        },
        {
          label: "Withdrawal",
          data: withdrawalData,
          backgroundColor: "#2D3E4E",
          borderColor: "#2D3E4E",
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: "top" },
        tooltip: {
          callbacks: {
            label: (ctx) => `₹${ctx.parsed.y.toLocaleString("en-IN")}`
          }
        }
      },
      scales: {
        y: { beginAtZero: true, stacked: true },
        x: { stacked: true }
      }
    }
  });
}

