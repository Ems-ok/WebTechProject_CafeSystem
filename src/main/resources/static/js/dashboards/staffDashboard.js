export async function renderStaffDashboard(dashboardRoot) {
    if (!dashboardRoot) return;

    dashboardRoot.innerHTML = `<div class="text-center p-5"><div class="spinner-border text-primary"></div></div>`;

    try {
        const todayStr = new Date().toISOString().split('T')[0];
        const headers = { 'Authorization': `Bearer ${localStorage.getItem("token")}` };

        const menuResponse= await fetch(`/manager/api/menus/date?date=${todayStr}`, { headers });

        let menuItems = [];
        if (menuResponse.ok) {
            const menu = await menuResponse.json();
            menuItems = menu.items || [];
        }

        dashboardRoot.innerHTML = `
        <div class="container-fluid animate__animated animate__fadeIn">
          <div class="row mb-4 align-items-center">
            <div class="col">
              <h2 class="fw-bold mb-1">Staff Dashboard</h2>
              <div class="text-muted">Operational updates for ${new Date().toLocaleDateString()}</div>
            </div>
          </div>

          <div class="row g-4">
            <div class="col-lg-8">
              <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                  <h5 class="mb-0 fw-bold"><i class="bi bi-journal-check me-2 text-primary"></i>Today's Menu Highlights</h5>
                  <span class="badge bg-soft-primary text-primary">${menuItems.length} Items Listed</span>
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    ${menuItems.length > 0 ? menuItems.map(item => `
                      <div class="col-md-6">
                        <div class="p-3 border rounded-3 bg-light">
                          <div class="d-flex justify-content-between">
                            <h6 class="fw-bold mb-1">${item.name}</h6>
                            <span class="text-success fw-bold">€${item.price.toFixed(2)}</span>
                          </div>
                          <small class="text-muted">${item.category}</small>
                        </div>
                      </div>
                    `).join('') : '<p class="text-center text-muted py-4">No menu set for today yet.</p>'}
                  </div>
                </div>
              </div>
            </div>

            <div class="col-lg-4">
              <div class="card border-0 shadow-sm bg-primary text-white mb-4">
                <div class="card-body p-4">
                  <h5 class="fw-bold"><i class="bi bi-megaphone me-2"></i>Shift Notes</h5>
                  <hr class="border-light">
                  <ul class="list-unstyled mb-0">
                    <li class="mb-2 small"><i class="bi bi-check2-circle me-2"></i> Clean espresso machine at 2PM</li>
                    <li class="mb-2 small"><i class="bi bi-check2-circle me-2"></i> VIP party booked for 4PM (Table 5)</li>
                    <li class="small"><i class="bi bi-check2-circle me-2"></i> New oat milk shipment in fridge</li>
                  </ul>
                </div>
              </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
    } catch (error) {
        console.error("Dashboard Error:", error);
        dashboardRoot.innerHTML = `<div class="alert alert-danger">Error loading dashboard data.</div>`;
    }

}