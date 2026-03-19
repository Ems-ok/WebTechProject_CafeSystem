let pendingDeleteOrderId = null;
let menuItems = [];

export function renderNewOrderForm(root) {
    const modalContainer = document.getElementById('modal-container');

    root.innerHTML = `
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold">Order Management</h2>
                    <p class="text-secondary mb-0">Track customer orders and total revenue.</p>
                </div>
                <button class="btn btn-primary" id="openAddOrderBtn">New Order</button>
            </div>
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <table id="orderTable" class="table table-hover" style="width:100%">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Order Name</th>
                                <th>Total Amount</th>
                                <th>Date/Time</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    modalContainer.innerHTML = getOrderModalsHTML();
    initOrderTable();
    bindOrderEvents();
}

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return token ? { "Authorization": "Bearer " + token } : {};
}

function initOrderTable() {
    if ($.fn.DataTable.isDataTable('#orderTable')) {
        $('#orderTable').DataTable().destroy();
    }

    $('#orderTable').DataTable({
        ajax: {
            url: '/api/orders',
            dataSrc: '',
            headers: getAuthHeaders()
        },
        columns: [
            { data: 'id' },
            { data: 'ordername' },
            {
                data: 'totalAmount',
                render: (data) => `$${data.toFixed(2)}`
            },
            {
                data: 'orderTimestamp',
                render: (data) => new Date(data).toLocaleString()
            },
            {
                data: null,
                orderable: false,
                render: function (data) {
                    return `
                        <button class="btn btn-warning btn-sm edit-btn" data-id="${data.id}">Edit</button> 
                        <button class="btn btn-danger btn-sm delete-btn" data-id="${data.id}">Delete</button>
                    `;
                }
            }
        ]
    });
}

function calculateTotal() {
    const itemName = $('#ordername').val();
    const quantity = parseInt($('#quantity').val()) || 0;
    const selectedItem = menuItems.find(item => item.name === itemName);

    if (selectedItem && quantity > 0) {
        const total = selectedItem.price * quantity;
        $('#totalAmount').val(total.toFixed(2));
    } else {
        $('#totalAmount').val('0.00');
    }
}

function bindOrderEvents() {
    $('#openAddOrderBtn').off('click').on('click', () => {

        $('#orderId').val('');
        $('#quantity').val(1);
        $('#totalAmount').val('0.00');

        const form = $('#orderForm')[0];
        if(form) form.reset();

        $('#orderModalLabel').text('Add New Order');

        fetchItemsAndPopulate(() => {
            $('#ordername').val('');
            $('#orderModal').modal('show');
        });
    });

    $('#orderTable').off('click', '.edit-btn').on('click', '.edit-btn', function () {
        const id = $(this).data('id');
        fetchItemsAndPopulate(() => {
            $.ajax({
                url: `/api/orders/${id}`,
                method: 'GET',
                headers: getAuthHeaders()
            })
                .done(order => {
                    $('#orderId').val(order.id);
                    $('#ordername').val(order.ordername);
                    $('#totalAmount').val(order.totalAmount.toFixed(2));
                    $('#quantity').val(1); // Default quantity for edit
                    $('#orderModalLabel').text('Edit Order');
                    $('#orderModal').modal('show');
                })
                .fail(() => alert('Error loading order details'));
        });
    });

    $(document).off('change', '#ordername').on('change', '#ordername', calculateTotal);
    $(document).off('input', '#quantity').on('input', '#quantity', calculateTotal);

    $('#saveOrderBtn').off('click').on('click', function () {
        const id = $('#orderId').val();
        const itemName = $('#ordername').val();
        const quantity = parseInt($('#quantity').val());
        const totalDisplay = parseFloat($('#totalAmount').val());

        const orderData = {
            ordername: "Order for " + itemName,
            totalAmount: totalDisplay,
            orderItems: [
                {
                    item: { name: itemName },
                    quantity: quantity
                }
            ]
        };

        if (!itemName || isNaN(quantity) || quantity <= 0 || totalDisplay <= 0) {
            alert("Please select an item and a valid quantity.");
            return;
        }

        const method = id ? 'PUT' : 'POST';
        const url = id ? `/api/orders/${id}` : '/api/orders';

        $('#saveOrderBtn').prop('disabled', true);

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            headers: getAuthHeaders(),
            data: JSON.stringify(orderData)
        })
            .done(() => {
                $('#orderModal').modal('hide');
                $('#orderId').val('');
                $('#orderTable').DataTable().ajax.reload();
            })
            .fail(xhr => {
                console.error("Server Error:", xhr.responseText);
                const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "Check server logs (500/400 error)";
                alert('Error saving order: ' + errorMsg);
            })
            .always(() => $('#saveOrderBtn').prop('disabled', false));
    });

    $('#orderTable').off('click', '.delete-btn').on('click', '.delete-btn', function () {
        pendingDeleteOrderId = $(this).data('id');
        $('#deleteOrderModal').modal('show');
    });

    $('#confirmDeleteBtn').off('click').on('click', function () {
        $.ajax({
            url: `/api/orders/${pendingDeleteOrderId}`,
            method: 'DELETE',
            headers: getAuthHeaders()
        })
            .done(() => {
                $('#deleteOrderModal').modal('hide');
                $('#orderTable').DataTable().ajax.reload();
            })
            .fail(xhr => alert("Delete failed"));
    });
}

function fetchItemsAndPopulate(callback) {
    $.ajax({
        url: '/manager/api/items',
        method: 'GET',
        headers: getAuthHeaders()
    })
        .done(items => {
            menuItems = items;
            let options = '<option value="" disabled selected>Choose an item...</option>';
            items.forEach(item => {

                options += `<option value="${item.name}" data-price="${item.price}">${item.name} ($${item.price.toFixed(2)})</option>`;
            });
            $('#ordername').html(options);
            if (callback) callback();
        })
        .fail(() => alert('Failed to load menu items. Verify the API path.'));
}

function getOrderModalsHTML() {
    return `
        <div class="modal fade" id="orderModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="orderModalLabel">Order Details</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="orderForm">
                            <input type="hidden" id="orderId">
                            <div class="mb-3">
                                <label class="form-label">Select Item</label>
                                <select class="form-select" id="ordername" required>
                                    <option value="" disabled selected>Loading...</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Quantity</label>
                                <input type="number" class="form-control" id="quantity" min="1" value="1" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Total Amount (€)</label>
                                <input type="text" class="form-control bg-light" id="totalAmount" readonly value="0.00">
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-primary" id="saveOrderBtn">Save Order</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="deleteOrderModal" tabindex="-1">
             <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Delete Order</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">Are you sure? This cannot be undone.</div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-danger" id="confirmDeleteBtn">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}