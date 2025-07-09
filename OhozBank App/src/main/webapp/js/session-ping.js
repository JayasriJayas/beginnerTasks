setInterval(() => {
  fetch("/api/ping")
    .then(response => {
      if (!response.ok) {
        console.error("Ping failed");
      }
    })
    .catch(error => {
      console.error("Ping error:", error);
    });
}, 300000);
