export function renderMenuManagement(container) {
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
                    <h5 class="mb-0 fw-semibold text-white" id="form-title">
                        <i class="bi bi-cup-hot me-2"></i>Create Item & Add to Menu
                    </h5>
                </div>
                <div class="card-body p-4">
                    <form id="menuItemForm">
                        <input type="hidden" id="itemId"> <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label">Menu Date</label>
                                <input type="date" id="menuDate" class="form-control" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Category</label>
                                <select id="itemCategory" class="form-select" required>
                                    <option value="Beverage">Beverage</option>
                                    <option value="Pastry">Pastry</option>
                                    <option value="Food">Food</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Item Name</label>
                                <input type="text" id="itemName" class="form-control" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Price (€)</label>
                                <input type="number" id="itemPrice" class="form-control" step="0.01" required>
                            </div>
                            <div class="col-12 text-end">
                                <button type="submit" id="saveBtn" class="btn btn-primary px-4">Save to Menu</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div id="menu-response-msg"></div>

            <div class="card shadow-sm border-0">
                <div class="card-body p-0">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="bg-light">
                            <tr>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody id="itemsTableBody">
                            </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    const loadItems = async () => {
        const token = localStorage.getItem("token");
        try {

            const response = await fetch('/manager/api/menus', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const menus = await response.json();
            const tbody = document.getElementById('itemsTableBody');
            tbody.innerHTML = '';

            menus.forEach(menu => {
                menu.items.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${item.name}</td>
                        <td><span class="badge bg-info">${item.category}</span></td>
                        <td>€${item.price.toFixed(2)}</td>
                        <td class="text-center">
                            <button class="btn btn-sm btn-outline-primary edit-trigger" 
                                    id="edit-item-${item.id}" 
                                    data-id="${item.id}"
                                    data-name="${item.name}"
                                    data-category="${item.category}"
                                    data-price="${item.price}"
                                    data-date="${menu.menuDate}">
                                Edit
                            </button>
                        </td>
                    `;
                    tbody.appendChild(row);
                });
            });

            document.querySelectorAll('.edit-trigger').forEach(btn => {
                btn.addEventListener('click', (e) => populateForm(e.target.dataset));
            });

        } catch (err) {
            console.error("Failed to load items", err);
        }
    };

    const populateForm = (data) => {
        document.getElementById('itemId').value = data.id;
        document.getElementById('itemName').value = data.name;
        document.getElementById('itemCategory').value = data.category;
        document.getElementById('itemPrice').value = data.price;
        document.getElementById('menuDate').value = data.date;
        document.getElementById('form-title').innerText = "Update Item";
        document.getElementById('saveBtn').innerText = "Update Item";
    };

    loadItems();

    const form = document.getElementById('menuItemForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('itemId').value;
        const date = document.getElementById('menuDate').value;
        const itemBody = {
            name: document.getElementById('itemName').value,
            price: parseFloat(document.getElementById('itemPrice').value),
            category: document.getElementById('itemCategory').value
        };

        const token = localStorage.getItem("token");
        const url = id
            ? `/manager/api/items/${id}`
            : `/manager/api/menus/create-and-add?date=${date}`;

        const method = id ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(itemBody)
            });

            if (response.ok) {
                $("#menu-response-msg").html('<div class="alert alert-success">Action Successful!</div>');
                form.reset();
                document.getElementById('itemId').value = '';
                loadItems();
            }
        } catch (error) {
            $("#menu-response-msg").html('<div class="alert alert-danger">Error saving item</div>');
        }
    });
}