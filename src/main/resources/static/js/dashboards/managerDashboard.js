export function renderManagerDashboard(dashboardRoot) {
    if (!dashboardRoot) return;

    dashboardRoot.innerHTML = `
    <div class="container-fluid">
      <div class="row mb-4 align-items-center">
        <div class="col">
          <h2 class="fw-bold mb-1">Manager Dashboard</h2>
          <div class="text-muted">Your café at a glance.</div>
        </div>
      </div>

      <div class="row g-4">
        <div class="col-md-8">
          <div class="card p-4">
            <div class="d-flex align-items-center justify-content-between mb-2">
              <h5 class="mb-0 fw-semibold">Quick Actions</h5>
              <span class="badge bg-secondary">Coming soon</span>
            </div>
            <div class="text-muted">Add actions here (new order, inventory update, reports, etc.).</div>
          </div>
        </div>

        <div class="col-md-4">
           <div id="top-selling-widget-container">
             </div>
        </div>
      </div>
    </div>
  `;

    fetchTopSellingItems();
}

export async function fetchTopSellingItems() {
    const container = document.getElementById('top-selling-widget-container');
    if (!container) return;

    container.innerHTML = `
        <div class="card shadow-sm">
            <div class="card-header bg-white">
                <h5 class="mb-0">Top 5 Best Selling Items</h5>
            </div>
            <div class="card-body" id="top-selling-content">
                <div class="text-muted">Loading analytics...</div>
            </div>
        </div>
    `;

    try {
        const response = await fetch('/api/orders/top-selling', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("token")}`
            }
        });

        if (response.ok) {
            const topItems = await response.json();
            renderTopFiveChart(topItems);
        } else {
            document.getElementById('top-selling-content').innerHTML = "Failed to load data.";
        }
    } catch (error) {
        console.error("Error fetching top selling items:", error);
    }
}

function renderTopFiveChart(items) {
    const container = document.getElementById('top-selling-content');
    if (!items || items.length === 0) {
        container.innerHTML = "No sales data available.";
        return;
    }

    container.innerHTML = items.map((item, index) => `
        <div class="item-rank mb-3">
            <div class="d-flex justify-content-between mb-1">
                <span>#${index + 1} <strong>${item.name}</strong></span>
                <small class="text-muted">${item.totalSold} Sold</small>
            </div>
            <div class="progress" style="height: 10px; background-color: #e9ecef;">
                <div class="progress-bar bg-primary" role="progressbar" 
                     style="width: ${item.percentage}%" 
                     aria-valuenow="${item.percentage}" aria-valuemin="0" aria-valuemax="100">
                </div>
            </div>
        </div>
    `).join('');
}