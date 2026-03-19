export async function renderMenuView(container) {
    container.innerHTML = `
        <div class="container mt-4 animate__animated animate__fadeIn">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold">Daily Menus</h2>
                    <p class="text-secondary mb-0">Browse through all our scheduled café offerings.</p>
                </div>
            </div>
            <div id="menus-container" class="row g-4">
                <div class="col-12 text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        </div>
    `;

    const menusContainer = document.getElementById('menus-container');

    try {
        const response = await fetch('/manager/api/menus', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("token")}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error("Failed to load menus");

        const menus = await response.json();

        if (menus.length === 0) {
            menusContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="bi bi-calendar-x display-1 text-muted"></i>
                    <p class="mt-3 fs-5">No menus have been created yet.</p>
                </div>`;
            return;
        }

        menus.sort((a, b) => new Date(b.menuDate) - new Date(a.menuDate));

        menusContainer.innerHTML = menus.map(menu => renderMenuCard(menu)).join('');

    } catch (error) {
        console.error("Error fetching menus:", error);
        menusContainer.innerHTML = `
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                Error loading menus. Please try again later.
            </div>`;
    }
}

function renderMenuCard(menu) {

    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const formattedDate = new Date(menu.menuDate).toLocaleDateString(undefined, options);

    const itemsHtml = menu.items && menu.items.length > 0
        ? menu.items.map(item => `
            <div class="d-flex justify-content-between align-items-start mb-2 border-bottom pb-2">
                <div>
                    <h6 class="mb-0 fw-bold">${item.name}</h6>
                    <small class="text-muted d-block">${item.description || 'No description'}</small>
                    <span class="badge bg-light text-dark border mt-1">${item.category}</span>
                </div>
                <span class="fw-bold text-success">$${item.price.toFixed(2)}</span>
            </div>
        `).join('')
        : '<p class="text-muted italic">No items added to this menu yet.</p>';

    return `
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-0 menu-card-hover">
                <div class="card-header bg-primary text-white py-3">
                    <h5 class="mb-0 text-center">${formattedDate}</h5>
                </div>
                <div class="card-body p-4">
                    ${itemsHtml}
                </div>
                <div class="card-footer bg-transparent border-0 text-center pb-3">
                    <small class="text-muted">Total Items: ${menu.items ? menu.items.length : 0}</small>
                </div>
            </div>
        </div>
    `;
}