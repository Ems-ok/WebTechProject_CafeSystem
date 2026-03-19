let pendingDeleteOrderId = null;

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

function bindOrderEvents() {
    $('#openAddOrderBtn').off('click').on('click', () => {
        $('#orderForm')[0].reset();
        $('#orderId').val('');
        $('#orderModalLabel').text('Add New Order');
        $('#orderModal').modal('show');
    });

    $('#orderTable').off('click', '.edit-btn').on('click', '.edit-btn', function () {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/orders/${id}`,
            method: 'GET',
            headers: getAuthHeaders()
        })
            .done(order => {
                $('#orderId').val(order.id);
                $('#ordername').val(order.ordername);
                $('#totalAmount').val(order.totalAmount);
                $('#orderModalLabel').text('Edit Order');
                $('#orderModal').modal('show');
            })
            .fail(() => alert('Error loading order details'));
    });

    $('#saveOrderBtn').off('click').on('click', function () {
        const id = $('#orderId').val();
        const orderData = {
            ordername: $('#ordername').val().trim(),
            totalAmount: parseFloat($('#totalAmount').val())
        };

        if (!orderData.ordername || isNaN(orderData.totalAmount)) {
            alert("Order Name and Total Amount are required.");
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
                $('#orderTable').DataTable().ajax.reload();
            })
            .fail(xhr => alert('Error saving order'))
            .always(() => $('#saveOrderBtn').prop('disabled', false));
    });

    $('#orderTable').off('click', '.delete-btn').on('click', '.delete-btn', function () {
        pendingDeleteOrderId = $(this).data('id');
        $('#deleteOrderModal').modal('show');
    });

    $('#confirmDeleteBtn').off('click').on('click', function () {
        const btn = $(this);
        btn.prop('disabled', true);

        $.ajax({
            url: `/api/orders/${pendingDeleteOrderId}`,
            method: 'DELETE',
            headers: getAuthHeaders()
        })
            .done(() => {
                $('#deleteOrderModal').modal('hide');
                $('#orderTable').DataTable().ajax.reload(); // Refresh the table
            })
            .fail(xhr => {
                alert("Delete failed: " + (xhr.responseText || "Server error"));
            })
            .always(() => {
                btn.prop('disabled', false);
            });
    });
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
                                <label class="form-label">Order Name (Reference)</label>
                                <input type="text" class="form-control" id="ordername" placeholder="e.g. Table 5 - Coffee" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Total Amount ($)</label>
                                <input type="number" step="0.01" class="form-control" id="totalAmount" required>
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