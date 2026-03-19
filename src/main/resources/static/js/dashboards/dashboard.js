import { renderManagerDashboard } from "./managerDashboard.js";
import { renderUserManagement } from "../features/userManagement.js";
import { renderMenuManagement } from "../features/renderMenuManagement.js";
import { renderStaffDashboard } from "./staffDashboard.js";
import {renderMenuView} from "../features/ViewMenu.js";
import {renderNewOrderForm} from "../features/orders.js";

function getUserRole() {
    const token = localStorage.getItem("token");
    if (!token || token === "null") return null;

    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(window.atob(base64));

        console.log("Debugging Token Payload:", payload);

        let rawRole = payload.role || payload.roles || payload.authorities || payload.auth;

        if (Array.isArray(rawRole)) {
            rawRole = rawRole[0];
        }

        if (rawRole && typeof rawRole === 'object' && rawRole.authority) {
            rawRole = rawRole.authority;
        }

        if (!rawRole) rawRole = payload.sub;
        if (!rawRole) return null;

        return rawRole.toString().toUpperCase().replace("ROLE_", "");
    } catch (e) {
        console.error("JWT Parsing Error:", e);
        return null;
    }
}

export function renderDashboard(mainAppDiv) {
    const userRole = getUserRole();

    mainAppDiv.innerHTML = `
        <nav class="navbar navbar-dark navbar-expand-lg top-navbar" aria-label="Top Navigation">
            <div class="container-fluid">
                <a class="navbar-brand d-flex align-items-center gap-2" href="#" id="brandLink">
                    <i class="bi bi-cup-hot-fill"></i> Mase Café
                </a>
                <div class="m-0">
                    <button id="logout_button" type="button" class="btn btn-outline-light btn-sm">Logout</button>
                </div>
            </div>
        </nav>
        <div class="container-fluid">
            <div class="row">
               <nav class="col-md-2 sidebar" aria-label="Main Sidebar">
                    <ul class="nav flex-column" id="sidebar-menu">
                        <li class="nav-item role-restricted" data-allowed="MANAGER">
                            <a class="nav-link" href="#" id="nav-dashboard">
                                <i class="bi bi-speedometer2 me-2"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item role-restricted" data-allowed="STAFF">
                            <a class="nav-link" href="#" id="staff-dashboard">
                                <i class="bi bi-speedometer2 me-2"></i> Staff Portal
                            </a>
                        </li>
                    
                        <li class="nav-item role-restricted" data-allowed="MANAGER">
                            <a class="nav-link" href="#" id="nav-users">
                                <i class="bi bi-person-fill-gear me-2"></i> Users
                            </a>
                        </li>
                    
                        <li class="nav-item role-restricted" data-allowed="MANAGER">
                            <a class="nav-link" href="#" id="nav-menus">
                                <i class="bi bi-journal-text me-2"></i> Menu Management
                            </a>
                        </li> 
                        
                        <li class="nav-item role-restricted" data-allowed="STAFF">
                            <a class="nav-link" href="#" id="nav-menus-view">
                                <i class="bi bi-book-half me-2"></i> View Menus
                            </a>
                        </li>
                         
                         <li class="nav-item role-restricted" data-allowed="STAFF">
                            <a class="nav-link" href="#" id="nav-orders">
                                <i class="bi bi-pencil-square me-2"></i> Orders
                            </a>
                        </li>
                    </ul>
                </nav>
                <main class="col-md-10 main-content">
                    <div id="dashboard-root"></div>
                </main>
            </div>
        </div>
        <div id="modal-container"></div>
    `;

    $(".role-restricted").each(function () {
        const allowedStr = $(this).attr("data-allowed") || "";
        const allowedRoles = allowedStr.split(',').map(r => r.trim());

        if (!allowedRoles.includes(userRole)) {
            $(this).remove();
        }
    });

    $("#logout_button").click(() => {
        localStorage.removeItem("token");
        globalThis.location.reload();
    });

    $("#nav-dashboard").click(e => {
        e.preventDefault();
        renderManagerDashboard($("#dashboard-root")[0]);
    });

    $("#staff-dashboard").click(e => {
        e.preventDefault();
        renderStaffDashboard($("#dashboard-root")[0]);
    });

    $("#nav-users").click(e => {
        e.preventDefault();
        renderUserManagement($("#dashboard-root")[0]);
    });

    $("#nav-menus").click(e => {
        e.preventDefault();
        renderMenuManagement($("#dashboard-root")[0]);
    });

    $("#nav-menus-view").click(e => {
        e.preventDefault();
        renderMenuView($("#dashboard-root")[0]);
    });

    $("#nav-orders").click(e => {
        e.preventDefault();
        renderNewOrderForm($("#dashboard-root")[0]);
    });


    if (userRole === "MANAGER") {
        renderManagerDashboard($("#dashboard-root")[0]);
    } else if (userRole === "STAFF") {
        renderStaffDashboard($("#dashboard-root")[0]);
    } else {
        $("#dashboard-root").html('<div class="alert alert-danger">Error: Unknown User Role</div>');
    }
}