export function renderMenuManagement(container) {
    container.innerHTML = `
        <div class="container-fluid animate__animated animate__fadeIn">
            <div class="row mb-4">
                <div class="col">
                    <h2 class="fw-bold" style="color: var(--cafe-espresso);">Menu Management</h2>
                    <p style="color: var(--cafe-mocha);">Create new items and assign them to your daily schedule.</p>
                </div>
            </div>

            <div class="card shadow-sm border-0">
                <div class="card-header text-white py-3" style="background: linear-gradient(135deg, var(--cafe-espresso), var(--cafe-mocha)); border-radius: 16px 16px 0 0;">
                    <h5 class="mb-0 fw-semibold"><i class="bi bi-cup-hot me-2"></i>Create Item & Add to Menu</h5>
                </div>
                <div class="card-body p-4">
                    <form id="menuItemForm">
                        <div class="row g-4">
                            <div class="col-md-6">
                                <label class="form-label fw-bold" style="color: var(--cafe-espresso);">Menu Date</label>
                                <input type="date" id="menuDate" class="form-control" style="background: var(--cafe-cream); border-radius: 10px;" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold" style="color: var(--cafe-espresso);">Category</label>
                                <select id="itemCategory" class="form-select cafe-dropdown-fix" style="background: var(--cafe-cream); border-radius: 10px;" required>
                                    <option value="Beverage">Beverage</option>
                                    <option value="Pastry">Pastry</option>
                                    <option value="Food">Food</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-bold" style="color: var(--cafe-espresso);">Item Name</label>
                                <input type="text" id="itemName" class="form-control" placeholder="e.g., Caramel Latte" style="background: var(--cafe-cream); border-radius: 10px;" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-bold" style="color: var(--cafe-espresso);">Description</label>
                                <textarea id="itemDescription" class="form-control" rows="2" style="background: var(--cafe-cream); border-radius: 10px;"></textarea>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold" style="color: var(--cafe-espresso);">Price</label>
                                <div class="input-group">
                                    <span class="input-group-text" style="background: var(--cafe-foam); border: 1px solid rgba(43,29,20,0.1);">€</span>
                                    <input type="number" id="itemPrice" class="form-control" step="0.01" style="background: var(--cafe-cream);" required>
                                </div>
                            </div>
                            <div class="col-12 mt-5 text-end">
                                <button type="submit" class="btn btn-primary px-5 py-2">
                                    <i class="bi bi-plus-circle me-2"></i> Save to Menu
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div id="menu-response-msg" class="mt-4"></div>
        </div>
    `;

    const form = document.getElementById('menuItemForm');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const date = document.getElementById('menuDate').value;
        const itemBody = {
            name: document.getElementById('itemName').value,
            description: document.getElementById('itemDescription').value,
            price: parseFloat(document.getElementById('itemPrice').value),
            category: document.getElementById('itemCategory').value
        };

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`/manager/api/menus/create-and-add?date=${date}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(itemBody)
            });

            if (response.ok) {
                $("#menu-response-msg").html(`
                    <div class="alert shadow-sm" style="background: var(--cafe-foam); color: var(--cafe-espresso); border-left: 5px solid var(--cafe-caramel); border-radius: 12px;">
                        <i class="bi bi-check-circle-fill me-2"></i> Successfully added <b>${itemBody.name}</b> to menu for ${date}!
                    </div>
                `);
                form.reset();
            } else {
                throw new Error("Failed to save item.");
            }
        } catch (error) {
            $("#menu-response-msg").html(`
                <div class="alert alert-danger shadow-sm" style="border-radius: 12px;">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i> Error: ${error.message}
                </div>
            `);
        }
    });
}