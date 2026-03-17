export function renderMenuManagement(container) {
    // 1. Added the missing container for the cards/table at the bottom
    container.innerHTML = `
      <div class="container-fluid animate__animated animate__fadeIn">
            <div class="row mb-4">
                <div class="col">
                    <h2 class="fw-bold dashboard-title">Menu Management</h2>
                    <p class="text-muted">Create new items and assign them to your daily schedule.</p>
                </div>
            </div>

            <div class="card menu-card shadow-lg border-0 mb-5">
                <div class="card-header cafe-header py-3">
                    <h5 id="formTitle" class="mb-0 fw-semibold text-white">
                        <i class="bi bi-cup-hot me-2"></i>Create Item & Add to Menu
                    </h5>
                </div>
                <div class="card-body p-4 p-lg-5">
                    <form id="menuItemForm">
                        <input type="hidden" id="editingItemId" value="">
                        <div class="row g-4">
                            <div class="col-md-6">
                                <label class="form-label custom-label">Menu Date</label>
                                <input type="date" id="menuDate" class="form-control cafe-input" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label custom-label">Category</label>
                                <select id="itemCategory" class="form-select cafe-input" required>
                                    <option value="Beverage">Beverage</option>
                                    <option value="Pastry">Pastry</option>
                                    <option value="Food">Food</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label custom-label">Item Name</label>
                                <input type="text" id="itemName" class="form-control cafe-input" placeholder="e.g., Caramel Latte" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label custom-label">Price</label>
                                <div class="input-group cafe-input-group">
                                    <span class="input-group-text">€</span>
                                    <input type="number" id="itemPrice" class="form-control" step="0.01" placeholder="0.00" required>
                                </div>
                            </div>
                            <div class="col-12">
                                <label class="form-label custom-label">Description</label>
                                <textarea id="itemDescription" class="form-control cafe-input" rows="3" placeholder="Describe the item..."></textarea>
                            </div>
                            <div class="col-12 mt-4 text-end">
                                <button type="button" id="cancelEdit" class="btn btn-secondary me-2 d-none">Cancel</button>
                                <button type="submit" id="submitBtn" class="btn btn-primary save-btn px-5 py-2">
                                    <i class="bi bi-plus-circle me-2"></i> Save to Menu
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div id="menu-response-msg" class="mt-4"></div>

            <hr class="my-5">
            <h3 class="fw-bold mb-4">Current Menus</h3>
            <div id="menuCardsContainer" class="row">
                </div>
        </div>
    `;

    const menuContainer = document.getElementById('menuCardsContainer');
    const form = document.getElementById('menuItemForm');
    const submitBtn = document.getElementById('submitBtn');
    const cancelBtn = document.getElementById('cancelEdit');
    const formTitle = document.getElementById('formTitle');
    const editingIdInput = document.getElementById('editingItemId');

    const loadAllMenus = async () => {
        const token = localStorage.getItem("token");
        try {
            const response = await fetch('/manager/api/menus', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const menus = await response.json();

            if (!menus || menus.length === 0) {
                menuContainer.innerHTML = '<p class="text-center text-muted">No menu items found.</p>';
                return;
            }

            menuContainer.innerHTML = menus.map(menu => `
                <div class="col-12 mb-3">
                    <div class="card border-0 shadow-sm overflow-hidden" style="border-radius: 15px;">
                        <div class="card-header bg-white border-bottom py-3 d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold mb-0 text-primary"><i class="bi bi-calendar-event me-2"></i>${menu.menuDate}</h6>
                            <span class="badge bg-light text-primary border">${menu.items.length} Items</span>
                        </div>
                        <div class="card-body p-0">
                            <ul class="list-group list-group-flush">
                                ${menu.items.map(item => `
                                    <li class="list-group-item d-flex justify-content-between align-items-center py-3 px-4">
                                        <div>
                                            <span class="fw-semibold d-block">${item.name}</span>
                                            <small class="text-muted">${item.category} • €${item.price.toFixed(2)}</small>
                                        </div>
                                        <div class="btn-group">
                                            <button class="btn btn-sm btn-outline-primary border-0 edit-btn" 
                                                data-item='${JSON.stringify(item)}' data-date="${menu.menuDate}">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-sm btn-outline-danger border-0 delete-btn" data-id="${item.id}">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </li>
                                `).join('')}
                            </ul>
                        </div>
                    </div>
                </div>
            `).join('');

            attachListeners();
        } catch (err) {
            console.error("Load Failed:", err);
            menuContainer.innerHTML = '<p class="text-danger">Failed to load menus.</p>';
        }
    };

    const attachListeners = () => {
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = JSON.parse(btn.dataset.item);
                const date = btn.dataset.date;

                editingIdInput.value = item.id;
                document.getElementById('menuDate').value = date;
                document.getElementById('itemName').value = item.name;
                document.getElementById('itemCategory').value = item.category;
                document.getElementById('itemPrice').value = item.price;
                document.getElementById('itemDescription').value = item.description || '';

                formTitle.innerHTML = `<i class="bi bi-pencil-square me-2"></i>Edit Item`;
                submitBtn.innerText = "Update Item";
                submitBtn.classList.replace('btn-primary', 'btn-warning');
                cancelBtn.classList.remove('d-none');
                window.scrollTo({ top: 0, behavior: 'smooth' });
            });
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                if(confirm('Remove this item?')) {
                    const id = btn.dataset.id;
                    const token = localStorage.getItem("token");
                    try {
                        const res = await fetch(`/manager/api/items/${id}`, {
                            method: 'DELETE',
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        if(res.ok) loadAllMenus();
                    } catch (err) { console.error("Delete Failed:", err); }
                }
            });
        });
    };

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const itemId = editingIdInput.value;
        const date = document.getElementById('menuDate').value;
        const payload = {
            name: document.getElementById('itemName').value,
            description: document.getElementById('itemDescription').value,
            price: Number.parseFloat(document.getElementById('itemPrice').value),
            category: document.getElementById('itemCategory').value
        };

        const url = itemId ? `/manager/api/items/${itemId}` : `/manager/api/menus/create-and-add?date=${date}`;
        const method = itemId ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem("token")}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                loadAllMenus();
                resetForm();
                $("#menu-response-msg").html(`
                    <div class="alert alert-success border-0 shadow-sm rounded-3">
                        <i class="bi bi-check-circle me-2"></i> Item ${itemId ? 'updated' : 'added'} successfully!
                    </div>
                `);
            } else {
                const err = await response.json();
                alert(err.error || "Action failed");
            }
        } catch (err) { console.error("Submit Failed:", err); }
    });

    const resetForm = () => {
        form.reset();
        editingIdInput.value = "";
        formTitle.innerHTML = `<i class="bi bi-plus-circle me-2"></i>Create Item & Add to Menu`;
        submitBtn.innerText = "Save to Menu";
        submitBtn.classList.replace('btn-warning', 'btn-primary');
        cancelBtn.classList.add('d-none');
    };

    cancelBtn.addEventListener('click', resetForm);

    loadAllMenus();
}