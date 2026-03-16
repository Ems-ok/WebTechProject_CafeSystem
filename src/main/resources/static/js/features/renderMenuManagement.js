export function renderMenuManagement(container) {
    container.innerHTML = `
    <div class="container-fluid animate__animated animate__fadeIn">
        <div class="row mb-4">
            <div class="col-12">
                <h2 class="fw-bold dashboard-title">Menu Management</h2>
                <p class="text-muted">Review and refine your <strong>Daily Selection</strong></p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="card menu-card shadow-sm border-0 sticky-top" style="top: 80px;">
                    <div class="card-header cafe-header py-3">
                        <h5 class="mb-0 fw-semibold text-white" id="formTitle">
                            <i class="bi bi-plus-circle me-2"></i>New Entry
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form id="menuItemForm">
                            <input type="hidden" id="editingItemId">
                            <div class="row g-3">
                                <div class="col-12">
                                    <label class="form-label custom-label">Menu Date</label>
                                    <input type="date" id="menuDate" class="form-control cafe-input" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label custom-label">Item Name</label>
                                    <input type="text" id="itemName" class="form-control cafe-input" required>
                                </div>
                                <div class="col-md-7">
                                    <label class="form-label custom-label">Category</label>
                                    <select id="itemCategory" class="form-select cafe-input">
                                        <option value="Beverage">Beverage</option>
                                        <option value="Pastry">Pastry</option>
                                        <option value="Food">Food</option>
                                    </select>
                                </div>
                                <div class="col-md-5">
                                    <label class="form-label custom-label">Price</label>
                                    <div class="input-group">
                                        <span class="input-group-text">€</span>
                                        <input type="number" id="itemPrice" class="form-control" step="0.01" required>
                                    </div>
                                </div>
                                <div class="col-12">
                                    <label class="form-label custom-label">Description</label>
                                    <textarea id="itemDescription" class="form-control cafe-input" rows="2"></textarea>
                                </div>
                                <div class="col-12 mt-4 d-flex gap-2 justify-content-end">
                                    <button type="button" id="cancelEdit" class="btn btn-light d-none">Cancel</button>
                                    <button type="submit" class="btn btn-primary save-btn px-4" id="submitBtn">Add Item</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <div id="menu-response-msg" class="mt-3"></div>
            </div>

            <div class="col-lg-7">
                <h5 class="fw-bold mb-3 text-secondary">Recent Menus</h5>
                <div id="menuCardsContainer" class="row g-3" style="max-height: 75vh; overflow-y: auto; padding-right: 5px;">
                    </div>
            </div>
        </div>
    </div>`;

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

            menuContainer.innerHTML = menus.map(menu => `
                <div class="col-12 mb-3">
                    <div class="card border-0 shadow-sm overflow-hidden" style="border-radius: 15px;">
                        <div class="card-header bg-white border-bottom py-3 d-flex justify-content-between align-items-center">
                            <h6 class="fw-bold mb-0 text-primary"><i class="bi bi-calendar-event me-2"></i>${menu.menuDate}</h6>
                            <span class="badge bg-soft-primary text-primary">${menu.items.length} Items</span>
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
        } catch (err) { console.error("Load Failed:", err); }
    };

    const attachListeners = () => {
        // Edit Listeners
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

        // Delete Listeners
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
            price: parseFloat(document.getElementById('itemPrice').value),
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
        formTitle.innerHTML = `<i class="bi bi-plus-circle me-2"></i>New Entry`;
        submitBtn.innerText = "Add Item";
        submitBtn.classList.replace('btn-warning', 'btn-primary');
        cancelBtn.classList.add('d-none');
    };

    cancelBtn.addEventListener('click', resetForm);
    loadAllMenus();
}