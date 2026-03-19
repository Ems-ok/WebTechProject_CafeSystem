export async function renderMenuView(container) {
    container.innerHTML = `
        <div class="container mt-4 animate__animated animate__fadeIn">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold">Daily Menus</h2>
                    <p class="text-secondary mb-0">Browse through all our scheduled café offerings.</p>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-6">
                    <div class="input-group shadow-sm">
                        <span class="input-group-text bg-white border-end-0">
                            <i class="bi bi-search text-muted"></i>
                        </span>
                        <input type="text" id="menuSearchInput" 
                               class="form-control border-start-0 ps-0" 
                               placeholder="Search by ID or Date (YYYY-MM-DD)...">
                    </div>
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
    const searchInput = document.getElementById('menuSearchInput');

    try {
        const response = await fetch('/manager/api/menus', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem("token")}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error("Failed to load menus");

        let allMenus = await response.json();

        allMenus.sort((a, b) => new Date(b.menuDate) - new Date(a.menuDate));

        const displayMenus = (filteredList) => {
            if (filteredList.length === 0) {
                menusContainer.innerHTML = `
                    <div class="col-12 text-center py-5">
                        <i class="bi bi-search display-1 text-muted"></i>
                        <p class="mt-3 fs-5">No menus found matching your search.</p>
                    </div>`;
                return;
            }
            menusContainer.innerHTML = filteredList.map(menu => renderMenuCard(menu)).join('');
        };

        displayMenus(allMenus);

        searchInput.addEventListener('input', (e) => {
            const searchTerm = e.target.value.toLowerCase().trim();

            if (!searchTerm) {
                displayMenus(allMenus);
                return;
            }

            const filtered = allMenus.filter(menu => {
                const menuIdStr = menu.id.toString();
                const menuDateStr = menu.menuDate.toLowerCase();

                const isExactId = menuIdStr === searchTerm;

                const isDateMatch = menuDateStr.includes(searchTerm);

                if (isExactId) return true;

                if (!isNaN(searchTerm) && searchTerm.length < 3) {
                    return isExactId;
                }

                return isDateMatch;
            });

            displayMenus(filtered);
        });

    } catch (error) {
        console.error("Error fetching menus:", error);
        menusContainer.innerHTML = `<div class="alert alert-danger">Error loading menus.</div>`;
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
                </div>
                <span class="fw-bold text-success">$${item.price.toFixed(2)}</span>
            </div>
        `).join('')
        : '<p class="text-muted italic">No items added yet.</p>';

    return `
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-0">
                <div class="card-header bg-primary text-white py-2 d-flex justify-content-between">
                    <small>ID: ${menu.id}</small>
                </div>
                <div class="card-body p-4">
                    <h5 class="mb-3 fw-bold text-primary">${formattedDate}</h5>
                    ${itemsHtml}
                </div>
                <div class="card-footer bg-transparent border-0 text-center">
                    <small class="text-muted">Total Items: ${menu.items ? menu.items.length : 0}</small>
                </div>
            </div>
        </div>
    `;
}