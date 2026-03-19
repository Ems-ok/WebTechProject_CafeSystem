export let pendingDeleteUserId = null;

export function renderUserManagement(root) {
    const modalContainer = document.getElementById('modal-container');

    root.innerHTML = `
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold">User Management</h2>
                    <p class="text-secondary mb-0">Manage system users, assign roles, and update credentials.</p>
                </div>
                <button class="btn btn-primary" id="openAddUserBtn">Add User</button>
            </div>
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <table id="userTable" class="table table-hover" style="width:100%">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    modalContainer.innerHTML = getUserModalsHTML();

    initUserTable();
    bindUserEvents();
}

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return token ? { "Authorization": "Bearer " + token } : {};
}

function initUserTable() {
    if ($.fn.DataTable.isDataTable('#userTable')) {
        $('#userTable').DataTable().destroy();
    }

    $('#userTable').DataTable({
        ajax: {
            url: '/manager/api/users',
            dataSrc: '',
            headers: getAuthHeaders()
        },
        columns: [
            { data: 'id' },
            { data: 'username' },
            { data: 'role' },
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

function bindUserEvents() {
    $('#openAddUserBtn').off('click').on('click', () => {
        $('#userForm')[0].reset();
        $('#userId').val('');
        $('#userModalLabel').text('Add User');
        $('#userModal').modal('show');
    });

    $('#userTable').off('click', '.edit-btn').on('click', '.edit-btn', function () {
        const id = $(this).data('id');
        $.ajax({
            url: `/manager/api/users/${id}`,
            method: 'GET',
            headers: getAuthHeaders()
        })
            .done(user => {
                $('#userId').val(user.id);
                $('#username').val(user.username);
                $('#role').val(user.role);
                $('#password').val('');
                $('#userModalLabel').text('Edit User');
                $('#userModal').modal('show');
            })
            .fail(() => alert('Error loading user details'));
    });

    $('#saveUserBtn').off('click').on('click', function () {
        const id = $('#userId').val();
        const userData = {
            username: $('#username').val().trim(),
            role: $('#role').val(),
            password: $('#password').val()
        };

        if (!userData.username || !userData.role) {
            alert("Username and Role are required.");
            return;
        }

        if (id && !userData.password) delete userData.password;

        const method = id ? 'PUT' : 'POST';
        const url = id ? `/manager/api/users/${id}` : '/manager/api/users';

        $('#saveUserBtn').prop('disabled', true);
        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            headers: getAuthHeaders(),
            data: JSON.stringify(userData)
        })
            .done(() => {
                $('#userModal').modal('hide');
                $('#userTable').DataTable().ajax.reload();
            })
            .fail(xhr => {
                const msg = xhr.responseJSON?.message || xhr.responseText;
                alert('Error saving user: ' + msg);
            })
            .always(() => {
                $('#saveUserBtn').prop('disabled', false);
            });
    });

    $('#userTable').off('click', '.delete-btn').on('click', '.delete-btn', function () {
        pendingDeleteUserId = $(this).data('id');
        $('#deleteUserModal').modal('show');
    });

    $('#confirmDeleteBtn').off('click').on('click', function () {
        if (!pendingDeleteUserId) return;

        $.ajax({
            url: `/manager/api/users/${pendingDeleteUserId}`,
            method: 'DELETE',
            headers: getAuthHeaders() // attach JWT
        })
            .done(() => {
                $('#deleteUserModal').modal('hide');
                $('#userTable').DataTable().ajax.reload();
                pendingDeleteUserId = null;
            })
            .fail(xhr => {
                const msg = xhr.responseJSON?.message || xhr.responseText;
                alert('Error deleting user: ' + msg);
            });
    });
}

function getUserModalsHTML() {
    return `
        <div class="modal fade" id="userModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="userModalLabel">Add User</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="userForm">
                            <input type="hidden" id="userId">
                            <div class="mb-3">
                                <label class="form-label">Username</label>
                                <input type="text" class="form-control" id="username" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Password</label>
                                <input type="password" class="form-control" id="password" placeholder="Leave empty to keep unchanged">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Role</label>
                                <select class="form-select" id="role" required>
                                    <option value="" disabled selected>Select Role</option>
                                    <option value="MANAGER">MANAGER</option>
                                    <option value="STAFF">STAFF</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-primary" id="saveUserBtn">Save</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="deleteUserModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Confirm Delete</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this user? This action cannot be undone.
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-danger" id="confirmDeleteBtn">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}