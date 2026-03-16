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

      <div class="row g-4 mb-4">
        ${renderStatCard('Today Sales', '$0.00', 'bi-cash-coin')}
        ${renderStatCard('Orders', '0', 'bi-receipt')}
        ${renderStatCard('Customers', '0', 'bi-people')}
        ${renderStatCard('Low Stock', '0', 'bi-exclamation-triangle')}
      </div>

      <div class="row g-4">
        <div class="col-12">
          <div class="card p-4">
            <div class="d-flex align-items-center justify-content-between mb-2">
              <h5 class="mb-0 fw-semibold">Quick Actions</h5>
              <span class="badge bg-secondary">Coming soon</span>
            </div>
            <div class="text-muted">Add actions here (new order, inventory update, reports, etc.).</div>
          </div>
        </div>
      </div>
    </div>
  `;
}

function renderStatCard(title, value, icon) {
    return `
    <div class="col-12 col-sm-6 col-xl-3">
      <div class="card p-4 h-100">
        <div class="d-flex align-items-center justify-content-between">
          <div>
            <div class="text-muted small">${title}</div>
            <div class="fs-4 fw-bold mt-1">${value}</div>
          </div>
          <div class="fs-3 text-muted">
            <i class="bi ${icon}"></i>
          </div>
        </div>
      </div>
    </div>
  `;
}